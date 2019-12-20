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

class ProblemTest extends TestCase
{

    use RefreshDatabase;

    //Tests that a problem can be created when correct data is provided and the user has correct authentication
    public function testCreateProblemCorrect()
    {
        $data = $this->getValidCreateProblemData();
        //Create a random teacher:
        $user = factory(User::class)->create();
        DB::table('teachers')->insert(['user_id' => $user->id]);
        $teacherid = DB::table('teachers')->where('user_id', $user->id)->first()->id;
        //Create a room that the teacher is a teacher in:
        $room = factory(Room::class)->create();
        //make that teacher the owner:
        DB::table('teachersrooms')->insert(['teacher_id' => $teacherid, 'room_id' => $room->id]);

        $response = $this->actingAs($user)->json('POST', route('problem.create', ['id' => $room->url]), $data);
        $response->assertStatus(302);
        $this->assertDatabaseHas(
            'problems',
            ['header' => $data['header'],
                'description' => $data['description'],
                'pre_conditions' => $data['pre_conditions'],
                'post_conditions' => $data['post_conditions'],
                'difficulty' => $data['difficulty'],
                'lives' => $data['lives'],
                'money' => $data['money'],
                'deadline' => 0,
                'room_id' => $room->id]
        );
    }

    //Test that a user without authentication cannot create a problem:
    public function testCreateProblem403()
    {
        $data = $this->getValidCreateProblemData();
        //create a random user that isn't a teacher:
        $user = factory(User::class)->create();
        //create a room:
        $room = factory(Room::class)->create();

        $response = $this->actingAs($user)->json('POST', route('problem.create', ['id' => $room->url]), $data);
        $response->assertStatus(403);
        $this->assertDatabaseMissing(
            'problems',
            ['header' => $data['header'],
                'description' => $data['description'],
                'pre_conditions' => $data['pre_conditions'],
                'post_conditions' => $data['post_conditions'],
                'difficulty' => $data['difficulty'],
                'lives' => $data['lives'],
                'money' => $data['money'],
                'deadline' => strtotime($data['deadline']),
                'room_id' => $room->id]
        );
    }

    //Tests that a problem cannot be created if there are not enough related problems in the database
    public function testCreateProblemNotEnoughRelated()
    {
        $data = $this->getValidCreateProblemData();
        $data['problemcount'] = 10;
        //Create a random teacher:
        $user = factory(User::class)->create();
        DB::table('teachers')->insert(['user_id' => $user->id]);
        $teacherid = DB::table('teachers')->where('user_id', $user->id)->first()->id;
        //Create a room that the teacher is a teacher in:
        $room = factory(Room::class)->create();
        //make that teacher the owner:
        DB::table('teachersrooms')->insert(['teacher_id' => $teacherid, 'room_id' => $room->id]);

        $response = $this->actingAs($user)->json('POST', route('problem.create', ['id' => $room->url]), $data);

        //Check if the problem hasn't been made:
        $this->assertDatabaseMissing(
            'problems',
            ['header' => $data['header'],
                'description' => $data['description'],
                'pre_conditions' => $data['pre_conditions'],
                'post_conditions' => $data['post_conditions'],
                'difficulty' => $data['difficulty'],
                'room_id' => $room->id]
        );
    }

    //Gets faker data to create a problem with
    private function getValidCreateProblemData()
    {
        $faker = Faker\Factory::create();
        return ['header' => $faker->unique()->asciify('********************'),
            'description' => $faker->text('2550'),
            'pre_conditions' => 'true && true || true',
            'post_conditions' => 'true || true && true',
            'difficulty' => 3,
            'problemcount' => 0,
            'lives' => 100,
            'money' => 5000,
            'deadline' => '01-01-2019',
            'displayDate' => '01-01-2019'
        ];
    }

    //Test that the rooms teacher can view the problem creation form.
    public function testProblemCreationFormCorrect()
    {
        //Create a random teacher:
        $user = factory(User::class)->create();
        DB::table('teachers')->insert(['user_id' => $user->id]);
        $teacherid = DB::table('teachers')->where('user_id', $user->id)->first()->id;
        //Create a room that the teacher is a teacher in:
        $room = factory(Room::class)->create();
        //make that teacher the owner:
        DB::table('teachersrooms')->insert(['teacher_id' => $teacherid, 'room_id' => $room->id]);

        $response = $this->actingAs($user)->json('GET', '/room/' . $room->url . '/createproblem');

        $response->assertStatus(200);
    }

    //Test that a user that isn't the teacher cannot view the room creation form.
    public function testProblemCreationForm401()
    {
        //create a random user that isn't a teacher:
        $user = factory(User::class)->create();
        //create a room:
        $room = factory(Room::class)->create();

        $response = $this->actingAs($user)->json('GET', '/room/' . $room->url . '/createproblem');
        $response->assertStatus(401);
    }

