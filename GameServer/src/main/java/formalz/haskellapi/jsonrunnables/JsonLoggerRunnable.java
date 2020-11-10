/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
package formalz.haskellapi.jsonrunnables;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A JsonRunnable which logs all integers, real numbers and booleans. It also
 * logs such primitives inside arrays.
 * 
 * @author Ludiscite
 * @version 1.0
 */
public class JsonLoggerRunnable extends JsonIntRealBoolRunnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonLoggerRunnable.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public void run(String name, int i, boolean inArray) {
        LOGGER.debug("Integer '{}': {}", name, i);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run(String name, float f, boolean inArray) {
        LOGGER.debug("Float '{}': ",name, f);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run(String name, boolean b, boolean inArray) {
        LOGGER.debug("Boolean '{}': ", name, b);
    }
}
