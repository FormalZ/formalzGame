<?php

namespace App\Analytics;

use Exception;

class AdminAnalyticsController extends BaseAnalyticsController
{
    private static $instance;
    private $admin_token = null;
    private $lastauth = null;

    static public function GetInstance(){
        if(!isset(self::$instance)){
            self::$instance = new AdminAnalyticsController();
        }
        return self::$instance;
    }

    private function __construct(){
    }

    public function GetAdminToken(){
        if(!isset($this->admin_token) || (time() - $this->lastauth > 1800)){
            $this->AdminLogin();
        }

        return $this->admin_token;
    }

    private function AdminLogin(){
        $result = self::request(
            config('services.analytics.apiBaseUrl') . 'api/login',
            array(
                'Content-Type' => 'application/json',
                'Accept' => 'application/json'
            ),
            array(
                'username' => config('services.analytics.admin_username'),
                'password' => config('services.analytics.admin_password')
            )
        );

        if(isset($result['error'])){
            throw new Exception("Error on admin login: " . json_encode($result['error']), 1);
        }

        $this->admin_token = $result['result']['user']['token'];

        return $result['result']['user']['token'];
    }

    /**
     * Creates a user for the logged with the external id from formalz.
     * IMPORTANT: Teachers can only create students. If you want to create a teacher use admin account.
     *
     * @param  string $id       ExternalID for the user to be created (IDs are UNIQUE).
     * @param  string $username Username of the user to be created (Usernames are UNIQUE).
     * @param  string $role     Role of the user to be created. Role can be student or teacher.
     * @return object           Object of the user created.
     */
    function createUser($id, $username, $role){
        $result = self::request(
            self::getWebhookUrl() . 'events/collector/user_created',
            array(
                'Content-Type' => 'application/json',
                'Accept' => 'application/json',
                'Authorization' => 'Bearer ' . $this->GetAdminToken()
            ),
            array('id' => $id, 'username' => $username, 'role' => $role)
        );

        if(isset($result['error'])){
            throw new Exception("Error creating user: " . json_encode($result['error']), 1);
        }

        return $result['result'];
    }
}