/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
package connection;

import static org.junit.Assert.*;

import org.java_websocket.WebSocket;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import static org.mockito.Mockito.*;

import java.util.ArrayList;

import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import data.Problem;
import gamelogic.gamestate.AdaptiveDifficulty;
import gamelogic.gamestate.GameState;
import gamelogic.gamestate.HintSystem;
import gamelogic.gamestate.ProblemState;

public class _ConnectionTest
{
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    WebSocket MWebSocket;

    @Mock
    HintSystem MHintSystem;

    /**
     * Test whether the start command is send correctly.
     */
    @Test
    public void testSendStart()
    {
        Connection connection = new Connection(MWebSocket);

        assertSame(connection.getSocket(), MWebSocket);

        connection.sendStart();

        verify(MWebSocket, times(1)).send(eq("start"));
    }

    /**
     * Test whether the finish command is send correctly.
     */
    @Test
    public void testSendFinish()
    {
        Connection connection = new Connection(MWebSocket);

        connection.sendFinish();

        verify(MWebSocket, times(1)).send(eq("finish"));
    }

    /**
     * Test whether the stop command is send correctly.
     */
    @Test
    public void testSendStop()
    {
        Connection connection = new Connection(MWebSocket);

        connection.sendStop();

        verify(MWebSocket, times(1)).send(eq("stop"));
    }

    /**
     * Test whether the send command is send correctly.
     */
    @Test
    public void testSendEnd()
    {
        Connection connection = new Connection(MWebSocket);

        connection.sendEnd();

        verify(MWebSocket, times(1)).send(eq("end"));
    }

    /**
     * Test whether the startup done command is send correctly.
     */
    @Test
    public void testSendStartUpCorrect()
    {
        Connection connection = new Connection(MWebSocket);

        connection.sendStartUpCorrect();

        verify(MWebSocket, times(1)).send(eq("startup done"));
    }

    /**
     * Test whether the startup wrong command is send correctly.
     */
    @Test
    public void testSendStartUpWrong()
    {
        Connection connection = new Connection(MWebSocket);

        connection.sendStartUpWrong();

        verify(MWebSocket, times(1)).send(eq("startup wrong"));
    }

    /**
     * Test whether the startup timeout command is send correctly.
     */
    @Test
    public void testSendStartUpTimeout()
    {
        Connection connection = new Connection(MWebSocket);

        connection.sendStartUpTimeOut();

        verify(MWebSocket, times(1)).send(eq("startup timeout"));
    }

    /**
     * Test whether the send error command is send correctly.
     */
    @Test
    public void testSendError()
    {
        Connection connection = new Connection(MWebSocket);

        connection.sendError("message of error testing");

        verify(MWebSocket, times(1)).send(eq("error message of error testing"));
    }

    /**
     * Test whether the send description command is send correctly.
     */
    @Test
    public void testSendDescription()
    {
        Connection connection = new Connection(MWebSocket);
        Problem MProblem = mock(Problem.class);

        when(MProblem.getDescription()).thenReturn("description test");

        connection.sendDescription(MProblem);

        verify(MWebSocket, times(1)).send(eq("description description test"));
    }

    /**
     * Test whether the send difficulty command is send correctly.
     */
    @Test
    public void testSendDifficulty()
    {
        Connection connection = new Connection(MWebSocket);

        connection.sendDifficulty(500);

        verify(MWebSocket, times(1)).send(eq("difficulty 500"));
    }

    /**
     * Test whether the send valid pretokens command is send correctly.
     */
    @Test
    public void testSendValidPreTokens()
    {
        Connection connection = new Connection(MWebSocket);

        connection.sendValidPreTokens(new String[] { "test1", "test2" });

        verify(MWebSocket, times(1)).send(eq("validPreTokens [test1, test2]"));
    }

    /**
     * Test whether the valid posttokens command is send correctly.
     */
    @Test
    public void testSendValidPostTokens()
    {
        Connection connection = new Connection(MWebSocket);

        connection.sendValidPostTokens(new String[] { "test1", "test2" });

        verify(MWebSocket, times(1)).send(eq("validPostTokens [test1, test2]"));
    }

