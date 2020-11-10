/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
package formalz.haskellapi.jsonrunnables;

import com.google.gson.JsonArray;

import formalz.haskellapi.Response;

/**
 * A JsonRunnable which constructs a string.
 * @author Ludiscite
 * @version 1.0
 */
public class JsonAccumulatorRunnable extends JsonIntRealBoolRunnable
{
    private static JsonAccumulatorRunnable runnable = new JsonAccumulatorRunnable();

    /**
     * Get a model string from a Response.
     * @param response The Response that will be used to make a model string.
     * @return The model string from the Response.
     */
    public static String getModelString(Response response)
    {
        runnable.initialize();
        JsonRunnable.iterateResponse(response, runnable);
        return runnable.toString();
    }

    private StringBuilder stringBuilder;
    private boolean first;
    private boolean firstArrayEntry;

    /**
     * Constructor for a Json accumulator runnable.
     */
    public JsonAccumulatorRunnable()
    {
        initialize();
    }

    /**
     * Initialize the private variables to setup for accumulating.
     */
    private void initialize()
    {
        stringBuilder = new StringBuilder();
        first = true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run(String name, int i, boolean inArray)
    {
        append(name.split(":")[0], Integer.toString(i), inArray);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run(String name, float f, boolean inArray)
    {
        append(name.split(":")[0], Float.toString(f), inArray);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run(String name, boolean b, boolean inArray)
    {
        append(name.split(":")[0], Boolean.toString(b), inArray);
    }

    /**
     * Append a value to the model string that is being built.
     * @param name The name of the value.
     * @param content The value as a string.
     * @param inArray This is true if the value is inside an array.
     */
    private void append(String name, String content, boolean inArray)
    {
        if (inArray)
        {
            if (!firstArrayEntry)
            {
                stringBuilder.append(',');
            }
            firstArrayEntry = false;
            stringBuilder.append(content);
        }
        else
        {
            if (!first)
            {
                stringBuilder.append(';');
            }
            first = false;
            stringBuilder.append('(');
            stringBuilder.append(name);
            stringBuilder.append("=");
            stringBuilder.append(content);
            stringBuilder.append(')');
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run(String name, JsonArray a)
    {
        if (!first)
        {
            stringBuilder.append(';');
        }
        first = false;
        stringBuilder.append('(');
        stringBuilder.append(name);
        stringBuilder.append('=');
        stringBuilder.append('[');
        firstArrayEntry = true;
        super.run(name, a);
        stringBuilder.append(']');
        stringBuilder.append(')');
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return stringBuilder.toString();
    }
}
