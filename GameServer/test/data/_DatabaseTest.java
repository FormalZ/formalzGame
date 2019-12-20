/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
package data;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.function.Function;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.mockito.Mockito.*;

public class _DatabaseTest
{
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    Connection MConnection;
    @Mock
    ResultSet MResultSet;
    @Mock
    PreparedStatement MPStatement;
    @Mock
    Function<Connection, PreparedStatement> MFunction;

    @Before
    public void init()
    {
        Database.setDoSetup(false);
        Database.setMockConnection(MConnection);
    }

    @After
    public void finish()
    {
        Database.setDoSetup(true);
        Database.setMockConnection(null);
        Database.setDoThrowConnectionException(false);
    }

    /**
     * Test whether a query without return is handled correctly when no exception occurs.
     * @throws Exception Exceptions from the queries.
     */
    @Test
    public void queryVoidTestTrue() throws Exception
    {
        when(MConnection.prepareStatement(anyString())).thenReturn(MPStatement);
        when(MPStatement.executeQuery()).thenReturn(MResultSet);
        when(MFunction.apply(any())).thenReturn(MPStatement);

        boolean output = Database.getInstance().queryVoid(MFunction);

        verify(MPStatement, times(1)).executeUpdate();
        assertEquals(true, output);
    }
    
    /**
     * Test whether a query without return is handled correctly when an exception occurs.
     * @throws Exception Exceptions from the queries.
     */
    @Test
    public void queryVoidTestException() throws Exception
    {
        Database.setDoSetup(false);
        Database.setMockConnection(MConnection);

        when(MConnection.prepareStatement(anyString())).thenReturn(MPStatement);
        when(MPStatement.executeUpdate()).thenThrow(new SQLException("sql exception"));
        when(MFunction.apply(any())).thenReturn(MPStatement);

        boolean output = Database.getInstance().queryVoid(MFunction);

        assertEquals(false, output);
    }

    /**
     * Test whether a query returning an int is handled correctly when no exception occurs.
     * @throws Exception Exceptions from the queries.
     */
    @Test
    public void testQueryIntTrue() throws Exception
    {
        Database.setDoSetup(false);
        Database.setMockConnection(MConnection);

        when(MConnection.prepareStatement(anyString())).thenReturn(MPStatement);
        when(MPStatement.executeQuery()).thenReturn(MResultSet);
        when(MFunction.apply(any())).thenReturn(MPStatement);

        when(MResultSet.next()).thenReturn(true);
        when(MResultSet.getInt(anyString())).thenReturn(new Integer(5));

        Integer output = Database.getInstance().queryInt(MFunction, "column");

        assertEquals(new Integer(5), output);
        verify(MPStatement, times(1)).executeQuery();
    }

    /**
     * Test whether a query returning an int is handled correctly when there are no results.
     * @throws Exception Exceptions from the queries.
     */
    @Test
    public void testQueryIntFalse() throws Exception
    {
        when(MConnection.prepareStatement(anyString())).thenReturn(MPStatement);
        when(MPStatement.executeQuery()).thenReturn(MResultSet);
        when(MFunction.apply(any())).thenReturn(MPStatement);

        when(MResultSet.next()).thenReturn(false);
        when(MResultSet.getInt(anyString())).thenReturn(new Integer(5));

        Integer output = Database.getInstance().queryInt(MFunction, "column");

        assertNull(output);
    }

    /**
     * Test whether a query returning an int is handled correctly when there occurs an exception.
     * @throws Exception Exceptions from the queries.
     */
    @Test
    public void testQueryIntException() throws Exception
    {
        when(MConnection.prepareStatement(anyString())).thenReturn(MPStatement);
        when(MPStatement.executeQuery()).thenThrow(new SQLException("sql exception"));
        when(MFunction.apply(any())).thenReturn(MPStatement);

        Integer output = Database.getInstance().queryInt(MFunction, "column");

        assertNull(output);
    }

    /**
     * Test whether a query returning a gamesession is handled correctly when there occurs no exception.
     * @throws Exception Exceptions from the queries.
     */
    @Test
    public void testQueryGameSessionTrue() throws Exception
    {
        when(MConnection.prepareStatement(anyString())).thenReturn(MPStatement);
        when(MPStatement.executeQuery()).thenReturn(MResultSet);
        when(MFunction.apply(any())).thenReturn(MPStatement);

        when(MResultSet.next()).thenReturn(true);
        int user_id = 5;
        int problem_id = 3;
        Timestamp MTimestamp = mock(Timestamp.class);
        when(MResultSet.getInt(eq("user_id"))).thenReturn(new Integer(user_id));
        when(MResultSet.getInt(eq("problem_id"))).thenReturn(new Integer(problem_id));
        when(MResultSet.getTimestamp("created_at")).thenReturn(MTimestamp);

        GameSession output = Database.getInstance().queryGameSession(MFunction);

        assertEquals((int) new Integer(user_id), (int) output.getUserId());
        assertEquals((int) new Integer(problem_id), (int) output.getProblemId());
        assertSame(MTimestamp, output.getCreatedAt());
    }

