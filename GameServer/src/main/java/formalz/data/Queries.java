/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
package formalz.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import formalz.data.Database.SQLFunction;
import formalz.data.Problem.Feature;
import formalz.gamelogic.gamestate.AdaptiveDifficulty.FeatureRequirement;

/**
 * The class with all important queries.
 * 
 * @author Ludiscite
 * @version 1.0
 */
public class Queries {
    private static final Logger LOGGER = LoggerFactory.getLogger(Queries.class);

    private static Queries queries;

    /**
     * Get the singleton instance of the Queries object.
     * 
     * @return Singleton Queries object.
     */
    public static Queries getInstance() {
        if (queries == null) {
            queries = new Queries();
        }

        return queries;
    }

    /**
     * Replace the Queries singleton object.
     * 
     * @param queries Queries object to set.
     */
    public static void setQueries(Queries queries) {
        Queries.queries = queries;
    }

    /**
     * The private constructor so that a singleton pattern works.
     */
    private Queries() {

    }

    private static String getProblemByIdQuery = "SELECT * FROM problems WHERE id = ?";

    /**
     * Fetch a teacher problem from the database given the id of a teacher problem.
     * 
     * @param id Id of the teacher problem.
     * @return Teacher problem.
     */
    public Problem getProblemById(int id) {
        Problem result = null;
        try {
            result = queryProblem(getSimpleStatementPreparer(getProblemByIdQuery, id), true, 0, 0, 0);
        } catch (SQLException e) {
            LOGGER.error(String.format("Can not get problem with id: %d", id), e);
        }
        return result;
    }


    /**
     * Query for a Problem object in the database.
     * 
     * @param statementMaker   Function that takes the Connection with the database
     *                         and returns a prepared statement to execute.
     * @param isTeacherProblem Whether the problem is a teacherProblem or not.
     * @param lives            If this is a random repo problem, indicates the
     *                         amount of lives for this problem
     * @param money            If this is a random repo problem, indicates the
     *                         amount of money for this problem
     * @param deadline         If this is a random repo problem, indicates the
     *                         deadline for this problem
     * @return Problem received from querying using the preparedstatement made using
     *         the statementMaker.
     * @throws SQLException
     */
    private Problem queryProblem(SQLFunction<Connection, PreparedStatement> statementMaker, boolean isTeacherProblem,
            int lives, int money, int deadline) throws SQLException {

        return Database.getInstance().queryWithResult(statementMaker, (rs) -> {
            Problem value = null;
            if (rs.next()) {
                value = constructProblem(rs, isTeacherProblem, lives, money, deadline);
            }
            return value;
        });

/*
        Problem p = null;
        Connection con = null;
        try {
            con = createConnection();

            PreparedStatement statement = statementMaker.apply(con);

            ResultSet result = statement.executeQuery();
            if (result.next()) {
                p = constructProblem(result, isTeacherProblem, lives, money, deadline);
            }
            statement.close();
        } catch (SQLException e) {
            LOGGER.error("Something went wrong with preparing the statement", e);
        } finally {
            closeConnection(con);
        }
        return p;
*/
    }

    /**
     * Construct a problem object from a given result set.
     * 
     * @param result           ResultSet from a query.
     * @param isTeacherProblem WHether the problem is a teacherProblem or not.
     * @param lives            If this is a random repo problem, indicates the
     *                         amount of lives for this problem
     * @param money            If this is a random repo problem, indicates the
     *                         amount of money for this problem
     * @param deadline         If this is a random repo problem, indicates the
     *                         deadline for this problem
     * @return Problem object constructed from the result set.
     * @throws SQLException Exception about what went wrong with the SQL query.
     */
    private Problem constructProblem(ResultSet result, boolean isTeacherProblem, int lives, int money, int deadline)
            throws SQLException {
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

        if (result.getBoolean("hasForAll")) {
            features.add(Feature.forAll);
        }

        if (result.getBoolean("hasExists")) {
            features.add(Feature.exists);
        }

        if (result.getBoolean("hasArrays")) {
            features.add(Feature.arrays);
        }

        if (result.getBoolean("hasEquality")) {
            features.add(Feature.equality);
        }

        if (result.getBoolean("hasLogicOperator")) {
            features.add(Feature.logicOperator);
        }

        if (result.getBoolean("hasRelationalComparer")) {
            features.add(Feature.relationalComparer);
        }

        if (result.getBoolean("hasArithmetic")) {
            features.add(Feature.arithmetic);
        }

        if (result.getBoolean("hasImplication")) {
            features.add(Feature.implication);
        }

        return new Problem(id, header, desc, pre, post, dif, lives, money, deadline, isTeacherProblem, features);
    }

