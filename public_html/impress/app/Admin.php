<?php
//This program has been developed by students from the bachelor Computer Science 
//at Utrecht University within the Software and Game project course (2018)
//©Copyright Utrecht University (Department of Information and Computing Sciences)
namespace App;

use Illuminate\Notifications\Notifiable;
use Illuminate\Foundation\Auth\User as Authenticatable;

/**
 * An admin on the server, who is able to invite teachers.
 *
 */
class Admin extends Authenticatable
{
    use Notifiable;
    
    //The protected admin attribute.
    protected $guard = 'admin';
    /**
     * The attributes for an admin. Name, email and job title are fillable.
     * The password and remembertoken are hidden.
     */
    protected $fillable = ['name','email','password', 'job_title',];
    protected $hidden = ['password', 'remember_token',];
}
