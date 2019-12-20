<?php
//This program has been developed by students from the bachelor Computer Science 
//at Utrecht University within the Software and Game project course (2018)
//©Copyright Utrecht University (Department of Information and Computing Sciences)
namespace Tests\Feature;

use Tests\TestCase;
use App\User;
use App\Room;
use App\Problem;
use Illuminate\Foundation\Testing\WithFaker;
use Illuminate\Foundation\Testing\RefreshDatabase;
use Faker;
use Illuminate\Support\Facades\DB;

class ProblemExampleTest extends TestCase
{

    use RefreshDatabase;
    
    //Test that a teacher can view the problem-example page.
    public function testExamplesShowTeacher()
    {
        //Create a random teacher:
        $user = factory(User::class)->create();
        DB::table('teachers')->insert(['user_id' => $user->id]);
        $teacherid = DB::table('teachers')->where('user_id', $user->id)->first()->id;

        $response = $this->actingAs($user)->json('GET', '/teacher/examples');
        $response->assertStatus(200);
    }

    //Test that an unauthorised user cannot view the examples page.
    public function testProblemShow401()
    {
        //create a random user that isn't a teacher:
        $user = factory(User::class)->create();

        $response = $this->actingAs($user)->json('GET', '/teacher/examples');
        $response->assertStatus(401);
    }
}
