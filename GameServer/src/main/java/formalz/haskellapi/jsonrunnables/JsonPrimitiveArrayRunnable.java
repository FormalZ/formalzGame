/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
package formalz.haskellapi.jsonrunnables;

import java.util.Map.Entry;

import com.google.gson.*;

/**
 * A JsonRunnable which only runs through primitives and arrays.
 * @author Ludiscite
 * @version 1.0
 */
public abstract class JsonPrimitiveArrayRunnable implements JsonRunnable
{
    /**
     * Runs through a primitive.
     * @param name The name of the field.
     * @param p The primitive through which to run.
     */
    public abstract void run(String name, JsonPrimitive p);

    /**
     * Runs through an array.
     * @param name The name of the field.
     * @param a The array through which to run.
     */
    public abstract void run(String name, JsonArray a);

    /**
     * {@inheritDoc}
     */
    @Override
    public void run(Entry<String, JsonElement> e)
    {
        if (e.getValue().isJsonPrimitive())
            run(e.getKey(), e.getValue().getAsJsonPrimitive());
        else if (e.getValue().isJsonArray())
            run(e.getKey(), e.getValue().getAsJsonArray());
    }
}
