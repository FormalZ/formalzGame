<?php
//This program has been developed by students from the bachelor Computer Science 
//at Utrecht University within the Software and Game project course (2018)
//©Copyright Utrecht University (Department of Information and Computing Sciences)
namespace Tests\Feature;

use Tests\TestCase;
use App\Room;
use App\Problem;
use App\Path;
use Illuminate\Foundation\Testing\WithFaker;
use Illuminate\Foundation\Testing\RefreshDatabase;
use Faker;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Config;
use Illuminate\Support\Facades\Mail;
use App\Mail\ExceptionMail;
use ErrorException;

class ExceptionHandlerTest extends TestCase
{

    use RefreshDatabase;
    
    //test if we can send an exceptionmail for an exception:
   /**
    * @expectedException ErrorException
    */
    public function testSendExceptionMailCorrect()
    {
        Mail::fake();

        $data = ['email' => Config::get('mail.errormail')];

        throw new \ErrorException();

        Mail::assertSent(ExceptionMail::class, function ($mail) use ($data) {
            return $mail->hasTo($data['email']);
        });
    }
}
