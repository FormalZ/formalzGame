<?php
//This program has been developed by students from the bachelor Computer Science 
//at Utrecht University within the Software and Game project course (2018)
//©Copyright Utrecht University (Department of Information and Computing Sciences)
namespace App\Http\Controllers;

use App\Room;
use App\Problem;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Validator;
use Illuminate\Support\Facades\Auth;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Log;
use App\Analytics\AnalyticsController;

class RoomController extends Controller
{
    public function __construct()
    {
        $this->middleware('auth');
    }

    /**
     * Get a validator for an incoming room create request.
     *
     * @param array $data (contains name and description)
     *
     * @return \Illuminate\Contracts\Validation\Validator
     */
    protected function createvalidator(array $data)
    {
        return Validator::make($data, [
            'name' => 'required|string|max:255|unique:rooms',
            'description' => 'required|string|max:2550',
        ]);
    }

   /**
    * Get a validator for an incoming room edit request.
    *
    * @param array $data (contains name and description)
    *
    * @return \Illuminate\Contracts\Validation\Validator
    */
    protected function editvalidator(array $data, Room $room)
    {
        if ($room->name == $data['name']) {
            return Validator::make($data, [
                'name' => 'required|string|max:255',
                'description' => 'required|string|max:2550',
            ]);
        } else {
            return $this->createvalidator($data);
        }
    }


    /**
     * Create a new room.
     *
     * @param array $data (contains name and description)
     *
     * @return The room, or a 403 if the user is not a teacher.
     */
    public function create(array $data)
    {
        //check if the creator is a teacher:
        $teacher = DB::table('teachers')->where('user_id', Auth::id())->first();
        if ($teacher != null) {
            //Create room:
            $roomid = DB::table('rooms')->max('id') + 1;
            $salt = uniqid(mt_rand(), true);
            //create a room:
            $room = Room::create([
                'name' => $data['name'],
                'description' => $data['description'],
                'url' => hash('md5', $roomid . $salt),
                'linkinvite' => array_key_exists('linkinvite', $data)
            ]);
            //Set room owner (is a direct query because eloquent cannot
            //handle composite primary keys)
            DB::table('teachersrooms')->insert(['teacher_id' => $teacher->id, 'room_id' => $room->id]);
            if (config('app.isAnalyticsEnabled')) {
                try {
                    $analyticsController = new AnalyticsController($teacher->id);
                    $analyticsController->createRoom($room->id, $data['name']);
                }
                catch(\Throwable $e){
                    // Leave everything as is
                    Log::error($e->getMessage());
                }
            }
            return $room;
        }
        //if the user isn't a teacher, throw 403.
        abort(403);
    }

    /**
     * Show the form for creating a new room.
     *
     * @return The form, or 401 if the user is not a teacher.
     */
    public function showRoomCreationForm()
    {
        //Only show this to teachers:
        $teacher = DB::table('teachers')->where('user_id', Auth::id())->first();
        if ($teacher != null) {
            return view('room.create');
        }
        abort(401);
    }

    /**
     * Handle a creation request for the room.
     *
     * @param  Request $request The request to the create form.
     *
     * @return A redirect to the room, or a 403 if the user is not a teacher.
     */
    public function createRoom(Request $request)
    {
        $data = $request->all();
        $this->createvalidator($data)->validate();
        $room = $this->create($data);
        return redirect('/room/' . $room['url']);
    }

    /**
     * Display the specified resource.
     *
     * @param  int  $id The room id.
     *
     * @return The room view, or a 401 if the user is not a teacher or member of the room.
     */
    public function show($id)
    {
        $room = Room::where('url', $id)->firstOrFail();
        $iscreator = $room->isCreator(Auth::id());
        if ($iscreator ||
        DB::table('usersrooms')->where(['user_id' => Auth::id(), 'room_id' => $room->id])->first() != null) {
            $users = [];
            $archive = [];
            if($iscreator) {
                $problems = Problem::where([['room_id', $room->id], ['archive', 0]])->orderBy('id')->get();
                $archive = Problem::where([['room_id', $room->id], ['archive', 1]])->orderBy('id')->get();
                // Show the teacher all users in the room
                $users = DB::table('usersrooms')->where('room_id', $room->id)
                    ->join('users', 'users.id', '=', 'usersrooms.user_id')->pluck('email');
            }
            else
                $problems = Problem::where([['room_id', $room->id], ['hide', 0], ['archive', 0]])->orderBy('id')->get();
            foreach($problems as $key=>$p){
                if ($p->deadline != 0 && $p->autohide && $p->deadline < time()){
                        $p->hide = true;
                        $p->save();
                        if(!$iscreator)
                            unset($problems[$key]);
                }
                if($p->displayDate != 0 && $p->displayDate > time()){
                    $p->hide = true;
                    if(!$iscreator)
                        unset($problems[$key]);
                }
            }
            return view('room.profile', ['room' => $room, 'iscreator' => $iscreator, 'problems' => $problems, 'users' => $users, 'archive' => $archive]);
        }

        //user is not authorised to view this room:
        abort(401);
    }

    /**
     * Show the form for editing the room.
     *
     * @param  int $id The room id.
     *
     * @return The edit form, or a 401 if the user is not the room's creator.
     */
    public function edit($id)
    {
        $room = Room::where('url', $id)->firstOrFail();
        if ($room->isCreator(Auth::id())) {
            return view('room.edit', ['room' => $room]);
        }
        abort(401);
    }

    /**
     * Update the room data if the room is edited.
     *
     * @param  Request  $request The request to the edit form.
     * @param  int      $id      The room id.
     *
     * @return A redirect to the room, or a 403 if the user is not the room's creator.
     */
    public function update(Request $request, $id)
    {
        $room = Room::where('url', $id)->firstOrFail();
        //only the owner can edit:
        if (!$room->isCreator(Auth::id())) {
            abort(403);
        }
        $data = $request->all();
        $this->editvalidator($data, $room)->validate();
        $room->description = $data['description'];
        $room->name = $data['name'];
        $room->linkinvite = array_key_exists('linkinvite', $data);
        $room->save();
        return redirect('/room/' . $room['url']);
    }

    /**
     * Shows the form to delete the room.
     *
     * @param int $id The room id.
     *
     * @return The delete form or 401 if the user is not the room's creator.
     */
    public function delete($id)
    {
        $room = Room::where('url', $id)->firstOrFail();
        if ($room->isCreator(Auth::id())) {
            return view('room.delete', ['room' => $room]);
        }
        abort(401);
    }

    /**
     * Remove the room from the database.
     *
     * @param  int $id The room id.
     *
     * @return A redirect to the user dashboard or a 403 if the user is not the room's creator.
     */
    public function destroy($id)
    {
        $room = Room::where('url', $id)->firstOrFail();
        //only the owner can edit:
        if (!$room->isCreator(Auth::id())) {
            abort(403);
        }

        //delete associated teachers, problems and users:
        DB::table('usersrooms')->where('room_id', $room->id)->delete();
        DB::table('teachersrooms')->where('room_id', $room->id)->delete();
        $problems = Problem::where('room_id', $room->id)->get();
        foreach($problems as $p) {
            $p->remove();
        }

        //delete the room:
        $room->delete();
        return redirect(route('home'));
    }
}
