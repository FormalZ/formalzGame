<?php
//This program has been developed by students from the bachelor Computer Science 
//at Utrecht University within the Software and Game project course (2018)
//©Copyright Utrecht University (Department of Information and Computing Sciences)
/*
|--------------------------------------------------------------------------
| Web Routes
|--------------------------------------------------------------------------
|
| Here is where you can register web routes for your application. These
| routes are loaded by the RouteServiceProvider within a group which
| contains the "web" middleware group. Now create something great!
|
*/

Route::get('/', function () {
    return view('welcome');
});

Auth::routes();

// Basic home route
Route::get('/home', 'HomeController@index')->name('home');
// About route:
Route::get('/about', 'AboutController@about')->name('about');

// User routes:
Route::prefix('user')->group(function () {
    //the route to the password change:
    Route::get('/changepassword', 'Auth\AccountManagementController@showChangePasswordForm');
    Route::post('/changepassword', ['as' => 'user.changepassword', 'uses' => 'Auth\AccountManagementController@changePassword']);
});

//Routes to the different room pages:
//The route to create a new room:
Route::get('createroom', 'RoomController@showRoomCreationForm');
Route::post('createroom', [ 'as' => 'createroom', 'uses' => 'RoomController@createRoom']);
Route::prefix('room')->group(function () {
    //room id specific routes:
    Route::prefix('/{id}')->group(function () {
        //The route to a room page:
        Route::get('', [ 'as' => 'room.show', 'uses' => 'RoomController@show']);
        //The route to edit a room:
        Route::get('/edit', 'RoomController@edit');
        Route::post('/edit', [ 'as' => 'room.edit', 'uses' => 'RoomController@update']);
        //The route to delete a room:
        Route::get('/delete', 'RoomController@delete');
        Route::post('/delete', [ 'as' => 'room.delete', 'uses' => 'RoomController@destroy']);
        //The route to invite a new user:
        Route::get('/invite', 'InviteController@roominvite')->name('room.invite');
        Route::post('/invite', 'InviteController@roomprocess')->name('room.process');
        //The route to join a linkinvite room:
        Route::get('/join', 'InviteController@acceptlink')->name('acceptlink');
        //The route to create a new problem:
        Route::get('/createproblem', 'ProblemController@create');
        Route::post('/createproblem', 'ProblemController@store')->name('problem.create');
        //The route to register a new user to a room using link:
        Route::post('/registerlink', 'Auth\RegisterController@registeruserlink')->name('room.registerlink');
    });
    //The route to register a new user to a room using mail:
    Route::post('/registermail/{token}', 'Auth\RegisterController@registerusermail')->name('room.registermail');
});
//Problem specific routes:
Route::prefix('/problem')->group(function () {
    //problem id specific routes:
    Route::prefix('/{id}')->group(function () {
        //The route to view a problem:
        Route::get('', 'ProblemController@show')->name('problem.show');
        //The route to the edit form for a problem:
        Route::get('/edit', 'ProblemController@edit');
        Route::post('/edit', 'ProblemController@update')->name('problem.edit');
        //The route to delete a problem:
        Route::get('/delete', 'ProblemController@delete');
        Route::post('/delete', 'ProblemController@destroy')->name('problem.delete');
        //The route to play a problem:
        Route::get('/play', 'ProblemController@play');
        //The route to retrieve assets for the game:
        Route::get('/assets/{filename}', 'ProblemController@asset');
        // Hide a problem from students
        Route::get('/hide', 'ProblemController@hide');
        // Archive a problem
        Route::get('/archive', 'ProblemController@archive');
        // Refresh a problem
        Route::get('/refresh', 'ProblemController@refresh');
        // Remake the path for a problem
        Route::get('/remakepath', 'ProblemController@remakePath');
        // Add analytics to a problem
        Route::get('/addtracking', 'ProblemController@addTracking');
    });
});
// Routes for the admin pages, grouped by prefix
Route::prefix('/admin')->group(function () {
    Route::get('/login', 'Auth\AdminLoginController@showLoginForm')->name('admin.login');
    Route::post('/login', 'Auth\AdminLoginController@login')->name('admin.login.submit');
    Route::get('/', 'AdminController@index')->name('admin.dashboard');
    //Routes for inviting new teachers:
    Route::get('/invite', 'InviteController@teacherinvite')->name('admin.invite')->middleware('auth:admin');
    Route::post('/invite', 'InviteController@teacherprocess')->name('admin.process')->middleware('auth:admin');
});

// {token} is a required parameter that will be exposed to us in the controller method
Route::get('/accept/{token}', 'InviteController@acceptmail')->name('acceptmail');

//Routes for the teacher:
Route::prefix('/teacher')->group(function () {
    //Route to show teacher example-problems:
    Route::get('/examples', 'ProblemExampleController@show')->name('teacher.examples');
    //Route for registering as a new teacher:
    Route::post('/register/{token}', 'Auth\RegisterController@registerteacher')->name('teacher.register');
});
