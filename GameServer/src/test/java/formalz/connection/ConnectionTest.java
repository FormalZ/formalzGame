/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
package formalz.connection;

import org.java_websocket.WebSocket;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import static org.mockito.Mockito.*;

import java.util.ArrayList;

import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import formalz.data.Problem;
import formalz.gamelogic.gamestate.AdaptiveDifficulty;
import formalz.gamelogic.gamestate.GameState;
import formalz.gamelogic.gamestate.HintSystem;
import formalz.gamelogic.gamestate.ProblemState;

public class ConnectionTest {
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    WebSocket socket;

    /**
     * Test whether the start command is send correctly.
     */
    @Test
    public void testSendStart() {
        // Given
        Connection connection = new Connection(socket);

        // When
        connection.sendStart();

        // Then
        verify(socket, times(1)).send(eq("start"));
    }

    /**
     * Test whether the finish command is send correctly.
     */
    @Test
    public void testSendFinish() {
        // Given
        Connection connection = new Connection(socket);

        // When
        connection.sendFinish();

        // Then
        verify(socket, times(1)).send(eq("finish"));
    }

    /**
     * Test whether the stop command is send correctly.
     */
    @Test
    public void testSendStop() {
        // Given
        Connection connection = new Connection(socket);

        // When
        connection.sendStop();

        // Then
        verify(socket, times(1)).send(eq("stop"));
    }

    /**
     * Test whether the send command is send correctly.
     */
    @Test
    public void testSendEnd() {
        // Given
        Connection connection = new Connection(socket);

        // When
        connection.sendEnd();

        // Then
        verify(socket, times(1)).send(eq("end"));
    }

    /**
     * Test whether the startup done command is send correctly.
     */
    @Test
    public void testSendStartUpCorrect() {
        // Given
        Connection connection = new Connection(socket);

        // When
        connection.sendStartUpCorrect();

        // Then
        verify(socket, times(1)).send(eq("startup done"));
    }

    /**
     * Test whether the startup wrong command is send correctly.
     */
    @Test
    public void testSendStartUpWrong() {
        // Given
        Connection connection = new Connection(socket);

        // When
        connection.sendStartUpWrong();

        // Then
        verify(socket, times(1)).send(eq("startup wrong"));
    }

    /**
     * Test whether the startup timeout command is send correctly.
     */
    @Test
    public void testSendStartUpTimeout() {
        // Given
        Connection connection = new Connection(socket);

        // When
        connection.sendStartUpTimeOut();

        // Then
        verify(socket, times(1)).send(eq("startup timeout"));
    }

    /**
     * Test whether the send error command is send correctly.
     */
    @Test
    public void testSendError() {
        // Given
        Connection connection = new Connection(socket);

        // When
        connection.sendError("message of error testing");

        // Then
        verify(socket, times(1)).send(eq("error message of error testing"));
    }

    /**
     * Test whether the send description command is send correctly.
     */
    @Test
    public void testSendDescription() {
        // Given
        Problem MProblem = mock(Problem.class);
        when(MProblem.getDescription()).thenReturn("description test");

        Connection connection = new Connection(socket);

        // When
        connection.sendDescription(MProblem);

        // Then
        verify(socket, times(1)).send(eq("description description test"));
    }

    /**
     * Test whether the send difficulty command is send correctly.
     */
    @Test
    public void testSendDifficulty() {
        // Given
        Connection connection = new Connection(socket);

        // When
        connection.sendDifficulty(500);

        // Then
        verify(socket, times(1)).send(eq("difficulty 500"));
    }

    /**
     * Test whether the send valid pretokens command is send correctly.
     */
    @Test
    public void testSendValidPreTokens() {
        // Given
        Connection connection = new Connection(socket);

        // When
        connection.sendValidPreTokens(new String[] { "test1", "test2" });

        // Then
        verify(socket, times(1)).send(eq("validPreTokens [test1, test2]"));
    }

