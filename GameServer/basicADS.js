/*
	This document is an example containing almost all features mentioned in
	the how to modify the adaptive difficulty document.
*/

// Tower defence adaptive difficulty

var getWaveSparkAmount = function(data) {
	return 10;
};

var getWaveSparkHealth = function(data) {
	return 30;
};

var getWaveSparkSpeed = function(data) {
	return 64;
};

var getWaveSparkSpawnTime = function(data) {
	return 1000;
};

var getPrePercentageMultipliers = function(data) {
	return [1.0, 1.0, 1.0, 1.0];
};

var getPostPercentageMultipliers = function(data) {
	return [1.0, 1.0, 1.0, 1.0];
};

var getSpecialSparkSpawnPercentage = function(data) {
	return [0.01, 0.01];
};

var processPreResponse = function(data, response) {
	var feedback = response.getPreFeedback();
	if(feedback == null && !feedback.isEquivalent())
	{
	    return;
	}
	print("T-T:" + feedback[0]);
	print("T-F:" + feedback[1]);
	print("F-T:" + feedback[2]);
	print("F-F:" + feedback[3]);
};

var processPostResponse = function(data, response) {
	var feedback = response.getPostFeedback();
	if(feedback == null && !feedback.isEquivalent())
	{
	    return;
	}
	print("T-T:" + feedback[0]);
	print("T-F:" + feedback[1]);
	print("F-T:" + feedback[2]);
	print("F-F:" + feedback[3]);
};

var preSpawned;
var prePassed;
var postSpawned;
var postPassed;
var health;

var processWaveData = function(data, waveData) {
	preSpawned = waveData.getPreSpawned();
	prePassed = waveData.getPrePassed();
	postSpawned = waveData.getPostSpawned();
	postPassed = waveData.getPostPassed();
	
	health = waveData.getHealth();
};

// Formal specification adaptive difficulty

var problemsCompleted = 0;
var currentLevel = 0;

var averageWavesNeeded = 0;
var averagePreMistakeCount = 0;
var averagePostMistakeCount = 0;

var processProblemStatistics = function(data, pStatistics) {
	averageWavesNeeded = pStatistics.getAverageWavesNeeded();
	averagePreMistakeCount = pStatistics.getAveragePreMistakeCount();
	averagePostMistakeCount = pStatistics.getAveragePostMistakeCount();
}

var getMinimumDifficulty = function(data) {
	return currentDifficulty(data) - 1;
};

var getMaximumDifficulty = function(data) {
	return currentDifficulty(data) + 1;
};

var getHasForAll = function(data) {
	if(data.getTeacherProblemHasForAll())
	{ return "have"; }
	else
	{ return "notHave"; }
};

var getHasExists = function(data) {
	if(data.getTeacherProblemHasExists())
	{ return "have"; }
	else
	{ return "notHave"; }
};

var getHasArrays = function(data) {
	if(data.getTeacherProblemHasArrays())
	{ return "have"; }
	else
	{ return "notHave"; }
};

var getHasEquality = function(data) {
	return "optional";
	if(data.getTeacherProblemHasEquality())
	{ return "have"; }
	else
	{ return "notHave"; }
};

var getHasLogicOperator = function(data) {
	return "optional";
	if(data.getTeacherProblemHasLogicOperator())
	{ return "have"; }
	else
	{ return "notHave"; }
};

var getHasRelationalOperator = function(data) {
	return "optional";
	if(data.getTeacherProblemHasRelationalComparer())
	{ return "have"; }
	else
	{ return "notHave"; }
};

var getHasArithmetic = function(data) {
	return "optional";
	if(data.getTeacherProblemHasArithmetic())
	{ return "have"; }
	else
	{ return "notHave"; }
};

var getHasImplication = function(data) {
	return "optional";
	if(data.getTeacherProblemHasImplication())
	{ return "have"; }
	else
	{ return "notHave"; }
};

var isFinalProblem = function(data) {
	return data.getQuestionAmount() <= currentLevel;
};

var newProblem = function(data) {
	if(doProgress(data))
	{
		currentLevel += 1;	
	}
	problemsCompleted += 1;
	currentWavesCompleted = 0;
};

// Helper Methods

var currentDifficulty = function(data) {
	return data.getTeacherProblemDifficulty();
}

var doProgress = function(data) {
	return ((data.getPreMistakeCount() + 1) < averagePreMistakeCount) &&
           ((data.getPostMistakeCount() + 1) < averagePostMistakeCount);
};
