/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
package formalz.haskellapi;

import static org.junit.Assert.*;

import java.util.Set;
import java.util.Map.Entry;

import org.junit.Test;
import org.mockito.Mockito;

import com.google.gson.*;

import formalz.haskellapi.Response.ResponseType;

public class ResponseTest {
    /**
     * Test whether the constructor works when there is no correct response.
     */
    @Test
    public void testConstructor1() {
        int respCode = 200;
        Response resp = new Response(200);

        assertEquals(resp.getResponseCode(), respCode);
    }

    /**
     * Test whether the constructor works when there is a correct response and with
     * equivalent result.
     */
    @Test
    public void testConstructor2Equiv() {
        ResponseType responseType = ResponseType.Equiv;
        @SuppressWarnings("unchecked")
        Set<Entry<String, JsonElement>> MModel = (Set<Entry<String, JsonElement>>) Mockito.mock(Set.class);

        Response resp = new Response(responseType, MModel, null, null, null);

        assertTrue(resp.isEquivalent());
        assertEquals(resp.getModel(), MModel);
    }

    /**
     * Test whether the constructor works when there is a correct response and with
     * not equivalen result.
     */
    @Test
    public void testConstructor2NotEquiv() {
        ResponseType responseType = ResponseType.NotEquiv;
        @SuppressWarnings("unchecked")
        Set<Entry<String, JsonElement>> MModel = (Set<Entry<String, JsonElement>>) Mockito.mock(Set.class);

        Response resp = new Response(responseType, MModel, null, null, null);

        assertTrue(!resp.isEquivalent());
        assertEquals(resp.getModel(), MModel);
    }

    /**
     * Test whether a JSON object containing equivalent response is parsed
     * correctly.
     */
    @Test
    public void testFromJsonObjectTrue() {
        JsonObject obj = new GsonBuilder().create().fromJson(
                "{\"model\":null,\"err\":null,\"responseType\":\"Equiv\",\"feedback\":null}", JsonObject.class);
        Response resp = Response.fromJsonObject(obj);
        assertTrue("Response test:     ", resp.isEquivalent());
        assertNull("Model test:        ", resp.getModel());
        assertNull("Error test:        ", resp.getErr());
        assertArrayEquals("Prefeedback test:  ", resp.getPreFeedback(), new boolean[] { true, false, false, true });
        assertArrayEquals("Pastfeedback test: ", resp.getPostFeedback(), new boolean[] { true, false, false, true });
    }

    /**
     * Test whether a JSON object containing not equivalent response is parsed
     * correctly.
     */
    @Test
    public void testFromJsonObjectFalse() {
        JsonObject obj = new GsonBuilder().create().fromJson(
                "{\"model\":{\"a\":1},\"err\":null,\"responseType\":\"NotEquiv\",\"feedback\":{\"pre\":[true,false,false,true],\"post\":[false,true,true,false]}}",
                JsonObject.class);

        Response resp = Response.fromJsonObject(obj);
        assertTrue("Response test:     ", !resp.isEquivalent());
        assertNotNull("Model test:        ", resp.getModel());
        assertNull("Error test:        ", resp.getErr());
        assertArrayEquals("Prefeedback test:  ", resp.getPreFeedback(), new boolean[] { true, false, false, true });
        assertArrayEquals("Pastfeedback test: ", resp.getPostFeedback(), new boolean[] { false, true, true, false });
    }
}