    public String getUserTrackingCode(int playerID) {
        String query = "SELECT trackingCode from users where id = ?";
        String result = null;
        try {
            result = Database.getInstance().queryString(getSimpleStatementPreparer(query, playerID), "trackingCode");
        } catch (SQLException e) {
            LOGGER.error("Get user tracking code failed", e);
        }
        return result;
    }

    public String getProblemTrackingCode(int problemID) {
        String query = "SELECT trackingCode from problems where id = ?";
        String result = null;
        try {
            result = Database.getInstance().queryString(getSimpleStatementPreparer(query, problemID), "trackingCode");
        } catch (SQLException e) {
            LOGGER.error("Get problem tracking code failed", e);
        }
        return result;
    }

    /**
     * Fetch a random problem from the problem repo, except for chosen ids.
     * 
     * @param oldIds   List of Ids that won't be chosen.
     * @param lives    If this is a random repo problem, indicates the amount of
     *                 lives for this problem
     * @param money    If this is a random repo problem, indicates the amount of
     *                 money for this problem
     * @param deadline If this is a random repo problem, indicates the deadline for
     *                 this problem
     * @return Repo problem whose id is not in oldIds.
     */
    public Problem getRandomRepoProblem(List<Integer> oldIds, int lives, int money, int deadline) {
        String where = createIdNot(oldIds.size());
        StringBuilder queryBuilder = new StringBuilder("SELECT * FROM problemrepo");
        if (where.length() != 0) {
            queryBuilder.append(" WHERE ").append(where);
        }
        queryBuilder.append(" ORDER BY RAND() LIMIT 1");

        String query = queryBuilder.toString();
        Problem result = null;
        try {
            result = queryProblem((con) -> {
                PreparedStatement statement = con.prepareStatement(query);
                for (int n = 1; n <= oldIds.size(); n++) {
                    statement.setInt(n, oldIds.get(n - 1));
                }
                return statement;
            }, false, lives, money, deadline);
        } catch (SQLException e) {
            LOGGER.error("Error getting random problem", e);
        }
        return result;
    }

