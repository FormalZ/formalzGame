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
use Illuminate\Validation\ValidationException;

/**
 * The controller that handles showing example problems to
 * the teacher.
 */
class ProblemExampleController extends Controller
{
    public function __construct()
    {
        $this->middleware('auth');
    }

    /**
     * Display the example problems.
     *
     * @param  int $id The problem id
     *
     * @return A 401 if the user is not teacher,
     * and a page with example problems for each difficulty otherwise.
     */
    public function show()
    {
        //check if user is teacher:
        $isteacher = (DB::table('teachers')->where('user_id', Auth::id())->first() != null);
        if($isteacher) {
            //get a random problem from the repo for each difficulty:
            for($i = 1; $i < 6; $i++) {
                $examples[$i] = DB::table('problemrepo')->where('difficulty', $i)->inRandomOrder()->first();
            }
            return view('problem.examples', ['examples' => $examples]);
        }
        //user is not authorised to view this page:
        abort(401);
    }
}
