<?php
//This program has been developed by students from the bachelor Computer Science 
//at Utrecht University within the Software and Game project course (2018)
//©Copyright Utrecht University (Department of Information and Computing Sciences)
use Illuminate\Support\Facades\Schema;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Database\Migrations\Migration;

class CreateProblemrepoTable extends Migration
{
    /**
     * Run the migrations.
     *
     * @return void
     */
    public function up()
    {
        Schema::create('problemrepo', function (Blueprint $table) {
            $table->increments('id');
            $table->string('header', 255);
            $table->string('description', 2550);
            $table->string('pre_conditions');
            $table->string('post_conditions');
            $table->integer('difficulty')->unsigned();
            $table->boolean('hasForAll');
            $table->boolean('hasExists');
            $table->boolean('hasArrays');
            $table->timestamps();
        });
    }

    /**
     * Reverse the migrations.
     *
     * @return void
     */
    public function down()
    {
        Schema::dropIfExists('problemrepo');
    }
}
