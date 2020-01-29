<?php
//This program has been developed by students from the bachelor Computer Science 
//at Utrecht University within the Software and Game project course (2018)
//©Copyright Utrecht University (Department of Information and Computing Sciences)
use Illuminate\Support\Facades\Schema;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Database\Migrations\Migration;

class FixProblemrepostatisticsTable extends Migration
{
    /**
     * Run the migrations.
     *
     * @return void
     */
    public function up()
    {
        Schema::table('problemrepostatistics', function (Blueprint $table) {
            $table->dropForeign(['problemstatistics_id']);
            $table->dropPrimary();
            $table->dropColumn('problemstatistics_id');
            $table->integer('playedgame_id')->unsigned()->first();
            $table->foreign('playedgame_id')->references('id')->on('playedgames');
            $table->primary(['playedgame_id', 'order']);
        });
    }

    /**
     * Reverse the migrations.
     *
     * @return void
     */
    public function down()
    {
        Schema::table('problemrepostatistics', function (Blueprint $table) {
            $table->dropForeign(['playedgame_id']);
            $table->dropPrimary();
            $table->dropColumn('playedgame_id');
            $table->integer('problemstatistics_id')->unsigned()->first();
            $table->foreign('problemstatistics_id')->references('id')->on('problemstatistics');
            $table->primary(['problemstatistics_id', 'order']);
        });
    }
}
