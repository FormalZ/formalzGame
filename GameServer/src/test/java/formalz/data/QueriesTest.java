/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
package formalz.data;

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

import formalz.data.Database.SQLFunction;

public class QueriesTest {
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    Database database;

    @Mock
    PreparedStatement preparedStatement;

    @Mock
    Connection connection;

    @Mock
    Problem problem;

    @Captor
    ArgumentCaptor<SQLFunction<Connection, PreparedStatement>> CFunction;

    @Before
    public void initMocks() throws SQLException {
        Database.setDatabase(database);
        Queries.setQueries(null);

        when(connection.prepareStatement(Mockito.anyString())).thenReturn(preparedStatement);
    }

    @After
    public void finish() {
        Database.setDatabase(null);
    }

    /**
     * Test whether the query for a problem id works correctly.
     * 
     * @throws SQLException Query exception.
     */
    @Test
    public void testGetProblemById() throws SQLException {

        // When
        int id = 10;
        Problem problem = Queries.getInstance().getProblemById(id);
        
/*
        when(MDatabase.queryProblem(CFunction.capture(), anyBoolean(), anyInt(), anyInt(), anyInt()))
                .thenReturn(MProblem);

        int id = 10;
        Problem problem = Queries.getInstance().getProblemById(id);

        verify(MDatabase, times(1)).queryProblem(CFunction.capture(), anyBoolean(), anyInt(), anyInt(), anyInt());
        assertEquals(MPreparedStatement, CFunction.getValue().apply(MConnection));
        verify(MPreparedStatement, times(1)).setInt(anyInt(), eq(id));
        assertEquals(MProblem, problem);
 */
    }

    /**
     * Test whether the query for a hint works correctly.
     * 
     * @throws SQLException Query exception.
     */
    @Test
    public void testGetHint() throws SQLException {
        String hint = "Hint";
        when(database.queryString(CFunction.capture(), any())).thenReturn(hint);

        String key = "key";
        String hintValue = Queries.getInstance().getHint(key, "");

        verify(database, times(1)).queryString(CFunction.capture(), any());
        assertEquals(preparedStatement, CFunction.getValue().apply(connection));
        verify(preparedStatement, times(1)).setString(anyInt(), eq(key));
        assertEquals(hint, hintValue);
    }

    /**
     * Test whether the query for a problem id works correctly when there occurs an
     * exception.
     * 
     * @throws SQLException Query exception.
     */
    @Test
    public void testGetProblemByIdException() throws SQLException {
/*
        when(database.queryProblem(CFunction.capture(), anyBoolean(), anyInt(), anyInt(), anyInt())).thenReturn(null);
        doThrow(new SQLException()).when(preparedStatement).setInt(anyInt(), anyInt());

        int id = 10;
        Problem problem = Queries.getInstance().getProblemById(id);

        verify(database, times(1)).queryProblem(CFunction.capture(), anyBoolean(), anyInt(), anyInt(), anyInt());
        assertNull(CFunction.getValue().apply(connection));

        assertNull(problem);
*/        
    }

    /**
     * Test whether the query for a random repo problem works correctly.
     * 
     * @throws SQLException Query exception.
     */
    @Test
    public void testGetRandomRepoProblem() throws SQLException {
/*         when(database.queryProblem(CFunction.capture(), anyBoolean(), anyInt(), anyInt(), anyInt()))
                .thenReturn(problem);

        List<Integer> oldIds = new ArrayList<Integer>();
        int id1 = 10;
        oldIds.add(id1);
        int id2 = 20;
        oldIds.add(id2);

        int lives = 100;
        int money = 1000;
        int deadline = 10;
        Problem problem = Queries.getInstance().getRandomRepoProblem(oldIds, lives, money, deadline);

        verify(database, times(1)).queryProblem(CFunction.capture(), anyBoolean(), eq(lives), eq(money), eq(deadline));
        assertEquals(preparedStatement, CFunction.getValue().apply(connection));
        verify(preparedStatement, atLeastOnce()).setInt(anyInt(), eq(id1));
        verify(preparedStatement, atLeastOnce()).setInt(anyInt(), eq(id2));
        assertEquals(problem, problem);
 */    }

    /**
     * Test whether the query for a random repo problem works correctly when an
     * exception occurs.
     * 
     * @throws SQLException Query exception.
     */
    @Test
    public void testGetRandomRepoProblemException() throws SQLException {
/*         when(database.queryProblem(CFunction.capture(), anyBoolean(), anyInt(), anyInt(), anyInt())).thenReturn(null);
        doThrow(new SQLException()).when(preparedStatement).setInt(anyInt(), anyInt());

        List<Integer> oldIds = new ArrayList<Integer>();
        int id1 = 10;
        oldIds.add(id1);
        int id2 = 20;
        oldIds.add(id2);
        int lives = 100;
        int money = 1000;
        int deadline = 10;
        Problem problem = Queries.getInstance().getRandomRepoProblem(oldIds, lives, money, deadline);

        verify(database, times(1)).queryProblem(CFunction.capture(), anyBoolean(), eq(lives), eq(money), eq(deadline));
        assertNull(CFunction.getValue().apply(connection));
        assertNull(problem); */
    }

