/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
package formalz.gamelogic;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.sql.Timestamp;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import formalz.data.GameSession;
import formalz.data.Problem;
import formalz.data.Queries;
import formalz.gamelogic.gamestate.AdaptiveDifficulty;
import formalz.gamelogic.gamestate.GameState;
import formalz.haskellapi.Prover;
import formalz.haskellapi.Response;

public class GameStateTest {
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    Queries MQueries;
    @Mock
    Prover MProver;
    @Mock
    Problem MRepoProblem1;
    @Mock
    Problem MRepoProblem2;
    @Mock
    Problem MSidetrackProblem;
    @Mock
    Problem MTeacherProblem;
    @Mock
    GameSession MGameSession;
    @Mock
    Response MResponse;
    @Mock
    AdaptiveDifficulty MAdaptiveDifficulty;

    @Before
    public void initMocks() {
        Queries.setQueries(MQueries);
        Prover.setProver(MProver);
    }

    /**
     * Test whether scoring is handled correctly.
     */
    @Test
    public void testScoring() {
        GameState state = new GameState();
        assertEquals(0, state.getScore());
        state.updateScore(5);
        assertEquals(5, state.getScore());
        state.updateScore(100);
        assertEquals(105, state.getScore());
    }

    /**
     * Test whether correct problems are received when the game progresses
     * correctly.
     * 
     * @throws Exception Some Exception.
     */
    @Test
    public void testGetProblemAndProcessCorrectAnswer() throws Exception {
        when(MQueries.getProblemCount(anyInt())).thenReturn(2);
        when(MQueries.getProblemById(anyInt())).thenReturn(MTeacherProblem);
        when(MAdaptiveDifficulty.isFinalProblem()).thenReturn(false);

        List<Problem> list = new ArrayList<Problem>();
        list.add(MRepoProblem1);
        list.add(MRepoProblem2);

        GameState state = new GameState();

        Timestamp MTimestamp = mock(Timestamp.class);
        state.updateGameSession(new GameSession(1, 1, MTimestamp));

        assertTrue(!state.isOngoing());

        state.start();

        assertTrue(state.isOngoing());
        assertEquals(0, state.getProblemNumber());

        when(MQueries.getRandomRepoProblemWithProperties(notNull(), any(), anyInt(), anyInt(), anyInt(), anyInt(),
                anyInt())).thenReturn(MRepoProblem1);
        Problem problem1 = state.getProblem();

        state.setAdaptiveDifficulty(MAdaptiveDifficulty);

        state.processCorrectAnswer();

        assertEquals(1, state.getProblemNumber());

        when(MQueries.getRandomRepoProblemWithProperties(notNull(), any(), anyInt(), anyInt(), anyInt(), anyInt(),
                anyInt())).thenReturn(MRepoProblem2);
        Problem problem2 = state.getProblem();
        state.processCorrectAnswer();

        assertEquals(2, state.getProblemNumber());
        when(MAdaptiveDifficulty.isFinalProblem()).thenReturn(true);

        Problem problem3 = state.getProblem();
        state.processCorrectAnswer();

        assertSame(MRepoProblem1, problem1);
        assertSame(MRepoProblem2, problem2);
        assertSame(MTeacherProblem, problem3);
        assertTrue(state.isFinished());
    }

    /**
     * Test whether correct problems are received when the player answers questions
     * wrongly.
     * 
     * @throws Exception Some exception.
     */
    @Test
    public void testGetProblemAndProcessInCorrectAnswer() throws Exception {
        when(MAdaptiveDifficulty.isFinalProblem()).thenReturn(false);
        List<Problem> list = new ArrayList<Problem>();
        list.add(MRepoProblem1);

        GameState state = new GameState();

        Timestamp MTimestamp = mock(Timestamp.class);
        state.updateGameSession(new GameSession(1, 1, MTimestamp));

        assertTrue(!state.isOngoing());

        state.start();

        assertTrue(state.isOngoing());
        assertEquals(0, state.getProblemNumber());

        when(MQueries.getProblemCount(anyInt())).thenReturn(1);
        when(MQueries.getProblemById(anyInt())).thenReturn(MTeacherProblem);
        when(MQueries.getRandomRepoProblemWithProperties(any(), any(), anyInt(), anyInt(), anyInt(), anyInt(),
                anyInt())).thenReturn(MRepoProblem1);

        Problem problem1 = state.getProblem();

        state.setAdaptiveDifficulty(MAdaptiveDifficulty);

        for (int i = 0; i < 10; i++) {
            state.processPreWrongAnswer();
        }
        state.processCorrectAnswer();

        when(MAdaptiveDifficulty.isFinalProblem()).thenReturn(true);
        Problem problemFinal = state.getProblem();
        state.processCorrectAnswer();

        assertSame(MRepoProblem1, problem1);
        assertSame(MTeacherProblem, problemFinal);
        assertTrue(state.isFinished());
    }

    /**
     * Test whether a game is ended correctly.
     */
    @Test
    public void testEndGameGood() {
        when(MQueries.getProblemById(anyInt())).thenReturn(MTeacherProblem);
        when(MQueries.getProblemCount(anyInt())).thenReturn(0);

        when(MGameSession.getProblemId()).thenReturn(1);

        GameState state = new GameState();

        assertFalse(state.end());

        state.updateGameSession(MGameSession);

        assertSame(state.getGameSession(), MGameSession);

        state.start();

        assertEquals(1, state.getTeacherProblemId());

        state.getProblem();

        state.processPreWrongAnswer();
        state.processCorrectAnswer();

        verify(MQueries, times(1)).insertStatistics(anyInt(), anyInt(), anyInt(), anyInt(), anyInt(), anyInt(),
                anyInt());
        assertTrue(state.isFinished());
        assertTrue(state.end());
    }
}
