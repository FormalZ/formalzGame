<?php
//This program has been developed by students from the bachelor Computer Science 
//at Utrecht University within the Software and Game project course (2018)
//©Copyright Utrecht University (Department of Information and Computing Sciences)
namespace App\Events;

use Illuminate\Broadcasting\Channel;
use Illuminate\Queue\SerializesModels;
use Illuminate\Broadcasting\PrivateChannel;
use Illuminate\Broadcasting\PresenceChannel;
use Illuminate\Foundation\Events\Dispatchable;
use Illuminate\Broadcasting\InteractsWithSockets;
use Illuminate\Contracts\Broadcasting\ShouldBroadcast;

/**
 * Event that is called when a user fails to change their password.
 * This happens when they enter the wrong password in the current password field.
 */
class PasswordChangeFail
{
    use Dispatchable, InteractsWithSockets, SerializesModels;

    //The user that failed to change their password:
    public $user;

    /**
     * Create a new event instance.
     *
     * @param User $user The user that failed to change their password.
     * @return void
     */
    public function __construct($user)
    {
        $this->user = $user;
    }
}
