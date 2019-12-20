<?php
//This program has been developed by students from the bachelor Computer Science 
//at Utrecht University within the Software and Game project course (2018)
//©Copyright Utrecht University (Department of Information and Computing Sciences)
namespace Tests\Feature;

use Tests\TestCase;
use App\User;
use App\Room;
use App\Admin;
use App\Mail\InviteCreated;
use Illuminate\Foundation\Testing\WithFaker;
use Illuminate\Foundation\Testing\RefreshDatabase;
use Faker;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Mail;

class InviteTest extends TestCase
{
   
    use RefreshDatabase;
    
    //tests if an admin can view the teacher invite form:
    public function testTeacherInviteFormCorrect()
    {
        $admin = factory(Admin::class)->create();

        $response = $this->actingAs($admin, 'admin')->json('GET', route('admin.invite'));
        $response->assertStatus(200);
    }
    //tests if a random user cannot access the teacher invite view:
    public function testTeacherInviteForm401()
    {
        //create a random user:
        $user = factory(User::class)->create();

        $response = $this->actingAs($user)->json('GET', route('admin.invite'));
        $response->assertStatus(401);
    }

    //tests if an admin can invite a teacher correctly:
    public function testTeacherInviteCorrect()
    {
        Mail::fake();
        $admin = factory(Admin::class)->create();

        $faker = Faker\Factory::create();
        $data = ['email' => $faker->email()];
        
        $response = $this->actingAs($admin, 'admin')->json('POST', route('admin.process'), $data);
        $response->assertStatus(302);

        $this->assertDatabaseHas('invites', ['email' => $data['email'], 'invitetype' => 'teacher']);
        Mail::assertSent(InviteCreated::class, function ($mail) use ($data) {
            return $mail->hasTo($data['email']);
        });
    }

    //tests if a non-admin cannot invite a teacher:
    public function testTeacherInvite401()
    {
        Mail::fake();
        $user = factory(User::class)->create();

        $faker = Faker\Factory::create();
        $data = ['email' => $faker->email()];
        
        $response = $this->actingAs($user)->json('POST', route('admin.process'), $data);
        $response->assertStatus(401);

        $this->assertDatabaseMissing('invites', ['email' => $data['email'], 'invitetype' => 'teacher']);
        Mail::assertNotSent(InviteCreated::class);
    }

    //tests if a creator teacher can view the user invite form:
    public function testUserInviteFormCorrect()
    {
        //make a teacher
        $teacher = factory(User::class)->create();
        DB::table('teachers')->insert(['user_id' => $teacher->id]);
        $teacherid = DB::table('teachers')->where('user_id', $teacher->id)->first()->id;
        //make a room:
        $room = factory(Room::class)->create();
        //make that teacher the owner:
        DB::table('teachersrooms')->insert(['teacher_id' => $teacherid, 'room_id' => $room->id]);

        $response = $this->actingAs($teacher)->json('GET', route('room.process', ['id' => $room->url]));
        $response->assertStatus(200);
    }
    //tests if a random user cannot access the user invite view:
    public function testUserInviteForm401()
    {
        //create a random user:
        $user = factory(User::class)->create();
        //make a room:
        $room = factory(Room::class)->create();

        $response = $this->actingAs($user)->json('GET', route('room.process', ['id' => $room->url]));
        $response->assertStatus(401);
    }

    //tests if a teacher can invite a user correctly:
    public function testUserInviteCorrect()
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
        
        $response = $this->actingAs($teacher)->json('POST', route('room.process', ['id' => $room->url]), $data);
        $response->assertStatus(302);

        $this->assertDatabaseHas('invites', ['email' => $data['email'],
        'invitetype' => 'room', 'room_id' => $room->id]);
        Mail::assertSent(InviteCreated::class, function ($mail) use ($data) {
            return $mail->hasTo($data['email']);
        });
    }

    //tests if a random user cannot invite a user:
    public function testUserInvite403()
    {
        Mail::fake();
        //make a user:
        $user = factory(User::class)->create();
        //make a room:
        $room = factory(Room::class)->create();

        $faker = Faker\Factory::create();
        $data = ['email' => $faker->email()];
        
        $response = $this->actingAs($user)->json('POST', route('room.process', ['id' => $room->url]), $data);
        $response->assertStatus(403);

        $this->assertDatabaseMissing('invites', ['email' => $data['email'],
        'invitetype' => 'room', 'room_id' => $room->id]);
        Mail::assertNotSent(InviteCreated::class);
    }
}
