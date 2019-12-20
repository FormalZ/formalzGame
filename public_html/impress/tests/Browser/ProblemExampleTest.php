<?php
//This program has been developed by students from the bachelor Computer Science 
//at Utrecht University within the Software and Game project course (2018)
//©Copyright Utrecht University (Department of Information and Computing Sciences)
namespace Tests\Browser;

use App\User;
use App\Invite;
use App\Room;
use App\Problem;
use Tests\DuskTestCase;
use Laravel\Dusk\Browser;
use Illuminate\Foundation\Testing\DatabaseMigrations;
use Faker;
use Illuminate\Support\Facades\DB;

class ProblemExampleTest extends DuskTestCase
{
    use DatabaseMigrations;

    //Test a teacher going to the edit form, then clicking the
    //example button.
    public function testProblemExample()
    {
        //Create a random teacher:
        $user = factory(User::class)->create();
        DB::table('teachers')->insert(['user_id' => $user->id]);
        $teacherid = DB::table('teachers')->where('user_id', $user->id)->first()->id;
        //Create a room:
        $room = factory(Room::class)->create();
        //Make the teacher the room owner:
        DB::table('teachersrooms')->insert(['teacher_id' => $teacherid, 'room_id' => $room->id]);
        //create a problem in the room:
        $problem = factory(Problem::class)->create(['room_id' => $room->id]);
        
        $this->browse(function (Browser $browser) use ($user, $room, $problem) {
            $browser->loginAs($user)
                    ->visit('/room/' . $room->url)
                    ->clickLink($problem->header)
                    ->press('Edit Problem')
                    ->clickLink('Examples for different difficulties')
                    ->assertPathIs('/teacher/examples');
        });
    }
}
