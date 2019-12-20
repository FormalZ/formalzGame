<?php
//This program has been developed by students from the bachelor Computer Science 
//at Utrecht University within the Software and Game project course (2018)
//©Copyright Utrecht University (Department of Information and Computing Sciences)
namespace App\Exceptions;

use Exception;
use Request;
use Illuminate\Auth\AuthenticationException;
use Response;
use Illuminate\Foundation\Exceptions\Handler as ExceptionHandler;
use Mail;
use Symfony\Component\Debug\Exception\FlattenException;
use Symfony\Component\Debug\ExceptionHandler as SymfonyExceptionHandler;
use App\Mail\ExceptionMail;
use Illuminate\Support\Facades\Config;
use Illuminate\Support\Facades\Log;

class Handler extends ExceptionHandler
{
    /**
     * A list of the exception types that are not reported.
     *
     * @var array
     */
    protected $dontReport = [
        //
    ];

    /**
     * A list of the inputs that are never flashed for validation exceptions.
     *
     * @var array
     */
    protected $dontFlash = [
        'password',
        'password_confirmation',
    ];

    /**
     * Report or log an exception.
     *
     * Mails the exception to an email address.
     *
     * @param  \Exception  $exception
     * @return void
     */
    public function report(Exception $exception, $mail = true)
    {
        if ($mail && $this->shouldReport($exception)) {
            $this->sendEmail($exception);
        }
        parent::report($exception);
    }

    /**
     * Email an exception to an admin.
     *
     * @param Exception $exception
     *
     */
    public function sendEmail(Exception $exception)
    {
        try {
            $e = FlattenException::create($exception);

            $handler = new SymfonyExceptionHandler();

            $html = $handler->getHtml($e);
            
            Mail::to(Config::get('mail.errormail'))->send(new ExceptionMail($html));
        } catch (Exception $ex) {
            //the mail cannot be sent, log this!
            Log::alert('Error mail failed to send.', ['mail' => Config::get('mail.errormail')]);
        }
    }

    /**
     * Render an exception into an HTTP response.
     *
     * @param  \Illuminate\Http\Request  $request
     * @param  \Exception  $exception
     * @return \Illuminate\Http\Response
     */
    public function render($request, Exception $exception)
    {
        return parent::render($request, $exception);
    }

    public function unauthenticated($request, AuthenticationException $exception)
    {
        if ($request->expectsJson()) {
            return response()->json(['error' => 'Unauthenticated.'], 401);
        }
        
        // Get the first guard option for this caught exception
        $guard = array_get($exception->guards(), 0);

        // Dynamically switch the route depending on the provider
        switch ($guard) {
            case 'admin':
                $login = 'admin.login';
                break;

            default:
                $login = 'login';
                break;
        }


        return redirect()->guest(route($login));
    }
}
