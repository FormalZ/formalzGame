<?php
//This program has been developed by students from the bachelor Computer Science 
//at Utrecht University within the Software and Game project course (2018)
//©Copyright Utrecht University (Department of Information and Computing Sciences)
namespace App;

use Illuminate\Notifications\Notifiable;
use Illuminate\Foundation\Auth\User as Authenticatable;

/**
 * Represents a user in our system, this can be both a teacher
 * and a student.
 *
 */
class User extends Authenticatable
{
    use Notifiable;

    /**
     * The attributes: Password and remember token are hidden,
     * the other attributes are fillable.
     */
    protected $fillable = [
        'name', 'email', 'password',
    ];

    protected $hidden = [
        'password', 'remember_token',
    ];
}
