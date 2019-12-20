<?php
//This program has been developed by students from the bachelor Computer Science 
//at Utrecht University within the Software and Game project course (2018)
//©Copyright Utrecht University (Department of Information and Computing Sciences)
use Illuminate\Support\Facades\Schema;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Database\Migrations\Migration;

class AdjustProblemstatisticsTable extends Migration
{
    /**
     * Run the migrations.
     *
     * @return void
     */
    public function up()
    {
        Schema::table('problemstatistics', function (Blueprint $table) {
            $table->integer('pre_mistakes')->unsigned()->after('sidetrackcount');
            $table->integer('post_mistakes')->unsigned()->after('pre_mistakes');
            $table->integer('user_id')->unsigned()->after('problem_id');
            $table->foreign('user_id')->references('id')->on('users');
        });
    }

    /**
     * Reverse the migrations.
     *
     * @return void
     */
    public function down()
    {
        Schema::table('problemstatistics', function (Blueprint $table) {
            $table->dropColumn('pre_mistakes');
            $table->dropColumn('post_mistakes');
            $table->dropForeign(['user_id']);
            $table->dropColumn('user_id');
        });
    }
}
