/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
package formalz.gamelogic;

import static org.junit.Assert.*;

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
import formalz.data.LocalStatistic;
import formalz.data.Problem;
import formalz.data.Queries;
import formalz.gamelogic.gamestate.AdaptiveDifficulty;
import formalz.gamelogic.gamestate.CheatDetector;
import formalz.gamelogic.gamestate.GameState;
import formalz.gamelogic.gamestate.ProblemState;
import formalz.gamelogic.gametasks.GameLogicTask;
import formalz.gamelogic.gametasks.GameTask;
import formalz.haskellapi.Response;
import formalz.utils.Tracker;

public class GameLogicTaskTest {
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    Client MClient;
    @Mock
    GameState MGameState;
    @Mock
    Tracker MTracker;
    @Mock
    ProblemState MProblemState;
    @Mock
    AdaptiveDifficulty MAdaptiveDifficulty;
    @Mock
    CheatDetector MCheatDetector;
    @Mock
    Queries MQueries;
    @Mock
    GameSession MGameSession;

    @Mock
    Connection MConnection;
    @Mock
    Problem MProblem;
    @Mock
    Response MResponse;

    @Before
    public void initMocks() {
        when(MClient.getTracker()).thenReturn(MTracker);
        when(MClient.getState()).thenReturn(MGameState);
        when(MGameState.getProblemState()).thenReturn(MProblemState);
        when(MGameState.getAdaptiveDifficulty()).thenReturn(MAdaptiveDifficulty);
        when(MGameState.getGameSession()).thenReturn(MGameSession);
        when(MGameState.getCheatDetector()).thenReturn(MCheatDetector);
        when(MClient.getConnection()).thenReturn(MConnection);
    }

    @Test
    public void testUnknownCommand() {
        GameTask task = new GameLogicTask(MClient);

        assertFalse(task.tryCommand("notrealcommand", "notrealarguments"));
    }

    @Test
    public void testSubmitPreCommandCorrect() {
        when(MProblemState.getPreFeedback()).thenReturn(new boolean[] { true, false, true, false });
        when(MProblemState.generatePrePercentages()).thenReturn(new float[] { 0.25f, 0.25f, 0.25f, 0.25f });
        when(MProblemState.getLastPreCondition()).thenReturn("precondition");
        when(MGameState.getProblem()).thenReturn(MProblem);

        when(MProblem.comparePre(anyString())).thenReturn(MResponse);

        when(MResponse.getResponseCode()).thenReturn(200);

        when(MResponse.isEquivalent()).thenReturn(true);

        GameLogicTask task = new GameLogicTask(MClient);

        String arguments = "arguments";
        task.tryCommand("submitPre", arguments);

        verify(MProblemState, times(1)).setLastPreCondition(eq(arguments));
        verify(MConnection, times(1)).sendPreResult(any(), any());
        verify(MProblemState, times(1)).setPreFeedback(any());
        verify(MProblemState, times(1)).setPreCorrect(eq(true));
    }

    @Test
    public void testSubmitPreCommandIncorrect() {
        when(MProblemState.getPreFeedback()).thenReturn(new boolean[] { true, false, true, false });
        when(MProblemState.generatePrePercentages()).thenReturn(new float[] { 0.25f, 0.25f, 0.25f, 0.25f });
        when(MProblemState.getLastPreCondition()).thenReturn("precondition");
        when(MGameState.getProblem()).thenReturn(MProblem);

        when(MProblem.comparePre(anyString())).thenReturn(MResponse);

        when(MResponse.getResponseCode()).thenReturn(500);

        GameLogicTask task = new GameLogicTask(MClient);

        String arguments = "arguments";
        task.tryCommand("submitPre", arguments);

        verify(MProblemState, times(1)).setLastPreCondition(eq(arguments));
        verify(MConnection, times(1)).sendPreResult(any(), any());
    }

    @Test
    public void testSubmitPostCommandIncorrect() {
        when(MProblemState.getPostFeedback()).thenReturn(new boolean[] { true, false, true, false });
        when(MProblemState.generatePostPercentages()).thenReturn(new float[] { 0.25f, 0.25f, 0.25f, 0.25f });
        when(MProblemState.getLastPostCondition()).thenReturn("postcondition");
        when(MGameState.getProblem()).thenReturn(MProblem);

        when(MProblem.comparePost(anyString())).thenReturn(MResponse);

        when(MResponse.getResponseCode()).thenReturn(400);

        GameLogicTask task = new GameLogicTask(MClient);

        String arguments = "arguments";
        task.tryCommand("submitPost", arguments);

        verify(MProblemState, times(1)).setLastPostCondition(eq(arguments));
        verify(MConnection, times(1)).sendPostResult(any(), any());

    }

