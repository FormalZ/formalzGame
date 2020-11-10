/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
package formalz.haskellapi.jsonrunnables;

import java.util.Iterator;

import com.google.gson.*;

/**
 * A JsonRunnable which only runs through primitives and primitives inside arrays.
 * @author Ludiscite
 * @version 1.0
 */
public abstract class JsonPrimitiveRunnable extends JsonPrimitiveArrayRunnable
{
    /**
     * Runs through a primitive.
     * @param name The name of the field.
     * @param p The primitive through which to run.
     * @param inArray A boolean value representing whether or not the primitive was inside an array.
     */
    public abstract void run(String name, JsonPrimitive p, boolean inArray);

    /**
     * {@inheritDoc}
     */
    @Override
    public void run(String name, JsonPrimitive p)
    {
        run(name, p, false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run(String name, JsonArray a)
    {
        Iterator<JsonElement> it = a.iterator();
        while (it.hasNext())
        {
            run(name, it.next().getAsJsonPrimitive(), true);
        }
    }
}