    /**
     * Fetch a random problem from the problem repo with given problems.
     * 
     * @param oldIds        Ids of problems already completed.
     * @param features      Features of the problem to have.
     * @param minDifficulty Minimal difficulty of the problem.
     * @param maxDifficulty Maximal difficulty of the problem.
     * @param lives         If this is a random repo problem, indicates the amount
     *                      of lives for this problem
     * @param money         If this is a random repo problem, indicates the amount
     *                      of money for this problem
     * @param deadline      If this is a random repo problem, indicates the deadline
     *                      for this problem
     * @return Problem with given property.
     */
    public Problem getRandomRepoProblemWithProperties(List<Integer> oldIds, Map<Feature, FeatureRequirement> features,
            int minDifficulty, int maxDifficulty, int lives, int money, int deadline) {
        String where = createIdNot(oldIds.size());
        StringBuilder queryBuilder = new StringBuilder("SELECT * FROM problemrepo");
        if (where.length() != 0) {
            queryBuilder.append(" WHERE ").append(where).append(" AND ? <= difficulty AND difficulty <= ?");
        } else {
            queryBuilder.append(" WHERE ? <= difficulty AND difficulty <= ?");
        }

        List<Boolean> values = new ArrayList<Boolean>();

        for (Entry<Feature, FeatureRequirement> entry : features.entrySet()) {
            Feature feature = entry.getKey();
            FeatureRequirement requirement = entry.getValue();
            if (requirement == FeatureRequirement.optional) {
                continue;
            }
            queryBuilder.append(" AND ");
            values.add(requirement == FeatureRequirement.have);

            switch (feature) {
                case arithmetic:
                    queryBuilder.append("hasArithmetic");
                    break;
                case arrays:
                    queryBuilder.append("hasArrays");
                    break;
                case equality:
                    queryBuilder.append("hasEquality");
                    break;
                case exists:
                    queryBuilder.append("hasExists");
                    break;
                case forAll:
                    queryBuilder.append("hasForAll");
                    break;
                case implication:
                    queryBuilder.append("hasImplication");
                    break;
                case logicOperator:
                    queryBuilder.append("hasLogicOperator");
                    break;
                case relationalComparer:
                    queryBuilder.append("hasRelationalComparer");
                    break;
            }
            queryBuilder.append(" = ? ");
        }

        queryBuilder.append(" ORDER BY RAND() LIMIT 1");

        String query = queryBuilder.toString();
        Problem result = null;
        try {
            result = queryProblem((con) -> {
                PreparedStatement statement = con.prepareStatement(query);
                for (int n = 1; n <= oldIds.size(); n++) {
                    statement.setInt(n, oldIds.get(n - 1));
                }
                statement.setInt(oldIds.size() + 1, minDifficulty);
                statement.setInt(oldIds.size() + 2, maxDifficulty);
                for (int i = 0; i < values.size(); i++) {
                    statement.setBoolean(oldIds.size() + 3 + i, values.get(i));
                }
                return statement;
            }, false, lives, money, deadline);
        } catch (SQLException e) {
            LOGGER.error("Error getting random problem", e);
        }
        return result;
    }

    /**
     * Create a part of an SQL query where the ids may not equal the free variables.
     * 
     * @param size Amount of ids.
     * @return Part of an SQL query.
     */
    private String createIdNot(int size) {
        StringBuilder buffer = new StringBuilder();
        if (size != 0) {
            buffer.append("NOT (");
            for (int n = 0; n < size; n++) {
                buffer.append("id= ? ");
                if (n == size - 1) {
                    break;
                }
                buffer.append(" OR ");
            }
            buffer.append(") ");
        }
        return buffer.toString();
    }

    /**
     * Return the least amount of problems that needs to be completed before the
     * teacher problem.
     * 
     * @param problemId Id of the teacher problem.
     * @return Least amount of problems to complete before the given teacher problem
     *         with the id.
     */
    public Integer getProblemCount(int problemId) {
        String query = "SELECT problemcount FROM problems WHERE id = ? ";
        Integer result = null;
        try {
            result = Database.getInstance().queryInt(getSimpleStatementPreparer(query, problemId), "problemcount");
        } catch (SQLException e) {
            LOGGER.error("Can not get problem count", e);
        }
        return result;
    }

    /**
     * Returns path for a given problemId.
     * 
     * @param problemId Id of the teacher problem.
     * @return Path Path for the given teacher problem.
     */
    public String getPath(int problemId) {
        String query = "SELECT path FROM paths WHERE problem_id = ? ";
        String result = null;
        try {
            result = Database.getInstance().queryString(getSimpleStatementPreparer(query, problemId), "path");
        } catch (SQLException e) {
            LOGGER.error("Can not get problem path", e);
        }
        return result;
    }

    /**
     * Queries the database for the recorded hash, and returns if it matches the
     * input
     * 
     * @param hashCheck the input hash
     * @param problemId the problem id
     * @param userId    the user id
     * @return a boolean value of if the recorded hash matches the input hash
     */
    public boolean getHashCheck(int hashCheck, int problemId, int userId) {
        String query = "SELECT hash FROM gamesessions WHERE problem_id = " + problemId + " AND user_id = ?";
        Integer hashDb = null;
        boolean check = false;
        try {
            hashDb = Database.getInstance().queryInt(getSimpleStatementPreparer(query, userId), "hash");
            check = hashDb == hashCheck;
        } catch (SQLException e) {
            LOGGER.error("Can no check hash", e);
        }
        return check;
    }

