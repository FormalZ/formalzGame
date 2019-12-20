<?php
//This program has been developed by students from the bachelor Computer Science 
//at Utrecht University within the Software and Game project course (2018)
//©Copyright Utrecht University (Department of Information and Computing Sciences)
use Illuminate\Support\Facades\Schema;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Database\Migrations\Migration;

class DropProblemlistTable extends Migration
{
    /**
     * Run the migrations.
     *
     * @return void
     */
    public function up()
    {
        Schema::table('problemlist', function (Blueprint $table) {
            Schema::drop('problemlist');
        });
    }

    /**
     * Reverse the migrations.
     *
     * @return void
     */
    public function down()
    {
        Schema::create('problemlist', function (Blueprint $table) {
            $table->integer('problem_id')->unsigned();
            $table->integer('problemrepo_id')->unsigned();
            $table->integer('order_by')->unsigned();
            $table->foreign('problem_id')->references('id')->on('problems');
            $table->foreign('problemrepo_id')->references('id')->on('problemrepo');
            $table->primary(['problem_id','problemrepo_id']);
        });
    }
}
