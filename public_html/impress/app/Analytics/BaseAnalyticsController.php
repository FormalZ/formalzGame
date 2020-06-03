<?php

namespace App\Analytics;

abstract class BaseAnalyticsController
{
    
    static public function getWebhookUrl()
    {
        return config('services.analytics.apiBaseUrl') . 'api/proxy/webhook/';
    }

    static public function getKibanaUrl()
    {
        return config('services.analytics.baseUrl') . 'api/proxy/kibana';
    }

    static public function getDashboardUrl($dasboardId)
    {
        return self::getKibanaUrl().'#/dashboard/dashboard_'.$dasboardId;
    }

    static protected function request($url, $headers, $body)
    {
        $resultobject = array();

        $options = array(
            'http' => array(
                'header' => self::formatheaders($headers),
                'method' => 'POST',
                'content' => json_encode($body),
                'ignore_errors' => true
            )
        );
        $context = stream_context_create($options);
        $result = file_get_contents($url, false, $context);
        $code = self::getHttpCode($http_response_header);

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

    static protected function formatheaders($headers)
    {
        $formatted = "";

        foreach ($headers as $key => $content) {
            $formatted .= $key . ': ' . $content . "\r\n";
        }

        return $formatted;
    }

    static protected function getHttpCode($http_response_header)
    {
        if(is_array($http_response_header))
        {
            $parts=explode(' ',$http_response_header[0]);
            if(count($parts)>1) //HTTP/1.0 <code> <text>
                return intval($parts[1]); //Get code
        }
        return 0;
    }
}