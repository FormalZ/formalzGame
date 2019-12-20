<?php
//This program has been developed by students from the bachelor Computer Science 
//at Utrecht University within the Software and Game project course (2018)
//©Copyright Utrecht University (Department of Information and Computing Sciences)
namespace Tests\Feature;

use Tests\TestCase;
use App\User;
use App\Room;
use App\Invite;
use App\Admin;
use Illuminate\Foundation\Testing\WithFaker;
use Illuminate\Foundation\Testing\RefreshDatabase;
use Faker;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Auth;

class AccountManagementTest extends TestCase
{

    use RefreshDatabase;

    //Test if a user can view the password change form:
    public function testPasswordChangeFormShow()
    {
        // generate a user:
        $user = factory(User::class)->create();
        
        //open the form:
        $response = $this->actingAs($user)->json('GET', 'user/changepassword');
        $response->assertStatus(200);
    }

    //Test if a user can change their password:
    public function testPasswordChangeCorrect()
    {
        $faker = Faker\Factory::create();

        //generate a user:
        $oldpassword = 'oldpassword';
        $user = factory(User::class)->create(['password' => bcrypt($oldpassword)]);
        
        //get correct data to change a password:
        $newpassword = $faker->asciify('********************');
        $data = ['password' => $oldpassword,
        'new_password' => $newpassword,
        'new_password_confirmation' => $newpassword
        ];

        $response = $this->actingAs($user)->json('POST', route('user.changepassword'), $data);

        $response->assertStatus(200);
        //check if we can login using the new password:
        $newcredentials = ['email' => $user->email, 'password' => $newpassword];
        $oldcredentials = ['email' => $user->email, 'password' => $oldpassword];
        $this->assertTrue(Auth::validate($newcredentials));
        //check if we cannot login using the old password:
        $this->assertFalse(Auth::validate($oldcredentials));
    }
}
