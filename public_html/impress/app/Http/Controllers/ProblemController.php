<?php
//This program has been developed by students from the bachelor Computer Science
//at Utrecht University within the Software and Game project course (2018)
//©Copyright Utrecht University (Department of Information and Computing Sciences)
namespace App\Http\Controllers;

use Illuminate\Http\Request;
use App\User;
use App\Room;
use App\Problem;
use App\GameSession;
use App\Path;
use Illuminate\Support\Facades\Validator;
use Illuminate\Support\Facades\Auth;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\URL;
use Illuminate\Support\Facades\Log;
use Illuminate\Validation\ValidationException;
use App\Analytics\AnalyticsController;

/**
 * The controller that handles the problem-logic.
 *
 */
class ProblemController extends Controller
{
    /**
     * Get a validator for an incoming room create request.
     *
     * @param array $data Contains header, description, pre_conditions, post_conditions, difficulty
     * and the problemcount (for the amount of related problems).
     *
     * @return A validator which validates this data.
     */
    protected function createvalidator(array $data)
    {
        return Validator::make($data, [
            'header' => 'required|string|max:255',
            'description' => 'required|string|max:2550',
            'pre_conditions' => 'required|string|max:2550',
            'post_conditions' => 'required|string|max:2550',
            'difficulty' => 'required|integer|min:1|max:5',
            'problemcount' => 'required|integer|min:0|max:10',
            'lives' => 'nullable|integer|min:50|max:500',
            'money' => 'nullable|integer|min:2500|max:10000',
            'deadline' => 'nullable|integer',
            'displayDate' => 'nullable|integer',
        ]);
    }

    /**
     * Show the form for creating a new problem.
     *
     * @param int id (the room id)
     *
     * @return 401 if the user is not a teacher, otherwise the
     * room creation form.
     */
    public function create($id)
    {
        //check if the person creating the problem is the room owner:
        $room = Room::where('url', $id)->firstOrFail();
        $iscreator = $room->isCreator(Auth::id());
        if ($iscreator) {
            //show the problem creation view:
            return view('problem.create', ['room' => $room]);
        }
        abort(401);
    }

    /**
     * Store a newly created problem in storage.
     *
     * @param  int $id The room id
     * @param  Request $request The request to the creation page.
     *
     * @return A 403 if the user is not a teacher, a redirect to
     * the newly created problem profile page otherwise.
     */
    public function store($id, Request $request)
    {
        $data = $request->all();
        $data['deadline'] = strtotime($data['deadline']);
        $data['displayDate'] = strtotime($data['displayDate']);
        // Check if deadline is valid and more than 3 hours after current time
        if (!$data['deadline'] || $data['deadline'] < (time() + 3 * 60 * 60))
            $data['deadline'] = 0;
        if (!$data['displayDate'] || $data['displayDate'] < time())
            $data['displayDate'] = 0;
        //check if the person creating the problem is the room owner:
        $room = Room::where('url', $id)->firstOrFail();
        $iscreator = $room->isCreator(Auth::id());
        if ($iscreator) {
            //create the problem:
            $this->createvalidator($data)->validate();
            //classify the problem:
            $classification = Problem::classifyProblem($data['pre_conditions'], $data['post_conditions']);

            $analyticsConfig = $this->registerPuzzleInAnalytics($room->id);

            $problem = Problem::create([
                'header' => $data['header'],
                'description' => $data['description'],
                'pre_conditions' => $data['pre_conditions'],
                'post_conditions' => $data['post_conditions'],
                'difficulty' => $data['difficulty'],
                'lives' => $data['lives'],
                'money' => $data['money'],
                'deadline' => $data['deadline'],
                'room_id' => $room->id,
                'hasForAll' => $classification['hasForAll'],
                'hasExists' => $classification['hasExists'],
                'hasArrays' => $classification['hasArrays'],
                'hasEquality' => $classification['hasEquality'],
                'hasLogicOperator' => $classification['hasLogicOperator'],
                'hasRelationalComparer' => $classification['hasRelationalComparer'],
                'hasArithmetic' => $classification['hasArithmetic'],
                'hasImplication' => $classification['hasImplication'],
                'problemcount' => $data['problemcount'],
                'displayDate' => $data['displayDate'],
                'trackingCode' => $analyticsConfig['trackingCode'],
                'trackingLink' => $analyticsConfig['activity'],
                'autohide' => array_key_exists('autohide', $data)
            ]);

            //add related problems to the problemlist:
            if (!$problem->findRepoProblems($data['problemcount'])) {
                //if this returns false, there are not enough problems in the repo:
                //so delete the problem:
                $problem->delete();
                //then return an error:
                $validator = Validator::make([], []);
                $validator->errors()
                    ->add('problemcount', 'There are not enough similar problems in the problem database.');
                throw new ValidationException($validator);
            }

            //generate an ingame path for the problem:
            Path::generatePath($problem->id);
            //redirect to the problem:
            return redirect(route('problem.show', ['id' => $problem->id]));
        }
        abort(403);
    }

