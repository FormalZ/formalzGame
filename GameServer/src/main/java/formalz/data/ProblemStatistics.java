/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
package formalz.data;

/**
 * The class that contains the statistics for a repo problem queried from the database.
 * @author Ludiscite
 * @version 1.0
 */
public class ProblemStatistics
{
    private float averageWavesNeeded;
    private float averagePreMistakeCount;
    private float averagePostMistakeCount;

    /**
     * Constructor for statistics of a repo problem.
     * @param averageWavesNeeded The average amount of waves needed to complete the problem.
     * @param averagePreMistakeCount The average amount of mistakes on the precondition.
     * @param averagePostMistakeCount The average amount of mistakes on the postcondition.
     */
    public ProblemStatistics(float averageWavesNeeded, float averagePreMistakeCount, float averagePostMistakeCount)
    {
        this.averageWavesNeeded = averageWavesNeeded;
        this.averagePreMistakeCount = averagePreMistakeCount;
        this.averagePostMistakeCount = averagePostMistakeCount;
    }

    /**
     * Returns the average amount of waves players needed to complete the problem.
     * @return Average amount of waves needed.
     */
    public float getAverageWavesNeeded()
    {
        return this.averageWavesNeeded;
    }

    /**
     * Returns the average amount of mistakes made on the precondition of the problem.
     * @return Average amount of mistakes made on the precondition.
     */
    public float getAveragePreMistakeCount()
    {
        return this.averagePreMistakeCount;
    }

    /**
     * Returns the average amount of mistakes made on the postcondition of the problem.
     * @return Average amount of mistakes made on the postcondition.
     */
    public float getAveragePostMistakeCount()
    {
        return this.averagePostMistakeCount;
    }

}
