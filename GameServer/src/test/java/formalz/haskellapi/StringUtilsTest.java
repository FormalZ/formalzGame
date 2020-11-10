/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
package formalz.haskellapi;

import static org.junit.Assert.*;

import org.junit.Test;

import formalz.utils.StringUtils;
import formalz.utils.TryParser;

public class StringUtilsTest extends StringUtils {
    /**
     * Test whether a string containing escape characters are escaped correctly for
     * in JSON.
     */
    @Test
    public void testEscapeJSONCorrect() {
        String input = "public static float real1_1(float a) {\\r\\n        pre(a >= (2 - 1 + 1));\\r\\n        a += a;\\r\\n        post(a >= (4 - 3 + 3));\\r\\n    }\\r\\n";
        String output = StringUtils.escapeJSON(input);
        String desiredOutput = "public static float real1_1(float a) {\\\\r\\\\n        pre(a >= (2 - 1 + 1));\\\\r\\\\n        a += a;\\\\r\\\\n        post(a >= (4 - 3 + 3));\\\\r\\\\n    }\\\\r\\\\n";
        assertEquals(output, desiredOutput);
    }

    /**
     * Test whether escape characters are escaped correctly.
     */
    @Test
    public void testEscapeJSONAll() {
        String input = "\b\r\n\t\f\"\\";
        String output = StringUtils.escapeJSON(input);
        String desiredOutput = "\\b\\r\\n\\t\\f\\\"\\\\";
        assertEquals(output, desiredOutput);
    }

    /**
     * Test whether an empty string is escaped correctly.
     */
    @Test
    public void testEscapeJSONEmpty() {
        String input = "";
        String output = StringUtils.escapeJSON(input);
        String desiredOutput = "";
        assertEquals(output, desiredOutput);
    }

    /**
     * Test whether a pretty string of an array is parsed correctly.
     */
    @Test
    public void testParsePrettyString() {
        String input = "[5, 6, 102, 42]";
        int[] array = new int[] { 5, 6, 102, 42 };
        assertArrayEquals(array, TryParser.parseStringToIntArray(input));
    }
}
