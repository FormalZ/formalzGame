<?php
//This program has been developed by students from the bachelor Computer Science
//at Utrecht University within the Software and Game project course (2018)
//©Copyright Utrecht University (Department of Information and Computing Sciences)
namespace App;

use Illuminate\Database\Eloquent\Model;

class Path extends Model
{
    //represents a path in the game:
    protected $fillable = ['problem_id', 'path'];

   /**
    * Generates a random path for a given problem.
    *
    * @param int $problemid The problem id.
    *
    * @return A random path.
    */
    public static function generatePath($problemid)
    {
        $result = self::getRandomGamePath();

        $path = Path::Create(['problem_id' => $problemid,
        'path' => $result]);
        return $path;
    }

    private static function getRandomGamePath()
    {
        $basePath = base_path() . DIRECTORY_SEPARATOR. 'resources'. DIRECTORY_SEPARATOR . 'pathgeneration';
        $subdirs = self::getSubdirs($basePath);
        $dir = rand(0, count($subdirs)-1);
        $filepath = $subdirs[$dir] . DIRECTORY_SEPARATOR .'PathGeneration.txt';
        $result = \file_get_contents($filepath);
        return $result;
    }

    private static function getSubdirs($path)
    {
        $subdirs = array();
        if ($handle = opendir($path)) {

            while (false !== ($entry = readdir($handle))) {
                if (is_dir($path.DIRECTORY_SEPARATOR.$entry) && substr($entry, 0, 1 ) != '.') {
                    $subdirs[] = $path.DIRECTORY_SEPARATOR.$entry;
                }
            }

            closedir($handle);
        }
        return $subdirs;
    }
}
