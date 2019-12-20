/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
package data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

import data.Problem.Feature;
import gamelogic.gamestate.AdaptiveDifficulty.FeatureRequirement;
import logger.Logger;

/**
 * The class with all important queries.
 * @author Ludiscite
 * @version 1.0
 */
public class Queries
{
    private static Queries queries;

    /**
     * Get the singleton instance of the Queries object.
     * @return Singleton Queries object.
     */
    public static Queries getInstance()
    {
        if (queries == null)
        {
            queries = new Queries();
        }

        return queries;
    }

    /**
     * Replace the Queries singleton object.
     * @param queries Queries object to set.
     */
    public static void setQueries(Queries queries)
    {
        Queries.queries = queries;
    }

    /**
     * The private constructor so that a singleton pattern works.
     */
    private Queries()
    {

    }

    private static String getProblemByIdQuery = "SELECT * FROM problems WHERE id = ?";

    /**
     * Fetch a teacher problem from the database given the id of a teacher problem.
     * @param id Id of the teacher problem.
     * @return Teacher problem.
     */
    public Problem getProblemById(int id)
    {
        return Database.getInstance().queryProblem(getSimpleStatementPreparer(getProblemByIdQuery, id), true, 0, 0,0);
    }

    private String query;

    public String getUserTrackingCode(int playerID){
        query = "SELECT trackingCode from users where id = ?";
        return Database.getInstance().queryString(getSimpleStatementPreparer(query, playerID), "trackingCode");
    }

    public String getProblemTrackingCode(int problemID){
        query = "SELECT trackingCode from problems where id = ?";
        return Database.getInstance().queryString(getSimpleStatementPreparer(query, problemID), "trackingCode");
    }

