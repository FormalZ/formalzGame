/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
package gamelogic.gametasks;

import static org.junit.Assert.*;

import data.Problem;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import connection.Client;
import connection.Connection;
import data.GameSession;
import data.Queries;
import gamelogic.gamestate.GameState;
import logger.AbstractLogger;
import utils.Tracker;

public class _MenuTaskTest
{
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    Client MClient;
    @Mock
    AbstractLogger MLogger;
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

    @Before
    public void initMocks()
    {
        when(MClient.getLogger()).thenReturn(MLogger);
        when(MClient.getTracker()).thenReturn(MTracker);
        when(MClient.getConnection()).thenReturn(MConnection);
        when(MClient.getState()).thenReturn(MGameState);
        when(MGameState.getGameSession()).thenReturn(MGameSession);
        when(MGameSession.getProblemId()).thenReturn(1);
        when(MQueries.getPath(anyInt())).thenReturn("Path");
        when(MQueries.getProblemById(anyInt())).thenReturn(MProblem);
        Queries.setQueries(MQueries);
    }

    /**
     * Test whether an unkown command is handled correctly.
     */
    @Test
    public void testUnknownCommand()
    {
        MenuTask menuTask = new MenuTask(MClient);

        assertFalse(menuTask.tryCommand("notrealcommand", "notrealarguments"));
    }

    /**
     * Test whether the start command is handled correctly.
     */
    @Test
    public void testStartCommandGood()
    {
        when(MGameState.start()).thenReturn(true);
        when(MQueries.getPath(anyInt())).thenReturn("String");
        MenuTask menuTask = new MenuTask(MClient);


        assertTrue(menuTask.tryCommand("startGame", "nothing"));
        assertTrue(menuTask.tryCommand("startProblem", "nothing"));

        verify(MLogger, times(1)).log("Sending path");
        verify(MLogger, times(1)).log("Game started");
        int problemId = MClient.getState().getGameSession().getProblemId();
        Queries queries = Queries.getInstance();
        verify(MConnection, times(1)).sendPath(queries.getPath(problemId));
        Problem problem = queries.getProblemById(problemId);
        verify(MConnection, times(1)).sendProblemData(problem.getLives(), problem.getMoney(), problem.getDeadline());
        verify(MConnection, times(1)).startNewQuestion(MGameState, MClient.getLogger(), anyInt());
    }

    /**
     * Test whether the start command is handled correctly when it is not recognized.
     */
    @Test
    public void testStartCommandBad()
    {
        when(MGameState.start()).thenReturn(false);

        MenuTask menuTask = new MenuTask(MClient);

        assertTrue(menuTask.tryCommand("startGame", "nothing"));
        assertTrue(menuTask.tryCommand("startProblem", "nothing"));

        verify(MLogger, times(1)).log("Invalid start time");
    }
}
