/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
package formalz.data;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The class with the main functionality corresponding to executing queries on
 * the database.
 * 
 * @author Ludiscite
 * @version 1.0
 */
public class Database {

    private static final Logger LOGGER = LoggerFactory.getLogger(Database.class);

    private static Database database;

    /**
     * A possible mock Connection for testing purposes.
     */
    private static Connection mConnection;

    /**
     * Whether to throw an exception when attempting to connect. For testing
     * purposes.
     */
    private static boolean doThrowConnectionException = false;
 
    private static boolean doSetup = true;

    /**
     * Whether to do the connection setup. Usefull for testing.
     * 
     * @param doSetup Whether to the connection setup.
     */
    public static void setDoSetup(boolean doSetup) {
        Database.doSetup = doSetup;
    }

    public static Database getInstance() {
        return getInstance(null);
    }

    /**
     * Get the singleton instance of the Database object.
     * 
     * @return Singleton Database.
     */
    public static Database getInstance(DataSource datasource) {
        if (database == null) {
            database = new Database(datasource);
        }

        return database;
    }

    /**
     * Replace the Database singleton object.
     * 
     * @param database new Database object.
     */
    public static void setDatabase(Database database) {
        Database.database = database;
    }

    /**
     * Set a mock connection. For testing purposes.
     * 
     * @param connection Connection to use as a mock.
     */
    public static void setMockConnection(Connection connection) {
        Database.mConnection = connection;
    }

    /**
     * Set whether to throw an exception when connecting or not. For testing
     * purposes.
     * 
     * @param doThrowConnectionException Whether to throw an exception when
     *                                   connecting.
     */
    public static void setDoThrowConnectionException(boolean doThrowConnectionException) {
        Database.doThrowConnectionException = doThrowConnectionException;
    }

    /**
     * The URL of the database.
     */
    private String url;

    private DataSource dataSource;

    /**
     * The private constructor so that a singleton pattern works.
     * 
     * @param datasource
     */
    private Database(DataSource datasource) {
        this.url = Settings.getDatabaseURL();
        this.dataSource = datasource;
    }

    @FunctionalInterface
    public interface SQLFunction<T, R> {
       R apply(T t) throws SQLException;
    }

    @FunctionalInterface
    public interface ResultSetProcessor<T, R> extends SQLFunction<T, R> {
    }

    public <T> T queryWithResult(Connection con, SQLFunction<Connection, PreparedStatement> statementMaker, SQLFunction<ResultSet, T> resultSetProcessor)
            throws SQLException {
        T result = null;
        boolean previousAutoCommit = con.getAutoCommit();
        con.setAutoCommit(false);
        try(PreparedStatement statement = statementMaker.apply(con)) {
            try(ResultSet rs = statement.executeQuery()) {
                result = resultSetProcessor.apply(rs);
            } catch(SQLException e) {
                LOGGER.error("Something executing the statement", e);
                throw e;
            }
        } catch(SQLException e) {
            if (con != null ) {
                con.rollback();
                con.setAutoCommit(previousAutoCommit);
            }
            LOGGER.error("Something preparing the statement", e);
            throw e;
        }
        con.setAutoCommit(previousAutoCommit);
        return result;
    }
 
    public <T> T queryWithResult(SQLFunction<Connection, PreparedStatement> statementMaker, SQLFunction<ResultSet, T> resultSetProcessor)
            throws SQLException {
        try(Connection con = createConnection()) {
            return queryWithResult(con, statementMaker, resultSetProcessor);
        } catch(SQLException e) {
            LOGGER.error("Something went wrong closing the connection", e);
            throw e;
        }
    }
   
