<?php
namespace App\Analytics;

use Exception;

class AnalyticsController extends BaseAnalyticsController
{

    private $userid = null;
    private $auth_token = null;
    private $lastauth = null;

    public function __construct($userid){
        $this->userid = $userid;
    }

    /**
     * Returns the AuthToken for this logged teacher
     */
    function GetAuthToken(){
        if(!isset($this->auth_token) || (time() - $this->lastauth > 1800)){
            $this->Login();
        }

        return $this->auth_token;
    }

    /**
     * Performs the login and saves the auth token into the internal auth_token variable.
     */
    function Login(){
        $result = self::request(
            config('services.analytics.apiBaseUrl') . 'api/login/formalz',
            array(
                'Content-Type' => 'application/json',
                'Accept' => 'application/json',
                'Authorization' => 'Bearer ' . AdminAnalyticsController::GetInstance()->GetAdminToken()
            ),
            array('id' => $this->userid)
        );

        if(isset($result['error'])){
            throw new Exception("Error on user login: " . json_encode($result['error']), 1);
        }

        $this->auth_token = $result['result']['user']['token'];

        return $this->auth_token;
    }

    /**
     * Creates a student for the logged teacher.
     * IMPORTANT: Teachers can only create students. If you want to create a teacher use admin account.
     *
     * @param  string $id       ExternalID for the user to be created (IDs are UNIQUE).
     * @param  string $username Username of the user to be created (Usernames are UNIQUE).
     * @return object           Object of the user created.
     */
    function createStudent($id, $username){
        $result = self::request(
            self::getWebhookUrl() . 'events/collector/user_created',
            array(
                'Content-Type' => 'application/json',
                'Accept' => 'application/json',
                'Authorization' => 'Bearer ' . $this->GetAuthToken()
            ),
            array('id' => $id, 'username' => $username, 'role' => 'student')
        );

        if(isset($result['error'])){
            throw new Exception("Error creating the student: " . json_encode($result['error']), 1);
        }

        return $result['result'];
    }

    /**
     * Creates a room with the external ID, name, and optional students including the logged teacher
     * as participant
     *
     * @param  string $id       ExternalID of the room to be created
     * @param  string $name     Descriptive name of the room
     * @param  array  $students List of students to be added to the room
     * @return array            Request response, usually success message.
     */
    function createRoom($id, $name, $students = null){

        $body = array('id' => $id, 'name' => $name);
        if(isset($students) && is_array($students)){
            $body['students'] = $students;
        }

        $result = self::request(
            self::getWebhookUrl() . 'events/collector/room_created',
            array(
                'Content-Type' => 'application/json',
                'Accept' => 'application/json',
                'Authorization' => 'Bearer ' . $this->GetAuthToken()
            ),
            $body
        );

        if(isset($result['error'])){
            throw new Exception("Error creating the room: " . json_encode($result['error']), 1);
        }

        return $result['result'];
    }

    /**
     * Removes a room using the given external ID.
     *
     * @param  string $id External ID of the room to remove.
     * @return array      Request response, usually success message.
     */
    function removeRoom($id){

        $result = self::request(
            self::getWebhookUrl() . 'events/collector/room_removed',
            array(
                'Content-Type' => 'application/json',
                'Accept' => 'application/json',
                'Authorization' => 'Bearer ' . $this->GetAuthToken()
            ),
            array('id' => $id)
        );

        if(isset($result['error'])){
            throw new Exception("Error creating the room: " . json_encode($result['error']), 1);
        }

        return $result['result'];
    }

    /**
     * Adds participants (either teachers or students) to a given room
     * @param string $roomId       External ID of the room where the participants are going to be added
     * @param array  $participants It can contain two arrays: {students: [], teachers: []} both optional.
     * @return array               Request response, usually success message.
     */
    function addParticipants($roomId, $participants){

        $result = self::request(
            self::getWebhookUrl() . 'events/collector/room_participants_added',
            array(
                'Content-Type' => 'application/json',
                'Accept' => 'application/json',
                'Authorization' => 'Bearer ' . $this->GetAuthToken()
            ),
            array('id' => $roomId, 'participants' => $participants)
        );

        if(isset($result['error'])){
            throw new Exception("Error adding participants to the room: " . json_encode($result['error']), 1);
        }

        return $result['result'];
    }

    /**
     * Removes participants (either teachers or students) from a given room
     * @param string $roomId       External ID of the room where the participants are going to be added
     * @param array  $participants It can contain two arrays: {students: [], teachers: []} both optional.
     * @return array               Request response, usually success message.
     */
    function removeParticipants($roomId, $participants){

        $result = self::request(
            self::getWebhookUrl() . 'events/collector/room_participants_removed',
            array(
                'Content-Type' => 'application/json',
                'Accept' => 'application/json',
                'Authorization' => 'Bearer ' . $this->GetAuthToken()
            ),
            array('id' => $roomId, 'participants' => $participants)
        );

        if(isset($result['error'])){
            throw new Exception("Error removing participants from the room: " . json_encode($result['error']), 1);
        }

        return $result['result'];
    }

    /**
     * Creates a puzzle activity into the analytics server and prepares it for traces to be sent.
     * @param  string $roomId The external ID of the room where the puzzle is created
     * @return array          Includes 'activity' being the ID for dashboards and 'trackingCode' for the
     *                        tracker to send traces to. It is recommended to persist both of them.
     */
    function createPuzzle($roomId){

        $result = self::request(
            self::getWebhookUrl() . 'events/collector/puzzle_created',
            array(
                'Content-Type' => 'application/json',
                'Accept' => 'application/json',
                'Authorization' => 'Bearer ' . $this->GetAuthToken()
            ),
            array('room' => $roomId)
        );

        if(isset($result['error'])){
            throw new Exception("Error adding problem to the room: " . json_encode($result['error']), 1);
        }

        return $result['result'];
    }
}