/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
package formalz.JsonRunnables;

import static org.junit.Assert.*;

import org.junit.Test;

import com.google.gson.JsonArray;

import formalz.haskellapi.jsonrunnables.JsonAccumulatorRunnable;

public class JsonAccumulatorRunnableTest {
    /**
     * Test whether a string is correctly accumulated.
     */
    @Test
    public void constructStringTest() {
        JsonAccumulatorRunnable runnable = new JsonAccumulatorRunnable();

        runnable.run("a:int", 1, false);
        runnable.run("b:bool", true, false);
        JsonArray ja = new JsonArray();
        ja.add(2.3f);
        ja.add(false);
        runnable.run("c", ja);

        assertEquals("(a=1);(b=true);(c=[2.3,false])", runnable.toString());
    }
}
