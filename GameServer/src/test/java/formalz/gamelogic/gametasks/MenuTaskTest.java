/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
package formalz.gamelogic.gametasks;

import static org.junit.Assert.*;

import formalz.data.Problem;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import formalz.connection.Client;
import formalz.connection.Connection;
import formalz.data.GameSession;
import formalz.data.Queries;
import formalz.gamelogic.gamestate.GameState;
import formalz.utils.Tracker;

public class MenuTaskTest {
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    Client MClient;

    @Mock
    Tracker MTracker;
    @Mock
    Connection MConnection;
    @Mock
    GameState MGameState;
    @Mock
    Queries MQueries;
    @Mock
    GameSession MGameSession;
    @Mock
    Problem MProblem;

    int problemId;

    private int problemLives;

    private int problemMoney;

    private int problemDeadline;

    @Before
    public void initMocks() {
        problemId = 1;
        problemLives = 10;
        problemMoney = 1000;
        problemDeadline = 20;
        when(MProblem.getLives()).thenReturn(problemLives);
        when(MProblem.getMoney()).thenReturn(problemMoney);
        when(MProblem.getDeadline()).thenReturn(problemDeadline);

        when(MClient.getTracker()).thenReturn(MTracker);
        when(MClient.getConnection()).thenReturn(MConnection);
        when(MClient.getState()).thenReturn(MGameState);
        when(MGameState.getGameSession()).thenReturn(MGameSession);
        when(MGameSession.getProblemId()).thenReturn(problemId);
        when(MQueries.getPath(anyInt())).thenReturn("Path");
        when(MQueries.getProblemById(anyInt())).thenReturn(MProblem);
        Queries.setQueries(MQueries);
    }

    /**
     * Test whether an unkown command is handled correctly.
     */
    @Test
    public void testUnknownCommand() {
        MenuTask menuTask = new MenuTask(MClient);

        assertFalse(menuTask.tryCommand("notrealcommand", "notrealarguments"));
    }

    /**
     * Test whether the start command is handled correctly.
     */
    @Test
    public void testStartCommandGood() {
        // Given
        when(MGameState.start()).thenReturn(true);
        when(MQueries.getPath(anyInt())).thenReturn("String");

        MenuTask menuTask = new MenuTask(MClient);

        // When
        boolean startGame = menuTask.tryCommand("startGame", "12345");
        boolean startProblem = menuTask.tryCommand("startProblem", Integer.toString(problemId));

        // Then
        assertTrue(startGame);
        assertTrue(startProblem);

        Queries queries = Queries.getInstance();
        verify(MConnection, times(1)).sendPath(eq(queries.getPath(problemId)));
        verify(MConnection, times(1)).sendProblemData(eq(problemLives), eq(problemMoney), eq(problemDeadline));
        verify(MConnection, times(1)).startNewQuestion(eq(MGameState), anyInt());
    }

    /**
     * Test whether the start command is handled correctly when it is not
     * recognized.
     */
    @Test
    public void testStartCommandBad() {
        when(MGameState.start()).thenReturn(false);

        MenuTask menuTask = new MenuTask(MClient);

        assertTrue(menuTask.tryCommand("startGame", "12345"));
        assertTrue(menuTask.tryCommand("startProblem", "12345"));
        // XXX
    }
}
