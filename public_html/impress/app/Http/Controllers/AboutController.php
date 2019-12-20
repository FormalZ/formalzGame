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
 * The controller with logic pertaining to the about page.
 *
 */
class AboutController extends Controller
{
    /**
     * Show the application about page.
     *
     * @return The dashboard view, customised for the login auth.
     */
    public function about()
    {
        return view('about');
    }
}
