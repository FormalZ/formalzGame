<?php
//This program has been developed by students from the bachelor Computer Science 
//at Utrecht University within the Software and Game project course (2018)
//©Copyright Utrecht University (Department of Information and Computing Sciences)
namespace App\Http\Controllers;

use Illuminate\Http\Request;

/**
 * The controller for Admins.
 */
class AdminController extends Controller
{
    /**
     * Specifies the guard for the admin in the auth middleware and config/auth.php
     *
     */
    public function __construct()
    {
        $this->middleware('auth:admin');
    }
    /**
     * Returns the admin view.
     *
     * @return The admin view.
     */
    public function index()
    {
        return view('admin');
    }
}
