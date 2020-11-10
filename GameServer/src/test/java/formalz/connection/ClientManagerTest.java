/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
package formalz.connection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.empty;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.InetAddress;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;

import org.java_websocket.WebSocket;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import formalz.data.Queries;
import formalz.gamelogic.gametasks.MainGameTask;
import formalz.utils.Tracker;

public class ClientManagerTest {
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    Client client;

    @Mock
    MainGameTask gameTask;

    @Mock
    Timer timer;

    @Mock
    ExecutorService commandRunner;

    @Mock
    Queries queries;

    @Mock
    Connection connection;

    @Mock
    WebSocket socket;

    @Mock
    Tracker tracker;

    InetAddress address;

    String sessionId;

    @Captor
    ArgumentCaptor<TimerTask> timerArgument;

    @Before
    public void init() throws Exception {
        this.address = InetAddress.getByName("127.0.0.1");
        this.sessionId = "sessionId";
        
        when(client.getConnection()).thenReturn(connection);
        when(client.getTracker()).thenReturn(tracker);
        when(connection.getSocket()).thenReturn(socket);
    }

    @Test
    public void authenticateInTime() {
        // Given

        // When
        ClientManager cut = new ClientManager(address, sessionId, client, gameTask, timer, commandRunner, queries);
        
        // Just to avoid unused warn
        cut.hashCode();

        // Then
        verify(timer, times(1)).schedule(any(), anyLong());
    }

    @Test
    public void authenticateNotInTime() {
        // Given

        // When
        ClientManager cut = new ClientManager(address, sessionId, client, gameTask,timer, commandRunner, queries);

        verify(timer).schedule(timerArgument.capture(), anyLong());
        timerArgument.getValue().run();

        // Then
        verify(timer, times(1)).schedule(any(), anyLong());
        verify(connection, times(1)).sendStartUpTimeOut();

        verify(connection, times(1)).sendStop();
        verify(socket, times(1)).close();

        verify(tracker, times(1)).stop();
        assertThat(cut.getCommands(), is(empty()));
    }

    @Test
    public void sendError() {
        // Given
        String message="message";

        // When
        ClientManager cut = new ClientManager(address, sessionId, client, gameTask, timer, commandRunner, queries);
        
        // Just to avoid unused warn
        cut.sendError(message);

        // Then
        verify(connection, times(1)).sendError(eq(message));
    }
}
