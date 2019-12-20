<?php

use Illuminate\Support\Facades\Schema;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Database\Migrations\Migration;

class AddMoneyLivesDeadline extends Migration
{
    /**
     * Run the migrations.
     *
     * @return void
     */
    public function up()
    {
        Schema::table('problems', function (Blueprint $table) {
            //
            $table->integer("money")->nullable();
            $table->integer("lives")->nullable();
            $table->integer("deadline")->nullable();
        });
    }

    /**
     * Reverse the migrations.
     *
     * @return void
     */
    public function down()
    {
        Schema::table('problems', function (Blueprint $table) {
            //
            $table->dropColumn("money");
            $table->dropColumn("lives");
            $table->dropColumn("deadline");
        });
    }
}
