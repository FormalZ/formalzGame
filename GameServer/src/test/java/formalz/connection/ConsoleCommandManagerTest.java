/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
package formalz.connection;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.mockito.Mockito.*;

public class ConsoleCommandManagerTest {
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    Server server;

    String clientIp = "127.0.0.1";

    ConsoleCommandManager cut;

    @Before
    public void setUp() {
        cut = new ConsoleCommandManager(server);
    }

    /**
     * Test whether the stop command is handled correctly.
     */
    @Test
    public void testStopCommand() {
        // Given

        // When
        cut.tryCommand("stop");

        // Then
        verify(server, times(1)).stopServer(anyInt());
    }

    /**
     * Test whether the rename command is handled correctly.
     */
    @Test
    public void testRenameCommand() {
        // Given

        // When
        String name = "testName";

        cut.tryCommand(String.format("rename %s %s", clientIp, name));

        // Then
        verify(server, times(1)).renameSession(eq(clientIp), eq(name));
    }

    /**
     * Test whether the disable log command is handled correctly.
     */
    @Test
    public void testDisableLogCommand() {
        // Given

        // When
        cut.tryCommand(String.format("disablelog %s", clientIp));

        // Then
        verify(server, times(1)).disableSessionLog(eq(clientIp));
    }

    /**
     * Test whether the file log command is handled correctly.
     */
    @Test
    public void testFileLogCommand() {
        // Given
        String name = "fileName";

        // When
        cut.tryCommand(String.format("filelog %s %s", clientIp, name));

        // Then
        verify(server, times(1)).enableFileSessionLog(eq(clientIp));
    }

    /**
     * Test whether the send error command is handled correctly.
     */
    @Test
    public void testSendErrorCommand() {
        // Given
        String message = "Test Message";
        
        // When
        cut.tryCommand(String.format("senderror %s %s", clientIp, message));

        verify(server, times(1)).sendError(eq(clientIp), eq(message));
    }

    /**
     * Test whether the send error all command is handled correctly.
     */
    @Test
    public void testSendErrorAllCommand() {
        // Given
        String message = "Test Message";

        // When
        cut.tryCommand(String.format("senderrorall %s", message));

        // Then
        verify(server, times(1)).sendErrorToAll(eq(message));
    }

    /**
     * Test whether the disconnect command is handled correctly.
     */
    @Test
    public void testDisconnectCommand() {
        // Given

        // When
        cut.tryCommand(String.format("disconnect %s", clientIp));

        // Then
        verify(server, times(1)).disconnectSession(eq(clientIp));;
    }

}
