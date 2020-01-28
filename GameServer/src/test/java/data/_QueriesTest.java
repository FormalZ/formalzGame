/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
package data;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

public class _QueriesTest
{
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    Database MDatabase;
    @Mock
    PreparedStatement MPreparedStatement;
    @Mock
    Connection MConnection;
    @Mock
    Problem MProblem;

    @Captor
    ArgumentCaptor<Function<Connection, PreparedStatement>> CFunction;

    @Before
    public void initMocks() throws SQLException
    {
        Database.setDatabase(MDatabase);
        Queries.setQueries(null);

        when(MConnection.prepareStatement(Mockito.anyString())).thenReturn(MPreparedStatement);
    }

    @After
    public void finish()
    {
        Database.setDatabase(null);
    }

    /**
     * Test whether the query for a problem id works correctly.
     * @throws SQLException Query exception.
     */
    @Test
    public void testGetProblemById() throws SQLException
    {
        when(MDatabase.queryProblem(CFunction.capture(), anyBoolean(), anyInt(), anyInt(), anyInt())).thenReturn(MProblem);

        int id = 10;
        Problem problem = Queries.getInstance().getProblemById(id);

        verify(MDatabase, times(1)).queryProblem(CFunction.capture(), anyBoolean(), anyInt(), anyInt(), anyInt());
        assertEquals(MPreparedStatement, CFunction.getValue().apply(MConnection));
        verify(MPreparedStatement, times(1)).setInt(anyInt(), eq(id));
        assertEquals(MProblem, problem);
    }

    /**
     * Test whether the query for a hint works correctly.
     * @throws SQLException Query exception.
     */
    @Test
    public void testGetHint() throws SQLException
    {
        String hint = "Hint";
        when(MDatabase.queryString(CFunction.capture(), any())).thenReturn(hint);

        String key = "key";
        String hintValue = Queries.getInstance().getHint(key);

        verify(MDatabase, times(1)).queryString(CFunction.capture(), any());
        assertEquals(MPreparedStatement, CFunction.getValue().apply(MConnection));
        verify(MPreparedStatement, times(1)).setString(anyInt(), eq(key));
        assertEquals(hint, hintValue);
    }

    /**
     * Test whether the query for a problem id works correctly when there occurs an exception.
     * @throws SQLException Query exception.
     */
    @Test
    public void testGetProblemByIdException() throws SQLException
    {
        when(MDatabase.queryProblem(CFunction.capture(), anyBoolean(), anyInt(), anyInt(), anyInt())).thenReturn(null);
        doThrow(new SQLException()).when(MPreparedStatement).setInt(anyInt(), anyInt());

        int id = 10;
        Problem problem = Queries.getInstance().getProblemById(id);

        verify(MDatabase, times(1)).queryProblem(CFunction.capture(), anyBoolean(), anyInt(), anyInt(), anyInt());
        assertNull(CFunction.getValue().apply(MConnection));

        assertNull(problem);
    }

    /**
     * Test whether the query for a random repo problem works correctly.
     * @throws SQLException Query exception.
     */
    @Test
    public void testGetRandomRepoProblem() throws SQLException
    {
        when(MDatabase.queryProblem(CFunction.capture(), anyBoolean(), anyInt(), anyInt(), anyInt())).thenReturn(MProblem);

        List<Integer> oldIds = new ArrayList<Integer>();
        int id1 = 10;
        oldIds.add(id1);
        int id2 = 20;
        oldIds.add(id2);
        Problem problem = Queries.getInstance().getRandomRepoProblem(oldIds, anyInt(), anyInt(), anyInt());

        verify(MDatabase, times(1)).queryProblem(CFunction.capture(), anyBoolean(), anyInt(), anyInt(), anyInt());
        assertEquals(MPreparedStatement, CFunction.getValue().apply(MConnection));
        verify(MPreparedStatement, atLeastOnce()).setInt(anyInt(), eq(id1));
        verify(MPreparedStatement, atLeastOnce()).setInt(anyInt(), eq(id2));
        assertEquals(MProblem, problem);
    }

