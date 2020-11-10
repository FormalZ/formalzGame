/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
package formalz.connection;

import org.junit.Test;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import formalz.data.Queries;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Timer;
import java.util.concurrent.ExecutorService;

public class ServerTest {
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    WebSocket socket;

    @Mock
    ClientHandshake clientHandshake;

    InetAddress address;

    InetSocketAddress socketAddress;

    @Mock
    ClientManagerFactory clientManagerFactory;

    @Mock
    ClientManager clientManager;

    @Mock
    ClientFactory clientFactory;

    @Mock
    Client client;

    @Mock
    Timer timer;

    @Mock
    ExecutorService executorService;

    @Mock
    Queries queries;
    
    @Before
    public void setUp() throws Exception {
        address = InetAddress.getByName("127.0.0.1");
        socketAddress = new InetSocketAddress(address, 8080);

        when(socket.getRemoteSocketAddress()).thenReturn(socketAddress);

        when(clientManagerFactory.create(any(), any(), any(), any(), any(), any(), any())).thenReturn(clientManager);
        when(clientFactory.constructClient(any())).thenReturn(client);

        when(clientManager.getIp()).thenReturn(address);
        when(clientManager.getDebugLog()).thenReturn(ClientManager.DebugLogDestination.NONE);
        when(clientManager.getSessionName()).thenReturn("sessionName");

    }

    @After
    public void tearDown() {
    }

    /**
     * Test whether opening a connection with the server works correct.
     */
    @Test
    public void onOpen() {
        // Given

        Server server = new Server(clientManagerFactory, clientFactory, timer, executorService, queries);

        // When
        server.onOpen(socket, clientHandshake);

        // Then
        assertTrue(server.getServerConnections().containsKey(socket));
    }

    /**
     * Test whether a connection to the server is closed correctly.
     */
    @Test
    public void onClose() {
        // Given
        Server server = new Server(clientManagerFactory, clientFactory, timer, executorService, queries);
        server.onOpen(socket, clientHandshake);

        // When
        server.onClose(socket, 500, "testing", true);

        // Then
        assertFalse(server.getServerConnections().containsKey(socket));
    }

    /**
     * Test whether a message from the client is processed correctly.
     */
    @Test
    public void onMessage() {
        // Given
        Server server = new Server(clientManagerFactory, clientFactory, timer, executorService, queries);
        server.onOpen(socket, clientHandshake);

        // When
        String message = "message";
        server.onMessage(socket, message);

        // Then
        verify(clientManager, times(1)).processMessage(message);
    }

    /**
     * Test whether an error with a connection is handled correctly.
     */
    @Test
    public void onError() {
        // Given
        Server server = new Server(clientManagerFactory, clientFactory, timer, executorService, queries);
        server.onOpen(socket, clientHandshake);

        // When
        Exception e = new Exception();
        server.onError(socket, e);

        // Then
        assertFalse(server.getServerConnections().containsKey(socket));
    }

    /**
     * Test whether stopping the server is handled correctly.
     */
    @Test
    public void stopServer() {
        // Given
        Server server = new Server(clientManagerFactory, clientFactory, timer, executorService, queries);
        server.onOpen(socket, clientHandshake);

        // When
        server.stopServer(1);

        // Then
        assertFalse(server.getServerConnections().containsKey(socket));
    }

    /**
     * Test whether the rename command is handled correctly.
     */
    @Test
    public void renameSession() {
        // Given
        Server server = new Server(clientManagerFactory, clientFactory, timer, executorService, queries);
        server.onOpen(socket, clientHandshake);

        String newSessionName="newSessionName";

        // When
        server.renameSession(address.getHostAddress(), newSessionName);

        // Then
        verify(clientManager).setSessionName(eq(newSessionName));
    }

    /**
     * Test whether the disable log command is handled correctly.
     */
    @Test
    public void disableSessionLog() {
        // Given
        Server server = new Server(clientManagerFactory, clientFactory, timer, executorService, queries);
        server.onOpen(socket, clientHandshake);

        // When
        server.disableSessionLog(address.getHostAddress());

        // Then
        verify(clientManager).setDebugLog(eq(ClientManager.DebugLogDestination.NONE));
    }

    /**
     * Test whether the file log command is handled correctly.
     */
    @Test
    public void testFileLogCommand() {
        // Given
        Server server = new Server(clientManagerFactory, clientFactory, timer, executorService, queries);
        server.onOpen(socket, clientHandshake);

        // When
        server.enableFileSessionLog(address.getHostAddress());

        // Then
        verify(clientManager).setDebugLog(eq(ClientManager.DebugLogDestination.FILE));
    }

    /**
     * Test whether the send error command is handled correctly.
     */
    @Test
    public void sendErrorToSession() {
        // Given
        Server server = new Server(clientManagerFactory, clientFactory, timer, executorService, queries);
        server.onOpen(socket, clientHandshake);

        String message="message";

        // When
        server.sendError(address.getHostAddress(), message);

        // Then
        verify(clientManager).sendError(eq(message));
    }

    /**
     * Test whether the send error all command is handled correctly.
     */
    @Test
    public void sendErroToAllSessions() {
        // Given
        Server server = new Server(clientManagerFactory, clientFactory, timer, executorService, queries);
        server.onOpen(socket, clientHandshake);

        String message="message";

        // When
        server.sendError(address.getHostAddress(), message);

        // Then
        verify(clientManager).sendError(eq(message));
    }
    /**
     * Test whether the disconnect command is handled correctly.
     */
    @Test
    public void testDisconnectCommand() {
        // Given
        Server server = new Server(clientManagerFactory, clientFactory, timer, executorService, queries);
        server.onOpen(socket, clientHandshake);

        // When
        server.disconnectSession(address.getHostAddress());

        // Then
        verify(clientManager).stop();
        assertNull(server.getEntry(address.getHostAddress()));
    }
}
