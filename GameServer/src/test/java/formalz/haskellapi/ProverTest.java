/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
package formalz.haskellapi;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import formalz.utils.Factory;

public class ProverTest {
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    HttpURLConnection MConnection;
    @Mock
    URL MURL;
    @Mock
    OutputStream MOutputStream;
    @Mock
    OutputStreamWriter MOutputStreamWriter;
    @Mock
    InputStream MInputStream;
    @Mock
    Scanner MScanner;

    @Before
    public void initMocks() {
        Factory.setMockURL(null);
        Factory.setMockOutputStreamWriter(null);
        Factory.setMockScanner(null);
    }

    public void init() throws Exception {
        Factory.setMockURL(MURL);
        Factory.setMockOutputStreamWriter(MOutputStreamWriter);
        Factory.setMockScanner(MScanner);

        when(MConnection.getOutputStream()).thenReturn(MOutputStream);
    }

    /**
     * Test whether a response with response code 200 is handled correctly.
     * 
     * @throws Exception Connection exceptions.
     */
    @Test
    public void testCompareResponseCode200() throws Exception {
        init();

        when(MURL.openConnection()).thenReturn(MConnection);
        when(MConnection.getResponseCode()).thenReturn(200);
        when(MConnection.getInputStream()).thenReturn(MInputStream);
        when(MScanner.next()).thenReturn(
                "{\"model\":{\"b:real\":5.031056532842699,\"a:real\":1.6374821786024496},\"err\":null,\"responseType\":\"NotEquiv\",\"feedback\":{\"pre\":[false,false,false,false],\"post\":[false,false,false,false]}}");

        Prover prover = Prover.getInstance();
        Response response = prover.compare("A", "B");
        assertFalse(response.isEquivalent());
        assertArrayEquals(new boolean[] { false, false, false, false }, response.getPreFeedback());

    }

    /**
     * Test whether a response with a response code other than 200 is handled
     * correctly.
     * 
     * @throws Exception Connection exceptions.
     */
    @Test
    public void testCompareResponseCodeOther() throws Exception {
        init();

        when(MURL.openConnection()).thenReturn(MConnection);
        when(MConnection.getResponseCode()).thenReturn(402);
        when(MConnection.getInputStream()).thenReturn(MInputStream);
        when(MScanner.next()).thenReturn(
                "{\"model\":{\"b:real\":5.031056532842699,\"a:real\":1.6374821786024496},\"err\":null,\"responseType\":\"NotEquiv\",\"feedback\":{\"pre\":[false,false,false,false],\"post\":[false,false,false,false]}}");

        Prover prover = Prover.getInstance();
        Response response = prover.compare("A", "B");
        assertEquals(402, response.getResponseCode());
    }

    /**
     * Test whether an io exception with response code 200 is handled correctly.
     * 
     * @throws Exception Connection exceptions.
     */
    @Test
    public void testCompareIOException200() throws Exception {
        init();

        when(MURL.openConnection()).thenReturn(MConnection);
        when(MConnection.getResponseCode()).thenReturn(200);
        when(MConnection.getInputStream()).thenThrow(new IOException());
        when(MScanner.next()).thenReturn(
                "{\"model\":{\"b:real\":5.031056532842699,\"a:real\":1.6374821786024496},\"err\":null,\"responseType\":\"NotEquiv\",\"feedback\":{\"pre\":[false,false,false,false],\"post\":[false,false,false,false]}}");

        Prover prover = Prover.getInstance();
        Response response = prover.compare("A", "B");
        assertEquals(400, response.getResponseCode());
    }

    /**
     * Test whether an io exception with a response code other than 200 is handled
     * correctly
     * 
     * @throws Exception Connection exceptions.
     */
    @Test
    public void testCompareIOExceptionOther() throws Exception {
        init();

        when(MURL.openConnection()).thenThrow(new IOException());
        when(MConnection.getResponseCode()).thenReturn(402);
        when(MScanner.next()).thenReturn(
                "{\"model\":{\"b:real\":5.031056532842699,\"a:real\":1.6374821786024496},\"err\":null,\"responseType\":\"NotEquiv\",\"feedback\":{\"pre\":[false,false,false,false],\"post\":[false,false,false,false]}}");

        Prover prover = Prover.getInstance();
        Response response = prover.compare("A", "B");
        assertEquals(400, response.getResponseCode());
    }

    /**
     * Test whether the real connection works with not equivalent conditions.
     */
    @Test
    public void testRealConnectionNotEqual() {
        Prover.setProver(null);
        String sourceA = "public static float real1_1(float a, float b) {\\npre(a >= 1 && b > 4);\\na += a;\\npost(a > 4);}";
        String sourceB = "public static float real1_1(float a, float b) {\\npre(a >= 2 && b >= 4);\\na += a;\\npost(a >= 4);}";

        Prover prover = Prover.getInstance();
        Response resp = prover.compare(sourceA, sourceB);
        assertTrue(!resp.isEquivalent());
    }

}
