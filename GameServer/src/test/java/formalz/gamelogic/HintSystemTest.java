/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
package formalz.gamelogic;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

import formalz.data.Problem;
import formalz.data.Problem.Feature;
import formalz.data.Queries;
import formalz.gamelogic.gamestate.HintSystem;

public class HintSystemTest {
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    Queries MQueries;

    @Mock
    Problem MProblem;

    /**
     * Test whether a new problem is processed correctly.
     */
    @Test
    public void testProcessNewProblem() {
        when(MQueries.getHint(eq("forAll"), anyString())).thenReturn("forAllHint");
        when(MQueries.getHint(eq("exists"), anyString())).thenReturn("existsHint");

        Queries.setQueries(MQueries);

        List<Feature> features = new ArrayList<Feature>();
        features.add(Feature.forAll);
        features.add(Feature.exists);

        when(MProblem.getFeatures()).thenReturn(features);

        HintSystem hintSystem = new HintSystem();
        hintSystem.processNewProblem(MProblem);

        List<String> hints = hintSystem.getHints();

        assertEquals("forAllHint", hints.get(0));
        assertEquals("existsHint", hints.get(1));

        Queries.setQueries(null);
    }

}
