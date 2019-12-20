/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
package logger;

/**
 * Logger that extends a given logger by adding a prefix to everything that gets logged.
 * @author Ludiscite
 * @version 1.0
 */
public class PrefixLogger implements AbstractLogger
{
    /**
     * The logger to which the prefix is added.
     */
    private AbstractLogger superLogger;
    /**
     * The prefix to be added to everything that gets logged.
     */
    private String prefix;

    /**
     * Constructor of a PrefixLogger.
     * @param superLogger The logger to be extended.
     * @param prefix The prefix to be added to everything that gets logged.
     */
    public PrefixLogger(AbstractLogger superLogger, String prefix)
    {
        this.superLogger = superLogger;
        this.prefix = prefix;
    }

    /**
     * Log a message.
     * @param message The message to be logged.
     */
    @Override
    public void log(String message)
    {
        superLogger.log(prefix + message);
    }

    /**
     * Log an exception.
     * @param exception The exception to be logged.
     */
    @Override
    public void log(Exception exception)
    {
        log("An error occurred:");
        superLogger.log(exception);
    }
}