    /**
     * Returns hint for a given key.
     * 
     * @param key Key of the hint.
     * @return Hint.
     */
    public String getHint(String key, String defaultValue) {
        String query = "SELECT hint as `c` FROM hints WHERE id = ? ";
        String hint = defaultValue;
        try {
            return Database.getInstance().queryString((Connection con) -> {
                PreparedStatement statement = con.prepareStatement(query);
                statement.setString(1, key);
                return statement;
            }, "`c`");
        } catch (SQLException e) {
            LOGGER.error("getHint failed", e);
        }
        return hint;
    }

    /**
     * Returns statistics about a repo problem.
     * 
     * @param problemId Id of the repo problem.
     * @return Statistics about the repo problem.
     */
    public ProblemStatistics getProblemStatistics(int problemId) {
        return new ProblemStatistics(getAverageWaves(problemId), getAveragePreMistakes(problemId),
                getAveragePostMistakes(problemId));
    }

    /**
     * Returns the average amount of waves needed to complete the repo problem of
     * the given id.
     * 
     * @param problemId Id of the repo problem.
     * @return Average amount of waves needed to complete the repo problem.
     */
    public Float getAverageWaves(int problemId) {
        String query = "SELECT AVG(waves) as `c` FROM problemrepostatistics WHERE problemrepo_id = ? ";
        Float result = null;
        try {
            result = Database.getInstance().queryFloat(getSimpleStatementPreparer(query, problemId), "`c`");
        } catch (SQLException e) {
            LOGGER.error("Can not get average waves", e);
        }
        return result;
    }

    /**
     * Returns the average amount of mistakes made on the precondition the given id.
     * 
     * @param problemId Id of the repo problem.
     * @return Average amount of mistakes made on the precondition the repo problem.
     */
    public Float getAveragePreMistakes(int problemId) {
        String query = "SELECT AVG(pre_mistakes) as `c` FROM problemrepostatistics WHERE problemrepo_id = ? ";
        Float result = null;
        try {
            result = Database.getInstance().queryFloat(getSimpleStatementPreparer(query, problemId), "`c`");
        } catch (SQLException e) {
            LOGGER.error("Can not get average pre mistakes", e);
        }
        return result;
    }

    /**
     * Returns the average amount of mistakes made on the postcondition the given
     * id.
     * 
     * @param problemId Id of the repo problem.
     * @return Average amount of mistakes made on the postcondition the repo
     *         problem.
     */
    public Float getAveragePostMistakes(int problemId) {
        String query = "SELECT AVG(post_mistakes) as `c` FROM problemrepostatistics WHERE problemrepo_id = ? ";
        Float result = null;
        try {
            result = Database.getInstance().queryFloat(getSimpleStatementPreparer(query, problemId), "`c`");
        } catch (SQLException e) {
            LOGGER.error("Can not get average post mistakes", e);
        }
        return result;
    }

    /**
     * Updates the recorded hash for a game session
     * 
     * @param hash      the new hash value
     * @param userId    the user id
     * @param problemId the problem id
     */
    public boolean updateHash(int hash, int userId, int problemId) {
        String query = "UPDATE gamesessions SET hash = ? WHERE problem_id = ? AND user_id = ?";
        int result = -1;
        try {
            result = Database.getInstance().update((Connection con) -> {
                PreparedStatement statement = con.prepareStatement(query);
                statement.setInt(1, hash);
                statement.setInt(2, problemId);
                statement.setInt(3, userId);
                return statement;
            });
        } catch (SQLException e) {
            LOGGER.error("Can not update hash", e);
        }
        return result == 1;
    }

