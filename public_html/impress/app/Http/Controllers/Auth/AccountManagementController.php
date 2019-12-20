<?php
//This program has been developed by students from the bachelor Computer Science 
//at Utrecht University within the Software and Game project course (2018)
//©Copyright Utrecht University (Department of Information and Computing Sciences)
namespace App\Http\Controllers\Auth;

use App\User;
use App\Events\PasswordChanged;
use App\Events\PasswordChangeFail;
use Illuminate\Http\Request;
use App\Http\Controllers\Controller;
use Illuminate\Support\Facades\Validator;
use Illuminate\Support\Facades\Auth;
use Illuminate\Validation\ValidationException;

class AccountManagementController extends Controller
{
    /*
    |--------------------------------------------------------------------------
    | Register Controller
    |--------------------------------------------------------------------------
    |
    | This controller handles the management of user accounts.
    |
    */

    /**
     * Where to redirect users after account changes.
     *
     * @var string
     */
    protected $redirectTo = '/home';

    /**
     * Create a new controller instance.
     *
     * @return void
     */
    public function __construct()
    {
        $this->middleware('auth');
    }

    /**
     * Get a validator for an incoming password change request.
     *
     * @param  array  $data
     * @return \Illuminate\Contracts\Validation\Validator
     */
    protected function passwordChangeValidator(array $data)
    {
        return Validator::make($data, [
            'password' => 'required|string|min:6',
            'new_password' => 'required|string|min:6|confirmed',
        ]);
    }

    /**
     * Change a password after data validation.
     *
     * @param  Request  $data
     * @return A validation error, or a redirect to home.
     */
    protected function changePassword(Request $request)
    {
        $data = $request->all();
        //validate the data:
        $this->passwordChangeValidator($data)->validate();
        //run an extra validator to see if the password is not too common:
        $filepath = base_path() . '/resources/commonpasswords/CommonPasswords.txt';
        $passwords = file($filepath, FILE_IGNORE_NEW_LINES);
        if (in_array($data['new_password'], $passwords)) {
            //if the password is in the common list, throw a validation error.
            $validator = Validator::make([], []);
            $validator->errors()
            ->add('new_password', 'This password is too common.');
            throw new ValidationException($validator);
        }
        
        $user= Auth::user();
        $credentials = ['email' => $user->email, 'password' => $data['password']];

        if (!Auth::validate($credentials)) {
            //log a password change fail event:
            event(new PasswordChangeFail($user));
            //if the password is wrong, throw a validation error.
            $validator = Validator::make([], []);
            $validator->errors()
            ->add('password', 'This password is incorrect.');
            throw new ValidationException($validator);
        }

        $user->password = bcrypt($data['new_password']);
        $user->save();
        //throw an event:
        event(new PasswordChanged($user));
        redirect($this->redirectTo);
    }

    /**
    * Show the password change form.
    *
    * @return The form.
    */
    protected function showChangePasswordForm()
    {
        return view('auth.passwords.change');
    }
}
