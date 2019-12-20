<?php
//This program has been developed by students from the bachelor Computer Science 
//at Utrecht University within the Software and Game project course (2018)
//©Copyright Utrecht University (Department of Information and Computing Sciences)
namespace Tests\Feature;

use Tests\TestCase;
use App\User;
use App\Room;
use Illuminate\Foundation\Testing\WithFaker;
use Illuminate\Foundation\Testing\RefreshDatabase;
use Faker;
use Illuminate\Support\Facades\DB;

class RoomTest extends TestCase
{

    use RefreshDatabase;
    
    //Tests that a room can be created when correct data is provided and the user has correct authentication
    public function testCreateRoomCorrect()
    {
        $data = $this->getValidCreateRoomData();
        //Create a random teacher:
        $user = factory(User::class)->create();
        DB::table('teachers')->insert(['user_id' => $user->id]);
        $teacherid = DB::table('teachers')->where('user_id', $user->id)->first()->id;
        $response = $this->actingAs($user)->json('POST', '/createroom', $data);
    
        $response->assertStatus(302);
        $this->assertDatabaseHas('rooms', ['name' => $data['name'], 'description' => $data['description']]);
        $room = Room::where('name', $data['name'])->firstOrFail();
        //make sure the 302 redirects to the room page
        $response->assertHeader('Location', route('room.show', ['id' => $room->url]));
        $this->assertDatabaseHas('teachersrooms', ['room_id' => $room->id, 'teacher_id' => $teacherid]);
    }
    
    //Test that a user without authentication cannot create a room
    public function testCreateRoom403()
    {
        $data = $this->getValidCreateRoomData();
        //create a random user that isn't a teacher:
        $user = factory(User::class)->create();

        $response = $this->actingAs($user)->json('POST', '/createroom', $data);
        $response->assertStatus(403);
        $this->assertDatabaseMissing('rooms', ['name' => $data['name'], 'description' => $data['description']]);
    }
    
    //Gets faker data to create a room with
    private function getValidCreateRoomData()
    {
        $faker = Faker\Factory::create();
        return ['name' => $faker->unique()->asciify('********************'),
        'description' => $faker->text('2550')];
    }

    //Test that a teacher can view the room creation form.
    public function testRoomCreationFormCorrect()
    {
        //Create a random teacher:
        $user = factory(User::class)->create();
        DB::table('teachers')->insert(['user_id' => $user->id]);
        $teacherid = DB::table('teachers')->where('user_id', $user->id)->first()->id;
        $response = $this->actingAs($user)->json('GET', '/createroom');
    
        $response->assertStatus(200);
    }

    //Test that a user that isn't a teacher cannot view the room creation form.
    public function testRoomCreationForm401()
    {
        //create a random user that isn't a teacher:
        $user = factory(User::class)->create();

        $response = $this->actingAs($user)->json('GET', '/createroom');
        $response->assertStatus(401);
    }
 
    //Test that a member of a room can view the room overview page.
    public function testRoomShowUser()
    {
        $room = factory(Room::class)->create();
        //create a random user that is in the room:
        $user = factory(User::class)->create();
        DB::table('usersrooms')->insert(['user_id' => $user->id, 'room_id' => $room->id]);

        $response = $this->actingAs($user)->json('GET', '/room/' . $room->url);
        $response->assertStatus(200);
    }

    //Test that the creator of a room can view the room overview page.
    public function testRoomShowCreator()
    {
        $room = factory(Room::class)->create();
        //create a user that is the creator of the room:
        //Create a random teacher:
        $user = factory(User::class)->create();
        DB::table('teachers')->insert(['user_id' => $user->id]);
        $teacherid = DB::table('teachers')->where('user_id', $user->id)->first()->id;
        //make that teacher the owner:
        DB::table('teachersrooms')->insert(['teacher_id' => $teacherid, 'room_id' => $room->id]);

        $response = $this->actingAs($user)->json('GET', '/room/' . $room->url);
        $response->assertStatus(200);
    }

    //Test that an unauthorised user cannot view the room overview page.
    public function testRoomShow401()
    {
        $room = factory(Room::class)->create();
        //create a random user that isn't the creator or in the room:
        $user = factory(User::class)->create();

        $response = $this->actingAs($user)->json('GET', '/room/' . $room->url);
        $response->assertStatus(401);
    }

    //test that a creator of a room can view the room edit page.
    public function testRoomEditFormCorrect()
    {
        $room = factory(Room::class)->create();
        //create a user that is the creator of the room:
        //Create a random teacher:
        $user = factory(User::class)->create();
        DB::table('teachers')->insert(['user_id' => $user->id]);
        $teacherid = DB::table('teachers')->where('user_id', $user->id)->first()->id;
        //make that teacher the owner:
        DB::table('teachersrooms')->insert(['teacher_id' => $teacherid, 'room_id' => $room->id]);

        $response = $this->actingAs($user)->json('GET', '/room/' . $room->url . '/edit');
        $response->assertStatus(200);
    }