    @Test
    public void testSubmitPostCommandCorrect() {
        when(MProblemState.getPostFeedback()).thenReturn(new boolean[] { true, false, true, false });
        when(MProblemState.generatePostPercentages()).thenReturn(new float[] { 0.25f, 0.25f, 0.25f, 0.25f });
        when(MProblemState.getLastPostCondition()).thenReturn("postcondition");
        when(MGameState.getProblem()).thenReturn(MProblem);

        when(MProblem.comparePost(anyString())).thenReturn(MResponse);

        when(MResponse.getResponseCode()).thenReturn(200);

        when(MResponse.isEquivalent()).thenReturn(true);

        GameLogicTask task = new GameLogicTask(MClient);

        String arguments = "arguments";
        task.tryCommand("submitPost", arguments);

        verify(MProblemState, times(1)).setLastPostCondition(eq(arguments));
        verify(MConnection, times(1)).sendPostResult(any(), any());
        verify(MProblemState, times(1)).setPostFeedback(any());
        verify(MProblemState, times(1)).setPostCorrect(eq(true));

    }

    @Test
    public void testWaveDoneCommandIncorrect() {
        when(MGameState.getProblem()).thenReturn(MProblem);
        when(MProblemState.problemCorrect()).thenReturn(false);
        when(MGameState.isFinished()).thenReturn(false);
        when(MProblemState.generatePrePercentages()).thenReturn(new float[] { 0.25f, 0.25f, 0.25f, 0.25f });
        when(MProblemState.generatePostPercentages()).thenReturn(new float[] { 0.25f, 0.25f, 0.25f, 0.25f });
        when(MAdaptiveDifficulty.getSpecialSparkSpawnPercentage()).thenReturn(new float[] { 0.01f });
        GameLogicTask task = new GameLogicTask(MClient);

        task.tryCommand("waveDone",
                "0;100;5050;40;5;[5, 0, 0, 5];[1, 2, 3, 4];[5, 6, 7, 8];[9, 10, 11, 12];[9, 12];[10, 11];[11, 12];[9, 10]");

        verify(MGameState, times(1)).setScore(anyInt());
        verify(MConnection, times(1)).sendSpawnWave(anyInt(), anyInt(), anyInt(), anyInt(), notNull(), notNull(),
                notNull());
    }

    @Test
    public void testWaveDoneCommandCorrectFinalProblem() {
        Queries MQueries = mock(Queries.class);
        Queries.setQueries(MQueries);
        GameSession MGameSession = mock(GameSession.class);
        LocalStatistic MLocalStatistic = mock(LocalStatistic.class);

        when(MGameState.getProblem()).thenReturn(MProblem);
        when(MGameState.isFinished()).thenReturn(true);
        when(MProblemState.problemCorrect()).thenReturn(true);
        when(MGameState.getGameSession()).thenReturn(MGameSession);
        when(MGameState.getTeacherProblemId()).thenReturn(0);
        when(MGameState.getScore()).thenReturn(100);
        when(MGameState.getCurrentStatistic()).thenReturn(MLocalStatistic);

        when(MGameSession.getUserId()).thenReturn(0);

        GameLogicTask task = new GameLogicTask(MClient);

        task.tryCommand("waveDone",
                "0;100;5050;40;5;[5, 0, 0, 5];[1, 2, 3, 4];[5, 6, 7, 8];[9, 10, 11, 12];[9, 12];[10, 11];[11, 12];[9, 10]");

        verify(MGameState, times(1)).setScore(anyInt());
        verify(MConnection, times(1)).sendFinish();
    }

    @Test
    public void testWaveDoneCommandCorrectIntermediateProblem() {
        Queries MQueries = mock(Queries.class);
        Queries.setQueries(MQueries);
        GameSession MGameSession = mock(GameSession.class);
        LocalStatistic MLocalStatistic = mock(LocalStatistic.class);

        when(MGameState.getProblem()).thenReturn(MProblem);
        when(MGameState.isFinished()).thenReturn(false);
        when(MProblemState.problemCorrect()).thenReturn(true);
        when(MGameState.getGameSession()).thenReturn(MGameSession);
        when(MGameState.getTeacherProblemId()).thenReturn(0);
        when(MGameState.getScore()).thenReturn(100);
        when(MGameState.getCurrentStatistic()).thenReturn(MLocalStatistic);

        when(MGameSession.getUserId()).thenReturn(0);

        GameLogicTask task = new GameLogicTask(MClient);

        task.tryCommand("waveDone",
                "0;100;5050;40;5;[5, 0, 0, 5];[1, 2, 3, 4];[5, 6, 7, 8];[9, 10, 11, 12];[9, 12];[10, 11];[11, 12];[9, 10]");

        verify(MGameState, times(1)).setScore(anyInt());
        verify(MConnection, times(1)).startNewQuestion(notNull(), anyInt());
    }

    @Test
    public void testFinalScoreNotFinished() {
        GameLogicTask task = new GameLogicTask(MClient);
        when(MGameState.isFinished()).thenReturn(false);

        String arguments = "40";
        task.tryCommand("finalScore", arguments);

        verify(MGameState, never()).setScore(anyInt());

    }

    @Test
    public void testFinalScoreFinished() {
        Queries.setQueries(MQueries);

        GameLogicTask task = new GameLogicTask(MClient);
        when(MGameState.isFinished()).thenReturn(true);
        when(MGameSession.getUserId()).thenReturn(0);
        when(MGameState.getTeacherProblemId()).thenReturn(1);
        when(MGameState.getScore()).thenReturn(100);
        when(MCheatDetector.hasCheated()).thenReturn(false);

        String arguments = "40";
        task.tryCommand("finalScore", arguments);

        verify(MGameState, times(1)).setScore(anyInt());
    }
}
