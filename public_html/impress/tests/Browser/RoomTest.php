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
use Illuminate\Support\Facades\DB;

class RoomTest extends DuskTestCase
{
    use DatabaseMigrations;

    //Test the creation, editing and deletion of a room by a teacher.
    public function testRoom()
    {
        //create data for creating and editing the rooms:
        $data = $this->getValidCreateRoomData();
        //Create a random teacher:
        $user = factory(User::class)->create();
        DB::table('teachers')->insert(['user_id' => $user->id]);
        $teacherid = DB::table('teachers')->where('user_id', $user->id)->first()->id;
        
        //Create a room we can edit:
        $room = factory(Room::class)->create();
        //Make the teacher the room owner:
        DB::table('teachersrooms')->insert(['teacher_id' => $teacherid, 'room_id' => $room->id]);

        $this->browse(function (Browser $browser) use ($data, $user, $room) {
            //create a room:
            $browser->loginAs($user)
                    ->visit('/createroom')
                    ->type('name', $data['name'])
                    ->type('description', $data['description'])
                    ->press('Create Room');
            
            //check if we redirect correctly.
            $url = Room::where('name', $data['name'])->firstOrFail()->url;
            $browser->assertPathIs('/room/' . $url);

            //check the database for relevant values.
            $this->assertDatabaseHas('rooms', ['name' => $data['name'], 'description' => $data['description']]);
            $roomid = Room::where('name', $data['name'])->firstOrFail()->id;
            $this->assertDatabaseHas('teachersrooms', ['room_id' => $roomid, 'teacher_id' => $user->id]);

            //delete the room we just created:
            $browser->press('Edit Room')
                    ->press('Delete this room')
                    ->press('Delete room!');
            
            //check if we redirect correctly.
            $browser->assertPathIs('/home');

            //check the database for correct deletion:
            $this->assertDatabaseMissing('rooms', ['id' => $roomid]);
            $this->assertDatabaseMissing('teachersrooms', ['room_id' => $roomid, 'teacher_id' => $user->id]);

            //edit the existing room:
            $browser->clickLink('Rooms that you are a teacher in')
                    ->clickLink($room->name)
                    ->press('Edit Room')
                    ->type('name', $data['name'])
                    ->type('description', $data['description'])
                    ->press('Save Changes');
            
            //check if we redirect correctly.
            $browser->assertPathIs('/room/' . $room->url);

            //check the database for relevant values.
            $this->assertDatabaseHas('rooms', ['name' => $data['name'], 'description' => $data['description']]);
            $this->assertDatabaseHas('teachersrooms', ['room_id' => $room->id, 'teacher_id' => $user->id]);
        });
    }

    //Gets faker data to create a room with
    private function getValidCreateRoomData()
    {
        $faker = Faker\Factory::create();
        return ['name' => $faker->unique()->asciify('********************'),
        'description' => $faker->text('2550')];
    }
}
