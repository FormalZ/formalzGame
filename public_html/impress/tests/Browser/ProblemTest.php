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

class ProblemTest extends DuskTestCase
{
    use DatabaseMigrations;

    //Test the creation, editing and deletion of a problem by a teacher.
    public function testProblem()
    {
        //data for creating/editing the problem:
        $data = $this->getValidCreateProblemData();
        
        //Create a random teacher:
        $user = factory(User::class)->create();
        DB::table('teachers')->insert(['user_id' => $user->id]);
        $teacherid = DB::table('teachers')->where('user_id', $user->id)->first()->id;
        //Create a room:
        $room = factory(Room::class)->create();
        //Make the teacher the room owner:
        DB::table('teachersrooms')->insert(['teacher_id' => $teacherid, 'room_id' => $room->id]);
        
        //create a problem in the room that we can edit:
        $problem = factory(Problem::class)->create(['room_id' => $room->id]);

        $this->browse(function (Browser $browser) use ($data, $user, $room, $problem) {
            //create the problem from $data
            $browser->loginAs($user)
                    ->visit('/room/' . $room->url)
                    ->press('Add new problem')
                    ->type('header', $data['header'])
                    ->type('description', $data['description'])
                    ->type('pre_conditions', $data['pre_conditions'])
                    ->type('post_conditions', $data['post_conditions'])
                    ->type('difficulty', $data['difficulty'])
                    ->type('problemcount', $data['problemcount'])
                ->type('lives', $data['lives'])
                ->type('money', $data['money'])
                ->type('deadline', $data['deadline'])
                    ->press('Create Problem');
                    
            //check the database for relevant values.
            $this->assertDatabaseHas('problems',
                ['header' => $data['header'],
                'description' => $data['description'],
                'pre_conditions' => $data['pre_conditions'],
                'post_conditions' => $data['post_conditions'],
                'difficulty' => $data['difficulty'],
                'room_id' => $room->id]
            );

            //delete the problem we just created:
            $browser->press('Back to Room')
                    ->clickLink($data['header'])
                    ->press('Edit Problem')
                    ->press('Delete this problem')
                    ->press('Delete problem!')
                    ->assertPathIs('/room/' . $room->url);

            //check the database for missing values.
            $this->assertDatabaseMissing('problems', ['header' => $data['header']]);

            //edit the existing problem using $data:
            $browser->clickLink($problem->header)
                    ->press('Edit Problem')
                    ->type('header', $data['header'])
                    ->type('description', $data['description'])
                    ->type('pre_conditions', $data['pre_conditions'])
                    ->type('post_conditions', $data['post_conditions'])
                    ->type('difficulty', $data['difficulty'])
                    ->type('problemcount', $data['problemcount'])
                ->type('lives', $data['lives'])
                ->type('money', $data['money'])
                ->type('deadline', $data['deadline'])
                    ->press('Save Changes')
                    ->assertPathIs('/problem/' . $problem->id);
                    
            //check the database for relevant values.
            $this->assertDatabaseHas('problems',
                ['header' => $data['header'],
                'description' => $data['description'],
                'pre_conditions' => $data['pre_conditions'],
                'post_conditions' => $data['post_conditions'],
                'difficulty' => $data['difficulty'],
                'room_id' => $room->id]
            );
        });
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
            'deadline' => 0
        ];
    }
}
