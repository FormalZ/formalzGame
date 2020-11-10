/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
package formalz.integration;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import formalz.data.Database;

public class IntegrationTestDatabaseFiller
{
    public static final String TOKEN = "123abc456DEF";
    public static final int HASH = 1;

    /**
     * Fill the test database for testing purposes.
     * 
     * @throws SQLException
     */
    public static void fillDatabase() throws SQLException
    {
        fillRooms();
        fillUsers();
        fillProblems();
        fillPaths();
        fillProblemRepo();
        fillGameSessions();
        fillHints();
    }

    /**
     * Clear the test database.
     * 
     * @throws SQLException
     */
    public static void clearDatabase() throws SQLException
    {
        Database.getInstance().update((con) ->
        {
            try
            {
                PreparedStatement statement = con.prepareStatement("SET GLOBAL FOREIGN_KEY_CHECKS=0");
                return statement;
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
            return null;
        });

        clearTable("problemrepostatistics");

        clearTable("hints");
        clearTable("gamesessions");
        clearTable("problemrepo");
        clearTable("problems");
        clearTable("paths");
        clearTable("rooms");
        clearTable("users");

        Database.getInstance().update((con) ->
        {
            try
            {
                PreparedStatement statement = con.prepareStatement("SET GLOBAL FOREIGN_KEY_CHECKS=1");
                return statement;
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
            return null;
        });
    }

    /**
     * Fill the rooms table.
     * 
     * @throws SQLException
     */
    private static void fillRooms() throws SQLException
    {
        Database.getInstance().update((con) ->
        {
            try
            {
                PreparedStatement statement = con.prepareStatement("INSERT INTO rooms(id, name, description, url) VALUES(?, ?, ?, ?)");
                statement.setInt(1, 1);
                statement.setString(2, "RoomName");
                statement.setString(3, "RoomDescription");
                statement.setString(4, "RoomURL");
                return statement;
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
            return null;
        });
    }

    /**
     * Fill the users table.
     * 
     * @throws SQLException
     */
    private static void fillUsers() throws SQLException
    {
        Database.getInstance().update((con) ->
        {
            try
            {
                PreparedStatement statement = con
                        .prepareStatement("INSERT INTO users(id, name, email, password, remember_token) VALUES(?, ?, ?, ?, ?)");
                statement.setInt(1, 1);
                statement.setString(2, "UserName");
                statement.setString(3, "UserEmail");
                statement.setString(4, "UserPassword");
                statement.setString(5, "UserRememberToken");
                return statement;
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
            return null;
        });
    }

    /**
     * Fill the problems table.
     * 
     * @throws SQLException
     */
    private static void fillProblems() throws SQLException
    {
        Database.getInstance().update((con) ->
        {
            try
            {
                PreparedStatement statement = con.prepareStatement(
                        "INSERT INTO problems(id, header, description, pre_conditions, post_conditions, difficulty, hasForAll, hasExists, hasArrays, hasEquality, hasLogicOperator, hasRelationalComparer, hasArithmetic, hasImplication, room_id, problemcount) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                statement.setInt(1, 1);
                statement.setString(2, "public static void multiply(int a, int b)");
                statement.setString(3, "Multiply not-strictly negative integers a and b resulting in c");
                statement.setString(4, "(a <= 0) && (b <= 0)");
                statement.setString(5, "c >= 0");
                statement.setInt(6, 1);
                statement.setBoolean(7, false);
                statement.setBoolean(8, false);
                statement.setBoolean(9, false);
                statement.setBoolean(10, false);
                statement.setBoolean(11, true);
                statement.setBoolean(12, true);
                statement.setBoolean(13, false);
                statement.setBoolean(14, false);
                statement.setInt(15, 1);
                statement.setInt(16, 1);
                return statement;
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
            return null;
        });
    }

    /**
     * Fill the gamesessions table.
     * 
     * @throws SQLException
     */
    private static void fillGameSessions() throws SQLException
    {
        Database.getInstance().update((con) ->
        {
            try
            {
                PreparedStatement statement = con.prepareStatement(
                        "INSERT INTO gamesessions(id, user_id, problem_id, token, hash, created_at) VALUES(?, ?, ?, ?, ?, CURDATE())");
                statement.setInt(1, 1);
                statement.setInt(2, 1);
                statement.setInt(3, 1);
                statement.setString(4, TOKEN);
                statement.setInt(5, HASH);
                return statement;
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
            return null;
        });
    }

    /**
     * Fill the hints table.
     * 
     * @throws SQLException
     */
    private static void fillHints() throws SQLException
    {
        insertHint("arithmetic",
                "The +,-,*,/,% are basic arithmetic operations to be used on numbers. They can be used together with variables to ensure some properties, for example, a + 1 > b * 3. These operations return a number.");
        insertHint("arrays",
                "An array a can be indexed using [] brackets, which is often used together with a forall statement, as you can check a property for every element in the array this way. The length of the array can be checked using .Length property. It is good to remember checking that the array exists by using an a != null statement.");
        insertHint("equality",
                "You can check the equality of two elements using ==, for example a == 3. On the contrary, you can explicitly check the inequality of two elements using !=, for example b != a. Do not forget that the elements should be of the same type, 3 == true is not valid! These operations return a boolean expression.");
        insertHint("exists",
                "An exists statement can be used to ensure the existence of an element in an array with a certain property. For example, the statement exists(a, i -> a[i] == 3) returns true when there is an element in a which is equal to 3.");
        insertHint("forAll",
                "A forall statement can be used to ensure a property for every element in an array. For example, with the statement forall(a, i -> a[i] <= 3) returns true when every element in the array a is less than or equal to 3.");
        insertHint("implication",
                "An implication can be used for more complex logic. You can interpret a imp b as if a is true, then so should b. The implication therefore only returns true if this logic applies. ");
        insertHint("logicOperator",
                "You can tie boolean logic together using the &&, ||, and the ! operators. The && operator corresponds to the logic AND gate, the || corresponds to the logic OR gate, and the ! corresponds to the logic NOT gate. Each can be used in combination with boolean expressions to return another boolean expression. Remember that the result of a pre- or post-condition should always be a boolean value corresponding to the validity of the value plugged in.");
        insertHint("relationalComparer",
                "You can compare numbers with the <, >, <=, and, >= comparers. For example, if you want a variable a to be strictly positive, you could use a > 0. These operations always return a boolean value.");
    }

    /**
     * Insert a hint into the test database.
     * 
     * @param id   Key of the hint.
     * @param hint The actual hint.
     * @throws SQLException
     */
    private static void insertHint(String id, String hint) throws SQLException
    {
        Database.getInstance().update((con) ->
        {
            try
            {
                PreparedStatement statement = con.prepareStatement("INSERT INTO hints(id, hint) VALUES(?, ?)");
                statement.setString(1, id);
                statement.setString(2, hint);
                return statement;
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
            return null;
        });
    }

    /**
     * Fill the paths table.
     * 
     * @throws SQLException
     */
    private static void fillPaths() throws SQLException
    {
        Database.getInstance().update((con) ->
        {
            try
            {
                PreparedStatement statement = con.prepareStatement("INSERT INTO paths(problem_id, path) VALUES(?, ?)");
                statement.setInt(1, 1);
                statement.setString(2,
                        "(2.27);[[5,0].[5,135].[5,-90].[5,-45].[5,45].[5,-45].[5,45]];(32.7);[[5,45].[5,-45].[5,-90].[5,-45].[5,90].[5,-135].[5,135].[5,-45]]");
                return statement;
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
            return null;
        });
    }

    /**
     * Fill the problemrepo table.
     * 
     * @throws SQLException
     */
    private static void fillProblemRepo() throws SQLException
    {
        Database.getInstance().update((con) ->
        {
            try
            {
                PreparedStatement statement = con.prepareStatement(
                        "INSERT INTO problemrepo(id, header, description, pre_conditions, post_conditions, difficulty, hasForAll, hasExists, hasArrays, hasEquality, hasLogicOperator, hasRelationalComparer, hasArithmetic, hasImplication) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                statement.setInt(1, 1);
                statement.setString(2, "public static void multiply(int a, int b)");
                statement.setString(3, "Multiply not-strictly negative integers a and b resulting in c");
                statement.setString(4, "(a <= 0) && (b <= 0)");
                statement.setString(5, "c >= 0");
                statement.setInt(6, 1);
                statement.setBoolean(7, false);
                statement.setBoolean(8, false);
                statement.setBoolean(9, false);
                statement.setBoolean(10, false);
                statement.setBoolean(11, true);
                statement.setBoolean(12, true);
                statement.setBoolean(13, false);
                statement.setBoolean(14, false);
                return statement;
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
            return null;
        });
    }

    /**
     * Clear a table.
     * 
     * @param tableName Name of the table to clear.
     * @throws SQLException
     */
    private static void clearTable(String tableName) throws SQLException
    {
        Database.getInstance().update((con) ->
        {
            try
            {
                PreparedStatement statement = con.prepareStatement("DELETE FROM " + tableName + "");
                return statement;
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
            return null;
        });
    }
}
