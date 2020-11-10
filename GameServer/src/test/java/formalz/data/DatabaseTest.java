/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
package formalz.data;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import formalz.data.Database.SQLFunction;

import static org.mockito.Mockito.*;

public class DatabaseTest {
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    Connection MConnection;
    @Mock
    ResultSet MResultSet;
    @Mock
    PreparedStatement MPStatement;
    @Mock
    SQLFunction<Connection, PreparedStatement> MFunction;

    @Before
    public void init() {
        Database.setDoSetup(false);
        Database.setMockConnection(MConnection);
    }

    @After
    public void finish() {
        Database.setDoSetup(true);
        Database.setMockConnection(null);
        Database.setDoThrowConnectionException(false);
    }

    /**
     * Test whether a query without return is handled correctly when no exception
     * occurs.
     * 
     * @throws Exception Exceptions from the queries.
     */
    @Test
    public void queryVoidTestTrue() throws Exception {
        when(MConnection.prepareStatement(anyString())).thenReturn(MPStatement);
        when(MPStatement.executeUpdate()).thenReturn(2);
        when(MFunction.apply(any())).thenReturn(MPStatement);

        boolean output = Database.getInstance().update(MFunction) > 0;

        verify(MPStatement, times(1)).executeUpdate();
        assertEquals(true, output);
    }

    /**
     * Test whether a query without return is handled correctly when an exception
     * occurs.
     * 
     * @throws Exception Exceptions from the queries.
     */
    @Test
    public void queryVoidTestException() throws Exception {
        Database.setDoSetup(false);
        Database.setMockConnection(MConnection);

        when(MConnection.prepareStatement(anyString())).thenReturn(MPStatement);
        when(MPStatement.executeUpdate()).thenThrow(new SQLException("sql exception"));
        when(MFunction.apply(any())).thenReturn(MPStatement);

        assertThrows(SQLException.class, () -> {
            Database.getInstance().update(MFunction);
        });
    }

    /**
     * Test whether a query returning an int is handled correctly when no exception
     * occurs.
     * 
     * @throws Exception Exceptions from the queries.
     */
    @Test
    public void testQueryIntTrue() throws Exception {
        Database.setDoSetup(false);
        Database.setMockConnection(MConnection);

        when(MConnection.prepareStatement(anyString())).thenReturn(MPStatement);
        when(MPStatement.executeQuery()).thenReturn(MResultSet);
        when(MFunction.apply(any())).thenReturn(MPStatement);

        when(MResultSet.next()).thenReturn(true);
        when(MResultSet.getInt(anyString())).thenReturn(Integer.valueOf(5));

        Integer output = Database.getInstance().queryInt(MFunction, "column");

        assertEquals(Integer.valueOf(5), output);
        verify(MPStatement, times(1)).executeQuery();
    }

    /**
     * Test whether a query returning an int is handled correctly when there are no
     * results.
     * 
     * @throws Exception Exceptions from the queries.
     */
    @Test
    public void testQueryIntFalse() throws Exception {
        when(MConnection.prepareStatement(anyString())).thenReturn(MPStatement);
        when(MPStatement.executeQuery()).thenReturn(MResultSet);
        when(MFunction.apply(any())).thenReturn(MPStatement);

        when(MResultSet.next()).thenReturn(false);
        when(MResultSet.getInt(anyString())).thenReturn(Integer.valueOf(5));

        Integer output = Database.getInstance().queryInt(MFunction, "column");

        assertNull(output);
    }

    /**
     * Test whether a query returning an int is handled correctly when there occurs
     * an exception.
     * 
     * @throws Exception Exceptions from the queries.
     */
    @Test
    public void testQueryIntException() throws Exception {
        when(MConnection.prepareStatement(anyString())).thenReturn(MPStatement);
        when(MPStatement.executeQuery()).thenThrow(new SQLException("sql exception"));
        when(MFunction.apply(any())).thenReturn(MPStatement);

        assertThrows(SQLException.class, () -> {
            Database.getInstance().queryInt(MFunction, "column");
        });
    }

    /**
     * Test whether a query returning a float is handled correctly when there occurs
     * no exception.
     * 
     * @throws Exception Exceptions from the queries.
     */
    @Test
    public void testQueryFloatTrue() throws Exception {
        Database.setDoSetup(false);
        Database.setMockConnection(MConnection);

        when(MConnection.prepareStatement(anyString())).thenReturn(MPStatement);
        when(MPStatement.executeQuery()).thenReturn(MResultSet);
        when(MFunction.apply(any())).thenReturn(MPStatement);

        when(MResultSet.next()).thenReturn(true);
        Float value = Float.valueOf(5.5f);
        when(MResultSet.getFloat(anyString())).thenReturn(value);

        Float output = Database.getInstance().queryFloat(MFunction, "column");

        assertEquals(value, output);
        verify(MPStatement, times(1)).executeQuery();
    }

    /**
     * Test whether a query returning a float is handled correctly when there are no
     * results.
     * 
     * @throws Exception Exceptions from the queries.
     */
    @Test
    public void testQueryFloatFalse() throws Exception {
        when(MConnection.prepareStatement(anyString())).thenReturn(MPStatement);
        when(MPStatement.executeQuery()).thenReturn(MResultSet);
        when(MFunction.apply(any())).thenReturn(MPStatement);

        when(MResultSet.next()).thenReturn(false);
        Float value = Float.valueOf(5.5f);
        when(MResultSet.getFloat(anyString())).thenReturn(value);

        Integer output = Database.getInstance().queryInt(MFunction, "column");

        assertNull(output);
    }

    /**
     * Test whether a query returning a float is handled correctly when an exception
     * occurs.
     * 
     * @throws Exception Exceptions from the queries.
     */
    @Test
    public void testQueryFloatException() throws Exception {
        when(MConnection.prepareStatement(anyString())).thenReturn(MPStatement);
        when(MPStatement.executeQuery()).thenThrow(new SQLException("sql exception"));
        when(MFunction.apply(any())).thenReturn(MPStatement);

        assertThrows(SQLException.class, () -> {
            Database.getInstance().queryFloat(MFunction, "column");
        });
    }

    /**
     * Test whether a query returning a string is handled correctly when there
     * occurs no exception.
     * 
     * @throws Exception Exceptions from the queries.
     */
    @Test
    public void testQueryStringTrue() throws Exception {
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
     * Test whether a query returning a string is handled correctly when there are
     * no results.
     * 
     * @throws Exception Exceptions from the queries.
     */
    @Test
    public void testQueryStringFalse() throws Exception {
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
     * Test whether a query returning a string is handled correctly when an
     * exception occurs.
     * 
     * @throws Exception Exceptions from the queries.
     */
    @Test
    public void testQueryStringException() throws Exception {
        when(MConnection.prepareStatement(anyString())).thenReturn(MPStatement);
        when(MPStatement.executeQuery()).thenThrow(new SQLException("sql exception"));
        when(MFunction.apply(any())).thenReturn(MPStatement);

        assertThrows(SQLException.class, () -> {
            Database.getInstance().queryString(MFunction, "column");
        });

    }
}