    /**
     * Display the specified problem.
     *
     * @param  int $id The problem id
     *
     * @return A 401 if the user is not a room member/creator,
     * and the problem profile page otherwise.
     */
    public function show($id)
    {
        //get the problem:
        $problem = Problem::where('id', $id)->firstOrFail();
        //get the room associated with the problem:
        $room = Room::where('id', $problem->room_id)->firstOrFail();
        $iscreator = $room->isCreator(Auth::id());
        if (!$problem->hide && $problem->deadline != 0 && $problem->autohide && $problem->deadline < time()) {
            $problem->hide = true;
            $problem->save();
        }
        if ($problem->displayDate != 0 && $problem->displayDate > time())
            $problem->hide = true;

        //get the highscores on the problem to display a ranking:
        $scores = DB::table('score')->where(['problem_id' => $id, 'legitimate' => true])->orderBy('score', 'created_at')
            ->join('users', 'score.user_id', '=', 'users.id')
            ->select('name', 'score', 'score.created_at', 'score.user_id')->get();

        //get the statistics on a problem to show the creator/user:
        $statistics = $problem->getStatistics($iscreator);
        if (!$iscreator && ($problem->hide || $problem->archive))
            abort(403);
        //show the problem to the creator or room member:
        if ($iscreator ||
            DB::table('usersrooms')->where(['user_id' => Auth::id(), 'room_id' => $room->id])->first() != null) {

            $dashboardUrl = null;
            if (config('app.isAnalyticsEnabled') && $problem->trackingLink != NULL) {
                $dashboardUrl = AnalyticsController::getDashboardUrl($problem->trackingLink);
            }
                
            return view(
                'problem.profile',
                ['problem' => $problem,
                    'room' => $room,
                    'iscreator' => $iscreator,
                    'scores' => $scores,
                    'statistics' => $statistics,
                    'dashboardUrl' => $dashboardUrl,
                    'isAnalyticsEnabled' => config('app.isAnalyticsEnabled'),
                ]
            );
        }

        //user is not authorised to view this problem:
        abort(401);
    }

    /**
     * Show the form for editing the specified resource.
     *
     * @param  int $id The problem id
     *
     * @return A 401 if the user is not the room's creator,
     * the problem edit form otherwise.
     */
    public function edit($id)
    {
        //get the problem:
        $problem = Problem::where('id', $id)->firstOrFail();
        //get the room associated with the problem:
        $room = Room::where('id', $problem->room_id)->firstOrFail();
        if ($room->isCreator(Auth::id())) {
            return view('problem.edit', ['problem' => $problem]);
        }
        abort(401);
    }

