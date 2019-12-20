<?php
//This program has been developed by students from the bachelor Computer Science 
//at Utrecht University within the Software and Game project course (2018)
//©Copyright Utrecht University (Department of Information and Computing Sciences)
namespace App;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Auth;

/**
 * Represents a single final problem as specified by a teacher.
 *
 */
class Problem extends Model
{
    /**
     * The attributes: A header, despriction, the pre and post
     * conditions and a difficulty given by the teacher.
     * Also booleans hasForAll, hasExists and hasArrays which
     * classify the problem and the room id of the room that the problem
     * is associated with.
     */
    protected $fillable = ['header', 'description','pre_conditions',
    'post_conditions', 'difficulty', 'lives', 'money', 'deadline', 'room_id',
    'hasForAll', 'hasExists', 'hasArrays', 'hasEquality',
    'hasLogicOperator', 'hasRelationalComparer', 'hasArithmetic',
    'hasImplication', 'problemcount', 'trackingCode', 'hide', 'archive',
        'displayDate', 'autohide', 'trackingLink'];

   /**
    * Removes this problem and all associated attributes from the database.
    *
    */
    public function remove() {
        //delete the path (deleting foreign key constraints):
        Path::where('problem_id', $this->id)->delete();
        //delete associated gamesessions:
        DB::table('gamesessions')->where('problem_id', $this->id)
        ->delete();
        //delete associated problemstatistics:
        DB::table('problemstatistics')->where('problem_id', $this->id)
        ->delete();
        //delete the problem:
        $this->delete();
    }

    /**
     * Retrieve statistics for a certain problem from the database.
     *
     * @return A dictionary of different statistics:
     * 'completecount' -> the amount of students who have completed the problem
     * 'averagescore' -> average score of the problem
     * 'averagetime' -> average time it took users to complete the problem
     * 'times' -> full array of people's times on the problem
     * 'lastgameproblems' -> statistics of the last game of the user
     * 'comp[letedmails' -> mails of students who have completed the problem
     */
    public function getStatistics($iscreator)
    {
        $id = $this->id;
        //only calculate the statistics if the user is the creator:
        if ($iscreator) {
            //count of people who have completed the problem:
            $completecount = DB::table('problemstatistics')->where('problem_id', $id)->count();

            if ($completecount > 0) {
                //average final problem completion time:
                $averagetime = round(DB::table('problemstatistics')
                ->where('problem_id', $id)->avg('problemtime')) . ' seconds';

                //average score:
                $averagescore = round(DB::table('score')->where(['problem_id' => $id, 'legitimate' => true])
                ->avg('score'));

                //full array of times:
                $times = DB::table('problemstatistics')->where('problem_id', $id)
                ->select('problemtime')->orderBy('problemtime')->get();

                //array of emails of people who have completed the problem:
                $completedmails = DB::table('score')->where('problem_id', $id)
                ->join('users', 'score.user_id', '=', 'users.id')
                ->select('users.email')->groupBy('users.email')->get();

            } else {
                $averagescore =
                $averagetime = 'Problem has not been played yet.';
                $times = 
                $completedmails = [];
            }
        } else {
            //if the user isn't the creator, just give nothing.
            $completecount =
            $averagetime =
            $averagescore =
            $completedmails =
            $times = null;
        }

        //get the statistics for the last played game of a player:
        //this will show their mistakes, to give feedback.
        $lastgame = DB::table('playedgames')->where(['user_id' => Auth::id(), 'problem_id' => $id])->orderBy('id', 'desc')->first();
        if ($lastgame !== null) {
            //get the played repoproblems and their stats:
            $lastgameproblems = DB::table('problemrepostatistics')->where('playedgame_id', $lastgame->id)
            ->join('problemrepo', 'problemrepo.id', '=', 'problemrepostatistics.problemrepo_id')->orderBy('orderby', 'asc')
            ->select('pre_mistakes', 'post_mistakes', 'difficulty')->get();
            //add the final problem and its stats:
            $finalproblem = DB::table('problemstatistics')->where('playedgame_id', $lastgame->id)
            ->join('problems', 'problems.id', '=', 'problemstatistics.problem_id')
            ->select('pre_mistakes', 'post_mistakes', 'difficulty')->first();
            if ($finalproblem !== null) {
                $lastgameproblems[count($lastgameproblems)] = $finalproblem;
            }
        } else {
            $lastgameproblems = [];
        }

        return ['completecount' => $completecount,
                'averagetime' => $averagetime,
                'averagescore' => $averagescore,
                'times' => $times,
                'lastgameproblems' => $lastgameproblems,
                'completedmails' => $completedmails
                ];    
    }
  
    /**
     * Check if there are enough different related problems in the problemrepo.
     *
     * @param int $problemcount The amount of problems the user wants
     *
     * @return True if there are, false if there are not
     * enough related problems in the DB.
     */
    public function findRepoProblems($problemcount)
    {
        //get related problems:
        $problems = DB::table('problemrepo')
        ->where('header', '<>', $this->header)
        ->count();

        //return if we have enough problems:
        return $problems >= $problemcount;
    }
  
   /**
    * Classify the problem using the different classification bools.
    *
    * @param string $pre  The problem's precondition.
    * @param string $post The problem's postcondition.
    *
    * @return An array containing the classification bools.
    */
    public static function classifyProblem($pre, $post)
    {
        //classify the problem by checking substrings:
        //(this looks horrible with the !== false,
        //but this is actually a result of php not being type strong and the return values of strpos.)
        return [
            'hasForAll' => self::checkConditionString($pre, $post, 'forall'),
            'hasExists' => self::checkConditionString($pre, $post, 'exists'),
            'hasArrays' => self::checkConditionString($pre, $post, '[') ||
            self::checkConditionString($pre, $post, 'length') ||
            self::checkConditionString($pre, $post, 'null'),
            'hasEquality' => self::checkConditionString($pre, $post, '==') ||
            self::checkConditionString($pre, $post, '!='),
            'hasLogicOperator' => self::checkConditionString($pre, $post, '&&') ||
            self::checkConditionString($pre, $post, '||'),
            'hasRelationalComparer' => self::checkConditionString($pre, $post, '>') ||
            self::checkConditionString($pre, $post, '<') ||
            self::checkConditionString($pre, $post, '>=') ||
            self::checkConditionString($pre, $post, '<='),
            'hasArithmetic' => self::checkConditionString($pre, $post, '+') ||
            self::checkConditionString($pre, $post, '-') ||
            self::checkConditionString($pre, $post, '*') ||
            self::checkConditionString($pre, $post, '%'),
            'hasImplication' => self::checkConditionString($pre, $post, 'imp')
        ];
    }

   /**
    * Check if a pre or post condition contains a string
    *
    * @param string $pre    The problem's precondition.
    * @param string $post   The problem's postcondition.
    * @param string $string The string to check for.
    *
    * @return True or false.
    */
    private static function checkConditionString($pre, $post, $string)
    {
        return strpos($pre, $string) !== false ||
               strpos($post, $string) !== false;
    }
}