    //Test that a member of a room can view the problem page.
    public function testProblemShowUser()
    {
        $room = factory(Room::class)->create();
        //create a random user that is in the room:
        $user = factory(User::class)->create();
        DB::table('usersrooms')->insert(['user_id' => $user->id, 'room_id' => $room->id]);
        //create a problem in the room:
        $problem = factory(Problem::class)->create(['room_id' => $room->id]);

        $response = $this->actingAs($user)->json('GET', '/problem/' . $problem->id);
        $response->assertStatus(200);
    }

    //Test that the creator of a room can view the problem page.
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
        //create a problem in the room:
        $problem = factory(Problem::class)->create(['room_id' => $room->id]);

        $response = $this->actingAs($user)->json('GET', '/problem/' . $problem->id);
        $response->assertStatus(200);
    }

    //Test that an unauthorised user cannot view the problem page.
    public function testProblemShow401()
    {
        $room = factory(Room::class)->create();
        //create a random user that isn't the creator or in the room:
        $user = factory(User::class)->create();
        //create a problem in the room:
        $problem = factory(Problem::class)->create(['room_id' => $room->id]);
        $response = $this->actingAs($user)->json('GET', '/problem/' . $problem->id);
        $response->assertStatus(401);
    }

    //test that a creator of a room can view the problem edit page.
    public function testProblemEditFormCorrect()
    {
        $room = factory(Room::class)->create();
        //create a user that is the creator of the room:
        //Create a random teacher:
        $user = factory(User::class)->create();
        DB::table('teachers')->insert(['user_id' => $user->id]);
        $teacherid = DB::table('teachers')->where('user_id', $user->id)->first()->id;
        //make that teacher the owner:
        DB::table('teachersrooms')->insert(['teacher_id' => $teacherid, 'room_id' => $room->id]);
        //create a problem in the room:
        $problem = factory(Problem::class)->create(['room_id' => $room->id]);

        $response = $this->actingAs($user)->json('GET', '/problem/' . $problem->id . '/edit');
        $response->assertStatus(200);
    }

    //test that an unauthorised user cannot view the problem edit page.
    public function testProblemEditForm401()
    {
        $room = factory(Room::class)->create();
        //create a random user that isn't the creator or in the room:
        $user = factory(User::class)->create();
        //create a problem in the room:
        $problem = factory(Problem::class)->create(['room_id' => $room->id]);

        $response = $this->actingAs($user)->json('GET', '/problem/' . $problem->id . '/edit');
        $response->assertStatus(401);
    }

    //Test a problem cannot be edited if there are not enough related problems
    public function testProblemEditNotEnoughRelated()
    {
        $data = $this->getValidCreateProblemData();
        $data['problemcount'] = 1;
        $room = factory(Room::class)->create();
        //create a user that is the creator of the room:
        //Create a random teacher:
        $user = factory(User::class)->create();
        DB::table('teachers')->insert(['user_id' => $user->id]);
        $teacherid = DB::table('teachers')->where('user_id', $user->id)->first()->id;
        //make that teacher the owner:
        DB::table('teachersrooms')->insert(['teacher_id' => $teacherid, 'room_id' => $room->id]);
        //create a problem in the room:
        $problem = factory(Problem::class)->create(['room_id' => $room->id]);

        $response = $this->actingAs($user)->json('POST', '/problem/' . $problem->id . '/edit', $data);

        //check if the problem has not been edited:
        $this->assertDatabaseMissing(
            'problems',
            ['id' => $problem->id,
                'header' => $data['header'],
                'description' => $data['description'],
                'pre_conditions' => $data['pre_conditions'],
                'post_conditions' => $data['post_conditions'],
                'difficulty' => $data['difficulty'],
                'room_id' => $room->id]
        );

        $this->assertDatabaseHas(
            'problems',
            ['id' => $problem->id,
                'header' => $problem->header,
                'description' => $problem->description,
                'pre_conditions' => $problem->pre_conditions,
                'post_conditions' => $problem->post_conditions,
                'difficulty' => $problem->difficulty,
                'room_id' => $room->id]
        );
    }

    //Test that an unauthorised user cannot edit a room.
    public function testProblemEdit403()
    {
        $data = $this->getValidCreateProblemData();
        $room = factory(Room::class)->create();
        //create a user that is NOT the creator of the room:
        $user = factory(User::class)->create();
        //create a problem in the room:
        $problem = factory(Problem::class)->create(['room_id' => $room->id]);

        $response = $this->actingAs($user)->json('POST', '/problem/' . $problem->id . '/edit', $data);

        $response->assertStatus(403);
        $this->assertDatabaseMissing(
            'problems',
            ['id' => $problem->id,
                'header' => $data['header'],
                'description' => $data['description'],
                'pre_conditions' => $data['pre_conditions'],
                'post_conditions' => $data['post_conditions'],
                'difficulty' => $data['difficulty'],
                'room_id' => $room->id]
        );
    }

    //Test that the creator of a room can properly edit a problem.
    public function testProblemEditCorrect()
    {
        $data = $this->getValidCreateProblemData();
        $room = factory(Room::class)->create();
        //create a user that is the creator of the room:
        //Create a random teacher:
        $user = factory(User::class)->create();
        DB::table('teachers')->insert(['user_id' => $user->id]);
        $teacherid = DB::table('teachers')->where('user_id', $user->id)->first()->id;
        //make that teacher the owner:
        DB::table('teachersrooms')->insert(['teacher_id' => $teacherid, 'room_id' => $room->id]);
        //create a problem in the room:
        $problem = factory(Problem::class)->create(['room_id' => $room->id]);

        $response = $this->actingAs($user)->json('POST', '/problem/' . $problem->id . '/edit', $data);

        $response->assertStatus(302);
        //check if the problem has been edited:
        $this->assertDatabaseHas(
            'problems',
            ['id' => $problem->id,
                'header' => $data['header'],
                'description' => $data['description'],
                'pre_conditions' => $data['pre_conditions'],
                'post_conditions' => $data['post_conditions'],
                'difficulty' => $data['difficulty'],
                'lives' => $data['lives'],
                'money' => $data['money'],
                'deadline' => 0,
                'room_id' => $room->id]
        );
    }

    //test that a creator of a room can view the problem delete page.
    public function testProblemDeleteFormCorrect()
    {
        $room = factory(Room::class)->create();
        //create a user that is the creator of the room:
        //Create a random teacher:
        $user = factory(User::class)->create();
        DB::table('teachers')->insert(['user_id' => $user->id]);
        $teacherid = DB::table('teachers')->where('user_id', $user->id)->first()->id;
        //make that teacher the owner:
        DB::table('teachersrooms')->insert(['teacher_id' => $teacherid, 'room_id' => $room->id]);
        //create a problem in the room:
        $problem = factory(Problem::class)->create(['room_id' => $room->id]);

        $response = $this->actingAs($user)->json('GET', '/problem/' . $problem->id . '/delete');
        $response->assertStatus(200);
    }

    //test that an unauthorised user cannot view the problem delete page.
    public function testProblemDeleteForm401()
    {
        $room = factory(Room::class)->create();
        //create a random user that isn't the creator or in the room:
        $user = factory(User::class)->create();
        //create a problem in the room:
        $problem = factory(Problem::class)->create(['room_id' => $room->id]);

        $response = $this->actingAs($user)->json('GET', '/problem/' . $problem->id . '/delete');
        $response->assertStatus(401);
    }

    //Test that the creator of a room can properly delete the problem.
    public function testProblemDeleteCorrect()
    {
        $room = factory(Room::class)->create();
        //create a user that is the creator of the room:
        //Create a random teacher:
        $teacher = factory(User::class)->create();
        DB::table('teachers')->insert(['user_id' => $teacher->id]);
        $teacherid = DB::table('teachers')->where('user_id', $teacher->id)->first()->id;
        //make that teacher the owner:
        DB::table('teachersrooms')->insert(['teacher_id' => $teacherid, 'room_id' => $room->id]);

        //create a problem in the room:
        $problem = factory(Problem::class)->create(['room_id' => $room->id]);

        $response = $this->actingAs($teacher)->json('POST', '/problem/' . $problem->id . '/delete');

        $response->assertStatus(302);
        $this->assertDatabaseMissing('problems', ['id' => $problem->id]);
        //make sure the redirect redirects to the room.
        $response->assertHeader('Location', route('room.show', ['id' => $room->url]));
    }

    //Test that an unauthorised user cannot delete a problem.
    public function testProblemDelete403()
    {
        $room = factory(Room::class)->create();
        //create a user that is NOT the creator of the room:
        $user = factory(User::class)->create();
        //create a problem in the room:
        $problem = factory(Problem::class)->create(['room_id' => $room->id]);

        $response = $this->actingAs($user)->json('POST', '/problem/' . $problem->id . '/delete');

        $response->assertStatus(403);
        //check if the problem isn't deleted:
        $this->assertDatabaseHas('problems', ['id' => $problem->id]);
    }

    //Test that a user in a classroom can play a problem:
    public function testProblemPlayCorrect()
    {
        $room = factory(Room::class)->create();
        //create a random user that is in the room:
        $user = factory(User::class)->create();
        DB::table('usersrooms')->insert(['user_id' => $user->id, 'room_id' => $room->id]);
        //create a problem in the room:
        $problem = factory(Problem::class)->create(['room_id' => $room->id]);

        $response = $this->actingAs($user)->json('GET', '/problem/' . $problem->id . '/play');

        //check the database for the gamesession:
        $this->assertDatabaseHas('gamesessions', ['user_id' => $user->id, 'problem_id' => $problem->id]);
        $response->assertStatus(200);
    }


    //Test that an unauthorised user cannot play a problem:
    public function testProblemPlay401()
    {
        $room = factory(Room::class)->create();
        //create a random user that is in the room:
        $user = factory(User::class)->create();
        //create a problem in the room:
        $problem = factory(Problem::class)->create(['room_id' => $room->id]);

        $response = $this->actingAs($user)->json('GET', '/problem/' . $problem->id . '/play');

        //check the database for the gamesession:
        $this->assertDatabaseMissing('gamesessions', ['user_id' => $user->id, 'problem_id' => $problem->id]);
        $response->assertStatus(401);
    }
}