    //test that an unauthorised user cannot view the room edit page.
    public function testRoomEditForm401()
    {
        $room = factory(Room::class)->create();
        //create a random user that isn't the creator or in the room:
        $user = factory(User::class)->create();
        $response = $this->actingAs($user)->json('GET', '/room/' . $room->url . '/edit');
        $response->assertStatus(401);
    }

    //Test that the creator of a room can properly edit the room.
    public function testRoomEditCorrect()
    {
        $data = $this->getValidCreateRoomData();
        $room = factory(Room::class)->create();
        //create a user that is the creator of the room:
        //Create a random teacher:
        $user = factory(User::class)->create();
        DB::table('teachers')->insert(['user_id' => $user->id]);
        $teacherid = DB::table('teachers')->where('user_id', $user->id)->first()->id;
        //make that teacher the owner:
        DB::table('teachersrooms')->insert(['teacher_id' => $teacherid, 'room_id' => $room->id]);
        $response = $this->actingAs($user)->json('POST', '/room/' . $room->url . '/edit', $data);
    
        $response->assertStatus(302);
        $this->assertDatabaseHas('rooms', ['name' => $data['name'], 'description' => $data['description']]);
        $newroom = Room::where('name', $data['name'])->firstOrFail();
        //make sure the 302 redirects to the room page
        $response->assertHeader('Location', route('room.show', ['id' => $newroom->url]));
        $this->assertDatabaseHas('teachersrooms', ['room_id' => $newroom->id, 'teacher_id' => $teacherid]);
    }

    //Test that an unauthorised user cannot edit a room.
    public function testRoomEdit403()
    {
        $data = $this->getValidCreateRoomData();
        $room = factory(Room::class)->create();
        //create a user that is NOT the creator of the room:
        $user = factory(User::class)->create();
        $response = $this->actingAs($user)->json('POST', '/room/' . $room->url . '/edit', $data);
    
        $response->assertStatus(403);
        $this->assertDatabaseMissing('rooms', ['name' => $data['name'], 'description' => $data['description']]);
    }

    //test that a creator of a room can view the room delete page.
    public function testRoomDeleteFormCorrect()
    {
        $room = factory(Room::class)->create();
        //create a user that is the creator of the room:
        //Create a random teacher:
        $user = factory(User::class)->create();
        DB::table('teachers')->insert(['user_id' => $user->id]);
        $teacherid = DB::table('teachers')->where('user_id', $user->id)->first()->id;
        //make that teacher the owner:
        DB::table('teachersrooms')->insert(['teacher_id' => $teacherid, 'room_id' => $room->id]);

        $response = $this->actingAs($user)->json('GET', '/room/' . $room->url . '/delete');
        $response->assertStatus(200);
    }

    //test that an unauthorised user cannot view the room delete page.
    public function testRoomDeleteForm401()
    {
        $room = factory(Room::class)->create();
        //create a random user that isn't the creator or in the room:
        $user = factory(User::class)->create();
        $response = $this->actingAs($user)->json('GET', '/room/' . $room->url . '/delete');
        $response->assertStatus(401);
    }

    //Test that the creator of a room can properly delete the room,
    //and also delete associations with teachers and users.
    public function testRoomDeleteCorrect()
    {
        $room = factory(Room::class)->create();
        //create a user that is the creator of the room:
        //Create a random teacher:
        $teacher = factory(User::class)->create();
        DB::table('teachers')->insert(['user_id' => $teacher->id]);
        $teacherid = DB::table('teachers')->where('user_id', $teacher->id)->first()->id;
        //make that teacher the owner:
        DB::table('teachersrooms')->insert(['teacher_id' => $teacherid, 'room_id' => $room->id]);

        //create a user that is not a teacher:
        $user = factory(User::class)->create();
        //Add him to the room:
        DB::table('usersrooms')->insert(['user_id' => $user->id, 'room_id' => $room->id]);


        $response = $this->actingAs($teacher)->json('POST', '/room/' . $room->url . '/delete');
    
        $response->assertStatus(302);
        $this->assertDatabaseMissing('rooms', ['name' => $room->name]);
        $response->assertHeader('Location', route('home')); //make sure the 302 redirects to the homepage
        //make sure associations are destroyed:
        $this->assertDatabaseMissing('teachersrooms', ['room_id' => $room->id, 'teacher_id' => $teacherid]);
        $this->assertDatabaseMissing('usersrooms', ['room_id' => $room->id, 'user_id' => $user->id]);
    }

    //Test that an unauthorised user cannot delete a room.
    public function testRoomDelete403()
    {
        $room = factory(Room::class)->create();
        //create a user that is NOT the creator of the room:
        $user = factory(User::class)->create();
        $response = $this->actingAs($user)->json('POST', '/room/' . $room->url . '/delete');
    
        $response->assertStatus(403);
        //check if the room isn't deleted:
        $this->assertDatabaseHas('rooms', ['name' => $room->name]);
    }
}