    /**
     * Query for an integer in the database.
     * 
     * @param statementMaker Function that takes the Connection with the database
     *                       and returns a prepared statement to execute.
     * @param column         Column of the resulting data to extract the integer
     *                       from.
     * @return Integer received from querying using the preparedstatement made using
     *         the statementMaker and extracting the integer from the given column.
     * @throws SQLException
     */
    public Integer queryInt(SQLFunction<Connection, PreparedStatement> statementMaker, String column)
            throws SQLException {
        Integer result = queryWithResult(statementMaker, (rs) -> {
            Integer value = null;
            if (rs.next()) {
                value = rs.getInt(column);
            }
            return value;
        });
        return result;
/*
        Connection con = null;
        Integer value = null;
        try {
            con = createConnection();

            PreparedStatement statement = statementMaker.apply(con);

            ResultSet result = statement.executeQuery();
            if (result.next()) {
                value = result.getInt(column);
            }
        } catch (SQLException e) {
            LOGGER.error("Something went wrong with preparing the statement", e);
        }
        return value;
*/
    };

    /**
     * Query for a float in the database.
     * 
     * @param statementMaker Function that takes the Connection with the database
     *                       and returns a prepared statement to execute.
     * @param column         Column of the resulting data to extract the float from.
     * @return Float received from querying using the preparedstatement made using
     *         the statementMaker and extracting the float from the given column.
     * @throws SQLException
     */
    public Float queryFloat(SQLFunction<Connection, PreparedStatement> statementMaker, String column)
            throws SQLException {
        Float result = queryWithResult(statementMaker, (rs) -> {
            Float value = null;
            if (rs.next()) {
                value = rs.getFloat(column);
            }
            return value;
        });
        return result;
        /*
        Connection con = null;
        Float value = null;
        try {
            con = createConnection();

            PreparedStatement statement = statementMaker.apply(con);

            ResultSet result = statement.executeQuery();
            if (result.next()) {
                value = result.getFloat(column);
            }
        } catch (SQLException e) {
            LOGGER.error("Something went wrong with preparing the statement", e);
        }
        return value;
*/        
    }

    /**
     * Query for a String in the database.
     * 
     * @param statementMaker Function that takes the Connection with the database
     *                       and returns a prepared statement to execute.
     * @param column         Column of the resulting data to extract the string
     *                       from.
     * @return String received from querying using the preparedstatement made using
     *         the statementMaker and extracting the string from the given column.
     * @throws SQLException
     */
    public String queryString(SQLFunction<Connection, PreparedStatement> statementMaker, String column)
            throws SQLException {
        String result = queryWithResult(statementMaker, (rs) -> {
            String value = null;
            if (rs.next()) {
                value = rs.getString(column);
            }
            return value;
        });
        return result;
/*
        Connection con = null;
        String value = null;
        try {
            con = createConnection();

            PreparedStatement statement = statementMaker.apply(con);

            ResultSet result = statement.executeQuery();
            if (result.next()) {
                value = result.getString(column);
            }
        } catch (SQLException e) {
            LOGGER.error("Something went wrong with preparing the statement", e);
        }
        return value;
*/        
    }

    /**
     * Query in the database.
     * 
     * @param mFunction Function that takes the Connection with the database
     *                       and returns a prepared statement to execute.
     * @return An int with the number of rows affected by the query.
     * @throws SQLException
     */
    public int update(SQLFunction<Connection, PreparedStatement> mFunction) throws SQLException {
        Integer result = notQuery(mFunction, (statement) -> {
            return statement.executeUpdate();
        });
        return result;
/*
        try(Connection con = createConnection()) {
            try(PreparedStatement statement = statementMaker.apply(con)) {
                try {
                    return statement.executeUpdate();
                } catch(SQLException e) {
                    LOGGER.error("Something executing the statement", e);
                    throw e;
                }
            } catch(SQLException e) {
                LOGGER.error("Something preparing the statement", e);
                throw e;
            }
        } catch(SQLException e) {
            LOGGER.error("Something went wrong closing the connection", e);
            throw e;
        }
*/
    /*
        Connection con = null;
        try {
            con = createConnection();

            PreparedStatement statement = statementMaker.apply(con);

            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            LOGGER.error("Something went wrong with preparing the statement", e);
        }
        return false;
*/
    }

