/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
package connection;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;

import org.java_websocket.WebSocket;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.mockito.Mockito.*;
import data.GameSession;
import data.Queries;
import gamelogic.gamestate.GameState;
import gamelogic.gametasks.MainGameTask;
import logger.AbstractLogger;
import utils.Factory;
import utils.Tracker;

public class _ConnectionManagerTest
{
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    WebSocket MSocket;
    @Mock
    Client MClient;
    @Mock
    Tracker MTracker;
    @Mock
    GameSession MGameSession;
    @Mock
    MainGameTask MGameTask;
    @Mock
    GameState MGameState;
    @Mock
    Connection MConnection;
    @Mock
    AbstractLogger MLogger;
    @Mock
    Queries MQueries;

    @Before
    public void init()
    {
        Factory.setMockURL(null);
        Factory.setMockOutputStreamWriter(null);
        Factory.setMockScanner(null);
        Factory.setMockMainGameTask(null);
        Factory.setMockClient(null);
        Factory.setMockClient(MClient);
        Factory.setMockMainGameTask(MGameTask);

        Queries.setQueries(MQueries);

        when(MClient.getLogger()).thenReturn(MLogger);
        when(MClient.getTracker()).thenReturn(MTracker);
        when(MClient.getState()).thenReturn(MGameState);
        when(MClient.getConnection()).thenReturn(MConnection);
        when(MConnection.getSocket()).thenReturn(MSocket);
    }

    /**
     * Test whether correct authentication is handled correctly.
     * @throws Exception Some exception.
     */
    @Test
    public void testCorrectAuthentication() throws Exception
    {
        // Mock methods of Mocks
        when(MGameSession.checkDifference()).thenReturn(true);

        // Mock statics
        when(MQueries.getGameSession(anyString())).thenReturn(MGameSession);

        // Create actual object to test
        ClientManager clientManager = new ClientManager(MSocket);

        clientManager.processMessage("startup token");

        // Verify calls
        verify(MGameState, times(1)).updateGameSession(eq(MGameSession));
        verify(MConnection, times(1)).sendStartUpCorrect();

        // Test if other command is tried.
        clientManager.processMessage("command arguments");

        verify(MGameTask, times(1)).tryCommand(eq("command"), endsWith("arguments"));
    }

    /**
     * Test whether outdated authentication is handled correctly.
     * @throws Exception Some exception.
     */
    @Test
    public void testWrongAuthenticationTimeout() throws Exception
    {

        // Mock methods of Mocks
        when(MGameSession.checkDifference()).thenReturn(false);

        when(MQueries.getGameSession(anyString())).thenReturn(MGameSession);

        InetSocketAddress M1 = mock(InetSocketAddress.class);
        InetAddress M2 = mock(Inet4Address.class);

        when(MSocket.getRemoteSocketAddress()).thenReturn(M1);
        when(M1.getAddress()).thenReturn(M2);
        when(M2.getHostAddress()).thenReturn("Test");

        // Create actual object to test
        ClientManager clientManager = new ClientManager(MSocket);

        clientManager.processMessage("startup token");

        // Verify calls
        verify(MConnection, times(1)).sendStartUpTimeOut();
        verify(MConnection, times(1)).sendStop();
        verify(MSocket, times(1)).close();

        // Test if authentication failed.
        clientManager.processMessage("command arguments");

        verify(MGameTask, times(0)).tryCommand(eq("command"), endsWith("arguments"));
    }

    /**
     * Test whether wrong authentication is handled correctly.
     * @throws Exception
     */
    @Test
    public void testWrongAuthenticationWrongToken() throws Exception
    {
        // Mock methods of Mocks
        when(MGameSession.checkDifference()).thenReturn(false);

        when(MQueries.getGameSession(anyString())).thenReturn(null);

        // Create actual object to test
        ClientManager clientManager = new ClientManager(MSocket);

        clientManager.processMessage("startup token");

        // Verify calls
        verify(MConnection, times(1)).sendStartUpWrong();
        verify(MConnection, times(1)).sendStop();
        verify(MSocket, times(1)).close();

        // Test if authentication failed.
        clientManager.processMessage("command arguments");

        verify(MGameTask, times(0)).tryCommand(eq("command"), endsWith("arguments"));
    }
}
