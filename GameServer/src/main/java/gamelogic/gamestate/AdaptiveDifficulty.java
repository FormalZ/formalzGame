/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
package gamelogic.gamestate;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import data.Problem;
import data.Problem.Feature;
import data.ProblemStatistics;
import data.WaveData;
import haskellapi.Response;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import logger.Logger;

/**
 * The class that keeps track of adaptive difficulty for a single user.
 * @author Ludiscite
 * @version 1.0
 */
public class AdaptiveDifficulty
{
    /**
     * The optionality options of a feature.
     */
    public enum FeatureRequirement
    {
        have,
        notHave,
        optional
    }

    private Problem teacherProblem;
    private Problem currentProblem;
    private int questionAmount;
    private int preMistakeCount;
    private int postMistakeCount;
    private int[] featureUsage = new int[] { 0, 0, 0, 0, 0, 0, 0, 0 };

    private AdaptiveDifficultyData data;

    private ScriptEngine scriptEngine;
    private Invocable invocable;

    private int defaultWaveSparkAmount = 10;
    private int defaultWaveSparkHealth = 30;
    private int defaultWaveSparkSpeed = 64;
    private int defaultWaveSparkSpawnTime = 1000;
    private float[] defaultPrePercentageMultiplier = new float[] { 1.0f, 1.0f, 1.0f, 1.0f };
    private float[] defaultPostPercentageMultiplier = new float[] { 1.0f, 1.0f, 1.0f, 1.0f };
    private float[] defaultSpecialSparkSpawnPercentage = new float[] { 0.01f, 0.01f };
    private int defaultMinimumDifficulty = 0;
    private int defaultMaximumDifficulty = 10000;
    private boolean defaultIsFinalProblem = true;

    private int defaultAvailableScore = 10000;
    private float defaultProgression = 1.0f;

    /**
     * Constructor for an AdaptiveDifficulty object.
     * @param teacherProblem Final problem of the teacher.
     * @param questionAmount Least amount of problems before the final problem.
     */
    public AdaptiveDifficulty(Problem teacherProblem, int questionAmount)
    {
        scriptEngine = new ScriptEngineManager().getEngineByName("nashorn");

        try
        {
            scriptEngine.eval(new FileReader("adaptiveDifficultyScript.js"));
            invocable = (Invocable) scriptEngine;
        }
        catch (FileNotFoundException | ScriptException e)
        {
            Logger.log(e);
        }

        this.teacherProblem = teacherProblem;
        this.questionAmount = questionAmount;

        data = new AdaptiveDifficultyData(teacherProblem, currentProblem, questionAmount, preMistakeCount, postMistakeCount, featureUsage);
    }

    /**
     * Set the amount of mistakes made.
     * @param preMistakeCount Amount of mistakes made on the precondition.
     * @param postMistakeCount Amount of mistakes made on the postcondition.
     */
    public void setMistakeCount(int preMistakeCount, int postMistakeCount)
    {
        this.preMistakeCount = preMistakeCount;
        this.postMistakeCount = postMistakeCount;
        data = new AdaptiveDifficultyData(teacherProblem, currentProblem, questionAmount, preMistakeCount, postMistakeCount, featureUsage);
    }

    /**
     * Increase the feature usage data.
     * @param featureUsage Features to increase.
     */
    public void updateFeatureUsage(int[] featureUsage)
    {
        for (int i = 0; i < 8; i++)
        {
            this.featureUsage[i] += featureUsage[i];
        }
        data = new AdaptiveDifficultyData(teacherProblem, currentProblem, questionAmount, preMistakeCount, postMistakeCount, featureUsage);
    }

    /**
     * Resets the feature usage information.
     */
    public void resetFeatureUsage()
    {
        this.featureUsage = new int[] { 0, 0, 0, 0, 0, 0, 0, 0 };
        data = new AdaptiveDifficultyData(teacherProblem, currentProblem, questionAmount, preMistakeCount, postMistakeCount, featureUsage);
    }

    /**
     * Set the current problem.
     * @param currentProblem Current problem.
     */
    public void setCurrentProblem(Problem currentProblem)
    {
        this.currentProblem = currentProblem;
        data = new AdaptiveDifficultyData(teacherProblem, currentProblem, questionAmount, preMistakeCount, postMistakeCount, featureUsage);
    }

    /**
     * Return the amount of sparks to spawn.
     * @return Amount of sparks to spawn.
     */
    public int getWaveSparkAmount()
    {
        return runReturnMethod("getWaveSparkAmount", defaultWaveSparkAmount);
    }

    /**
     * Returns the health of sparks.
     * @return Health of sparks.
     */
    public int getWaveSparkHealth()
    {
        return runReturnMethod("getWaveSparkHealth", defaultWaveSparkHealth);
    }