    /**
     * Test whether the query for the amount of problems works correctly.
     * 
     * @throws SQLException Query exception.
     */
    @Test
    public void testGetProblemCount() throws SQLException {
        Integer count = 10;
        when(database.queryInt(CFunction.capture(), anyString())).thenReturn(count);

        int id = 10;
        Integer resultCount = Queries.getInstance().getProblemCount(id);

        verify(database, times(1)).queryInt(CFunction.capture(), anyString());
        assertEquals(preparedStatement, CFunction.getValue().apply(connection));
        verify(preparedStatement, times(1)).setInt(eq(1), eq(id));
        assertEquals(count, resultCount);
    }

    /**
     * Test whether the query for the amount of problems works correctly when there
     * is an exception.
     * 
     * @throws SQLException Query exception.
     */
    @Test
    public void testGetProblemCountException() throws SQLException {
        when(database.queryInt(CFunction.capture(), anyString())).thenReturn(null);
        doThrow(new SQLException()).when(preparedStatement).setInt(anyInt(), anyInt());

        int id = 10;
        Integer resultCount = Queries.getInstance().getProblemCount(id);

        verify(database, times(1)).queryInt(CFunction.capture(), anyString());
        assertNull(CFunction.getValue().apply(connection));

        assertNull(resultCount);
    }

    /**
     * Test whether the query for inserting score works correctly.
     * 
     * @throws SQLException Query exception.
     */
    @Test
    public void testInsertScore() throws SQLException {
/*         when(database.update(CFunction.capture())).thenReturn(true);

        int userId = 10;
        int problemId = 100;
        int score = 1000;
        boolean legit = true;
        Queries.getInstance().insertScore(userId, problemId, score, legit);

        verify(database, times(1)).update(CFunction.capture());
        assertEquals(preparedStatement, CFunction.getValue().apply(connection));
        verify(preparedStatement, atLeastOnce()).setInt(anyInt(), eq(userId));
        verify(preparedStatement, atLeastOnce()).setInt(anyInt(), eq(problemId));
        verify(preparedStatement, atLeastOnce()).setInt(anyInt(), eq(score));
        verify(preparedStatement, atLeastOnce()).setBoolean(anyInt(), eq(legit)); */
    }

    /**
     * Test whether the query for inserting score works correctly when an exception
     * occurs.
     * 
     * @throws SQLException Query exception.
     */
    @Test
    public void testInsertScoreException() throws SQLException {
/*         when(database.update(CFunction.capture())).thenReturn(false);
        doThrow(new SQLException()).when(preparedStatement).setInt(anyInt(), anyInt());

        int userId = 10;
        int problemId = 100;
        int score = 1000;
        boolean legit = false;
        Queries.getInstance().insertScore(userId, problemId, score, legit);

        verify(database, times(1)).update(CFunction.capture());
        assertNull(CFunction.getValue().apply(connection)); */
    }

    /**
     * Test whether the query for inserting statistics works correctly.
     * 
     * @throws SQLException Query exception.
     */
    @Test
    public void testInsertStatistics() throws SQLException {
/*         when(database.update(CFunction.capture())).thenReturn(true);

        int problemId = 10;
        int playedGamesId = 11;
        int totalTime = 100;
        int problemTime = 1000;
        int sidetrackCount = 10000;
        int preMistakes = 102;
        int postMistakes = 103;

        Queries.getInstance().insertStatistics(problemId, playedGamesId, totalTime, problemTime, sidetrackCount,
                preMistakes, postMistakes);

        verify(database, times(1)).update(CFunction.capture());
        assertEquals(preparedStatement, CFunction.getValue().apply(connection));
        verify(preparedStatement, atLeastOnce()).setInt(anyInt(), eq(problemId));
        verify(preparedStatement, atLeastOnce()).setInt(anyInt(), eq(playedGamesId));
        verify(preparedStatement, atLeastOnce()).setInt(anyInt(), eq(totalTime));
        verify(preparedStatement, atLeastOnce()).setInt(anyInt(), eq(problemTime));
        verify(preparedStatement, atLeastOnce()).setInt(anyInt(), eq(sidetrackCount));
        verify(preparedStatement, atLeastOnce()).setInt(anyInt(), eq(preMistakes));
        verify(preparedStatement, atLeastOnce()).setInt(anyInt(), eq(postMistakes)); */
    }

