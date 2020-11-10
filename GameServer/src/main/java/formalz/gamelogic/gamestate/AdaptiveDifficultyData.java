/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
package formalz.gamelogic.gamestate;

import java.util.List;

import formalz.data.Problem;
import formalz.data.Problem.Feature;

/**
 * The class that keeps track of some data that is usefull for adaptive difficulty.
 * @author Ludiscite
 * @version 1.0
 */
public class AdaptiveDifficultyData
{
    private int teacherProblemDifficulty;
    private List<Feature> features;
    private int questionAmount;

    private int preMistakeCount;
    private int postMistakeCount;

    private int[] featureUsage;

    private boolean[] featureMask;

    /**
     * Constructor of an AdaptiveDifficultyData object.
     * @param teacherProblem The final teacher problem.
     * @param currentProblem The current problem.
     * @param questionAmount The least amount of problems before the final problem.
     * @param preMistakeCount Amount of mistakes currently made on the precondition of the current problem.
     * @param postMistakeCount Amount of mistakes currently made on the postcondition of the current problem.
     * @param featureUsage Amount of times different features have been used in the pre-and postconditions.
     */
    public AdaptiveDifficultyData(Problem teacherProblem, Problem currentProblem, int questionAmount, int preMistakeCount,
            int postMistakeCount, int[] featureUsage)
    {
        this.teacherProblemDifficulty = teacherProblem.getDifficulty();
        this.features = teacherProblem.getFeatures();
        this.questionAmount = questionAmount;
        this.preMistakeCount = preMistakeCount;
        this.postMistakeCount = postMistakeCount;

        this.featureUsage = featureUsage;

        if (currentProblem != null)
        {
            this.featureMask = createFeatureMask(currentProblem.getFeatures());
        }
        else
        {
            this.featureMask = new boolean[] { false, false, false, false, false, false, false, false };
        }
    }

    /**
     * Returns the difficulty of the teacher problem.
     * @return Difficulty of the teacher problem.
     */
    public int getTeacherProblemDifficulty()
    {
        return this.teacherProblemDifficulty;
    }

    /**
     * Returns whether the teacher problem contains the forall quantifier.
     * @return Whether the teacher problem contains the forall quantifier.
     */
    public boolean getTeacherProblemHasForAll()
    {
        return features.contains(Feature.forAll);
    }

    /**
     * Returns whether the teacher problem contains the exists quantifier.
     * @return Whether the teacher problem contains the exists quantifier.
     */
    public boolean getTeacherProblemHasExists()
    {
        return features.contains(Feature.exists);
    }

    /**
     * Returns whether the teacher problem contains arrays.
     * @return Whether the teacher problem contains arrays.
     */
    public boolean getTeacherProblemHasArrays()
    {
        return features.contains(Feature.arrays);
    }

    /**
     * Returns whether the teacher problem contains equality.
     * @return Whether the teacher problem contains equality.
     */
    public boolean getTeacherProblemHasEquality()
    {
        return features.contains(Feature.equality);
    }

    /**
     * Returns whether the teacher problem contains logic operator.
     * @return Whether the teacher problem contains logic operator.
     */
    public boolean getTeacherProblemHasLogicOperator()
    {
        return features.contains(Feature.logicOperator);
    }

    /**
     * Returns whether the teacher problem contains relational comparer.
     * @return Whether the teacher problem contains relational comparer.
     */
    public boolean getTeacherProblemHasRelationalComparer()
    {
        return features.contains(Feature.relationalComparer);
    }

    /**
     * Returns whether the teacher problem contains arithmetic.
     * @return Whether the teacher problem contains arithmetic.
     */
    public boolean getTeacherProblemHasArithmetic()
    {
        return features.contains(Feature.arithmetic);
    }

    /**
     * Returns whether the teacher problem contains implication.
     * @return Whether the teacher problem contains implication.
     */
    public boolean getTeacherProblemHasImplication()
    {
        return features.contains(Feature.implication);
    }

    /**
     * Returns the least amount of questions before the final problem.
     * @return The least amount of questions before the final problem.
     */
    public int getQuestionAmount()
    {
        return this.questionAmount;
    }

    /**
     * Returns the amount of mistakes made on the pre condition.
     * @return The amount of mistakes made on the pre condition.
     */
    public int getPreMistakeCount()
    {
        return this.preMistakeCount;
    }

    /**
     * Returns the amount of mistakes made on the post condition.
     * @return The amount of mistakes made on the post condition.
     */
    public int getPostMistakeCount()
    {
        return this.postMistakeCount;
    }

    /**
     * Returns the amount of times certain features have been used for the current problem.
     * @return Amount of times features were used by the student.
     */
    public int[] getFeatureUsage()
    {
        return featureUsage;
    }

    /**
     * Returns a boolean mask of what features are in the current problem.
     * @return Mask of which features are in the teacher solution.
     */
    public boolean[] getFeatureMask()
    {
        return featureMask;
    }

    private boolean[] createFeatureMask(List<Feature> features)
    {
        boolean[] mask = new boolean[] { false, false, false, false, false, false, false, false };
        for (Feature feature : features)
        {
            switch (feature)
            {
                case forAll:
                    mask[0] = true;
                    break;
                case exists:
                    mask[1] = true;
                    break;
                case arrays:
                    mask[2] = true;
                    break;
                case equality:
                    mask[3] = true;
                    break;
                case logicOperator:
                    mask[4] = true;
                    break;
                case relationalComparer:
                    mask[5] = true;
                    break;
                case arithmetic:
                    mask[6] = true;
                    break;
                case implication:
                    mask[7] = true;
                    break;
            }
        }
        return mask;
    }
}