    /**
     * Test whether a query returning a gamesession is handled correctly when there are no results.
     * @throws Exception Exceptions from the queries.
     */
    @Test
    public void testQueryGameSessionFalse() throws Exception
    {
        when(MConnection.prepareStatement(anyString())).thenReturn(MPStatement);
        when(MPStatement.executeQuery()).thenReturn(MResultSet);
        when(MFunction.apply(any())).thenReturn(MPStatement);

        when(MResultSet.next()).thenReturn(false);

        GameSession output = Database.getInstance().queryGameSession(MFunction);

        assertNull(output);
    }

    /**
     * Test whether a query returning a gamesession is handled correctly when there an exception occurs.
     * @throws Exception Exceptions from the queries.
     */
    @Test
    public void testQueryGameSessionException() throws Exception
    {
        when(MConnection.prepareStatement(anyString())).thenReturn(MPStatement);
        when(MPStatement.executeQuery()).thenThrow(new SQLException("sql exception"));
        when(MFunction.apply(any())).thenReturn(MPStatement);

        when(MResultSet.next()).thenReturn(false);

        GameSession output = Database.getInstance().queryGameSession(MFunction);

        assertNull(output);
    }

    /**
     * Test whether a query returning a float is handled correctly when there occurs no exception.
     * @throws Exception Exceptions from the queries.
     */
    @Test
    public void testQueryFloatTrue() throws Exception
    {
        Database.setDoSetup(false);
        Database.setMockConnection(MConnection);

        when(MConnection.prepareStatement(anyString())).thenReturn(MPStatement);
        when(MPStatement.executeQuery()).thenReturn(MResultSet);
        when(MFunction.apply(any())).thenReturn(MPStatement);

        when(MResultSet.next()).thenReturn(true);
        Float value = new Float(5.5);
        when(MResultSet.getFloat(anyString())).thenReturn(value);

        Float output = Database.getInstance().queryFloat(MFunction, "column");

        assertEquals(value, output);
        verify(MPStatement, times(1)).executeQuery();
    }

    /**
     * Test whether a query returning a float is handled correctly when there are no results.
     * @throws Exception Exceptions from the queries.
     */
    @Test
    public void testQueryFloatFalse() throws Exception
    {
        when(MConnection.prepareStatement(anyString())).thenReturn(MPStatement);
        when(MPStatement.executeQuery()).thenReturn(MResultSet);
        when(MFunction.apply(any())).thenReturn(MPStatement);

        when(MResultSet.next()).thenReturn(false);
        Float value = new Float(5.5);
        when(MResultSet.getFloat(anyString())).thenReturn(value);

        Integer output = Database.getInstance().queryInt(MFunction, "column");

        assertNull(output);
    }

    /**
     * Test whether a query returning a float is handled correctly when an exception occurs.
     * @throws Exception Exceptions from the queries.
     */
    @Test
    public void testQueryFloatException() throws Exception
    {
        when(MConnection.prepareStatement(anyString())).thenReturn(MPStatement);
        when(MPStatement.executeQuery()).thenThrow(new SQLException("sql exception"));
        when(MFunction.apply(any())).thenReturn(MPStatement);

        Float output = Database.getInstance().queryFloat(MFunction, "column");

        assertNull(output);
    }

    /**
     * Test whether a query returning a string is handled correctly when there occurs no exception.
     * @throws Exception Exceptions from the queries.
     */
    @Test
    public void testQueryStringTrue() throws Exception
    {
        Database.setDoSetup(false);
        Database.setMockConnection(MConnection);

        when(MConnection.prepareStatement(anyString())).thenReturn(MPStatement);
        when(MPStatement.executeQuery()).thenReturn(MResultSet);
        when(MFunction.apply(any())).thenReturn(MPStatement);

        when(MResultSet.next()).thenReturn(true);
        String value = "stringStringSTRING";
        when(MResultSet.getString(anyString())).thenReturn(value);

        String output = Database.getInstance().queryString(MFunction, "column");

        assertEquals(value, output);
        verify(MPStatement, times(1)).executeQuery();
    }

    /**
     * Test whether a query returning a string is handled correctly when there are no results.
     * @throws Exception Exceptions from the queries.
     */
    @Test
    public void testQueryStringFalse() throws Exception
    {
        when(MConnection.prepareStatement(anyString())).thenReturn(MPStatement);
        when(MPStatement.executeQuery()).thenReturn(MResultSet);
        when(MFunction.apply(any())).thenReturn(MPStatement);

        when(MResultSet.next()).thenReturn(false);
        String value = "stringStringSTRING";
        when(MResultSet.getString(anyString())).thenReturn(value);

        String output = Database.getInstance().queryString(MFunction, "column");

        assertNull(output);
    }

    /**
     * Test whether a query returning a string is handled correctly when an exception occurs.
     * @throws Exception Exceptions from the queries.
     */
    @Test
    public void testQueryStringException() throws Exception
    {
        when(MConnection.prepareStatement(anyString())).thenReturn(MPStatement);
        when(MPStatement.executeQuery()).thenThrow(new SQLException("sql exception"));
        when(MFunction.apply(any())).thenReturn(MPStatement);

        String output = Database.getInstance().queryString(MFunction, "column");

        assertNull(output);
    }
}
