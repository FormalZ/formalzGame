/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
package formalz.data;

import java.sql.Timestamp;

/**
 * The class contains the basic information about a game session.
 * @author Ludiscite
 * @version 1.0
 */
public class GameSession
{
    private int userId;
    private int problemId;
    private int deadline;
    private Timestamp createdAt;

    /**
     * Create an object with basic game session information.
     * @param userId The id of the user.
     * @param problemId The id of the teacher problem.
     * @param createdAt The moment the token was created.
     */
    public GameSession(int userId, int problemId, Timestamp createdAt)
    {
        this.userId = userId;
        this.problemId = problemId;
        this.createdAt = createdAt;
    }

    /**
     * @return userId The id of the user.
     */
    public int getUserId()
    {
        return userId;
    }

    /**
     * @return problemId The id of the teacher problem.
     */
    public int getProblemId()
    {
        return problemId;
    }

    /**
     * @return createdAt The moment the token was created.
     */
    public Timestamp getCreatedAt()
    {
        return createdAt;
    }

    /**
     * checks if the current time difference is between the set margins.
     * @return Whether the time difference is between set margins.
     */
    public boolean checkDifference()
    {
        return (System.currentTimeMillis() - createdAt.getTime()) <= Settings.getMaxSessionCreatedDifference();
    }

    public void setDeadline(int deadline){ this.deadline = deadline; }

    public boolean checkDeadline()
    {
        return deadline != 0 && deadline < (System.currentTimeMillis()/1000);
    }
}
