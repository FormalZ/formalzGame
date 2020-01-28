/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
package logger;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.junit.Test;
import static org.mockito.Mockito.*;

public class _FileLoggerTest
{

    /**
     * Test whether a string is logged correctly.
     */
    @Test
    public void testLogString()
    {
        String fileName = "fileLoggerTest.txt";
        AbstractLogger logger = new FileLogger(fileName);

        String line1 = "line1";
        String line2 = "line2";

        logger.log(line1);
        logger.log(line2);

        try
        {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            String line = reader.readLine();
            String[] split = line.split(" ; ");
            assertEquals(line1, split[1]);
            line = reader.readLine();
            split = line.split(" ; ");
            assertEquals(line2, split[1]);
            reader.close();
        }
        catch (Exception e)
        {
            fail("Log file reading failed");
        }

        File file = new File(fileName);
        file.delete();
    }

    /**
     * Test whether an exception is logged correctly.
     */
    @Test
    public void testLogException()
    {
        Exception MException = mock(Exception.class);
        String errorMessage = "ErrorMessage";
        when(MException.getMessage()).thenReturn("ErrorMessage");

        String fileName = "fileLoggerTest.txt";
        AbstractLogger logger = new FileLogger(fileName);

        logger.log(MException);

        try
        {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            String line = reader.readLine();
            assertEquals(errorMessage, line);
            reader.close();
        }
        catch (Exception e)
        {
            fail("Log file reading failed");
        }

        File file = new File(fileName);
        file.delete();
    }

}
