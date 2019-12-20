<?php
//This program has been developed by students from the bachelor Computer Science 
//at Utrecht University within the Software and Game project course (2018)
//©Copyright Utrecht University (Department of Information and Computing Sciences)
namespace App\Http\Controllers\Auth;

use Illuminate\Http\Request;
use App\Http\Controllers\Controller;
use Auth;
use Illuminate\Support\Facades\Validator;
use Illuminate\Validation\ValidationException;
use App\Admin;

class AdminLoginController extends Controller
{

    public function __construct()
    {
        $this->middleware('guest:admin');
    }

    public function showLoginForm()
    {
        return view('auth.admin-login');
    }

    public function login(Request $request)
    {
        $data = $request->all();
        // Validate the form data
        $this->validate($request, [
            'email' => 'required|email',
            'password' => 'required|min:6'
        ]);
        // Attempt to log the user in, specify the admin guard
        $mail = $data['email'];
        $pass = $data['password'];
        if (Auth::guard('admin')->attempt(['email' => $mail, 'password' => $pass], $request->remember)) {
            // If succesful, delete old session:
            $admin = Admin::where('email', $mail)->firstOrFail();
            $previous_session = $admin->session_id;

            if ($previous_session) {
                \Session::getHandler()->destroy($previous_session);
            }

            $admin->session_id = \Session::getId();
            $admin->save();
            // If successful, then redirect to their intended location:
            return redirect()->intended(route('admin.dashboard'));
        }

        $validator = Validator::make([], []);
        $validator->errors()
        ->add('email', 'This email and password combination is unknown.');
        throw new ValidationException($validator);
        // If unsuccessful, then redirect back to the login with the form data and error.
        return back()->withInput($request->only('email', 'remember'))
        ->withErrors($validator);
    }
}
