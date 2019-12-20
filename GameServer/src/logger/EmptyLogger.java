/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
package logger;

/**
 * A logger that does nothing.
 * @author Ludiscite
 * @version 1.0
 */
public class EmptyLogger implements AbstractLogger
{

    /**
     * Log nothing.
     * @param message Message to not log.
     */
    @Override
    public void log(String message)
    {
        // Do nothing.
    }

    /**
     * Log nothing.
     * @param exception Exception to not log.
     */
    @Override
    public void log(Exception exception)
    {
        // Do nothing.
    }
}