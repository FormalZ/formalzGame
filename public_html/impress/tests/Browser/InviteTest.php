<?php
//This program has been developed by students from the bachelor Computer Science 
//at Utrecht University within the Software and Game project course (2018)
//©Copyright Utrecht University (Department of Information and Computing Sciences)
namespace Tests\Browser;

use App\User;
use App\Invite;
use App\Room;
use App\Admin;
use App\Mail\InviteCreated;
use Tests\DuskTestCase;
use Laravel\Dusk\Browser;
use Illuminate\Foundation\Testing\DatabaseMigrations;
use Faker;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Mail;

class InviteTest extends DuskTestCase
{
    use DatabaseMigrations;

    //Test the invitation of a teacher by an admin.
    public function testTeacherInvite()
    {
        Mail::fake();
        $password = 'secret';
        $admin = factory(Admin::class)->create(['password' => bcrypt($password)]);

        $faker = Faker\Factory::create();
        $data = ['email' => $faker->email()];

        //browse to the website, login as admin and send email
        $this->browse(function (Browser $browser) use ($data, $admin, $password) {
            $browser->visit('/admin/')
                    ->type('email', $admin->email)
                    ->type('password', $password)
                    ->press('Login')
                    ->press('Invite teacher')
                    ->type('email', $data['email'])
                    ->press('Send invite');
        });

        //check the database for relevant values.
        $this->assertDatabaseHas('invites', ['email' => $data['email'], 'invitetype' => 'teacher']);
        //check if the mail was sent:
        //CANNOT BE DONE RIGHT NOW, BECAUSE LARAVEL DUSK DOES NOT SUPPORT MOCKING (YET)
        //Mail::assertSent(InviteCreated::class, function ($mail) use ($data) {
        //    return $mail->hasTo($data['email']);
        //});
    }

    //Test the invitation of a user by teacher.
    public function testUserInvite()
    {
        Mail::fake();
        //make a teacher
        $teacher = factory(User::class)->create();
        DB::table('teachers')->insert(['user_id' => $teacher->id]);
        $teacherid = DB::table('teachers')->where('user_id', $teacher->id)->first()->id;
        //make a room:
        $room = factory(Room::class)->create();
        //make that teacher the owner:
        DB::table('teachersrooms')->insert(['teacher_id' => $teacherid, 'room_id' => $room->id]);

        $faker = Faker\Factory::create();
        $data = ['email' => $faker->email()];
        
        //browse to a room and invite.
        $this->browse(function (Browser $browser) use ($data, $teacher, $room) {
            $browser->loginAs($teacher)
                    ->visit('/room/' . $room->url)
                    ->press('Invite students')
                    ->type('email', $data['email'])
                    ->press('Send invite')
                    ->assertPathIs('/room/' . $room->url);
        });

        //check the database for relevant values.
        $this->assertDatabaseHas('invites', ['email' => $data['email'],
        'invitetype' => 'room', 'room_id' => $room->id]);
        //check if the invite was sent:
        //CANNOT BE DONE RIGHT NOW, BECAUSE LARAVEL DUSK DOES NOT SUPPORT MOCKING (YET)
        //Mail::assertSent(InviteCreated::class, function ($mail) use ($data) {
        //    return $mail->hasTo($data['email']);
        //});
    }
}
