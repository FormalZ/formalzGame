<?php
//This program has been developed by students from the bachelor Computer Science 
//at Utrecht University within the Software and Game project course (2018)
//©Copyright Utrecht University (Department of Information and Computing Sciences)
use Illuminate\Support\Facades\Schema;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Database\Migrations\Migration;

class AdjustProblemrepostatisticsTable extends Migration
{
    /**
     * Run the migrations.
     *
     * @return void
     */
    public function up()
    {
        Schema::table('problemrepostatistics', function (Blueprint $table) {
            $table->dropColumn('id');
            $table->integer('pre_mistakes')->unsigned()->after('waves');
            $table->integer('post_mistakes')->unsigned()->after('pre_mistakes');
            $table->integer('problemstatistics_id')->unsigned()->first();
            $table->foreign('problemstatistics_id')->references('id')->on('problemstatistics');
            $table->integer('order')->unsigned()->after('problemstatistics_id');
            $table->primary(['problemstatistics_id', 'order']);
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
            $table->dropForeign(['problemstatistics_id']);
            $table->dropColumn('order');
            $table->dropColumn('problemstatistics_id');
        });

        Schema::table('problemrepostatistics', function (Blueprint $table) {
            $table->increments('id');
            $table->dropColumn('pre_mistakes');
            $table->dropColumn('post_mistakes');
        });
    }
}
