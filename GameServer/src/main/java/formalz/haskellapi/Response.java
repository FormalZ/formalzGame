/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
package formalz.haskellapi;

import java.util.Set;
import java.util.Map.Entry;

import com.google.gson.*;

/**
 * An object to parse a response from the Haskell API into.
 * @author Ludiscite
 * @version 1.0
 */
public class Response
{
    /**
     * Feedback for the preconditions. [0] True-True [1] True-False [2] False-True [3] False-False
     */
    private boolean[] preFeedback;

    /**
     * Feedback for the postconditions. [0] True-True [1] True-False [2] False-True [3] False-False
     */
    private boolean[] postFeedback;

    /**
     * Different types of responses the Haskell backend can give.
     */
    public enum ResponseType
    {
        Equiv,
        NotEquiv,
        Undef,
        ResponseErr
    }

    /**
     * The response code from the Haskell API. A successful query gives 200, a syntax error gives 500 and a connection exception gives 400.
     */
    private int responseCode;

    /**
     * The response type given by the Haskell API. If the methods are found to be equivalent, this will be Equiv. Otherwise it will be
     * NotEquiv.
     */
    private ResponseType responseType;
    /**
     * The counterexample found by the Haskell API, or null if the methods were found to be equivalent.
     */
    private Set<Entry<String, JsonElement>> model;

    /**
     * Error given by the haskell backend
     */
    private String err;

    /**
     * Constructor of a Response which was not successful.
     * @param responseCode The responseCode of the Haskell API.
     */
    public Response(int responseCode)
    {
        this.responseCode = responseCode;
    }

    /**
     * Constructor of a successful Response.
     * @param responseType The response type given by the Haskell API. If the methods are found to be equivalent, this should be Equiv.
     * Otherwise it should be NotEquiv.
     * @param model The counterexample found by the Haskell API, or null if the methods were found to be equivalent.
     * @param err Error from the checker.
     * @param preFeedback Feedback booleans about the precondition.
     * @param postFeedback Feedback booleans about the postcondition.
     */
    public Response(ResponseType responseType, Set<Entry<String, JsonElement>> model, String err, boolean[] preFeedback,
            boolean[] postFeedback)
    {
        responseCode = 200;

        this.responseType = responseType;
        this.model = model;
        this.err = err;
        this.preFeedback = preFeedback;
        this.postFeedback = postFeedback;
    }

    /**
     * Gives a boolean representing if the methods were found to be equivalent.
     * @return A boolean representing if the methods were found to be equivalent.
     */
    public boolean isEquivalent()
    {
        return responseType == ResponseType.Equiv;
    }

    /**
     * Gives the response code from the Haskell API. A successful query gives 200, a syntax error gives 500 and a connection exception gives
     * 400.
     * @return The response code from the Haskell API.
     */
    public int getResponseCode()
    {
        return responseCode;
    }

    /**
     * Gives the counterexample found by the Haskell API, or null if the methods were found to be equivalent.
     * @return The counterexample found by the Haskell API, or null if the methods were found to be equivalent.
     */
    public Set<Entry<String, JsonElement>> getModel()
    {
        return model;
    }

    /**
     * Gives the error given by the haskell backend.
     * @return The error from the Haskell API.
     */
    public String getErr()
    {
        return err;
    }

    /**
     * Gives the feedback for the precondition from the Haskell API.
     * @return The feedback for the precondition from the Haskell API. [0] True-True [1] True-False [2] False-True [3] False-False
     */
    public boolean[] getPreFeedback()
    {
        return preFeedback;
    }

    /**
     * Gives the feedback for the postcondition from the Haskell API.
     * @return The feedback for the postcondition from the Haskell API. [0] True-True [1] True-False [2] False-True [3] False-False
     */
    public boolean[] getPostFeedback()
    {
        return postFeedback;
    }

    /**
     * Parses a JSON-formatted string into a JsonObject.
     * @param s The JSON-formatted string to be parsed.
     * @return The JsonObject parsed from the given string.
     */
    public static JsonObject parseJsonObject(String s)
    {
        return new GsonBuilder().create().fromJson(s, JsonObject.class);
    }

    /**
     * Parses a JsonObject representing a response from the Haskell API into a Response object.
     * @param o The JsonObject to be parsed, representing a response from the Haskell API.
     * @return The Response object parsed from the given JsonObject.
     */
    public static Response fromJsonObject(JsonObject o)
    {
        JsonElement eResponseType = o.get("responseType");
        JsonElement eModel = o.get("model");
        JsonElement eErr = o.get("err");
        JsonElement eFeedback = o.get("feedback");

        ResponseType responseType = ResponseType.valueOf(eResponseType.getAsString());
        Set<Entry<String, JsonElement>> model = null;
        String err = null;
        boolean[] preFeedback = null;
        boolean[] postFeedback = null;

        if (eModel.isJsonObject())
        {
            model = eModel.getAsJsonObject().entrySet();
        }

        if (eErr != null && !eErr.isJsonNull())
        {
            err = eErr.getAsString();
        }

        if (eFeedback != null && !eFeedback.isJsonNull())
        {
            JsonArray aPreFeedback = eFeedback.getAsJsonObject().get("pre").getAsJsonArray();
            JsonArray aPostFeedback = eFeedback.getAsJsonObject().get("post").getAsJsonArray();

            preFeedback = new boolean[] { aPreFeedback.get(0).getAsBoolean(), aPreFeedback.get(1).getAsBoolean(),
                    aPreFeedback.get(2).getAsBoolean(), aPreFeedback.get(3).getAsBoolean() };
            postFeedback = new boolean[] { aPostFeedback.get(0).getAsBoolean(), aPostFeedback.get(1).getAsBoolean(),
                    aPostFeedback.get(2).getAsBoolean(), aPostFeedback.get(3).getAsBoolean() };
        }

        if (eFeedback != null && eFeedback.isJsonNull() && responseType == ResponseType.Equiv)
        {
            preFeedback = new boolean[] { true, false, false, true };
            postFeedback = new boolean[] { true, false, false, true };
        }

        if (eFeedback != null && eFeedback.isJsonNull() && responseType != ResponseType.Equiv)
        {
            preFeedback = new boolean[] { false, false, false, false };
            postFeedback = new boolean[] { false, false, false, false };
        }

        return new Response(responseType, model, err, preFeedback, postFeedback);
    }

    /**
     * Parses a string response from the Haskell API into a Response object.
     * @param s The string response from the Haskell API.
     * @return The Response object parsed from the given string.
     */
    public static Response fromString(String s)
    {
        return fromJsonObject(parseJsonObject(s));
    }
}
