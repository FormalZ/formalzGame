<?php
//This program has been developed by students from the bachelor Computer Science 
//at Utrecht University within the Software and Game project course (2018)
//©Copyright Utrecht University (Department of Information and Computing Sciences)
namespace App;

use Illuminate\Database\Eloquent\Model;

/**
  * Represents a gamesession within the phaser game.
  *
  */
class GameSession extends Model
{
    /**
      * The attributes: The user id is the id of the playing user,
      * the problem id is the id of the problem that is played and
      * the token is an unique attribute to couple the session.
      */
    protected $fillable = ['user_id', 'problem_id', 'token'];
    protected $table = 'gamesessions';
}
