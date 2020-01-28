/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
package haskellapi.jsonrunnables;

import logger.Logger;

/**
 * A JsonRunnable which logs all integers, real numbers and booleans. It also logs such primitives inside arrays.
 * @author Ludiscite
 * @version 1.0
 */
public class JsonLoggerRunnable extends JsonIntRealBoolRunnable
{
    /**
     * {@inheritDoc}
     */
    @Override
    public void run(String name, int i, boolean inArray)
    {
        Logger.log("Integer : " + name + " : " + i);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run(String name, float f, boolean inArray)
    {
        Logger.log("Float : " + name + " : " + f);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run(String name, boolean b, boolean inArray)
    {
        Logger.log("Boolean : " + name + " : " + b);
    }
}
