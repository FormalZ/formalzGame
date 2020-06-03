<?php
//This program has been developed by students from the bachelor Computer Science 
//at Utrecht University within the Software and Game project course (2018)
//©Copyright Utrecht University (Department of Information and Computing Sciences)
namespace App;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Support\Facades\DB;

/**
 * A classroom on the server, in which teachers can input problems and students can play problems.
 *
 */
class Room extends Model
{
    /**
     * The fillable attributes of room.
     * The name of the room, a description and the 'url' which is the string used to
     * navigate to the room through an url. linkinvite determines if the room has an invitation
     * link or invitation by email.
     */
    protected $fillable = ['name', 'description', 'url', 'linkinvite'];


    /**
     * Checks if an user is the creator of a room.
     *
     * @param int $userid The id of the user that should be checked.
     *
     * @return True if the user is the creator, false otherwise.
     */
    public function isCreator($userid)
    {
        $creatorids = DB::table('teachersrooms')->where('room_id', $this->id)->pluck('teacher_id');
        foreach ($creatorids as $id) {
            $creatorid = DB::table('teachers')->where('id', $id)->value('user_id');
            if ($userid == $creatorid) {
                return true;
            }
        }
        return false;
    }

    public function getTeachersIds()
    {
        return DB::table('teachersrooms')->where('room_id', $this->id)->pluck('teacher_id');
    }
}
