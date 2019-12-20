<?php
//This program has been developed by students from the bachelor Computer Science 
//at Utrecht University within the Software and Game project course (2018)
//©Copyright Utrecht University (Department of Information and Computing Sciences)
namespace App\Http\Controllers;

use Illuminate\Http\Request;
use App\User;
use App\Invite;
use App\Room;
use App\Mail\InviteCreated;
use Illuminate\Support\Facades\Validator;
use Illuminate\Support\Facades\Mail;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Auth;
use Illuminate\Validation\ValidationException;

/**
 * The controller that handles invite-logic.
 *
 */
class InviteController extends Controller
{
    /**
     * Get a validator for an incoming invite request.
     *
     * @param array $data Contains the email
     *
     * @return A validator that validates this data.
     */
    protected function invitevalidator(array $data)
    {
        return Validator::make($data, [
        'email' => 'required|string|max:255|email',
        ]);
    }
    
   /**
    * Returns the form for inviting a teacher.
    *
    * @return The form where an admin can invite a teacher.
    */
    public function teacherinvite()
    {
        // show the admin a form with an email filed to invite a new teacher
        return view('auth/admin-invite');
    }
    
   /**
    * Returns the form to invite a user to a classroom.
    *
    * @return The room invite view, or a 401 if the user is not
    * the room's creator.
    */
    public function roominvite($id)
    {
        // show the roomowner a form with an email field to invite a new student to a room
        $room = Room::where('url', $id)->firstOrFail();
        if ($room->isCreator(Auth::id())) {
            return view('auth/invite', ['room' => $room]);
        }
        abort(401);
    }

   /**
    * Processes a request for a teacher-invite.
    *
    * @param Request $request The postrequest on the teacherinvite form.
    *
    * @return A redirect.
    */
    public function teacherprocess(Request $request)
    {
        //if the email is already registered to a user, throw an error.
        $teacher = User::where('email', $request->all()['email'])->first();
        if($teacher) {
            $validator = Validator::make([], []);
            $validator->errors()
            ->add('email', 'This email is already registered.');
            throw new ValidationException($validator);
        }
        //process the request, telling the process function that this is a teacher invite.
        return $this->process(null, $request);
    }

   /**
    * Processes a request for a room-invite.
    *
    * @param Request $request The postrequest on the roominvite form.
    * @param int     $id      The roomid.
    *
    * @return A redirect.
    */
    public function roomprocess($id, Request $request)
    {
        //only process the request if sent by the rooms creator:
        $room = Room::where('url', $id)->firstOrFail();
        if (!$room->isCreator(Auth::id())) {
            abort(403);
        }

        //if we do a roominvite and the room is linkinvite, this shouldn't be called:
        if($room->linkinvite) {
            return $this->roominvite($id);
        }

        //if the email is already registered, just add the user to the room:
        $user = User::where('email', $request->all()['email'])->first();
        if($user) {
            //but you cannot invite yourself:
            if($user->id == Auth::id()) {
                $validator = Validator::make([], []);
                $validator->errors()
                ->add('email', 'You cannot invite yourself.');
                throw new ValidationException($validator);
            }
            
            //user is already in the room:
            if(DB::table('usersrooms')->where(['user_id' => $user->id, 'room_id' => $room->id])->first()) {
                return redirect()->back()->with('success', ['User is already in this room.']);   
            }

            //just add the user:
            DB::table('usersrooms')->insert(['user_id' => $user->id, 'room_id' => $room->id]);
            return redirect()->back()->with('success', ['User is already registered, but has been added to the room!']);   
        }
        
        return $this->process($room->id, $request);
    }

   /**
    * Processes an invite. If $id is null, save it as a teacher invite, otherwise save it as a room invite.
    *
    * @param int     $id      The roomid.
    * @param Request $request The request to the invite-form.
    *
    * @return A redirect to the admin dashboard in case of a teacher invite, a redirect
    * to the room in case of a room invite.
    */
    private function process($id, Request $request)
    {
        // process the form submission and send the invite by email
        // validate the incoming request data
        $data = $request->all();
        $this->invitevalidator($data)->validate();

        $hashid = DB::table('invites')->max('id') + 1;
        $salt = uniqid(mt_rand(), true);
        $token = hash("sha256", $hashid . $salt);
    
        $invitetype = 'teacher';
        if ($id != null) {
            $invitetype = 'room';
        }

        // create a new invite record
        $invite = Invite::create([
            'email' => $data['email'],
            'token' => $token,
            'invitetype' => $invitetype
        ]);

        if ($id != null) {
            $invite->room_id = $id;
            $invite->save();
        }
        // send the email
        Mail::to($request->get('email'))->send(new InviteCreated($invite));
        // redirect back with successmessage:
        return redirect()->back()->with('success', ['Invite has been sent!']);   
    }

   /**
    * Accepts an email invite and sends the user to the register form.
    *
    * @param string $token The invitetoken.
    *
    * @return $404 if the token does not exist, the invite-register view otherwise.
    */
    public function acceptmail($token)
    {
        if (!$invite = Invite::where('token', $token)->first()) {
            abort(404);
        }
        //Only guests can make a new account, so logout if you visit an invite link as a user:
        if (Auth::check()) {
            Auth::logout();
        }
        return view('auth/register', ['link' => false, 'invite' => $invite]);
    }

   /**
    * Accepts a link invite and sends the user to the register form.
    *
    * @param string $id The room-url.
    *
    * @return $404 if the room does not exist, 401 if the room is not linkinvite,
    * the invite-register view otherwise.
    */
    public function acceptlink($id)
    {
        //Only guests can make a new account, so logout if you visit an invite link as a user:
        $room = Room::where('url', $id)->firstOrFail();
        if (Auth::check()) {
            $inRoom = DB::table('usersrooms')->where(['user_id' => Auth::id(), 'room_id' => $room->id])->first();
            if(!$inRoom)
                DB::table('usersrooms')->insert(['user_id' => Auth::id(), 'room_id' => $room->id]);
            return redirect('/home');
            //Auth::logout();
        }
        if(!$room->linkinvite)
            abort(401);
        return view('auth/register', ['link' => true, 'room' => $room]);
    }
}
