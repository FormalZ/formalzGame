/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
package logger;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

public class _PrefixLoggerTest
{
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    AbstractLogger MLogger;

    /**
     * Test whether a string is logged correctly.
     */
    @Test
    public void testLogString()
    {
        String prefix = "prefix:";
        String message = "testMessage";

        AbstractLogger logger = new PrefixLogger(MLogger, prefix);

        logger.log(message);

        verify(MLogger, times(1)).log(prefix + message);
    }
    
    /**
     * Test whether an error is logged correctly.
     */
    @Test
    public void testLogException()
    {
        Exception e = mock(Exception.class);

        String prefix = "prefix:";

        AbstractLogger logger = new PrefixLogger(MLogger, prefix);

        logger.log(e);

        verify(MLogger, times(1)).log(prefix + "An error occurred:");
        verify(MLogger, times(1)).log(e);
    }

}