    /**
     * Fetch a random problem from the problem repo, except for chosen ids.
     * @param oldIds List of Ids that won't be chosen.
     * @param lives If this is a random repo problem, indicates the amount of lives for this problem
     * @param money If this is a random repo problem, indicates the amount of money for this problem
     * @param deadline If this is a random repo problem, indicates the deadline for this problem
     * @return Repo problem whose id is not in oldIds.
     */
    public Problem getRandomRepoProblem(List<Integer> oldIds, int lives, int money, int deadline)
    {
        String where = createIdNot(oldIds.size());
        query = "SELECT * FROM problemrepo ";
        if (where.length() != 0)
        {
            query += "WHERE ";
            query += where;
        }
        query += "ORDER BY RAND() LIMIT 1";

        return Database.getInstance().queryProblem((Connection con) ->
        {
            try
            {
                PreparedStatement statement = con.prepareStatement(query);
                for (int n = 1; n <= oldIds.size(); n++)
                {
                    statement.setInt(n, oldIds.get(n - 1));
                }
                return statement;
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
            return null;
        } , false, lives, money, deadline);
    }

    /**
     * Fetch a random problem from the problem repo with given problems.
     * @param oldIds Ids of problems already completed.
     * @param features Features of the problem to have.
     * @param minDifficulty Minimal difficulty of the problem.
     * @param maxDifficulty Maximal difficulty of the problem.
     * @param lives If this is a random repo problem, indicates the amount of lives for this problem
     * @param money If this is a random repo problem, indicates the amount of money for this problem
     * @param deadline If this is a random repo problem, indicates the deadline for this problem
     * @return Problem with given property.
     */
    public Problem getRandomRepoProblemWithProperties(List<Integer> oldIds, Map<Feature, FeatureRequirement> features, int minDifficulty,
            int maxDifficulty, int lives, int money, int deadline)
    {
        String where = createIdNot(oldIds.size());
        query = "SELECT * FROM problemrepo ";
        if (where.length() != 0)
        {
            query += "WHERE ";
            query += where;
            query += " AND ? <= difficulty AND difficulty <= ?";
        }
        else
        {
            query += "WHERE ? <= difficulty AND difficulty <= ?";
        }

        List<Boolean> values = new ArrayList<Boolean>();

        for (Entry<Feature, FeatureRequirement> entry : features.entrySet())
        {
            Feature feature = entry.getKey();
            FeatureRequirement requirement = entry.getValue();
            if (requirement == FeatureRequirement.optional)
            {
                continue;
            }
            String add = " AND ";
            values.add(requirement == FeatureRequirement.have);

            switch (feature)
            {
                case arithmetic:
                    add += "hasArithmetic";
                    break;
                case arrays:
                    add += "hasArrays";
                    break;
                case equality:
                    add += "hasEquality";
                    break;
                case exists:
                    add += "hasExists";
                    break;
                case forAll:
                    add += "hasForAll";
                    break;
                case implication:
                    add += "hasImplication";
                    break;
                case logicOperator:
                    add += "hasLogicOperator";
                    break;
                case relationalComparer:
                    add += "hasRelationalComparer";
                    break;
            }
            add += " = ? ";
            query += add;
        }

        query += " ORDER BY RAND() LIMIT 1";

        return Database.getInstance().queryProblem((Connection con) ->
        {
            try
            {
                PreparedStatement statement = con.prepareStatement(query);
                for (int n = 1; n <= oldIds.size(); n++)
                {
                    statement.setInt(n, oldIds.get(n - 1));
                }
                statement.setInt(oldIds.size() + 1, minDifficulty);
                statement.setInt(oldIds.size() + 2, maxDifficulty);
                for (int i = 0; i < values.size(); i++)
                {
                    statement.setBoolean(oldIds.size() + 3 + i, values.get(i));
                }
                return statement;
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
            return null;
        } , false, lives, money, deadline);
    }

    /**
     * Create a part of an SQL query where the ids may not equal the free variables.
     * @param size Amount of ids.
     * @return Part of an SQL query.
     */
    private String createIdNot(int size)
    {
        StringBuffer buffer = new StringBuffer();
        if (size != 0)
        {
            buffer.append("NOT (");
            for (int n = 0; n < size; n++)
            {
                buffer.append("id= ? ");
                if (n == size - 1)
                {
                    break;
                }
                buffer.append(" OR ");
            }
            buffer.append(") ");
        }
        return buffer.toString();
    }

    /**
     * Return the least amount of problems that needs to be completed before the teacher problem.
     * @param problemId Id of the teacher problem.
     * @return Least amount of problems to complete before the given teacher problem with the id.
     */
    public Integer getProblemCount(int problemId)
    {
        query = "SELECT problemcount FROM problems WHERE id = ? ";
        return Database.getInstance().queryInt(getSimpleStatementPreparer(query, problemId), "problemcount");
    }

    /**
     * Returns path for a given problemId.
     * @param problemId Id of the teacher problem.
     * @return Path Path for the given teacher problem.
     */
    public String getPath(int problemId)
    {
        query = "SELECT path FROM paths WHERE problem_id = ? ";
        return Database.getInstance().queryString(getSimpleStatementPreparer(query, problemId), "path");
    }

    /**
     * Queries the database for the recorded hash, and returns if it matches the input
     * @param hashCheck the input hash
     * @param problemId the problem id
     * @param userId the user id
     * @return a boolean value of if the recorded hash matches the input hash
     */
    public boolean getHashCheck(int hashCheck, int problemId, int userId)
    {
        query = "SELECT hash FROM gamesessions WHERE problem_id = " + problemId + " AND user_id = ?";
        int hashDb = Database.getInstance().queryInt(getSimpleStatementPreparer(query, userId), "hash");
        return hashDb == hashCheck;
    }

    /**
     * Returns hint for a given key.
     * @param key Key of the hint.
     * @return Hint.
     */
    public String getHint(String key)
    {
        query = "SELECT hint as \"c\" FROM hints WHERE id = ? ";
        return Database.getInstance().queryString((Connection con) ->
        {
            try
            {
                PreparedStatement statement = con.prepareStatement(query);
                statement.setString(1, key);
                return statement;
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
            return null;
        } , "c");
    }

    /**
     * Returns statistics about a repo problem.
     * @param problemId Id of the repo problem.
     * @return Statistics about the repo problem.
     */
    public ProblemStatistics getProblemStatistics(int problemId)
    {
        return new ProblemStatistics(getAverageWaves(problemId), getAveragePreMistakes(problemId), getAveragePostMistakes(problemId));
    }

    /**
     * Returns the average amount of waves needed to complete the repo problem of the given id.
     * @param problemId Id of the repo problem.
     * @return Average amount of waves needed to complete the repo problem.
     */
    public Float getAverageWaves(int problemId)
    {
        query = "SELECT AVG(waves) as \"c\" FROM problemrepostatistics WHERE problemrepo_id = ? ";
        return Database.getInstance().queryFloat(getSimpleStatementPreparer(query, problemId), "c");
    }

    /**
     * Returns the average amount of mistakes made on the precondition the given id.
     * @param problemId Id of the repo problem.
     * @return Average amount of mistakes made on the precondition the repo problem.
     */
    public Float getAveragePreMistakes(int problemId)
    {
        query = "SELECT AVG(pre_mistakes) as \"c\" FROM problemrepostatistics WHERE problemrepo_id = ? ";
        return Database.getInstance().queryFloat(getSimpleStatementPreparer(query, problemId), "c");
    }

    /**
     * Returns the average amount of mistakes made on the postcondition the given id.
     * @param problemId Id of the repo problem.
     * @return Average amount of mistakes made on the postcondition the repo problem.
     */
    public Float getAveragePostMistakes(int problemId)
    {
        query = "SELECT AVG(post_mistakes) as \"c\" FROM problemrepostatistics WHERE problemrepo_id = ? ";
        return Database.getInstance().queryFloat(getSimpleStatementPreparer(query, problemId), "c");
    }

    /**
     * Updates the recorded hash for a game session
     * @param hash the new hash value
     * @param userId the user id
     * @param problemId the problem id
     */
    public boolean updateHash(int hash, int userId, int problemId)
    {
        String query = "UPDATE gamesessions SET hash = ? WHERE problem_id = ? AND user_id = ?";
        return Database.getInstance().queryVoid((Connection con) ->
        {
            try
            {
                PreparedStatement statement = con.prepareStatement(query);
                statement.setInt(1, hash);
                statement.setInt(2, problemId);
                statement.setInt(3, userId);
                return statement;
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
            return null;
        });
    }

    private String insertScoreQuery = "INSERT INTO score (user_id,problem_id,score,legitimate) VALUES ( ? , ? , ? , ?)";

    /**
     * Insert a score in the database.
     * @param userId Id of the user that gets the score.
     * @param problemId Id of the problem the user got the score on.
     * @param score Score.
     * @param legitimate Whether the score was gained without cheating.
     * @return Whether the insertion succeeded.
     */
    public boolean insertScore(int userId, int problemId, int score, boolean legitimate)
    {
        return Database.getInstance().queryVoid((Connection con) ->
        {
            try
            {
                PreparedStatement statement = con.prepareStatement(insertScoreQuery);
                statement.setInt(1, userId);
                statement.setInt(2, problemId);
                statement.setInt(3, score);
                statement.setBoolean(4, legitimate);
                return statement;
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
            return null;
        });
    }

    private String insertStatisticsQuery = "INSERT INTO problemstatistics (problem_id, playedgame_id, totaltime, problemtime, sidetrackcount, pre_mistakes, post_mistakes) VALUES ( ? , ? , ? , ? , ? , ? , ?)";

    /**
     * Insert game session statistics in the database.
     * @param problemId Id of the problem
     * @param playedGamesId Played game Id.
     * @param totalTime Total time spent on the problem
     * @param problemTime Total time spent on the teacher problem
     * @param sidetrackCount Amount of sidetrack problems played
     * @param preMistakes Amount of mistakes made on precondition.
     * @param postMistakes Amount of mistakes made on postcondition.
     * @return Whether the insertion succeeded or failed
     */
    public boolean insertStatistics(int problemId, int playedGamesId, int totalTime, int problemTime, int sidetrackCount, int preMistakes,
            int postMistakes)
    {
        return Database.getInstance().queryVoid((con) ->
        {
            try
            {
                PreparedStatement statement = con.prepareStatement(insertStatisticsQuery);
                statement.setInt(1, problemId);
                statement.setInt(2, playedGamesId);
                statement.setInt(3, totalTime);
                statement.setInt(4, problemTime);
                statement.setInt(5, Math.max(0, sidetrackCount));
                statement.setInt(6, preMistakes);
                statement.setInt(7, postMistakes);
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
     * Fetch the game session connected to the given token.
     * @param token Token of the game session.
     * @return Game session of the token.
     */
    public GameSession getGameSession(String token)
    {
        query = "SELECT user_id, problem_id, created_at FROM gamesessions WHERE token = ?";
        return Database.getInstance().queryGameSession(getSimpleStatementPreparer(query, token));
    }

    /**
     * Create an id for the game session.
     * @param userId The id of the user.
     * @param problemId The id of the problem.
     * @return Id for the game session.
     */
    public Integer createPlayedGamesId(int userId, int problemId)
    {
        Logger.log("Created played Game Id");
        Database.getInstance().queryVoid((con) ->
        {
            try
            {
                PreparedStatement statement = con.prepareStatement("INSERT INTO playedgames(user_id, problem_id) VALUES(?, ?)");
                statement.setInt(1, userId);
                statement.setInt(2, problemId);
                return statement;
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
            return null;
        });
        return Database.getInstance().queryInt((con) ->
        {
            try
            {
                PreparedStatement statement = con
                        .prepareStatement("SELECT id as \"c\" FROM playedgames where user_id = ? ORDER BY created_at DESC LIMIT 1");
                statement.setInt(1, userId);
                return statement;
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return null;
        } , "c");
    }

    private String insertRepoStatisticsQuery = "INSERT INTO problemrepostatistics (problemrepo_id, orderby, playedgame_id, waves, pre_mistakes, post_mistakes) VALUES ( ? , ? , ? , ? , ? , ?)";

    /**
     * Insert statistics about a repo problem.
     * @param problemId Id of the repo problem.
     * @param playedGamesId Id of the game session.
     * @param order When the problem was played.
     * @param waves Amount of waves it took to complete the problem.
     * @param preMistakes Amount of mistakes made on the preconditions.
     * @param postMistakes Amount of mistakes made on the postconditions.
     * @return Whether the insertion was succesful.
     */
    public boolean insertRepoStatistics(int problemId, int playedGamesId, int order, int waves, int preMistakes, int postMistakes)
    {
        Logger.log("Inserted repo statistics");
        return Database.getInstance().queryVoid((con) ->
        {
            try
            {
                PreparedStatement statement = con.prepareStatement(insertRepoStatisticsQuery);
                statement.setInt(1, problemId);
                statement.setInt(2, order);
                statement.setInt(3, playedGamesId);
                statement.setInt(4, waves);
                statement.setInt(5, preMistakes);
                statement.setInt(6, postMistakes);
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
     * Creates a function that creates a prepared statement for a connection from a query and a single string.
     * @param query Query to create prepared statement of.
     * @param string Value to set in the prepared statement.
     * @return Function that takes a connection and returns a prepared statement.
     */
    private Function<Connection, PreparedStatement> getSimpleStatementPreparer(String query, String string)
    {
        return (Connection con) ->
        {
            try
            {
                PreparedStatement statement = con.prepareStatement(query);
                statement.setString(1, string);
                return statement;
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
            return null;
        };
    }

    /**
     * Creates a function that creates a prepared statement for a connection from a query and a single string.
     * @param query Query to create prepared statement of.
     * @param value Value to set in the prepared statement.
     * @return Function that takes a connection and returns a prepared statement.
     */
    private Function<Connection, PreparedStatement> getSimpleStatementPreparer(String query, int value)
    {
        return (Connection con) ->
        {
            try
            {
                PreparedStatement statement = con.prepareStatement(query);
                statement.setInt(1, value);
                return statement;
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
            return null;
        };
    }
}
