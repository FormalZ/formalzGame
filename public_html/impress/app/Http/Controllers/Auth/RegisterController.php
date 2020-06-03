<?php
//This program has been developed by students from the bachelor Computer Science 
//at Utrecht University within the Software and Game project course (2018)
//©Copyright Utrecht University (Department of Information and Computing Sciences)
namespace App\Http\Controllers\Auth;

use App\User;
use App\Invite;
use App\Room;
use Illuminate\Http\Request;
use App\Http\Controllers\Controller;
use Illuminate\Support\Facades\Validator;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Log;
use Illuminate\Foundation\Auth\RegistersUsers;
use Illuminate\Auth\Events\Registered;
use Illuminate\Validation\ValidationException;
use App\Analytics\AnalyticsController;
use App\Analytics\AdminAnalyticsController;

class RegisterController extends Controller
{
    /*
    |--------------------------------------------------------------------------
    | Register Controller
    |--------------------------------------------------------------------------
    |
    | This controller handles the registration of new users as well as their
    | validation and creation. By default this controller uses a trait to
    | provide this functionality without requiring any additional code.
    |
    */

    use RegistersUsers;

    /**
     * Where to redirect users after registration.
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
        $this->middleware('guest');
    }

    /**
     * Get a validator for an incoming registration request.
     *
     * @param  array  $data
     * @return \Illuminate\Contracts\Validation\Validator
     */
    protected function validator(array $data)
    {
        return Validator::make($data, [
            'name' => 'required|string|max:255',
            'email' => 'required|string|email|max:255|unique:users',
            'password' => 'required|string|min:6|confirmed',
        ]);
    }

    /**
     * Create a new user instance after data validation.
     *
     * @param  array  $data
     * @return \App\User
     */
    protected function create(array $data)
    {
        //validate the data:
        $this->validator($data)->validate();
        //run an extra validator to see if the password is not too common:
        $filepath = base_path() . '/resources/commonpasswords/CommonPasswords.txt';
        $passwords = file($filepath, FILE_IGNORE_NEW_LINES);
        if (in_array($data['password'], $passwords)) {
            //if the password is in the common list, throw a validation error.
            $validator = Validator::make([], []);
            $validator->errors()
            ->add('password', 'This password is too common.');
            throw new ValidationException($validator);
        }

        //run an extra validator to verify if the username is not profane:
        if ($this->checkProfanity($data['name'])) {
            //if the name is profane, throw a validation error.
            $validator = Validator::make([], []);
            $validator->errors()
            ->add('name', 'This username contains profanity.');
            throw new ValidationException($validator);
        }

        return User::create([
            'name' => $data['name'],
            'email' => $data['email'],
            'password' => bcrypt($data['password']),
        ]);
    }

    /**
     * Create a new teacher from an invite token.
     *
     * @param string $token
     * @param Request $request (contains the register information)
     * @return redirect
     */
    public function registerteacher($token, Request $request)
    {
        $this->guard()->guest();
        $data = $request->all();
        //check if the invite exists and the email is correct:
        if (!$invite = Invite::where('token', $token)->first()) {
            abort(403);
        }
        if ($invite->email != $data['email']) {
            abort(403);
        }

        //register the teacher:
        event(new Registered($user = $this->create($data)));
        DB::table('teachers')->insert(['user_id' => $user->id]);
        if (config('app.isAnalyticsEnabled')) {
            AdminAnalyticsController::GetInstance()->createUser($user->id, $user->name, 'teacher');
        }
        $invite->delete();
        $this->guard()->login($user);

        return $this->registered($request, $user)
                        ?: redirect($this->redirectPath());
    }

    /**
     * Create a new student from an invite token.
     *
     * @param string $token
     * @param Request $request (contains the register information)
     * @return redirect
     */
    public function registerusermail($token, Request $request)
    {
        $this->guard()->guest();
        $data = $request->all();
        //check if the invite exists and the email is correct:
        if (!$invite = Invite::where('token', $token)->first()) {
            abort(403);
        }
        if ($invite->email != $data['email']) {
            abort(403);
        }

        //get the room:
        $room = Room::where('id', $invite->room_id)->firstOrFail();
        //register the user to the room:
        event(new Registered($user = $this->create($data)));
        DB::table('usersrooms')->insert(['user_id' => $user->id, 'room_id' => $room->id]);
        if (config('app.isAnalyticsEnabled')) {
            try{
                $teachers = $room->getTeachersIds();
                $analyticsController = new AnalyticsController($teachers[0]);
                $analyticsController->createStudent($user->id, $user->name);
                $analyticsController->addParticipants($room->id, ['students' => [$user->id]]);
                DB::table('users')->where(['id' =>$user->id])->update(['trackingCode' => $user->id]);
            }
            catch(\Throwable $e){
                // Leave everything as is
                Log::error($e->getMessage());
            }
        }
        $invite->delete();
        $this->guard()->login($user);

        return $this->registered($request, $user)
                        ?: redirect($this->redirectPath());
    }