    /**
     * Test whether the query for a random repo problem works correctly when an exception occurs.
     * @throws SQLException Query exception.
     */
    @Test
    public void testGetRandomRepoProblemException() throws SQLException
    {
        when(MDatabase.queryProblem(CFunction.capture(), anyBoolean(), anyInt(), anyInt(), anyInt())).thenReturn(null);
        doThrow(new SQLException()).when(MPreparedStatement).setInt(anyInt(), anyInt());

        List<Integer> oldIds = new ArrayList<Integer>();
        int id1 = 10;
        oldIds.add(id1);
        int id2 = 20;
        oldIds.add(id2);
        Problem problem = Queries.getInstance().getRandomRepoProblem(oldIds, anyInt(), anyInt(), anyInt());

        verify(MDatabase, times(1)).queryProblem(CFunction.capture(), anyBoolean(), anyInt(), anyInt(), anyInt());
        assertNull(CFunction.getValue().apply(MConnection));
        assertNull(problem);
    }

    /**
     * Test whether the query for the amount of problems works correctly.
     * @throws SQLException Query exception.
     */
    @Test
    public void testGetProblemCount() throws SQLException
    {
        Integer count = 10;
        when(MDatabase.queryInt(CFunction.capture(), anyString())).thenReturn(count);

        int id = 10;
        Integer resultCount = Queries.getInstance().getProblemCount(id);

        verify(MDatabase, times(1)).queryInt(CFunction.capture(), anyString());
        assertEquals(MPreparedStatement, CFunction.getValue().apply(MConnection));
        verify(MPreparedStatement, times(1)).setInt(eq(1), eq(id));
        assertEquals(count, resultCount);
    }

    /**
     * Test whether the query for the amount of problems works correctly when there is an exception.
     * @throws SQLException Query exception.
     */
    @Test
    public void testGetProblemCountException() throws SQLException
    {
        when(MDatabase.queryInt(CFunction.capture(), anyString())).thenReturn(null);
        doThrow(new SQLException()).when(MPreparedStatement).setInt(anyInt(), anyInt());

        int id = 10;
        Integer resultCount = Queries.getInstance().getProblemCount(id);

        verify(MDatabase, times(1)).queryInt(CFunction.capture(), anyString());
        assertNull(CFunction.getValue().apply(MConnection));

        assertNull(resultCount);
    }

    /**
     * Test whether the query for inserting score works correctly.
     * @throws SQLException Query exception.
     */
    @Test
    public void testInsertScore() throws SQLException
    {
        when(MDatabase.queryVoid(CFunction.capture())).thenReturn(true);

        int userId = 10;
        int problemId = 100;
        int score = 1000;
        boolean legit = true;
        Queries.getInstance().insertScore(userId, problemId, score, legit);

        verify(MDatabase, times(1)).queryVoid(CFunction.capture());
        assertEquals(MPreparedStatement, CFunction.getValue().apply(MConnection));
        verify(MPreparedStatement, atLeastOnce()).setInt(anyInt(), eq(userId));
        verify(MPreparedStatement, atLeastOnce()).setInt(anyInt(), eq(problemId));
        verify(MPreparedStatement, atLeastOnce()).setInt(anyInt(), eq(score));
        verify(MPreparedStatement, atLeastOnce()).setBoolean(anyInt(), eq(legit));
    }

    /**
     * Test whether the query for inserting score works correctly when an exception occurs.
     * @throws SQLException Query exception.
     */
    @Test
    public void testInsertScoreException() throws SQLException
    {
        when(MDatabase.queryVoid(CFunction.capture())).thenReturn(false);
        doThrow(new SQLException()).when(MPreparedStatement).setInt(anyInt(), anyInt());

        int userId = 10;
        int problemId = 100;
        int score = 1000;
        boolean legit = false;
        Queries.getInstance().insertScore(userId, problemId, score, legit);

        verify(MDatabase, times(1)).queryVoid(CFunction.capture());
        assertNull(CFunction.getValue().apply(MConnection));
    }

