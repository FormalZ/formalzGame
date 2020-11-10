package formalz.connection.manager;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import formalz.connection.Client;
import formalz.connection.Connection;
import formalz.data.GameSession;
import formalz.data.Queries;
import formalz.gamelogic.gamestate.GameState;
import formalz.gamelogic.gametasks.GameTask;

public class CommandBatchRunnerTest {
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    CommandRunnerContext clientState;

    @Mock
    Client client;

    @Mock
    Connection connection;

    @Mock
    GameTask gameTask;

    @Mock
    Queries queries;

    @Mock
    GameSession gameSession;

    @Mock
    GameState state;

    AtomicBoolean authenticated;

    Map<String, String> contextMap;

    Deque<GameCommand> commands;

    @Before
    public void init() {
        authenticated = new AtomicBoolean(false);
        contextMap = new HashMap<>();
        commands = new LinkedList<>();
        when(clientState.getAuthenticated()).thenReturn(authenticated);
        when(clientState.getClient()).thenReturn(client);
        when(clientState.getContextMap()).thenReturn(contextMap);
        when(clientState.getGame()).thenReturn(gameTask);
        when(clientState.getCommands()).thenReturn(commands);
        when(clientState.getQueries()).thenReturn(queries);

        when(queries.getGameSession(anyString())).thenReturn(gameSession);
        when(client.getConnection()).thenReturn(connection);
        when(client.getState()).thenReturn(state);
    }

    /**
     * Test whether correct authentication is handled correctly.
     * 
     * @throws Exception Some exception.
     */
    @Test
    public void notAuthenticatedWithSessionTryAuthenticateInTime() {
        // Given
        String command = "startup";
        String arguments = "token";
        when(gameTask.tryCommand(arguments, arguments)).thenReturn(false);

        when(gameSession.checkDifference()).thenReturn(true);

        GameCommand startup = new GameCommand(command, arguments);
        commands.add(startup);
        CommandBatchRunner cut = new CommandBatchRunner(this.clientState);

        // When
        cut.run();

        // Then
        verify(state, times(1)).updateGameSession(any());

        assertTrue(authenticated.get());

        verify(connection, times(1)).sendStartUpCorrect();
        verify(connection, times(1)).sendSessionId(anyInt(), anyInt());
    }

    @Test
    public void notAuthenticatedWithoutSessionTryAuthenticate() {
        // Given
        String command = "startup";
        String arguments = "token";
        when(gameTask.tryCommand(arguments, arguments)).thenReturn(false);

        when(queries.getGameSession(anyString())).thenReturn(null);

        GameCommand startup = new GameCommand(command, arguments);
        commands.add(startup);
        CommandBatchRunner cut = new CommandBatchRunner(this.clientState);

        // When
        cut.run();

        // Then
        verify(client, times(1)).getConnection();
        verify(connection, times(1)).sendStartUpWrong();
        verify(clientState, times(1)).stop();
    }

    @Test
    public void notAuthenticatedWithSessionTryAuthenticateNotInTime() {
        // Given
        String command = "startup";
        String arguments = "token";
        when(gameTask.tryCommand(arguments, arguments)).thenReturn(false);

        when(gameSession.checkDifference()).thenReturn(false);

        GameCommand startup = new GameCommand(command, arguments);
        commands.add(startup);
        CommandBatchRunner cut = new CommandBatchRunner(this.clientState);

        // When
        cut.run();

        // Then
        verify(connection, times(1)).sendStartUpTimeOut();
        verify(clientState, times(1)).stop();
    }

    @Test
    public void authenticatedTryRunCommand() {
        // Given
        this.authenticated.set(true);

        String command = "command";
        String arguments = "arguments";
        when(this.gameTask.tryCommand(arguments, arguments)).thenReturn(false);

        GameCommand startup = new GameCommand(command, arguments);
        this.commands.add(startup);
        CommandBatchRunner cut = new CommandBatchRunner(this.clientState);

        // When
        cut.run();

        // Then
        verify(this.gameTask, times(1)).tryCommand(eq(command), eq(arguments));
    }

    @Test
    public void notAuthenticatedTryRunCommand() {
        // Given

        String command = "command";
        String arguments = "arguments";
        when(this.gameTask.tryCommand(arguments, arguments)).thenReturn(false);

        GameCommand startup = new GameCommand(command, arguments);
        this.commands.add(startup);
        CommandBatchRunner cut = new CommandBatchRunner(this.clientState);

        // When
        cut.run();

        // Then
    }
}
