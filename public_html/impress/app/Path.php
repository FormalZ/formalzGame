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
        //The path is generated in an external java file,
        //because PHP has very bad datastructure support.
        $filepath = base_path() . '/resources/pathgeneration/PathGeneration.jar';
        $result = `java -jar $filepath 17 8 4 4 3`;

        $path = Path::Create(['problem_id' => $problemid,
        'path' => $result]);
        return $path;
    }
}