    /**
     * Test whether the query for inserting statistics works correctly.
     * @throws SQLException Query exception.
     */
    @Test
    public void testInsertStatistics() throws SQLException
    {
        when(MDatabase.queryVoid(CFunction.capture())).thenReturn(true);

        int problemId = 10;
        int playedGamesId = 11;
        int totalTime = 100;
        int problemTime = 1000;
        int sidetrackCount = 10000;
        int preMistakes = 102;
        int postMistakes = 103;

        Queries.getInstance().insertStatistics(problemId, playedGamesId, totalTime, problemTime, sidetrackCount, preMistakes, postMistakes);

        verify(MDatabase, times(1)).queryVoid(CFunction.capture());
        assertEquals(MPreparedStatement, CFunction.getValue().apply(MConnection));
        verify(MPreparedStatement, atLeastOnce()).setInt(anyInt(), eq(problemId));
        verify(MPreparedStatement, atLeastOnce()).setInt(anyInt(), eq(playedGamesId));
        verify(MPreparedStatement, atLeastOnce()).setInt(anyInt(), eq(totalTime));
        verify(MPreparedStatement, atLeastOnce()).setInt(anyInt(), eq(problemTime));
        verify(MPreparedStatement, atLeastOnce()).setInt(anyInt(), eq(sidetrackCount));
        verify(MPreparedStatement, atLeastOnce()).setInt(anyInt(), eq(preMistakes));
        verify(MPreparedStatement, atLeastOnce()).setInt(anyInt(), eq(postMistakes));
    }

    /**
     * Test whether the query for inserting statistics works correctly when an exception occurs.
     * @throws SQLException Query exception.
     */
    @Test
    public void testInsertStatisticsException() throws SQLException
    {
        when(MDatabase.queryVoid(CFunction.capture())).thenReturn(false);
        doThrow(new SQLException()).when(MPreparedStatement).setInt(anyInt(), anyInt());

        int problemId = 10;
        int playedGamesId = 11;
        int totalTime = 100;
        int problemTime = 1000;
        int sidetrackCount = 10000;
        int preMistakes = 102;
        int postMistakes = 103;

        Queries.getInstance().insertStatistics(problemId, playedGamesId, totalTime, problemTime, sidetrackCount, preMistakes, postMistakes);

        verify(MDatabase, times(1)).queryVoid(CFunction.capture());
        assertNull(CFunction.getValue().apply(MConnection));
    }

    /**
     * Test whether the query for inserting statistics about repo problems works correctly.
     * @throws SQLException Query exception.
     */
    @Test
    public void testInsertRepoStatistics() throws SQLException
    {
        when(MDatabase.queryVoid(CFunction.capture())).thenReturn(true);

        int problemId = 10;
        int playedGamesId = 101;
        int order = 0;
        int waves = 100;
        int preMistakes = 99;
        int postMistakes = 1022;
        Queries.getInstance().insertRepoStatistics(problemId, playedGamesId, order, waves, preMistakes, postMistakes);

        verify(MDatabase, times(1)).queryVoid(CFunction.capture());
        assertEquals(MPreparedStatement, CFunction.getValue().apply(MConnection));
        verify(MPreparedStatement, atLeastOnce()).setInt(anyInt(), eq(problemId));
        verify(MPreparedStatement, atLeastOnce()).setInt(anyInt(), eq(playedGamesId));
        verify(MPreparedStatement, atLeastOnce()).setInt(anyInt(), eq(order));
        verify(MPreparedStatement, atLeastOnce()).setInt(anyInt(), eq(waves));
        verify(MPreparedStatement, atLeastOnce()).setInt(anyInt(), eq(preMistakes));
        verify(MPreparedStatement, atLeastOnce()).setInt(anyInt(), eq(postMistakes));
    }

    /**
     * Test whether the query for inserting statistics about repo problems works correctly when an exception occurs.
     * @throws SQLException Query exception.
     */
    @Test
    public void testInsertRepoStatisticsException() throws SQLException
    {
        when(MDatabase.queryVoid(CFunction.capture())).thenReturn(false);
        doThrow(new SQLException()).when(MPreparedStatement).setInt(anyInt(), anyInt());

        int problemId = 10;
        int playedGamesId = 101;
        int order = 0;
        int waves = 100;
        int preMistakes = 99;
        int postMistakes = 1022;
        Queries.getInstance().insertRepoStatistics(problemId, playedGamesId, order, waves, preMistakes, postMistakes);

        verify(MDatabase, times(1)).queryVoid(CFunction.capture());
        assertNull(CFunction.getValue().apply(MConnection));
    }
}