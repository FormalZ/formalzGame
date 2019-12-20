/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
package logger;

/**
 * An interface for loggers.
 * @author Ludiscite
 * @version 1.0
 */
public interface AbstractLogger
{
    /**
     * Log a message.
     * @param message The message to be logged.
     */
    public void log(String message);

    /**
     * Log an exception.
     * @param exception The exception to be logged.
     */
    public void log(Exception exception);
}
