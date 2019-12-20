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

class PathTest extends TestCase
{

    use RefreshDatabase;
    
    //test if we can generate a path for a problem:
    public function testCreatePathCorrect()
    {
        //create a room with a problem in it:
        $room = factory(Room::class)->create();
        $problem = factory(Problem::class)->create(['room_id' => $room->id]);

        //call the pathgeneration:
        $path = Path::generatePath($problem->id);
        
        //check if the path exists and is coupled to the problem:
        $this->assertDatabaseHas('paths', ['problem_id' => $problem->id, 'path' => $path->path]);
    }
}
