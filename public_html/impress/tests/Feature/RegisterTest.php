<?php
//This program has been developed by students from the bachelor Computer Science 
//at Utrecht University within the Software and Game project course (2018)
//©Copyright Utrecht University (Department of Information and Computing Sciences)
namespace Tests\Feature;

use Tests\TestCase;
use App\User;
use App\Room;
use App\Invite;
use Illuminate\Foundation\Testing\WithFaker;
use Illuminate\Foundation\Testing\RefreshDatabase;
use Faker;
use Illuminate\Support\Facades\DB;

class RegisterTest extends TestCase
{

    use RefreshDatabase;

    //Test the registration of a teacher with valid data and an invite:
    public function testValidTeacherRegistration()
    {
        // generate userdata:
        $data = $this->getValidCreateUserData();
        // create a new teacherinvite record for this user:
        $faker = Faker\Factory::create();
        $token = $faker->sha256();
        $invite = Invite::create([
            'email' => $data['email'],
            'token' => $token,
            'invitetype' => 'teacher'
        ]);

        $response = $this->json('POST', '/teacher/register/' . $invite->token, $data);

        $response->assertStatus(302);
        $this->assertDatabaseHas('users', ['name' => $data['name'], 'email' => $data['email']]);
        $user = User::where('email', $data['email'])->firstOrFail();
        $this->assertDatabaseHas('teachers', ['user_id' => $user->id]);
        $this->assertDatabaseMissing('invites', ['token' => $token]);
        $response->assertHeader('Location', route('home')); //make sure the 302 redirects to the dashboard
    }

    //Test the registration of a user to a room with valid data and an invite:
    public function testValidUserRegistrationMail()
    {
        // generate userdata:
        $data = $this->getValidCreateUserData();
        // create a new room:
        $room = factory(Room::class)->create();
        $faker = Faker\Factory::create();
        $token = $faker->sha256();
        //create a new roominvite record for this user:
        $invite = Invite::create([
            'email' => $data['email'],
            'token' => $token,
            'invitetype' => 'room',
            'room_id' => $room->id
        ]);

        $response = $this->json('POST', route('room.registermail', ['token' => $invite->token]), $data);
        $response->assertStatus(302);
        $this->assertDatabaseHas('users', ['name' => $data['name'], 'email' => $data['email']]);
        $user = User::where('email', $data['email'])->firstOrFail();
        $this->assertDatabaseHas('usersrooms', ['user_id' => $user->id, 'room_id' => $room->id]);
        $this->assertDatabaseMissing('invites', ['token' => $token]);
        $response->assertHeader('Location', route('home')); //make sure the 302 redirects to the dashboard
    }

    //Test the registration of a user to a room with valid data and linkinvitation:
    public function testValidUserRegistrationLink()
    {
        // generate userdata:
        $data = $this->getValidCreateUserData();
        // create a new room:
        $room = factory(Room::class)->create(['linkinvite' => true]);
        $faker = Faker\Factory::create();
        $token = $faker->sha256();

        $response = $this->json('POST', route('room.registerlink', ['id' => $room->url]), $data);
        $response->assertStatus(302);
        $this->assertDatabaseHas('users', ['name' => $data['name'], 'email' => $data['email']]);
        $user = User::where('email', $data['email'])->firstOrFail();
        $this->assertDatabaseHas('usersrooms', ['user_id' => $user->id, 'room_id' => $room->id]);
        $this->assertDatabaseMissing('invites', ['token' => $token]);
        $response->assertHeader('Location', route('home')); //make sure the 302 redirects to the dashboard
    }
    
    //Make sure that teacher registration cannot happen if there is no invite:
    public function testTeacherRegistrationNoInvite403()
    {
        $data = $this->getValidCreateUserData();
        //get a random token:
        $faker = Faker\Factory::create();
        $token = $faker->sha256();

        $response = $this->json('POST', '/teacher/register/' . $token, $data);

        $response->assertStatus(403);
        $this->assertDatabaseMissing('users', ['name' => $data['name'], 'email' => $data['email']]);
    }

    //Make sure that user registration cannot happen if there is no invite:
    public function testUserRegistrationMailNoInvite403()
    {
        $data = $this->getValidCreateUserData();
        // create a new room:
        $room = factory(Room::class)->create();
        //get a random token:
        $faker = Faker\Factory::create();
        $token = $faker->sha256();

        $response = $this->json('POST', route('room.registermail', ['token' => $token]), $data);

        $response->assertStatus(403);
        $this->assertDatabaseMissing('users', ['name' => $data['name'], 'email' => $data['email']]);
    }
       
    //Gets valid faker data to create a user with
    private function getValidCreateUserData()
    {
        $faker = Faker\Factory::create();
        $password = $faker->password();
        return ['name' => 'username',
        'email' => $faker->unique()->email(),
        'password' => $password,
        'password_confirmation' => $password];
    }
}
