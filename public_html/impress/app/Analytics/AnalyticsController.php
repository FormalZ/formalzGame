<?php
namespace App\Analytics;

use Exception;

const BASE_URL = 'https://analytics.e-ucm.es/';
const WEBHOOK_URL = BASE_URL . 'api/proxy/webhook/';
const KIBANA_URL = BASE_URL . 'api/proxy/kibana/';
const ADMIN_USERNAME = 'formalz-admin-test';
const ADMIN_PASSWORD = 'admintest123456';


class AnalyticsController {
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
        $result = request(
            BASE_URL . 'api/login/formalz',
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
        $result = request(
            WEBHOOK_URL . 'events/collector/user_created',
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

        $result = request(
            WEBHOOK_URL . 'events/collector/room_created',
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

        $result = request(
            WEBHOOK_URL . 'events/collector/room_removed',
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

        $result = request(
            WEBHOOK_URL . 'events/collector/room_participants_added',
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

        $result = request(
            WEBHOOK_URL . 'events/collector/room_participants_removed',
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

        $result = request(
            WEBHOOK_URL . 'events/collector/puzzle_created',
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

// #####################################################################
// ################## UTIL FUNCTIONS FOR THE REQUESTS ##################
// #####################################################################

function request($url, $headers, $body){
    $resultobject = array();

    $options = array(
        'http' => array(
            'header' => formatheaders($headers),
            'method' => 'POST',
            'content' => json_encode($body),
            'ignore_errors' => true
        )
    );
    $context = stream_context_create($options);
    $result = file_get_contents($url, false, $context);
    $code = getHttpCode($http_response_header);

    $resultobject['code'] = $code;
    $resultobject['context'] = $options;
    $resultobject['context']['url'] = $url;

    if ($result === FALSE) {
        $resultobject['error'] = true;
    }else{
        $decoded = json_decode($result, true);

        if($decoded){
            if($code !== 200){
                $resultobject['error'] = $decoded;
            }else{
                $resultobject['result'] = $decoded;
            }
        }else{
            $resultobject['error'] = true;
        }
    }

    return $resultobject;
}

function formatheaders($headers){
    $formatted = "";

    foreach ($headers as $key => $content) {
        $formatted .= $key . ': ' . $content . "\r\n";
    }

    return $formatted;
}

function getHttpCode($http_response_header)
{
    if(is_array($http_response_header))
    {
        $parts=explode(' ',$http_response_header[0]);
        if(count($parts)>1) //HTTP/1.0 <code> <text>
            return intval($parts[1]); //Get code
    }
    return 0;
}