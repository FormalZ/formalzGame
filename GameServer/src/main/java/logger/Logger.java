/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
package logger;

/**
 * The standard and static logger. Currently implemented to log everything on console.
 * @author Ludiscite
 * @version 1.0
 */
public class Logger
{
    /**
     * The standard logger.
     */
    private static final AbstractLogger logger = new PrintStreamLogger(System.out);

    /**
     * Log a message on the standard logger.
     * @param message The message to be logged.
     */
    public static void log(String message)
    {
        logger.log(message);
    }

    /**
     * Log an exception on the standard logger.
     * @param exception The exception to be logged.
     */
    public static void log(Exception exception)
    {
        logger.log(exception);
    }

    /**
     * Get the standard logger.
     * @return The standard logger.
     */
    public static AbstractLogger getLogger()
    {
        return logger;
    }
}
