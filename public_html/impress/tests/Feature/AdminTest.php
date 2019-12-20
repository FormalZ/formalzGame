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

class AdminTest extends TestCase
{

    use RefreshDatabase;

    //Test if an admin can view the admin overview:
    public function testAdminShowCorrect()
    {
        // generate admin:
        $admin = factory(Admin::class)->create();
        
        //open the dashboard:
        $response = $this->actingAs($admin, 'admin')->json('GET', route('admin.dashboard'));
        $response->assertStatus(200);
    }

    //Test if a normal user cannot view the admin dashboard:
    public function testAdminShow401()
    {
        //create a user that is not an admin:
        $user = factory(User::class)->create();

        $response = $this->actingAs($user)->json('GET', route('admin.dashboard'));
        $response->assertStatus(401);
    }

    //Test if anyone can view the admin login form:
    public function testAdminLoginForm()
    {
        $response = $this->json('GET', route('admin.login'));
        $response->assertStatus(200);
    }

    //Test if an admin can login:
    public function testAdminLoginCorrect()
    {
        //create an admin:
        $faker = Faker\Factory::create();
        $password = $faker->asciify('********************');
        $admin = factory(Admin::class)->create(['password' => bcrypt($password)]);

        //login:
        $data = ['email' => $admin->email, 'password' => $password];
        $response = $this->json('POST', route('admin.login'), $data);
        $response->assertStatus(302);
        //check if the redirect is to the admin dashboard:
        $response->assertHeader('Location', route('admin.dashboard'));
        //check if we are logged in as admin:
        $this->assertAuthenticatedAs($admin, 'admin');
    }

    
    //Test if an user cannot login as admin with the wrong password:
    public function testAdminLoginWrongPassword()
    {
        //create an admin:
        $password = 'correct';
        $admin = factory(Admin::class)->create(['password' => bcrypt($password)]);

        //login:
        $data = ['email' => $admin->email, 'password' => 'incorrect'];
        $response = $this->json('POST', route('admin.login'), $data);
        $response->assertStatus(422);
        //check if we are NOT logged in:
        $this->assertGuest('admin');
    }

    //Test if a user cannot log into a non-existant account:
    public function testAdminLoginNoAccount()
    {
        //create random credentials:
        $faker = Faker\Factory::create();
        $password = $faker->asciify('********************');
        $email = $faker->email();

        //login:
        $data = ['email' => $email, 'password' => $password];
        $response = $this->json('POST', route('admin.login'), $data);
        $response->assertStatus(422);
        //check if we are NOT logged in:
        $this->assertGuest('admin');
    }
}
