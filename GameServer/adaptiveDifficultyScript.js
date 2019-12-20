// Tower defence adaptive difficulty

var preSpecificationScore = 0;
var postSpecificationScore = 0;
var towerDefenceScore = 0;

var wavesCompleted = 0;
var problemsCompleted = 0;
// Player performance is a float between -2 (everything wrong, nothing killed)
// and 2 (everything right, every marked spark killed). If the player performs
// better than average (~1.4), the difficulty ramps up more quickly.
var playerPerformance = 0;

// Depends only on amount of waves completed, up to a maximum of 30.
var getWaveSparkAmount = function(data) {
    return Math.min(30, 10 + wavesCompleted * 2 + ((playerPerformance >= 1.4) ? 1 : 0));
};

// Depends on waves and problems completed, and if the player is performing better than average.
// Up to a maximum of 40.
var getWaveSparkHealth = function(data) {
    var sum = wavesCompleted + problemsCompleted * 2 + ((playerPerformance >= 1.4) ? 1 : 0);
    return Math.min(40, 20 + sum);
};

// Depends on waves and problems completed, and if the player is performing better than average.
// Up to a maximum of 150.
var getWaveSparkSpeed = function(data) {
    var sum = wavesCompleted + problemsCompleted * 2 + ((playerPerformance >= 1.4) ? 2 : 0);
    return Math.min(150, 80 + 5 * sum);
};

// Depends only on problems completed, up to a maximum of 300.
var getWaveSparkSpawnTime = function(data) {
    return Math.max(300, 500 - 25 * wavesCompleted);
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
    if(feedback == null)
    {
        return;
    }
    if(!feedback[0] && !feedback[1] && !feedback[2] && !feedback[3])
    {
        preSpecificationScore -= 0.1;
    }
    else
    {
        preSpecificationScore += feedback[0] ?  0.05  : 0;
        preSpecificationScore += feedback[1] ?  -0.15 : 0;
        preSpecificationScore += feedback[2] ?  -0.15 : 0;
        preSpecificationScore += feedback[3] ?  0.05  : 0;
    }
};

var processPostResponse = function(data, response) {
    var feedback = response.getPostFeedback();
    if(feedback == null)
    {
        return;
    }
    if(!feedback[0] && !feedback[1] && !feedback[2] && !feedback[3])
    {
        postSpecificationScore -= 0.1;
    }
    else
    {
        postSpecificationScore += feedback[0] ?  0.05  : 0;
        postSpecificationScore += feedback[1] ?  -0.15 : 0;
        postSpecificationScore += feedback[2] ?  -0.15 : 0;
        postSpecificationScore += feedback[3] ?  0.05  : 0;
    }
};

var processWaveData = function(data, waveData) {
    wavesCompleted += 1;
    availableScore -= waveData.getDeltaScore();
    availableScore *= 0.8;

    var preSpawned = waveData.getPreSpawned();
    var prePassed = waveData.getPrePassed();
    var postSpawned = waveData.getPostSpawned();
    var postPassed = waveData.getPostPassed();

    var wrongPreSpawned = preSpawned[1] + preSpawned[3];
    var wrongPrePassed = prePassed[1] + prePassed[3];

    var correctPreSpawned = preSpawned[0] + preSpawned[2];
    var correctPrePassed = prePassed[0] + prePassed[2];

    var wrongPostSpawned = postSpawned[1] + postSpawned[3];
    var wrongPostPassed = postPassed[1] + postPassed[3];

    var correctPostSpawned = postSpawned[0] + postSpawned[2];
    var correctPostPassed = postPassed[0] + postPassed[2];

    var correctPreRatio = (correctPreSpawned == 0) ? 0 : correctPrePassed/correctPreSpawned;
    var correctPostRatio = (correctPostSpawned == 0) ? 0 : correctPostPassed/correctPostSpawned;

    var wrongPreRatio = (wrongPreSpawned == 0) ? 0 : wrongPrePassed/wrongPreSpawned;
    var wrongPostRatio = (wrongPostSpawned == 0) ? 0 : wrongPostPassed/wrongPostSpawned;

    // Deduce whether the player is performing better than average,
    // based on his skill in tower defence and formal specifications combined.
    playerPerformance = (correctPreRatio - wrongPreRatio) + (correctPostRatio - wrongPostRatio);
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
    return Math.floor(currentDifficulty(data));
};

var getMaximumDifficulty = function(data) {
    return Math.floor(currentDifficulty(data));
};

// Feature getters
var getHasForAll = function(data) {
    if(data.getTeacherProblemHasForAll() && (progressionPercentage(data) >= 0.6))
    { return "have"; }
    else
    { return "notHave"; }
};

var getHasExists = function(data) {
    if(data.getTeacherProblemHasExists() && (progressionPercentage(data) >= 0.6))
    { return "have"; }
    else
    { return "notHave"; }
};

var getHasArrays = function(data) {
    if(data.getTeacherProblemHasArrays() && (progressionPercentage(data) >= 0.3))
    { return "have"; }
    else
    { return "notHave"; }
};

var getHasEquality = function(data) {
    if(minIndex(data.getFeatureUsage(), data.getFeatureMask()) == 3)
    {
        return "have";
    }
    return "optional";
};

var getHasLogicOperator = function(data) {
    if(minIndex(data.getFeatureUsage(), data.getFeatureMask()) == 4)
    {
        return "have";
    }
    return "optional";
};

var getHasRelationalOperator = function(data) {
    if(minIndex(data.getFeatureUsage(), data.getFeatureMask()) == 5)
    {
        return "have";
    }
    return "optional";
};

var getHasArithmetic = function(data) {
    if(minIndex(data.getFeatureUsage(), data.getFeatureMask()) == 6)
    {
        return "have";
    }
    return "optional";
};

var getHasImplication = function(data) {
    if(data.getTeacherProblemHasImplication() && (progressionPercentage(data) >= 0.3))
    { return "have"; }
    else
    { return "notHave"; }
};

// General problem methods

var isFinalProblem = function(data) {
    return data.getQuestionAmount() <= currentLevel;
};

var newProblem = function(data) {
    // Whether to go to the next level
    if((data.getPreMistakeCount() <= averagePreMistakeCount + 1)&&(data.getPostMistakeCount() <= averagePostMistakeCount + 1))
    {
        currentLevel += 1;
    }
    problemsCompleted += 1;
    currentWavesCompleted = 0;
};

var setChallenge = function(data) {
  currentLevel = data;
  problemsCompleted = data;
};



var getAvailableScore = function(data) {
    availableScore = 100.0 * currentDifficulty(data);
    return availableScore;
}

var progressionPercentage = function(data) {
	if(data.getQuestionAmount() == 0)
	{
		return 1.0;
	}
    return currentLevel / data.getQuestionAmount();
}

// Helper

// If currentLevel = 0, then currentDifficulty = 1
// If currentLevel = data.getQuestionAmount(), then currentDifficulty = data.getTeacherProblemDifficulty()
var currentDifficulty = function(data) {
	if(data.getQuestionAmount() == 0)
	{
		return data.getTeacherProblemDifficulty();
	}
    return 1 + (currentLevel * ((data.getTeacherProblemDifficulty()-1)/data.getQuestionAmount()));
}

var minIndex = function(array, mask)
{
    minI = -1;
    minVal = 999999999;
    for (i = 0; i < array.length; i++)
    {
        if (mask[i] && array[i] < minVal)
        {
            minVal = array[i];
            minI = i;
        }
    }
    return minI;
}