    /**
     * Insert a score in the database.
     * 
     * @param userId     Id of the user that gets the score.
     * @param problemId  Id of the problem the user got the score on.
     * @param score      Score.
     * @param legitimate Whether the score was gained without cheating.
     * @return Whether the insertion succeeded.
     */
    public boolean insertScore(int userId, int problemId, int score, boolean legitimate) {
        String insertScoreQuery = "INSERT INTO score (user_id,problem_id,score,legitimate) VALUES ( ? , ? , ? , ?)";
        int result = -1;
        try {
            result = Database.getInstance().update((Connection con) -> {
                PreparedStatement statement = con.prepareStatement(insertScoreQuery);
                statement.setInt(1, userId);
                statement.setInt(2, problemId);
                statement.setInt(3, score);
                statement.setBoolean(4, legitimate);
                return statement;
            });
        } catch (SQLException e) {
            LOGGER.error("Can insert score", e);
        }
        return result == 1;
    }

    /**
     * Insert game session statistics in the database.
     * 
     * @param problemId      Id of the problem
     * @param playedGamesId  Played game Id.
     * @param totalTime      Total time spent on the problem
     * @param problemTime    Total time spent on the teacher problem
     * @param sidetrackCount Amount of sidetrack problems played
     * @param preMistakes    Amount of mistakes made on precondition.
     * @param postMistakes   Amount of mistakes made on postcondition.
     * @return Whether the insertion succeeded or failed
     */
    public boolean insertStatistics(int problemId, int playedGamesId, int totalTime, int problemTime,
            int sidetrackCount, int preMistakes, int postMistakes) {

        String insertStatisticsQuery = "INSERT INTO problemstatistics (problem_id, playedgame_id, totaltime, problemtime, sidetrackcount, pre_mistakes, post_mistakes) VALUES ( ? , ? , ? , ? , ? , ? , ?)";

        int result = -1;
        try {
            result = Database.getInstance().update((con) -> {
                PreparedStatement statement = con.prepareStatement(insertStatisticsQuery);
                statement.setInt(1, problemId);
                statement.setInt(2, playedGamesId);
                statement.setInt(3, totalTime);
                statement.setInt(4, problemTime);
                statement.setInt(5, Math.max(0, sidetrackCount));
                statement.setInt(6, preMistakes);
                statement.setInt(7, postMistakes);
                return statement;
            });
        } catch (SQLException e) {
            LOGGER.error("Can insert statistics", e);
        }
        return result == 1;
    }

    /**
     * Fetch the game session connected to the given token.
     * 
     * @param token Token of the game session.
     * @return Game session of the token.
     */
    public GameSession getGameSession(String token) {
        String query = "SELECT user_id, problem_id, created_at FROM gamesessions WHERE token = ?";
        GameSession result = null;
        try {
            result = queryGameSession(getSimpleStatementPreparer(query, token));
        } catch (SQLException e) {
            LOGGER.error(String.format("Can not get game session %s", token), e);
        }
        return result;
    }

    /**
     * Query for a GameSession object in the database.
     * 
     * @param statementMaker Function that takes the Connection with the database
     *                       and returns a prepared statement to execute.
     * @return GameSession received from querying using the preparedstatement made
     *         using the statementMaker.
     * @throws SQLException
     */
    private GameSession queryGameSession(SQLFunction<Connection, PreparedStatement> statementMaker)
            throws SQLException {
        GameSession result = Database.getInstance().queryWithResult(statementMaker, (rs) -> {
            GameSession value = null;
            if (rs.next()) {
                value = new GameSession(rs.getInt("user_id"), rs.getInt("problem_id"), rs.getTimestamp("created_at"));
            }
            return value;
        });
        return result;
/*
        GameSession gs = null;
        Connection con = null;
        try {
            con = createConnection();

            PreparedStatement statement = statementMaker.apply(con);
            ResultSet result = statement.executeQuery();

            if (result.next()) {
                gs = new GameSession(result.getInt("user_id"), result.getInt("problem_id"),
                        result.getTimestamp("created_at"));
            }

        } catch (SQLException e) {
            LOGGER.error("Something went wrong with preparing the statement", e);
        } finally {
            closeConnection(con);
        }
        return gs;
*/
    }