    /**
     * Update the specified resource in storage.
     *
     * @param  Request $request The request to the edit form
     * @param  int $id The problem id
     *
     * @return A 403 if the user is not the room's creator,
     * a redirect to the profile page of the edited problem otherwise.
     */
    public function update(Request $request, $id)
    {
        $data = $request->all();
        $data['deadline'] = strtotime($data['deadline']);
        $data['displayDate'] = strtotime($data['displayDate']);
        //get the problem:
        $problem = Problem::where('id', $id)->firstOrFail();
        // Check if deadline is valid and more than 3 hours after current time
        if (!$data['deadline'] || ($data['deadline'] != $problem->deadline && $data['deadline'] < (time() + 3 * 60 * 60)))
            $data['deadline'] = 0;
        if (!$data['displayDate'] || ($data['displayDate'] != $problem->displayDate && $data['displayDate'] < time()))
            $data['displayDate'] = 0;
        //get the room associated with the problem:
        $room = Room::where('id', $problem->room_id)->firstOrFail();

        if ($room->isCreator(Auth::id())) {
            //edit the problem:
            $this->createvalidator($data)->validate();
            $problem->header = $data['header'];
            $problem->description = $data['description'];
            $problem->difficulty = $data['difficulty'];
            $problem->lives = $data['lives'];
            $problem->money = $data['money'];
            $problem->deadline = $data['deadline'];
            $problem->displayDate = $data['displayDate'];
            $problem->autohide = array_key_exists('autohide', $data);
            //if pre/postconditions/count are edited,
            //we need to reclassify the problem.
            $reclassified = false;
            if ($problem->pre_conditions != $data['pre_conditions'] ||
                $problem->post_conditions != $data['post_conditions']) {
                $classification = Problem::classifyProblem($data['pre_conditions'], $data['post_conditions']);
                $problem->pre_conditions = $data['pre_conditions'];
                $problem->post_conditions = $data['post_conditions'];
                //reclassify the problem:
                $problem->hasForAll = $classification['hasForAll'];
                $problem->hasExists = $classification['hasExists'];
                $problem->hasArrays = $classification['hasArrays'];
                $problem->hasEquality = $classification['hasEquality'];
                $problem->hasLogicOperator = $classification['hasLogicOperator'];
                $problem->hasRelationalComparer = $classification['hasRelationalComparer'];
                $problem->hasArithmetic = $classification['hasArithmetic'];
                $problem->hasImplication = $classification['hasImplication'];
                $reclassified = true;
            }
            if ($problem->problemcount != $data['problemcount'] ||
                $reclassified) {
                //get new repoproblems:
                if (!$problem->findRepoProblems($data['problemcount'])) {
                    //if this returns false, there are not enough problems, so throw an error:
                    $validator = Validator::make([], []);
                    $validator->errors()
                        ->add('problemcount', 'There are not enough similar problems in the problem database.');
                    throw new ValidationException($validator);
                }
                $problem->problemcount = $data['problemcount'];
            }

            $problem->save();
            //redirect to the problem:
            return redirect(route('problem.show', ['id' => $problem->id]));
        }
        abort(403);
    }

    /**
     * Shows the form to delete the problem
     *
     * @param int $id The problem id
     *
     * @return A 401 if the user is not the room's creator,
     * and the problem deletion form otherwise.
     */
    public function delete($id)
    {
        //get the problem:
        $problem = Problem::where('id', $id)->firstOrFail();
        //get the room associated with the problem:
        $room = Room::where('id', $problem->room_id)->firstOrFail();
        if ($room->isCreator(Auth::id())) {
            return view('problem.delete', ['problem' => $problem]);
        }
        abort(401);
    }

    /**
     * Remove the specified problem from storage.
     *
     * @param  int $id The problem id.
     *
     * @return 403 if the user is not the room's creator, a
     * redirect to the room profile page otherwise.
     */
    public function destroy($id)
    {
        //get the problem:
        $problem = Problem::where('id', $id)->firstOrFail();
        //get the room associated with the problem:
        $room = Room::where('id', $problem->room_id)->firstOrFail();
        if ($room->isCreator(Auth::id())) {
            //delete the problem:
            $problem->remove();
            //return to the room:
            return redirect('/room/' . $room['url']);
        }
        abort(403);
    }

    /**
     * Play the specified problem.
     *
     * @param  int $id The problem id.
     *
     * @return 401 if the user is not in the room or not the
     * creator, the play view of the problem otherwise.
     */
    public function play($id)
    {
        //get the problem:
        $problem = Problem::where('id', $id)->firstOrFail();
        //get the room associated with the problem:
        $room = Room::where('id', $problem->room_id)->firstOrFail();
        $iscreator = $room->isCreator(Auth::id());
        if (!$iscreator && (($problem->deadline != 0 && $problem->deadline <= time()) || $problem->hide || $problem->archive))
            abort(403);
        if ($iscreator ||
            DB::table('usersrooms')->where(['user_id' => Auth::id(), 'room_id' => $room->id])->first() != null) {
            $sessionid = DB::table('gamesessions')->max('id') + 1;
            $salt = uniqid(mt_rand(), true);
            //create a gamesession for the java backend:
            if (config('app.isAnalyticsEnabled')) {
                $analyticsController = new AnalyticsController(Auth::id());
                try {
                    $token = $analyticsController->Login();
                    $user = User::where('id', Auth::id())->firstOrFail();
                    $user->trackingCode = $token;
                    $user->save();
                } catch (\Exception $e) {
                    // Leave everything as is
                    Log::error($e->getMessage());
                }
            }
            $session = GameSession::create([
                'user_id' => Auth::id(),
                'problem_id' => $id,
                'token' => hash('sha256', $sessionid . Auth::id() . $id . $salt),
		        'hash' => 0
            ]);

            //show the game view and pass the token:
            return view('problem.play', ['token' => $session->token,
                'problemroute' => route('problem.show', ['id' => $id])]);
        }
        abort(401);
    }

