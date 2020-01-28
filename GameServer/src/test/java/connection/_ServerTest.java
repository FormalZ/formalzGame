/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
package connection;

import org.junit.Test;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.net.InetAddress;
import java.net.InetSocketAddress;

import utils.Factory;

public class _ServerTest
{
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    WebSocket MWebSocket;
    @Mock
    ClientHandshake MClientHandShake;
    @Mock
    ClientManager MClientManager;
    @Mock
    InetSocketAddress MSocketAddress;
    @Mock
    InetAddress MAddress;

    @Before
    public void initMocks()
    {
        Factory.setMockClientManager(MClientManager);

        when(MWebSocket.getRemoteSocketAddress()).thenReturn(MSocketAddress);
        when(MSocketAddress.getAddress()).thenReturn(MAddress);
        when(MAddress.getHostAddress()).thenReturn("address");
    }

    @After
    public void finish()
    {
        Factory.setMockClientManager(null);
    }

    /**
     * Test whether opening a connection with the server works correct.
     */
    @Test
    public void testOnOpen()
    {
        Server server = new Server(420);

        server.onOpen(MWebSocket, MClientHandShake);

        assertTrue(server.getConnections().containsKey(MWebSocket));
    }

    /**
     * Test whether a connection to the server is closed correctly.
     */
    @Test
    public void testOnClose()
    {
        Server server = new Server(420);
        server.onOpen(MWebSocket, MClientHandShake);

        assertTrue(server.getConnections().containsKey(MWebSocket));

        server.onClose(MWebSocket, 500, "testing", true);

        assertFalse(server.getConnections().containsKey(MWebSocket));
    }

    /**
     * Test whether a message from the client is processed correctly.
     */
    @Test
    public void testOnMessage()
    {
        Server server = new Server(420);
        server.onOpen(MWebSocket, MClientHandShake);

        String message = "message";

        server.onMessage(MWebSocket, message);

        verify(MClientManager, times(1)).processMessage(message);
    }

    /**
     * Test whether an error with a connection is handled correctly.
     */
    @Test
    public void testOnError()
    {
        Server server = new Server(420);
        server.onOpen(MWebSocket, MClientHandShake);

        assertTrue(server.getConnections().containsKey(MWebSocket));

        Exception e = new Exception();

        server.onError(MWebSocket, e);

        assertFalse(server.getConnections().containsKey(MWebSocket));
    }

    /**
     * Test whether stopping the server is handled correctly.
     */
    @Test
    public void testStopServerCorrect()
    {
        Server server = new Server(420);
        server.onOpen(MWebSocket, MClientHandShake);

        assertTrue(server.getConnections().containsKey(MWebSocket));

        server.stopServer(1);

        assertFalse(server.getConnections().containsKey(MWebSocket));
    }
}