    /**
     * Test whether the spawn wave command is send correctly.
     */
    @Test
    public void testSendSpawnWave()
    {
        Connection connection = new Connection(MWebSocket);

        connection.sendSpawnWave(100, 100, 100, 1000, new float[] { 0.5f, 0.5f }, new float[] { 0.25f, 0.75f }, new float[] { 0.01f });

        verify(MWebSocket, times(1)).send(eq("spawnWave 100;[0.5, 0.5];[0.25, 0.75];100;100;1000;[0.01]"));
    }

    /**
     * Test whether the prefeedback command is send correctly.
     */
    @Test
    public void testSendPreFeedback()
    {
        Connection connection = new Connection(MWebSocket);

        connection.sendPreResult(new boolean[] { true, false }, new float[] { 0.5f, 0.5f });

        verify(MWebSocket, times(1)).send(eq("resultPre [0.5, 0.5]"));
    }

    /**
     * Test whether the postfeedback command is send correctly.
     */
    @Test
    public void testSendPostFeedback()
    {
        Connection connection = new Connection(MWebSocket);

        connection.sendPostResult(new boolean[] { true, false }, new float[] { 0.5f, 0.5f });

        verify(MWebSocket, times(1)).send(eq("resultPost [0.5, 0.5]"));
    }

    /**
     * Test whether a new question is send correctly.
     */
    @Test
    public void testStartNewQuestion()
    {
        Connection connection = new Connection(MWebSocket);

        GameState MGameState = mock(GameState.class);
        ProblemState MProblemState = mock(ProblemState.class);
        AdaptiveDifficulty MAdaptiveDifficulty = mock(AdaptiveDifficulty.class);
        Problem MProblem = mock(Problem.class);
        Client MClient = mock(Client.class);

        when(MGameState.getProblem()).thenReturn(MProblem);
        when(MGameState.getProblemState()).thenReturn(MProblemState);
        when(MGameState.getAdaptiveDifficulty()).thenReturn(MAdaptiveDifficulty);
        
        when(MProblem.getDescription()).thenReturn("description test");
        when(MProblem.getDifficulty()).thenReturn(500);
        when(MProblem.getPreTokens()).thenReturn(new String[] { "test1", "test2" });
        when(MProblem.getPostTokens()).thenReturn(new String[] { "test1", "test2" });
        when(MProblemState.generatePrePercentages()).thenReturn(new float[] { 0.5f, 0.5f });
        when(MProblemState.generatePostPercentages()).thenReturn(new float[] { 0.5f, 0.5f });
        when(MAdaptiveDifficulty.getWaveSparkAmount()).thenReturn(10);
        when(MAdaptiveDifficulty.getWaveSparkHealth()).thenReturn(30);
        when(MAdaptiveDifficulty.getWaveSparkSpeed()).thenReturn(64);
        when(MAdaptiveDifficulty.getWaveSparkSpawnTime()).thenReturn(1000);
        when(MAdaptiveDifficulty.getSpecialSparkSpawnPercentage()).thenReturn(new float[] { 0.01f });
        when(MGameState.getHintSystem()).thenReturn(MHintSystem);
        when(MHintSystem.getHints()).thenReturn(new ArrayList<String>());

        connection.startNewQuestion(MGameState, MClient.getLogger(), anyInt());

        verify(MWebSocket, times(1)).send(eq("description description test"));
        verify(MWebSocket, times(1)).send(eq("difficulty 500"));
        verify(MWebSocket, times(1)).send(eq("validPreTokens [test1, test2]"));
        verify(MWebSocket, times(1)).send(eq("validPostTokens [test1, test2]"));
        verify(MWebSocket, times(1)).send(eq("spawnWave 10;[0.5, 0.5];[0.5, 0.5];30;64;1000;[0.01]"));
    }
}
