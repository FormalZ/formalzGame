/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
package data;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import data.Problem.Feature;
import logger.Logger;

/**
 * The class with the main functionality corresponding to executing queries on the database.
 * @author Ludiscite
 * @version 1.0
 */
public class Database
{
    private static Database database;

    /**
     * Get the singleton instance of the Database object.
     * @return Singleton Database.
     */
    public static Database getInstance()
    {
        if (database == null)
        {
            database = new Database();
        }

        return database;
    }

    /**
     * Replace the Database singleton object.
     * @param database new Database object.
     */
    public static void setDatabase(Database database)
    {
        Database.database = database;
    }

    /**
     * The private constructor so that a singleton pattern works.
     */
    private Database()
    {

    }

    /**
     * The URL of the database.
     */
    private static final String url = Settings.getDatabaseURLPrefix() + Settings.getDatabaseHostURL() + "/" + Settings.getDatabaseName()
            + "?useSSL=" + Settings.getDatabaseUseSSL();

    /**
     * Query for a Problem object in the database.
     * @param statementMaker Function that takes the Connection with the database and returns a prepared statement to execute.
     * @param isTeacherProblem Whether the problem is a teacherProblem or not.
     * @param lives If this is a random repo problem, indicates the amount of lives for this problem
     * @param money If this is a random repo problem, indicates the amount of money for this problem
     * @param deadline If this is a random repo problem, indicates the deadline for this problem
     * @return Problem received from querying using the preparedstatement made using the statementMaker.
     */
    public Problem queryProblem(Function<Connection, PreparedStatement> statementMaker, boolean isTeacherProblem, int lives, int money, int deadline)
    {
        Problem p = null;
        Connection con = null;
        try
        {
            con = createConnection();

            PreparedStatement statement = statementMaker.apply(con);

            ResultSet result = statement.executeQuery();
            if (result.next())
            {
                p = constructProblem(result, isTeacherProblem, lives, money, deadline);
            }
        }
        catch (SQLException e)
        {
            Logger.log("Something went wrong with preparing the statement");
            e.printStackTrace();
        }
        finally
        {
            closeConnection(con);
        }
        return p;

    }

    /**
     * Query for a GameSession object in the database.
     * @param statementMaker Function that takes the Connection with the database and returns a prepared statement to execute.
     * @return GameSession received from querying using the preparedstatement made using the statementMaker.
     */
    public GameSession queryGameSession(Function<Connection, PreparedStatement> statementMaker)
    {
        GameSession gs = null;
        Connection con = null;
        try
        {
            con = createConnection();

            PreparedStatement statement = statementMaker.apply(con);
            ResultSet result = statement.executeQuery();

            if (result.next())
            {
                gs = new GameSession(result.getInt("user_id"), result.getInt("problem_id"), result.getTimestamp("created_at"));
            }

        }
        catch (SQLException e)
        {
            Logger.log("Something went wrong with preparing the statement");
            e.printStackTrace();
        }
        finally
        {
            closeConnection(con);
        }
        return gs;
    }

    /**
     * Query for an integer in the database.
     * @param statementMaker Function that takes the Connection with the database and returns a prepared statement to execute.
     * @param column Column of the resulting data to extract the integer from.
     * @return Integer received from querying using the preparedstatement made using the statementMaker and extracting the integer from the
     * given column.
     */
    public Integer queryInt(Function<Connection, PreparedStatement> statementMaker, String column)
    {
        Connection con = null;
        Integer value = null;
        try
        {
            con = createConnection();

            PreparedStatement statement = statementMaker.apply(con);

            ResultSet result = statement.executeQuery();
            if (result.next())
            {
                value = result.getInt(column);
            }
        }
        catch (SQLException e)
        {
            Logger.log("Something went wrong with preparing the statement");
            e.printStackTrace();
        }
        return value;
    };

    /**
     * Query for a float in the database.
     * @param statementMaker Function that takes the Connection with the database and returns a prepared statement to execute.
     * @param column Column of the resulting data to extract the float from.
     * @return Float received from querying using the preparedstatement made using the statementMaker and extracting the float from the
     * given column.
     */
    public Float queryFloat(Function<Connection, PreparedStatement> statementMaker, String column)
    {
        Connection con = null;
        Float value = null;
        try
        {
            con = createConnection();

            PreparedStatement statement = statementMaker.apply(con);

            ResultSet result = statement.executeQuery();
            if (result.next())
            {
                value = result.getFloat(column);
            }
        }
        catch (SQLException e)
        {
            Logger.log("Something went wrong with preparing the statement");
            e.printStackTrace();
        }
        return value;
    }

    /**
     * Query for a String in the database.
     * @param statementMaker Function that takes the Connection with the database and returns a prepared statement to execute.
     * @param column Column of the resulting data to extract the string from.
     * @return String received from querying using the preparedstatement made using the statementMaker and extracting the string from the
     * given column.
     */
    public String queryString(Function<Connection, PreparedStatement> statementMaker, String column)
    {
        Connection con = null;
        String value = null;
        try
        {
            con = createConnection();

            PreparedStatement statement = statementMaker.apply(con);

            ResultSet result = statement.executeQuery();
            if (result.next())
            {
                value = result.getString(column);
            }
        }
        catch (SQLException e)
        {
            Logger.log("Something went wrong with preparing the statement");
            e.printStackTrace();
        }
        return value;
    }