    /**
     * Create an id for the game session.
     * 
     * @param userId    The id of the user.
     * @param problemId The id of the problem.
     * @return Id for the game session.
     */
    public Integer createPlayedGamesId(int userId, int problemId) {
        LOGGER.trace("Created played Game Id");
        
        List<Integer> ids = Arrays.asList(-1);
        try {
            ids = Database.getInstance().inserReturningIds(Integer.class, (con) -> {
                PreparedStatement statement = con.prepareStatement(
                        "INSERT INTO playedgames(user_id, problem_id) VALUES(?, ?)", Statement.RETURN_GENERATED_KEYS);
                statement.setInt(1, userId);
                statement.setInt(2, problemId);
                return statement;
            });
        } catch (SQLException e) {
            LOGGER.error("Can not insert played games", e);
        }
        return ids.get(0);
/*
        Database.getInstance().update((con) -> {
            PreparedStatement statement = con
                    .prepareStatement("INSERT INTO playedgames(user_id, problem_id) VALUES(?, ?)", Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, userId);
            statement.setInt(2, problemId);
            return statement;
        });
        return Database.getInstance().queryInt((con) -> {
            PreparedStatement statement = con.prepareStatement(
                    "SELECT id as \"c\" FROM playedgames where user_id = ? ORDER BY created_at DESC LIMIT 1");
            statement.setInt(1, userId);
            return statement;
        }, "c");
*/
    }

    /**
     * Insert statistics about a repo problem.
     * 
     * @param problemId     Id of the repo problem.
     * @param playedGamesId Id of the game session.
     * @param order         When the problem was played.
     * @param waves         Amount of waves it took to complete the problem.
     * @param preMistakes   Amount of mistakes made on the preconditions.
     * @param postMistakes  Amount of mistakes made on the postconditions.
     * @return Whether the insertion was succesful.
     */
    public boolean insertRepoStatistics(int problemId, int playedGamesId, int order, int waves, int preMistakes,
            int postMistakes) {

        String insertRepoStatisticsQuery = "INSERT INTO problemrepostatistics (problemrepo_id, orderby, playedgame_id, waves, pre_mistakes, post_mistakes) VALUES ( ? , ? , ? , ? , ? , ?)";
        int rowCount = -1;
        try {
            rowCount = Database.getInstance().update((con) -> {
                PreparedStatement statement = con.prepareStatement(insertRepoStatisticsQuery);
                statement.setInt(1, problemId);
                statement.setInt(2, order);
                statement.setInt(3, playedGamesId);
                statement.setInt(4, waves);
                statement.setInt(5, preMistakes);
                statement.setInt(6, postMistakes);
                return statement;
            });
        } catch (SQLException e) {
            LOGGER.error("Can not insert statistics", e);
        }
        LOGGER.debug("Inserted repo statistics");
        return rowCount > 0;
    }

    /**
     * Creates a function that creates a prepared statement for a connection from a
     * query and a single string.
     * 
     * @param query  Query to create prepared statement of.
     * @param string Value to set in the prepared statement.
     * @return Function that takes a connection and returns a prepared statement.
     */
    private SQLFunction<Connection, PreparedStatement> getSimpleStatementPreparer(String query, String string) {
        return (Connection con) -> {
            PreparedStatement statement = con.prepareStatement(query);
            statement.setString(1, string);
            return statement;
        };
    }

    /**
     * Creates a function that creates a prepared statement for a connection from a
     * query and a single string.
     * 
     * @param query Query to create prepared statement of.
     * @param value Value to set in the prepared statement.
     * @return Function that takes a connection and returns a prepared statement.
     */
    private SQLFunction<Connection, PreparedStatement> getSimpleStatementPreparer(String query, int value) {
        return (Connection con) -> {
            PreparedStatement statement = con.prepareStatement(query);
            statement.setInt(1, value);
            return statement;
        };
    }
}
