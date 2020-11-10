/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
package formalz.gamelogic.gamestate;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import formalz.haskellapi.Response;

/**
 * An object to store the state of the problems.
 * @author Ludiscite
 * @version 1.0
 */
public class ProblemState
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ProblemState.class);

    private String lastPreCondition = "false";
    private String lastPostCondition = "false";

    private boolean[] preFeedback;

    private boolean preChanged = false;
    private boolean preCorrect = false;

    private Response lastPreResponse = null;
    private Response lastPostResponse = null;

    private boolean[] postFeedback;

    private boolean postChanged = false;
    private boolean postCorrect = false;
    
    private GameState gameState;

    
    public ProblemState(GameState gameState)
    {
        this.gameState = gameState;
    }
    
    /**
     * Clear progress of the current problem.
     */ 
    public void clearProgress()
    {
        lastPreCondition = "false";
        lastPostCondition = "false";
        preCorrect = false;
        preFeedback = null;
        postCorrect = false;
        postFeedback = null;
    }
    
    /**
     * Whether the current problem is answered correct.
     * @return isCorrect
     */
    public boolean problemCorrect()
    {
        return preCorrect && postCorrect;
    }
    

    /**
     * Generates the percentages for different sparks for the pre conditions.
     * @return Float array with percentages
     */
    public float[] generatePrePercentages()
    {
        return generatePercentages(() ->
        {
            return getPreFeedback();
        } , (boolean[] feedback) ->
        {
            setPreFeedback(feedback);
        } , () ->
        {
            return getLastPreCondition();
        } , (Response response) ->
        {
            this.setLastPreResponse(response);
        } , (String string) ->
        {
            return gameState.getProblem().comparePre(string);
        } , () ->
        {
            return gameState.getAdaptiveDifficulty().getPrePercentageMultipliers();
        } , (Response response) ->
        {
            return response.getPreFeedback();
        });
    }

    /**
     * Generates the percentages for different sparks for the post conditions.
     * @return Float array with percentages
     */
    public float[] generatePostPercentages()
    {
        return generatePercentages(() ->
        {
            return getPostFeedback();
        } , (boolean[] feedback) ->
        {
            setPostFeedback(feedback);
        } , () ->
        {
            return getLastPostCondition();
        } , (Response response) ->
        {
            this.setLastPostResponse(response);
        } , (String string) ->
        {
            return gameState.getProblem().comparePost(string);
        } , () ->
        {
            return gameState.getAdaptiveDifficulty().getPostPercentageMultipliers();
        } , (Response response) ->
        {
            return response.getPostFeedback();
        });
    }

    private float[] generatePercentages(Supplier<boolean[]> getFeedback, Consumer<boolean[]> setFeedback, Supplier<String> getLastCondition,
            Consumer<Response> setLastResponse, Function<String, Response> compare, Supplier<float[]> getPercentageModifier,
            Function<Response, boolean[]> extractFeedback)
    {
        try
        {
            if (getFeedback.get() == null)
            {
                Response response = compare.apply(getLastCondition.get());
                setLastResponse.accept(response);

                int responseCode = response.getResponseCode();
                if (responseCode == 200)
                {
                    setFeedback.accept(extractFeedback.apply(response));
                }
                else
                {
                    throw new Exception("Response code " + responseCode + " not correct");
                }
            }

            float[] percentages = generatePercentages(getFeedback.get());
            float[] modifier = getPercentageModifier.get();
            for (int i = 0; i < 4; ++i)
            {
                percentages[i] *= modifier[i];
            }
            return percentages;
        }
        catch (Exception e)
        {
            LOGGER.warn("Can not generate percentages", e);
            return new float[] { 0.25f, 0.25f, 0.25f, 0.25f };
        }
    }

    private float[] generatePercentages(boolean[] bools)
    {
        int trueCount = 0;
        for (int i = 0; i < bools.length; i++)
        {
            if (bools[i])
            {
                trueCount++;
            }
        }

        if (trueCount == 0)
        {
            return new float[] { 0.25f, 0.25f, 0.25f, 0.25f };
        }

        float value = 1f / (float) (trueCount);
        float[] percentages = new float[bools.length];
        for (int i = 0; i < bools.length; i++)
        {
            percentages[i] = bools[i] ? value : 0.0f;
        }
        return percentages;
    }

    /**
     * Set the last precondition send by the user.
     * @param lastPreCondition,
     */
    public void setLastPreCondition(String lastPreCondition)
    {
        this.lastPreCondition = lastPreCondition;
    }

    /**
     * Returns the last precondition send by the user.
     * @return lastPreCondition,
     */
    public String getLastPreCondition()
    {
        return lastPreCondition;
    }

    /**
     * Returns the last postcondition send by the user.
     * @return lastPostCondition.
     */
    public String getLastPostCondition()
    {
        return lastPostCondition;
    }

    /**
     * Set the last postcondition send by the user.
     * @param lastPostCondition.
     */
    public void setLastPostCondition(String lastPostCondition)
    {
        this.lastPostCondition = lastPostCondition;
    }

    /**
     * Returns the last response received for the precondition.
     * @return Precondition response.
     */
    public Response getLastPreResponse()
    {
        return lastPreResponse;
    }

    /**
     * Set the last response received for the precondition.
     * @param response Precondition response.
     */
    public void setLastPreResponse(Response response)
    {
        this.lastPreResponse = response;
    }

    /**
     * Returns the last response received for the postcondition.
     * @return Postcondition response.
     */
    public Response getLastPostResponse()
    {
        return lastPostResponse;
    }

    /**
     * Set the last response received for the postcondition.
     * @param response Postcondition response.
     */
    public void setLastPostResponse(Response response)
    {
        this.lastPostResponse = response;
    }

    /**
     * Returns the last feedback received for the preconditions. [0] True-True [1] True-False [2] False-True [3] False-False
     * @return preFeedback.
     */
    public boolean[] getPreFeedback()
    {
        return preFeedback;
    }

    /**
     * Set the last feedback received for the preconditions.
     * @param preFeedback.
     */
    public void setPreFeedback(boolean[] preFeedback)
    {
        this.preFeedback = preFeedback;
    }

    /**
     * Returns the last feedback received for the postconditions. [0] True-True [1] True-False [2] False-True [3] False-False
     * @return postFeedback.
     */
    public boolean[] getPostFeedback()
    {
        return postFeedback;
    }

    /**
     * set the last feedback received for the postconditions.
     * @param postFeedback.
     */
    public void setPostFeedback(boolean[] postFeedback)
    {
        this.postFeedback = postFeedback;
    }

    /**
     * Set whether the preconditions checked where correct
     * @param preCorrect.
     */
    public void setPreCorrect(boolean preCorrect)
    {
        this.preCorrect = preCorrect;
    }

    /**
     * Returns whether the previous preconditions check was correct
     * @return Whether the previous preconditions was correct.
     */
    public boolean getPreCorrect()
    {
        return preCorrect;
    }

    /**
     * Set whether the postcondition checked where correct
     * @param postCorrect Set whether the previous postcondition was correct.
     */
    public void setPostCorrect(boolean postCorrect)
    {
        this.postCorrect = postCorrect;
    }

    /**
     * Returns whether the previous postconditions check was correct
     * @return Whether the previous postconditions was correct.
     */
    public boolean getPostCorrect()
    {
        return postCorrect;
    }

    /**
     * Set whether the precondition was changed by the user.
     * @param preChanged Whether the precondition has changed.
     */
    public void setPreChanged(boolean preChanged)
    {
        this.preChanged = preChanged;
    }

    /**
     * Get whether the precondition was changed by the user.
     * @return Whether the precondition has changed.
     */
    public boolean getPreChanged()
    {
        return this.preChanged;
    }

    /**
     * Set whether the postcondition was changed by the user.
     * @param postChanged Whether the postcondition has changed.
     */
    public void setPostChanged(boolean postChanged)
    {
        this.postChanged = postChanged;
    }

    /**
     * Get whether the post condition was changed by the user.
     * @return Whether the postcondition has changed.
     */
    public boolean getPostChanged()
    {
        return this.postChanged;
    }
}
