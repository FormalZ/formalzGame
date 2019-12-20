/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
package logger;

import java.io.PrintStream;

/**
 * Logger that logs everything to a given PrintStream.
 * @author Ludiscite
 * @version 1.0
 */
public class PrintStreamLogger implements AbstractLogger
{
    /**
     * The PrintStream to which to log.
     */
    private PrintStream out;

    /**
     * Constructor of a PrintStreamLogger.
     * @param out The PrintStream to which to be logged.
     */
    public PrintStreamLogger(PrintStream out)
    {
        this.out = out;
    }

    /**
     * Log a message.
     * @param message The message to be logged.
     */
    @Override
    public void log(String message)
    {
        out.println(message);
    }

    /**
     * Log an exception.
     * @param exception The exception to be logged.
     */
    @Override
    public void log(Exception exception)
    {
        exception.printStackTrace(out);
    }
}
