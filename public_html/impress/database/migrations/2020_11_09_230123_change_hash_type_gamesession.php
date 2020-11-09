<?php

use Illuminate\Support\Facades\Schema;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Database\Migrations\Migration;

class ChangeHashTypeGamesession extends Migration
{
    /**
     * Run the migrations.
     *
     * @return void
     */
    public function up()
    {
        Schema::table('gamesessions', function (Blueprint $table) {
            $table->integer('hash')->signed()->nullable()->change();
        });
    }

    /**
     * Reverse the migrations.
     *
     * @return void
     */
    public function down()
    {
        Schema::table('gamesessions', function (Blueprint $table) {
            $table->integer('hash')->unsigned()->nullable()->change();
        });
    }
}