    /**
     * Create a new student from a register link.
     *
     * @param string $id       The room url
     * @param Request $request Contains the register information
     * @return redirect
     */
    public function registeruserlink($id, Request $request)
    {
        $this->guard()->guest();
        $data = $request->all();
        //check if the room exists and is linkonly:
        $room = Room::where('url', $id)->firstOrFail();
        if (!$room->linkinvite) {
            abort(403);
        }
        //check if a student with this mail has not been registered to this room yet:
        $registered = DB::table('users')->where('email', $data['email'])->join('usersrooms', 'users.id', '=', 'usersrooms.user_id')
        ->where('room_id', $room->id)->get();
        if(count($registered) > 0) {
            $validator = Validator::make([], []);
            $validator->errors()
            ->add('email', 'This address is already in use. Try logging in.');
            throw new ValidationException($validator);
        }
        //register the user to the room:
        event(new Registered($user = $this->create($data)));
        DB::table('usersrooms')->insert(['user_id' => $user->id, 'room_id' => $room->id]);
        if (config('app.isAnalyticsEnabled')) {
            try{
                $teachers = $room->getTeachersIds();
                $analyticsController = new AnalyticsController($teachers[0]);
                $analyticsController->createStudent($user->id, $user->name);
                $analyticsController->addParticipants($room->id, ['students' => [$user->id]]);
                DB::table('users')->where(['id' =>$user->id])->update(['trackingCode' => $user->id]);
            }
            catch(\Exception $e){
                // Leave everything as is
                Log::error($e->getMessage());
            }
        }
        $this->guard()->login($user);

        return $this->registered($request, $user)
                        ?: redirect($this->redirectPath());
    }

    //functions to block out the standard routes that Laravel sets for authentication:
    public function showRegistrationForm()
    {
        abort(404);
    }

    public function register()
    {
        abort(404);
    }

   /**
    * Checks a string for profanity.
    *
    * @param string $name
    *
    * @return True if profanity, false otherwise.
    */
    function checkProfanity($name)
    {
        $filepath = base_path() . '/resources/profanity/Profanity.txt';
        $badwords = file($filepath, FILE_IGNORE_NEW_LINES);
        foreach ($badwords as $word) {
            if (preg_match($this->profanityRegEx($word), $name) === 1) {
                return true;
            }
        }
        return false;
    }

   /**
    * Creates different regexp for one swearword.
    *
    * @param string $word The bad word.
    *
    * @return The regexp, which also takes numbers into account.
    *
    * @author unkwntech@unkwndesign.com
    */
    function profanityRegEx($word)
    {
        $pattern['a'] = '/[a]/'; $replace['a'] = '[aA@]';
        $pattern['b'] = '/[b]/'; $replace['b'] = '([bB]|I3|l3|i3)';
        $pattern['c'] = '/[c]/'; $replace['c'] = '[cC]';
        $pattern['d'] = '/[d]/'; $replace['d'] = '[dD]';
        $pattern['e'] = '/[e]/'; $replace['e'] = '[eE3]';
        $pattern['f'] = '/[f]/'; $replace['f'] = '([fF]|(ph|pH|Ph|PH))';
        $pattern['g'] = '/[g]/'; $replace['g'] = '[gG6]';
        $pattern['h'] = '/[h]/'; $replace['h'] = '[hH]';
        $pattern['i'] = '/[i]/'; $replace['i'] = '[iIl!1]';
        $pattern['j'] = '/[j]/'; $replace['j'] = '[jJ]';
        $pattern['k'] = '/[k]/'; $replace['k'] = '[kK]';
        $pattern['l'] = '/[l]/'; $replace['l'] = '[lL1!i]';
        $pattern['m'] = '/[m]/'; $replace['m'] = '[mM]';
        $pattern['n'] = '/[n]/'; $replace['n'] = '[nN]';
        $pattern['o'] = '/[o]/'; $replace['o'] = '[oO0]';
        $pattern['p'] = '/[p]/'; $replace['p'] = '[pP]';
        $pattern['q'] = '/[q]/'; $replace['q'] = '[qQ9]';
        $pattern['r'] = '/[r]/'; $replace['r'] = '[rR]';
        $pattern['s'] = '/[s]/'; $replace['s'] = '[sS$5]';
        $pattern['t'] = '/[t]/'; $replace['t'] = '[tT7]';
        $pattern['u'] = '/[u]/'; $replace['u'] = '[uUvV]';
        $pattern['v'] = '/[v]/'; $replace['v'] = '[vVuU]';
        $pattern['w'] = '/[w]/'; $replace['w'] = '([wW]|vv|VV)';
        $pattern['x'] = '/[x]/'; $replace['x'] = '[xX]';
        $pattern['y'] = '/[y]/'; $replace['y'] = '[yY]';
        $pattern['z'] = '/[z]/'; $replace['z'] = '[zZ2]';
        //remove spaces:
        $word = preg_replace('/\s+/', '', $word);
        $word = str_split(strtolower($word));

        $i=0;
        while($i < count($word)) {
            if(!is_numeric($word[$i])) {
                if(array_key_exists ($word[$i],$pattern)) {
                    $word[$i] = preg_replace($pattern[$word[$i]], $replace[$word[$i]], $word[$i]);
                }
            }
            $i++;
        }
        return "/" . implode('', $word) . "/";
    }
}
