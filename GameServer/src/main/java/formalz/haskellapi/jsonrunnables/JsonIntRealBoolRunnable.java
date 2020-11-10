/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
package formalz.haskellapi.jsonrunnables;

import com.google.gson.JsonPrimitive;

/**
 * A JsonRunnable which only runs through integers, real numbers and booleans. It also runs through such primitives inside arrays.
 * @author Ludiscite
 * @version 1.0
 */
public abstract class JsonIntRealBoolRunnable extends JsonPrimitiveRunnable
{
    /**
     * Runs through an integer.
     * @param name The name of the field.
     * @param i The integer through which to run.
     * @param inArray A boolean value representing whether or not the integer was inside an array.
     */
    public void run(String name, int i, boolean inArray)
    {
    }

    /**
     * Runs through a real number.
     * @param name The name of the field.
     * @param f The real number through which to run.
     * @param inArray A boolean value representing whether or not the real number was inside an array.
     */
    public void run(String name, float f, boolean inArray)
    {
    }

    /**
     * Runs through a boolean.
     * @param name The name of the field.
     * @param b The boolean through which to run.
     * @param inArray A boolean value representing whether or not the boolean was inside an array.
     */
    public void run(String name, boolean b, boolean inArray)
    {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run(String name, JsonPrimitive p, boolean inArray)
    {
        if (p.isBoolean())
        {
            run(name, p.getAsBoolean(), inArray);
        }
        else if (p.isNumber())
        {
            Number n = p.getAsNumber();
            int i = n.intValue();
            float f = n.floatValue();
            if (Math.abs((double) f - i) < 0.00001D)
            {
                run(name, i, inArray);
            }
            else
            {
                run(name, f, inArray);
            }
        }
    }
}