    /**
     * Query in the database.
     * @param statementMaker Function that takes the Connection with the database and returns a prepared statement to execute.
     * @return A boolean value representing whether the query was successfully executed.
     */
    public boolean queryVoid(Function<Connection, PreparedStatement> statementMaker)
    {
        Connection con = null;
        try
        {
            con = createConnection();

            PreparedStatement statement = statementMaker.apply(con);

            statement.executeUpdate();
            return true;
        }
        catch (SQLException e)
        {
            Logger.log("Something went wrong with preparing the statement");
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Construct a problem object from a given result set.
     * @param result ResultSet from a query.
     * @param isTeacherProblem WHether the problem is a teacherProblem or not.
     * @param lives If this is a random repo problem, indicates the amount of lives for this problem
     * @param money If this is a random repo problem, indicates the amount of money for this problem
     * @param deadline If this is a random repo problem, indicates the deadline for this problem
     * @return Problem object constructed from the result set.
     * @throws SQLException Exception about what went wrong with the SQL query.
     */
    private Problem constructProblem(ResultSet result, boolean isTeacherProblem, int lives, int money, int deadline) throws SQLException
    {
        int id = result.getInt("id");
        String header = result.getString("header");
        String desc = result.getString("description");
        String pre = result.getString("pre_conditions");
        String post = result.getString("post_conditions");
        int dif = result.getInt("difficulty");
        if (isTeacherProblem) {
            lives = result.getInt("lives");
            money = result.getInt("money");
            deadline = result.getInt("deadline");
        }

        List<Feature> features = new ArrayList<Feature>();

        if (result.getBoolean("hasForAll"))
        {
            features.add(Feature.forAll);
        }

        if (result.getBoolean("hasExists"))
        {
            features.add(Feature.exists);
        }

        if (result.getBoolean("hasArrays"))
        {
            features.add(Feature.arrays);
        }

        if (result.getBoolean("hasEquality"))
        {
            features.add(Feature.equality);
        }

        if (result.getBoolean("hasLogicOperator"))
        {
            features.add(Feature.logicOperator);
        }

        if (result.getBoolean("hasRelationalComparer"))
        {
            features.add(Feature.relationalComparer);
        }

        if (result.getBoolean("hasArithmetic"))
        {
            features.add(Feature.arithmetic);
        }

        if (result.getBoolean("hasImplication"))
        {
            features.add(Feature.implication);
        }

        return new Problem(id, header, desc, pre, post, dif, lives, money, deadline, isTeacherProblem, features);
    }

    /**
     * Sets up the database driver to be able to start a connection.
     */
    private void setUpDriver()
    {
        if (!doSetup)
        {
            return;
        }

        try
        {
            Class.forName(Settings.getDatabaseDriver()).newInstance();
        }
        catch (Exception e)
        {
            Logger.log("The databaseDriver class is invalid or missing");
            e.printStackTrace();
        }
    }

    private static boolean doSetup = true;

    /**
     * Whether to do the connection setup. Usefull for testing.
     * @param doSetup Whether to the connection setup.
     */
    public static void setDoSetup(boolean doSetup)
    {
        Database.doSetup = doSetup;
    }

    /**
     * Starts a connection with the database.
     * @return The connection with the database.
     * @throws SQLException If a database error occurs.
     */
    public Connection createConnection() throws SQLException
    {
        setUpDriver();
        if (doThrowConnectionException)
        {
            throw new SQLException();
        }
        if (mConnection == null)
        {
            return DriverManager.getConnection(url, Settings.getDatabaseUsername(), Settings.getDatabasePassword());
        }
        return mConnection;
    }

    /**
     * A possible mock Connection for testing purposes.
     */
    private static Connection mConnection;

    /**
     * Whether to throw an exception when attempting to connect. For testing purposes.
     */
    private static boolean doThrowConnectionException = false;

    /**
     * Set a mock connection. For testing purposes.
     * @param connection Connection to use as a mock.
     */
    public static void setMockConnection(Connection connection)
    {
        Database.mConnection = connection;
    }

    /**
     * Set whether to throw an exception when connecting or not. For testing purposes.
     * @param doThrowConnectionException Whether to throw an exception when connecting.
     */
    public static void setDoThrowConnectionException(boolean doThrowConnectionException)
    {
        Database.doThrowConnectionException = doThrowConnectionException;
    }

    /**
     * Close a connection with the database.
     * @param con The connection to be closed.
     */
    public void closeConnection(Connection con)
    {
        if (con != null)
        {
            try
            {
                con.close();
            }
            catch (Exception e)
            {
                Logger.log("Could not close the connection with the database");
                e.printStackTrace();
            }
        }
    }
}
