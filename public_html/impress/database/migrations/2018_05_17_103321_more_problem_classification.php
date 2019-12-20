<?php
//This program has been developed by students from the bachelor Computer Science 
//at Utrecht University within the Software and Game project course (2018)
//©Copyright Utrecht University (Department of Information and Computing Sciences)
use Illuminate\Support\Facades\Schema;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Database\Migrations\Migration;

class MoreProblemClassification extends Migration
{
    /**
     * Run the migrations.
     *
     * @return void
     */
    public function up()
    {
        Schema::table('problems', function (Blueprint $table) {
            $table->boolean('hasEquality')->after('hasArrays');
            $table->boolean('hasLogicOperator')->after('hasEquality');
            $table->boolean('hasRelationalComparer')->after('hasLogicOperator');
            $table->boolean('hasArithmetic')->after('hasRelationalComparer');
            $table->boolean('hasImplication')->after('hasArithmetic');
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
            $table->dropColumn('hasEquality');
            $table->dropColumn('hasLogicOperator');
            $table->dropColumn('hasRelationalComparer');
            $table->dropColumn('hasArithmetic');
            $table->dropColumn('hasImplication');
        });
    }
}
