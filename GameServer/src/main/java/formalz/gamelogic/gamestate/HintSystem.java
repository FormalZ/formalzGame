/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
package formalz.gamelogic.gamestate;

import java.util.ArrayList;
import java.util.List;

import formalz.data.Problem;
import formalz.data.Queries;
import formalz.data.Problem.Feature;

/**
 * The system generating the hints.
 * 
 * @author Ludiscite
 * @version 1.0
 */
public class HintSystem {
    private List<Feature> hadFeatures;

    private List<String> hints;

    /**
     * Constructor for a hint system.
     */
    public HintSystem() {
        hadFeatures = new ArrayList<Feature>();
    }

    /**
     * Returns the hints for the current problem.
     * 
     * @return List of hints.
     */
    public List<String> getHints() {
        return this.hints;
    }

    /**
     * Processes a new problem to find hints.
     * 
     * @param problem Problem to process.
     */
    public void processNewProblem(Problem problem) {
        List<String> h = new ArrayList<String>();
        List<Feature> problemFeatures = problem.getFeatures();

        for (Feature feature : problemFeatures) {
            if (!hadFeatures.contains(feature)) {
                hadFeatures.add(feature);
                h.add(Queries.getInstance().getHint(feature.toString(), ""));
            }
        }

        this.hints = h;
    }
}
