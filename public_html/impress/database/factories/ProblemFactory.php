<?php
//This program has been developed by students from the bachelor Computer Science 
//at Utrecht University within the Software and Game project course (2018)
//©Copyright Utrecht University (Department of Information and Computing Sciences)
use Faker\Generator as Faker;

/*
|--------------------------------------------------------------------------
| Model Factories
|--------------------------------------------------------------------------
|
| This directory should contain each of the model factory definitions for
| your application. Factories provide a convenient way to generate new
| model instances for testing / seeding your application's database.
|
*/

$factory->define(App\Problem::class, function (Faker $faker) {
    $name = $faker->unique()->text('200');
    return [
        'header' => $name,
        'description' => $faker->text('2550'),
        'pre_conditions' => $faker->boolean(),
        'post_conditions' => $faker->boolean(),
        'difficulty' => $faker->numberBetween($min = 1, $max = 5),
        'hasArrays' => false,
        'hasForAll' => false,
        'hasExists' => false,
        'hasEquality' => false,
        'hasLogicOperator' => false,
        'hasRelationalComparer' => false,
        'hasArithmetic' => false,
        'hasImplication' => false,
        'problemcount' => 0
    ];
});