    public <T> List<T> inserReturningIds(Class<T> clazz, SQLFunction<Connection, PreparedStatement> statementMaker) throws SQLException {
        return notQuery(statementMaker, (statement) -> {
            int rowCount = statement.executeUpdate();
            List<T> generatedIds = new ArrayList<>(rowCount);
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                while (generatedKeys.next()) {
                    if (clazz.isAssignableFrom(Integer.class)) {
                        @SuppressWarnings("unchecked")
                        T value = (T)Integer.valueOf(generatedKeys.getInt(1));
                        generatedIds.add(value);
                    } else if (clazz.isAssignableFrom(Long.class)){
                        @SuppressWarnings("unchecked")
                        T value = (T)Long.valueOf(generatedKeys.getLong(1));
                        generatedIds.add(value);
                    } else if (clazz.isAssignableFrom(Float.class)) {
                        @SuppressWarnings("unchecked")
                        T value = (T)Float.valueOf(generatedKeys.getFloat(1));
                        generatedIds.add(value);
                    } else if (clazz.isAssignableFrom(Double.class)){
                        @SuppressWarnings("unchecked")
                        T value = (T)Double.valueOf(generatedKeys.getDouble(1));
                        generatedIds.add(value);
                    } else if (clazz.isAssignableFrom(String.class)){
                        @SuppressWarnings("unchecked")
                        T value = (T)generatedKeys.getString(1);
                        generatedIds.add(value);
                    } else if (clazz.isAssignableFrom(Date.class)){
                        @SuppressWarnings("unchecked")
                        T value = (T)generatedKeys.getDate(1);
                        generatedIds.add(value);
                    } else {
                        @SuppressWarnings("unchecked")
                        T value = (T)generatedKeys.getObject(1);
                        generatedIds.add(value);
                    }
                }
            } catch(SQLException e) {
                LOGGER.error("Something went getting generated ids", e);
                throw e;
            }
            return generatedIds;
        });
    }

    public <T> T notQuery(Connection con, SQLFunction<Connection, PreparedStatement> statementMaker, SQLFunction<PreparedStatement, T> statementProcessor) throws SQLException {
        T result = null;
        boolean previousAutoCommit = con.getAutoCommit();
        try(PreparedStatement statement = statementMaker.apply(con)) {
            try {
                result = statementProcessor.apply(statement);
            } catch(SQLException e) {
                LOGGER.error("Something went executing the statement", e);
                throw e;
            }
        } catch(SQLException e) {
            if (con != null ) {
                con.rollback();
                con.setAutoCommit(previousAutoCommit);
            }
            LOGGER.error("Something went preparing the statement", e);
            throw e;
        }
        con.setAutoCommit(previousAutoCommit);
        return result;
    }

    public <T> T notQuery(SQLFunction<Connection, PreparedStatement> statementMaker, SQLFunction<PreparedStatement, T> statementProcessor) throws SQLException {
        try(Connection con = createConnection()) {
            return notQuery(con, statementMaker, statementProcessor);
        } catch(SQLException e) {
            LOGGER.error("Something went wrong closing the connection", e);
            throw e;
        }
    }

    /**
     * Sets up the database driver to be able to start a connection.
     */
    private void setUpDriver() {
        if (!doSetup) {
            return;
        }
    }
    
    /**
     * Starts a connection with the database.
     * 
     * @return The connection with the database.
     * @throws SQLException If a database error occurs.
     */
    public Connection createConnection() throws SQLException {
        setUpDriver();
        if (doThrowConnectionException) {
            throw new SQLException();
        }
        if (mConnection == null) {
            if (dataSource != null) {
                return dataSource.getConnection(Settings.getDatabaseUsername(), Settings.getDatabasePassword());
            }
            return DriverManager.getConnection(url, Settings.getDatabaseUsername(), Settings.getDatabasePassword());
        }
        return mConnection;
    }

    /**
     * Close a connection with the database.
     * 
     * @param con The connection to be closed.
     */
    public void closeConnection(Connection con) {
        if (con != null) {
            try {
                con.close();
            } catch (Exception e) {
                LOGGER.error("Could not close the connection with the database", e);
            }
        }
    }
}
