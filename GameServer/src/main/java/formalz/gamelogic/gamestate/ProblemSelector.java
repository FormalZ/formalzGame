/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
package formalz.gamelogic.gamestate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import formalz.data.Problem;
import formalz.data.Queries;
import formalz.data.Problem.Feature;
import formalz.gamelogic.gamestate.AdaptiveDifficulty.FeatureRequirement;

/**
 * The system for selecting a random problem from the problem repo
 * 
 * @author Ludiscite
 * @version 1.0
 */
public class ProblemSelector {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProblemSelector.class);

    public static Problem getRandomProblem(int minimumDifficulty, int maximumDifficulty,
            Map<Feature, FeatureRequirement> features, List<Integer> oldIds, int lives, int money, int deadline) {

        Problem problem = null;

        // First priority (strict difficulty and features and no problems already
        // completed)
        problem = Queries.getInstance().getRandomRepoProblemWithProperties(oldIds, features, minimumDifficulty,
                maximumDifficulty, lives, money, deadline);
        if (problem != null) {
            return problem;
        }

        Map<Feature, FeatureRequirement> newFeatures = new HashMap<Feature, FeatureRequirement>();
        for (Entry<Feature, FeatureRequirement> pair : features.entrySet()) {
            if (pair.getKey() == Feature.forAll || pair.getKey() == Feature.exists || pair.getKey() == Feature.arrays
                    || pair.getKey() == Feature.implication) {
                newFeatures.put(pair.getKey(), pair.getValue());
            }
        }

        // Second priority (strict difficulty and features)
        problem = Queries.getInstance().getRandomRepoProblemWithProperties(new ArrayList<Integer>(), newFeatures,
                minimumDifficulty, maximumDifficulty, lives, money, deadline);
        if (problem != null) {
            return problem;
        }

        // Third priority (arbitrary)
        problem = Queries.getInstance().getRandomRepoProblem(new ArrayList<Integer>(), lives, money, deadline);
        if (problem == null) {
            // XXX
           LOGGER.error("No random problem was found, something went wrong with the database of problem selection");
        }
        return problem;
    }
}