    /**
     * Returns the speed of sparks.
     * @return Speed of sparks.
     */
    public int getWaveSparkSpeed()
    {
        return runReturnMethod("getWaveSparkSpeed", defaultWaveSparkSpeed);
    }

    /**
     * Returns the amount of time between sparks.
     * @return Spawn time of sparks.
     */
    public int getWaveSparkSpawnTime()
    {
        return runReturnMethod("getWaveSparkSpawnTime", defaultWaveSparkSpawnTime);
    }

    /**
     * Returns modifiers for the pre percentages.
     * @return Pre percentage modifiers.
     */
    public float[] getPrePercentageMultipliers()
    {
        return runReturnMethod("getPrePercentageMultipliers", defaultPrePercentageMultiplier, 4);
    }

    /**
     * Returns modifiers for the post percentages.
     * @return Post percentage modifiers.
     */
    public float[] getPostPercentageMultipliers()
    {
        return runReturnMethod("getPostPercentageMultipliers", this.defaultPostPercentageMultiplier, 4);
    }

    /**
     * Returns the percentages for spawning special types of sparks.
     * @return Float array of percentages for spawning special sparks.
     */
    public float[] getSpecialSparkSpawnPercentage()
    {
        return runReturnMethod("getSpecialSparkSpawnPercentage", this.defaultSpecialSparkSpawnPercentage, 2);
    }

    /**
     * Returns the minimum difficulty of the random problem to choose.
     * @return Minimum difficulty of random problem.
     */
    public int getMinimumDifficulty()
    {
        return runReturnMethod("getMinimumDifficulty", defaultMinimumDifficulty);
    }

    /**
     * Returns the maximum difficulty of the random problem to choose.
     * @return Maximum difficulty of random problem.
     */
    public int getMaximumDifficulty()
    {
        return runReturnMethod("getMaximumDifficulty", defaultMaximumDifficulty);
    }

    /**
     * Returns features and requirements.
     * @return Features and requirements.
     */
    public Map<Feature, FeatureRequirement> getFeatureRequirements()
    {
        Map<Feature, FeatureRequirement> featureRequirements = new HashMap<>();
        featureRequirements.put(Feature.forAll, runReturnMethod("getHasForAll", FeatureRequirement.optional));
        featureRequirements.put(Feature.exists, runReturnMethod("getHasExists", FeatureRequirement.optional));
        featureRequirements.put(Feature.arrays, runReturnMethod("getHasArrays", FeatureRequirement.optional));
        featureRequirements.put(Feature.equality, runReturnMethod("getHasEquality", FeatureRequirement.optional));
        featureRequirements.put(Feature.logicOperator, runReturnMethod("getHasLogicOperator", FeatureRequirement.optional));
        featureRequirements.put(Feature.relationalComparer, runReturnMethod("getHasRelationalOperator", FeatureRequirement.optional));
        featureRequirements.put(Feature.arithmetic, runReturnMethod("getHasArithmetic", FeatureRequirement.optional));
        featureRequirements.put(Feature.implication, runReturnMethod("getHasImplication", FeatureRequirement.optional));
        return featureRequirements;
    }

    /**
     * Returns whether the next problem is the final problem.
     * @return Whether the next problem is the final problem.
     */
    public boolean isFinalProblem()
    {
        return runReturnMethod("isFinalProblem", defaultIsFinalProblem);
    }

    /**
     * Process a response for the precondition.
     * @param preResponse Response for precondition.
     */
    public void processPreResponse(Response preResponse)
    {
        runMethod("processPreResponse", preResponse);
    }

    /**
     * Process a response for the postcondition.
     * @param postResponse Response for postcondition.
     */
    public void processPostResponse(Response postResponse)
    {
        runMethod("processPostResponse", postResponse);
    }

    /**
     * Process the data of a completed wave.
     * @param waveData Data of completed wave.
     */
    public void processWaveData(WaveData waveData)
    {
        this.runMethod("processWaveData", waveData);
    }

    /**
     * Process the event of starting a new problem.
     */
    public void newProblem()
    {
        this.runMethod("newProblem");
    }

    /**
     * Process the statistics of the current repo problem. Only if the problem is not the final problem.
     * @param statistics Statistics of the current repo problem.
     */
    public void processProblemStatistics(ProblemStatistics statistics)
    {
        this.runMethod("processProblemStatistics", statistics);
    }

    /**
     * Returns the amount of score the player is allowed to gain during the current problem.
     * @return The amount of score the player is allowed to gain during the current problem.
     */
    public int getAvailableScore()
    {
        return runReturnMethod("getAvailableScore", defaultAvailableScore);
    }

    /**
     * Returns the current progression of the game.
     * @return The current progression of the game.
     */
    public float getProgression()
    {
        return runReturnMethod("progressionPercentage", defaultProgression);
    }

