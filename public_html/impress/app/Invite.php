<?php
//This program has been developed by students from the bachelor Computer Science 
//at Utrecht University within the Software and Game project course (2018)
//©Copyright Utrecht University (Department of Information and Computing Sciences)
namespace App;

use Illuminate\Database\Eloquent\Model;

/**
 * Represents an invite for either a student or a teacher.
 *
 */
class Invite extends Model
{
    /**
     * The attributes: Email is the email that the invite is sent to,
     * token is an unique token to identify this invite, the
     * invitetype specifies if the invite is for a teacher or student
     * and room_id shows which room the invite is for if it is a student invite.
     */
    protected $fillable = ['email', 'token','invitetype', 'room_id'];
}
