<?php
//This program has been developed by students from the bachelor Computer Science 
//at Utrecht University within the Software and Game project course (2018)
//©Copyright Utrecht University (Department of Information and Computing Sciences)
return [

    /*
    |--------------------------------------------------------------------------
    | Logging Channels
    |--------------------------------------------------------------------------
    |
    | This structure defines the channels that the application can log to.
    |
    */

    'channels' => [
        'stack' => [
            'driver' => 'stack',
            'channels' => ['syslog'],
        ],
        
        //the system log:
        'syslog' => [
            'driver' => 'syslog',
            'level' => 'debug',
        ],

    ]
];
