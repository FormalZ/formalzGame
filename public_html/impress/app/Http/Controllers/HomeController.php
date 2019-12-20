<?php
//This program has been developed by students from the bachelor Computer Science 
//at Utrecht University within the Software and Game project course (2018)
//©Copyright Utrecht University (Department of Information and Computing Sciences)
namespace App\Http\Controllers;

use App\Room;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Auth;

/**
 * The controller with logic pertaining to the homepage.
 *
 */
class HomeController extends Controller
{
    /**
     * Create a new controller instance, setting the middleware.
     *
     * @return void
     */
    public function __construct()
    {
        $this->middleware('auth');
    }

    /**
     * Show the application dashboard.
     *
     * @return The dashboard view, customised for the login auth.
     */
    public function index()
    {
        //check if user is teacher, if yes, tell view and give view a list of
        //rooms that the teacher is the owner of.
        $teacherobj = DB::table('teachers')->where('user_id', Auth::id())->first();
        $teacherrooms = [];
        if ($teacherobj != null) {
            $isteacher = true;
            $teacherid = $teacherobj->id;
            //get a list of room_ids that the teacher owns:
            $roomids = DB::table('teachersrooms')->where('teacher_id', $teacherid)->pluck('room_id');
        
            foreach ($roomids as $id) {
                $teacherrooms[] = Room::where('id', $id)->firstOrFail();
            }
        } else {
            $isteacher = false;
        }
    
        //Give the view a list of rooms that the user is a student in:
        $roomids = DB::table('usersrooms')->where('user_id', Auth::id())->pluck('room_id');
        $userrooms = [];
        foreach ($roomids as $id) {
            $userrooms[] = Room::where('id', $id)->firstOrFail();
        }
        return view('home', ['isteacher' => $isteacher, 'userrooms' => $userrooms, 'teacherrooms' => $teacherrooms]);
    }

    public function about()
    {
        return view('about');
    }
}
