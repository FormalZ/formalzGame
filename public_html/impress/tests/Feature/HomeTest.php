<?php
//This program has been developed by students from the bachelor Computer Science 
//at Utrecht University within the Software and Game project course (2018)
//©Copyright Utrecht University (Department of Information and Computing Sciences)
namespace Tests\Feature;

use Tests\TestCase;
use App\User;
use App\Room;
use App\Invite;
use App\Admin;
use Illuminate\Foundation\Testing\WithFaker;
use Illuminate\Foundation\Testing\RefreshDatabase;
use Faker;
use Illuminate\Support\Facades\DB;

class HomeTest extends TestCase
{

    use RefreshDatabase;

    //Test if a user can view the dashboard:
    public function testHomeShowUser()
    {
        // generate a user:
        $user = factory(User::class)->create();
        
        //open the dashboard:
        $response = $this->actingAs($user)->json('GET', route('home'));
        $response->assertStatus(200);
    }

    //Test if a guest cannot view the dashboard:
    public function testHomeShow401()
    {
        $response = $this->json('GET', route('admin.dashboard'));
        $response->assertStatus(401);
    }
}