    /**
    * Sets the challenge counter based on given local data from the client
    * @param challengeCounter the counter to which to set.
    */
    public void setChallenge(int challengeCounter) {
      this.runMethod("setChallenge", challengeCounter);
    }

    /**
     * Run a method in the script. Method will always receive AdaptiveDifficultyData as parameter.
     * @param methodName Name of the method to run.
     */
    private void runMethod(String methodName)
    {
        try
        {
            invocable.invokeFunction(methodName, this.data);
        }
        catch (NoSuchMethodException | ScriptException e)
        {
            Logger.log(e);
            e.printStackTrace();
        }
    }

    /**
     * Run a method in the script. Method will always receive AdaptiveDifficultyData as first parameter.
     * @param methodName Name of the method to run.
     * @param t Additional data to send as second parameter.
     * @param <T> Type of the variable send to the method.
     */
    private <T> void runMethod(String methodName, T t)
    {
        try
        {
            invocable.invokeFunction(methodName, this.data, t);
        }
        catch (NoSuchMethodException | ScriptException e)
        {
            Logger.log(e);
            e.printStackTrace();
        }
    }

    /**
     * Run a method in the script returing an int. Method will always receive AdaptiveDifficultyData as first parameter.
     * @param methodName Name of the method to run.
     * @param defaultValue Default value to return in case of the script failing.
     * @return Integer returned by the method or the default value.
     */
    private int runReturnMethod(String methodName, int defaultValue)
    {
        try
        {
            int value = (int) Math.floor((double) invocable.invokeFunction(methodName, this.data));
            return value;
        }
        catch (NoSuchMethodException | ScriptException e)
        {
            Logger.log(e);
            return defaultValue;
        }
    }

    /**
     * Run a method in the script returing a boolean. Method will always receive AdaptiveDifficultyData as first parameter.
     * @param methodName Name of the method to run.
     * @param defaultValue Default value to return in case of the script failing.
     * @return Boolean returned by the method or the default value.
     */
    private boolean runReturnMethod(String methodName, boolean defaultValue)
    {
        try
        {
            Boolean obj = (Boolean) invocable.invokeFunction(methodName, this.data);
            boolean value = (boolean) obj;
            return value;
        }
        catch (NoSuchMethodException | ScriptException e)
        {
            Logger.log(e);
            return defaultValue;
        }
    }

    /**
     * Run a method in the script returning a float. Method will always receive AdaptiveDifficultyData as first parameter.
     * @param methodName Name of the method to run.
     * @param defaultValue Default value to return in case of the script failing.
     * @return Float returned by the method or the default value.
     */
    private float runReturnMethod(String methodName, float defaultValue)
    {
        try
        {
            Double obj = (Double) invocable.invokeFunction(methodName, this.data);
            float value = (float) obj.doubleValue();
            return value;
        }
        catch (NoSuchMethodException | ScriptException e)
        {
            Logger.log(e);
            return defaultValue;
        }
    }

    /**
     * Run a method in the script returing a array of four float. Method will always receive AdaptiveDifficultyData as first parameter.
     * @param methodName Name of the method to run.
     * @param defaultValue Default value to return in case of the script failing.
     * @param amount Amount of floats in the array.
     * @return Float[] returned by the method or the default value.
     */
    private float[] runReturnMethod(String methodName, float[] defaultValue, int amount)
    {
        try
        {
            ScriptObjectMirror mirror = (ScriptObjectMirror) invocable.invokeFunction(methodName, this.data);
            float[] array = new float[amount];
            for (int i = 0; i < amount; i++)
            {
                array[i] = Float.parseFloat(mirror.get(i + "").toString());
            }
            return array;
        }
        catch (NoSuchMethodException | ScriptException e)
        {
            Logger.log(e);
            return defaultValue;
        }
    }

    /**
     * Run a method in the script returning a feature requirement. Method will always receive AdaptiveDifficultyData as first parameter.
     * @param methodName Name of the method to run.
     * @param defaultValue Default value to return in case of the script failing.
     * @return feature requirement returned by the method or the default value.
     */
    private FeatureRequirement runReturnMethod(String methodName, FeatureRequirement defaultValue)
    {
        try
        {
            String obj = (String) invocable.invokeFunction(methodName, this.data);
            obj = obj.toLowerCase();
            switch (obj)
            {
                case "true":
                case "have":
                    return FeatureRequirement.have;
                case "false":
                case "nothave":
                    return FeatureRequirement.notHave;
                case "optional":
                    return FeatureRequirement.optional;

            }
            return defaultValue;
        }
        catch (NoSuchMethodException | ScriptException e)
        {
            Logger.log(e);
            return defaultValue;
        }
    }
}
