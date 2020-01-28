/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
package logger;

import java.io.PrintStream;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

public class _PrintStreamLoggerTest
{
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    PrintStream MPrintStream;

    /**
     * Test whether a string is logged correctly.
     */
    @Test
    public void testLogString()
    {
        AbstractLogger logger = new PrintStreamLogger(MPrintStream);

        String testMessage = "testMessage";

        logger.log(testMessage);

        verify(MPrintStream, times(1)).println(testMessage);
    }

    /**
     * Test whether an exception is logged correctly.
     */
    @Test
    public void testLogException()
    {
        AbstractLogger logger = new PrintStreamLogger(MPrintStream);

        Exception e = mock(Exception.class);

        logger.log(e);

        verify(e, times(1)).printStackTrace(MPrintStream);
    }
}