    /**
     * Test whether the valid posttokens command is send correctly.
     */
    @Test
    public void testSendValidPostTokens() {
        // Given
        Connection connection = new Connection(socket);

        // When
        connection.sendValidPostTokens(new String[] { "test1", "test2" });

        // Then
        verify(socket, times(1)).send(eq("validPostTokens [test1, test2]"));
    }

    /**
     * Test whether the spawn wave command is send correctly.
     */
    @Test
    public void testSendSpawnWave() {
        // Given
        Connection connection = new Connection(socket);

        // When
        connection.sendSpawnWave(100, 100, 100, 1000, new float[] { 0.5f, 0.5f }, new float[] { 0.25f, 0.75f },
                new float[] { 0.01f });

        // Then
        verify(socket, times(1)).send(eq("spawnWave 100;[0.5, 0.5];[0.25, 0.75];100;100;1000;[0.01]"));
    }

    /**
     * Test whether the prefeedback command is send correctly.
     */
    @Test
    public void testSendPreFeedback() {
        // Given
        Connection connection = new Connection(socket);

        // When
        connection.sendPreResult(new boolean[] { true, false }, new float[] { 0.5f, 0.5f });

        // Then
        verify(socket, times(1)).send(eq("resultPre [0.5, 0.5]"));
    }

    /**
     * Test whether the postfeedback command is send correctly.
     */
    @Test
    public void testSendPostFeedback() {
        // Given
        Connection connection = new Connection(socket);

        // When
        connection.sendPostResult(new boolean[] { true, false }, new float[] { 0.5f, 0.5f });

        // Then
        verify(socket, times(1)).send(eq("resultPost [0.5, 0.5]"));
    }

    /**
     * Test whether a new question is send correctly.
     */
    @Test
    public void testStartNewQuestion() {
        // Given
        Problem MProblem = mock(Problem.class);
        when(MProblem.getDescription()).thenReturn("description test");
        when(MProblem.getDifficulty()).thenReturn(500);
        when(MProblem.getPreTokens()).thenReturn(new String[] { "test1", "test2" });
        when(MProblem.getPostTokens()).thenReturn(new String[] { "test1", "test2" });

        ProblemState MProblemState = mock(ProblemState.class);
        when(MProblemState.generatePrePercentages()).thenReturn(new float[] { 0.5f, 0.5f });
        when(MProblemState.generatePostPercentages()).thenReturn(new float[] { 0.5f, 0.5f });

        AdaptiveDifficulty MAdaptiveDifficulty = mock(AdaptiveDifficulty.class);
        when(MAdaptiveDifficulty.getWaveSparkAmount()).thenReturn(10);
        when(MAdaptiveDifficulty.getWaveSparkHealth()).thenReturn(30);
        when(MAdaptiveDifficulty.getWaveSparkSpeed()).thenReturn(64);
        when(MAdaptiveDifficulty.getWaveSparkSpawnTime()).thenReturn(1000);
        when(MAdaptiveDifficulty.getSpecialSparkSpawnPercentage()).thenReturn(new float[] { 0.01f });

        HintSystem hintSystem = mock(HintSystem.class);
        when(hintSystem.getHints()).thenReturn(new ArrayList<String>());

        GameState gameState = mock(GameState.class);
        when(gameState.getProblem()).thenReturn(MProblem);
        when(gameState.getProblemState()).thenReturn(MProblemState);
        when(gameState.getAdaptiveDifficulty()).thenReturn(MAdaptiveDifficulty);
        when(gameState.getHintSystem()).thenReturn(hintSystem);

        Connection connection = new Connection(socket);

        // When
        connection.startNewQuestion(gameState, 1);

        // Then
        verify(socket, times(1)).send(eq("description description test"));
        verify(socket, times(1)).send(eq("difficulty 500"));
        verify(socket, times(1)).send(eq("validPreTokens [test1, test2]"));
        verify(socket, times(1)).send(eq("validPostTokens [test1, test2]"));
        verify(socket, times(1)).send(eq("spawnWave 10;[0.5, 0.5];[0.5, 0.5];30;64;1000;[0.01]"));
    }
}
