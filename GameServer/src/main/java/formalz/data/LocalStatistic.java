/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
package formalz.data;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Stores statistic information about a problem/challenge played by a user.
 * @author Ludiscite
 * @version 1.0
 */
public class LocalStatistic
{
    private boolean isRepoProblem;
    private int problemId;

    private Timestamp startTime;
    private Timestamp endTime;

    private int preMistakeCount;
    private int postMistakeCount;

    private List<WaveData> waves;

    /**
     * Constructor for a Client object.
     * @param problemId Id of the problem.
     * @param isRepoProblem Whether the problem is a repo problem or a teacher problem.
     * @param startTime When the user started the problem.
     */
    public LocalStatistic(int problemId, boolean isRepoProblem, Timestamp startTime)
    {
        this.isRepoProblem = isRepoProblem;
        this.problemId = problemId;
        this.startTime = startTime;
        this.endTime = new Timestamp(Long.MAX_VALUE);
        this.waves = new ArrayList<WaveData>();
    }

    /**
     * Returns whether the problem is a repo problem, else it is a teacher problem.
     * @return isRepoProblem Whether the problem is a repo problem or a teacher problem.
     */
    public boolean getIsRepoProblem()
    {
        return isRepoProblem;
    }

    /**
     * Returns the id of the problem.
     * @return problemId The id of the problem.
     */
    public int getProblemId()
    {
        return problemId;
    }

    /**
     * Returns the timestamp of the start of the problem
     * @return startTime When the user started the problem.
     */
    public Timestamp getStartTime()
    {
        return startTime;
    }

    /**
     * Return the amount of mistakes made on the precondition.
     * @return preMistakeCount.
     */
    public int getPreMistakeCount()
    {
        return preMistakeCount;
    }

    /**
     * Increment the precondition mistake count with one.
     */
    public void incrementPreMistakeCount()
    {
        preMistakeCount++;
    }

    /**
     * Return the amount of mistakes made on the postcondition.
     * @return preMistakeCount.
     */
    public int getPostMistakeCount()
    {
        return postMistakeCount;
    }

    /**
     * Increment the postcondition mistake count with one.
     */
    public void incrementPostMistakeCount()
    {
        postMistakeCount++;
    }

    /**
     * Set the time the related problem was finished at.
     * @param endTime The time the user ended the problem.
     */
    public void setEndTime(Timestamp endTime)
    {
        this.endTime = endTime;
    }

    /**
     * Returns the amount of time spent on the problem.
     * @return Time spent on problem in milliseconds.
     */
    public long getTimeSpent()
    {
        return endTime.getTime() - startTime.getTime();
    }

    /**
     * Add data about a wave.
     * @param wave Data about a wave.
     */
    public void addWaveData(WaveData wave)
    {
        waves.add(wave);
    }

    /**
     * Returns the amount of waves completed.
     * @return Amount of waves completed.
     */
    public int getWaveAmount()
    {
        return waves.size();
    }

    public WaveData getLastWave() { return waves.get(waves.size() - 1); }
}
