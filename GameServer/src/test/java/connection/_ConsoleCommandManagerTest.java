/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
package connection;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.java_websocket.WebSocket;

import logger.EmptyLogger;
import logger.FileLogger;
import logger.PrefixLogger;

public class _ConsoleCommandManagerTest
{
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    Server MServer;
    @Mock
    WebSocket MWebSocket;
    @Mock
    ClientManager MClientManager;
    @Mock
    Client MClient;
    @Mock
    Connection MConnection;
    @Mock
    Entry<WebSocket, ClientManager> MEntry;

    private ConsoleCommandManager manager;

    private String ip = "123.456.789";

    @Before
    public void initialize()
    {
        manager = new ConsoleCommandManager(MServer);

        when(MEntry.getValue()).thenReturn(MClientManager);
        when(MEntry.getKey()).thenReturn(MWebSocket);
        when(MClientManager.getClient()).thenReturn(MClient);
        when(MClient.getConnection()).thenReturn(MConnection);
        when(MServer.getEntry(eq(ip))).thenReturn(MEntry);
    }

    /**
     * Test whether the stop command is handled correctly.
     */
    @Test
    public void testStopCommand()
    {
        manager.tryCommand("stop");

        verify(MServer, times(1)).stopServer(anyInt());
    }

    /**
     * Test whether the rename command is handled correctly.
     */
    @Test
    public void testRenameCommand()
    {
        String name = "testName";

        manager.tryCommand("rename " + ip + " " + name);

        verify(MClient, times(1)).setLogger(isA(PrefixLogger.class));
    }

    /**
     * Test whether the disable log command is handled correctly.
     */
    @Test
    public void testDisableLogCommand()
    {
        manager.tryCommand("disablelog " + ip);

        verify(MClient, times(1)).setLogger(isA(EmptyLogger.class));
    }

    /**
     * Test whether the file log command is handled correctly.
     */
    @Test
    public void testFileLogCommand()
    {
        String name = "fileName";

        manager.tryCommand("filelog " + ip + " " + name);

        verify(MClient, times(1)).setLogger(isA(FileLogger.class));
    }

    /**
     * Test whether the send error command is handled correctly.
     */
    @Test
    public void testSendErrorCommand()
    {
        String message = "Test Message";
        manager.tryCommand("senderror " + ip + " " + message);

        verify(MConnection, times(1)).sendError(eq(message));
    }

    /**
     * Test whether the send error all command is handled correctly.
     */
    @Test
    public void testSendErrorAllCommand()
    {
        Map<WebSocket, ClientManager> map = new HashMap<WebSocket, ClientManager>();
        map.put(MWebSocket, MClientManager);

        when(MServer.getConnections()).thenReturn(map);

        String message = "Test Message";
        manager.tryCommand("senderrorall " + message);

        verify(MConnection, times(1)).sendError(eq(message));
    }

    /**
     * Test whether the disconnect command is handled correctly.
     */
    @Test
    public void testDisconnectCommand()
    {
        manager.tryCommand("disconnect " + ip);

        verify(MWebSocket, times(1)).close();
    }

}