    public function hide($id)
    {
        $problem = Problem::where('id', $id)->firstOrFail();
        $room = Room::where('id', $problem->room_id)->firstOrFail();
        if ($room->isCreator(Auth::id())) {
            if (!$problem->hide && $problem->displayDate > time())
                $problem->displayDate = 0;
            else {
                if ($problem->autohide && $problem->deadline < time())
                    $problem->autohide = false;
                $problem->hide = !$problem->hide;
            }
            $problem->save();
            return redirect(route('problem.show', ['id' => $problem->id]));
        }
        abort(403);
    }

    public function archive($id)
    {
        $problem = Problem::where('id', $id)->firstOrFail();
        $room = Room::where('id', $problem->room_id)->firstOrFail();
        if ($room->isCreator(Auth::id())) {
            $problem->archive = !$problem->archive;
            $problem->save();
            if ($problem->archive)
                return redirect('/room/' . $room['url']);
            else
                return redirect(route('problem.show', ['id' => $problem->id]));
        }
        abort(403);
    }

    public function refresh($id)
    {
        $problem = Problem::where('id', $id)->firstOrFail();
        $room = Room::where('id', $problem->room_id)->firstOrFail();
        if ($room->isCreator(Auth::id())) {
            $newProblem = $problem->replicate();
            $analyticsConfig = $this->registerPuzzleInAnalytics($room->id);
            $newProblem->trackingCode = $analyticsConfig['trackingCode'];
            $newProblem->trackingLink = $analyticsConfig['activity'];
            $problem->archive = true;
            $problem->save();
            $newProblem->save();
            return redirect(route('problem.show', ['id' => $newProblem->id]));
        }
        abort(403);
    }

    public function remakePath($id)
    {
        $problem = Problem::where('id', $id)->firstOrFail();
        $room = Room::where('id', $problem->room_id)->firstOrFail();
        if ($room->isCreator(Auth::id())) {
            Path::where('problem_id', $id)->delete();
            Path::generatePath($id);
            return redirect(route('problem.show', ['id' => $id]));
        }
        abort(403);
    }

    public function addTracking($id){
        $problem = Problem::where('id', $id)->firstOrFail();
        $room = Room::where('id', $problem->room_id)->firstOrFail();
        if ($room->isCreator(Auth::id())) {
            if (config('app.isAnalyticsEnabled')) {
                $analyticsConfig = $this->registerPuzzleInAnalytics($room->id);
                if($analyticsConfig == null){
                    // Maybe room not already created
                    try{
                        $analyticsController = new AnalyticsController(Auth::id());
                        $analyticsController->createRoom($room->id, $room->name);
                        $analyticsConfig = $this->registerPuzzleInAnalytics($room->id);
                    }
                    catch(\Error $e){
                        Log::error($e->getMessage());
                    }
                }
                $problem->trackingCode = $analyticsConfig['trackingCode'];
                $problem->trackingLink = $analyticsConfig['activity'];
                $problem->save();
            }
            return redirect(route('problem.show', ['id' => $id]));
        }
        abort(403);
    }

    private function registerPuzzleInAnalytics($roomID)
    {
        $trackingCode = array (
            'activity' => NULL,
            'trackingCode' => NULL,
        );
        if (config('app.isAnalyticsEnabled')) {
            try {
                $room = Room::where('id', $roomID)->firstOrFail();
                $teachers = $room->getTeachersIds();
                $analyticsController = new AnalyticsController($teachers[0]);
                $trackingCode = $analyticsController->createPuzzle($roomID);
            } catch (\Throwable $e) {
                $trackingCode = NULL;
                Log::error($e->getMessage());
            }
        }
        return $trackingCode;
    }

    /**
     * Redirects to the correct location for assets:
     *
     * @param string $filename The name of the asset.
     * @param int $id The problem id (is needed for the route!)
     *
     * @return A redirect to the specified asset.
     */
    public function asset($id, $filename)
    {
        return redirect(URL::asset('/assets/' . $filename));
    }
}
