/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
package logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.time.Instant;
import java.util.*;

/**
 * A logger for writing to a file.
 * @author Ludiscite
 * @version 1.0
 */
public class FileLogger implements AbstractLogger
{
    private String filename;

    /**
     * Create a logger that logs to a file.
     * @param filename filename of the file to output to.
     */
    public FileLogger(String filename)
    {
        this.filename = filename;
        File file = new File(filename);
        try
        {
            if (!file.createNewFile())
            {
                PrintWriter output = new PrintWriter(file);
                output.append("");
                output.close();
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Log a message.
     * @param message Message to log
     */
    @Override
    public void log(String message)
    {
        Writer output = null;
        ;
        try
        {
            output = new BufferedWriter(new FileWriter(filename, true));
            output.append(Date.from(Instant.now()).toString() + " ; ");
            output.append(message);
            output.append("\r\n");
            output.flush();
            output.close();
        }
        catch (IOException e)
        {
            System.out.println("IOExceptIO has occured");
            e.printStackTrace();
        }
    }

    /**
     * Log an exception.
     * @param exception Exception to log.
     */
    @Override
    public void log(Exception exception)
    {
        Writer output = null;
        ;
        try
        {
            output = new BufferedWriter(new FileWriter(filename, true));
            output.append(exception.getMessage());
            output.close();
        }
        catch (IOException e)
        {
            System.out.println("IOExceptio has occured");
            e.printStackTrace();
        }
    }
}