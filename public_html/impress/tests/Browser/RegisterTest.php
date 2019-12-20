<?php
//This program has been developed by students from the bachelor Computer Science 
//at Utrecht University within the Software and Game project course (2018)
//©Copyright Utrecht University (Department of Information and Computing Sciences)
namespace Tests\Browser;

use App\User;
use App\Invite;
use App\Room;
use Tests\DuskTestCase;
use Laravel\Dusk\Browser;
use Illuminate\Foundation\Testing\DatabaseMigrations;
use Faker;

class RegisterTest extends DuskTestCase
{
    use DatabaseMigrations;

    //Test the registration of a teacher after an invite.
    public function testTeacherRegistration()
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

        $this->browse(function (Browser $browser) use ($data, $invite) {
            $browser->visit(route('accept', $invite->token))
                    ->type('name', $data['name'])
                    ->type('password', $data['password'])
                    ->type('password_confirmation', $data['password'])
                    ->press('Register')
                    ->assertPathIs('/home');
        });
        
        //test if the teacher is registered in the database and the invite has been deleted:
        $this->assertDatabaseHas('users', ['name' => $data['name'], 'email' => $data['email']]);
        $user = User::where('email', $data['email'])->firstOrFail();
        $this->assertDatabaseHas('teachers', ['user_id' => $user->id]);
        $this->assertDatabaseMissing('invites', ['token' => $token]);
    }

    //Test the registration of a user after an invite.
    public function testUserRegistration()
    {
        // generate userdata:
        $data = $this->getValidCreateUserData();
        // create a new room:
        $room = factory(Room::class)->create();
        // create a new invite record for this user:
        $faker = Faker\Factory::create();
        $token = $faker->sha256();
        $invite = Invite::create([
            'email' => $data['email'],
            'token' => $token,
            'invitetype' => 'room',
            'room_id' => $room->id
        ]);

        $this->browse(function (Browser $browser) use ($data, $invite) {
            $browser->visit(route('accept', $invite->token))
                    ->type('name', $data['name'])
                    ->type('password', $data['password'])
                    ->type('password_confirmation', $data['password'])
                    ->press('Register')
                    ->assertPathIs('/home');
        });
        
        //test if the user is registered in the database and the invite has been deleted:
        $this->assertDatabaseHas('users', ['name' => $data['name'], 'email' => $data['email']]);
        $user = User::where('email', $data['email'])->firstOrFail();
        $this->assertDatabaseMissing('invites', ['token' => $token]);
    }

    //Gets valid faker data to create a user with
    private function getValidCreateUserData()
    {
        $faker = Faker\Factory::create();
        return ['name' => 'username',
        'email' => $faker->unique()->email(),
        'password' => $faker->password
        ];
    }
}