    /**
     * Test whether the query for inserting statistics works correctly when an
     * exception occurs.
     * 
     * @throws SQLException Query exception.
     */
    @Test
    public void testInsertStatisticsException() throws SQLException {
/*         when(database.update(CFunction.capture())).thenReturn(false);
        doThrow(new SQLException()).when(preparedStatement).setInt(anyInt(), anyInt());

        int problemId = 10;
        int playedGamesId = 11;
        int totalTime = 100;
        int problemTime = 1000;
        int sidetrackCount = 10000;
        int preMistakes = 102;
        int postMistakes = 103;

        Queries.getInstance().insertStatistics(problemId, playedGamesId, totalTime, problemTime, sidetrackCount,
                preMistakes, postMistakes);

        verify(database, times(1)).update(CFunction.capture());
        assertNull(CFunction.getValue().apply(connection)); */
    }

    /**
     * Test whether the query for inserting statistics about repo problems works
     * correctly.
     * 
     * @throws SQLException Query exception.
     */
    @Test
    public void testInsertRepoStatistics() throws SQLException {
/*         when(database.update(CFunction.capture())).thenReturn(true);

        int problemId = 10;
        int playedGamesId = 101;
        int order = 0;
        int waves = 100;
        int preMistakes = 99;
        int postMistakes = 1022;
        Queries.getInstance().insertRepoStatistics(problemId, playedGamesId, order, waves, preMistakes, postMistakes);

        verify(database, times(1)).update(CFunction.capture());
        assertEquals(preparedStatement, CFunction.getValue().apply(connection));
        verify(preparedStatement, atLeastOnce()).setInt(anyInt(), eq(problemId));
        verify(preparedStatement, atLeastOnce()).setInt(anyInt(), eq(playedGamesId));
        verify(preparedStatement, atLeastOnce()).setInt(anyInt(), eq(order));
        verify(preparedStatement, atLeastOnce()).setInt(anyInt(), eq(waves));
        verify(preparedStatement, atLeastOnce()).setInt(anyInt(), eq(preMistakes));
        verify(preparedStatement, atLeastOnce()).setInt(anyInt(), eq(postMistakes)); */
    }

    /**
     * Test whether the query for inserting statistics about repo problems works
     * correctly when an exception occurs.
     * 
     * @throws SQLException Query exception.
     */
    @Test
    public void testInsertRepoStatisticsException() throws SQLException {
/*         when(database.update(CFunction.capture())).thenReturn(false);
        doThrow(new SQLException()).when(preparedStatement).setInt(anyInt(), anyInt());

        int problemId = 10;
        int playedGamesId = 101;
        int order = 0;
        int waves = 100;
        int preMistakes = 99;
        int postMistakes = 1022;
        Queries.getInstance().insertRepoStatistics(problemId, playedGamesId, order, waves, preMistakes, postMistakes);

        verify(database, times(1)).update(CFunction.capture());
        assertNull(CFunction.getValue().apply(connection)); */
    }

    /**
     * Test whether a query returning a gamesession is handled correctly when there
     * occurs no exception.
     * 
     * @throws Exception Exceptions from the queries.
     */
    @Test
    public void testQueryGameSessionTrue() throws Exception {
/*         when(connection.prepareStatement(anyString())).thenReturn(MPStatement);
        when(MPStatement.executeQuery()).thenReturn(MResultSet);
        when(MFunction.apply(any())).thenReturn(MPStatement);

        when(MResultSet.next()).thenReturn(true);
        int user_id = 5;
        int problem_id = 3;
        Timestamp MTimestamp = mock(Timestamp.class);
        when(MResultSet.getInt(eq("user_id"))).thenReturn(user_id);
        when(MResultSet.getInt(eq("problem_id"))).thenReturn(problem_id);
        when(MResultSet.getTimestamp("created_at")).thenReturn(MTimestamp);

        GameSession output = Database.getInstance().queryGameSession(MFunction);

        assertEquals(user_id, output.getUserId());
        assertEquals(problem_id, output.getProblemId());
        assertSame(MTimestamp, output.getCreatedAt()); */
    }

    /**
     * Test whether a query returning a gamesession is handled correctly when there
     * are no results.
     * 
     * @throws Exception Exceptions from the queries.
     */
    @Test
    public void testQueryGameSessionFalse() throws Exception {
/*         when(connection.prepareStatement(anyString())).thenReturn(MPStatement);
        when(MPStatement.executeQuery()).thenReturn(MResultSet);
        when(MFunction.apply(any())).thenReturn(MPStatement);

        when(MResultSet.next()).thenReturn(false);

        GameSession output = Database.getInstance().queryGameSession(MFunction);

        assertNull(output); */
    }

    /**
     * Test whether a query returning a gamesession is handled correctly when there
     * an exception occurs.
     * 
     * @throws Exception Exceptions from the queries.
     */
    @Test
    public void testQueryGameSessionException() throws Exception {
/*         when(connection.prepareStatement(anyString())).thenReturn(MPStatement);
        when(MPStatement.executeQuery()).thenThrow(new SQLException("sql exception"));
        when(MFunction.apply(any())).thenReturn(MPStatement);

        when(MResultSet.next()).thenReturn(false);

        GameSession output = Database.getInstance().queryGameSession(MFunction);

        assertNull(output); */
    }

}