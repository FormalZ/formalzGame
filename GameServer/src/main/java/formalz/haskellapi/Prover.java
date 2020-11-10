/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
package formalz.haskellapi;

import java.io.*;
import java.net.*;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import formalz.data.Settings;
import formalz.utils.Factory;

/**
 * The class with the main functionality corresponding to executing queries on
 * the Haskell API.
 * 
 * @author Ludiscite
 * @version 1.0
 */
public class Prover {

    private static final Logger LOGGER = LoggerFactory.getLogger(Prover.class);

    /**
     * Singleton prover.
     */
    private static Prover prover;

    /**
     * Get an instance of the singleton prover.
     * 
     * @return Singleton prover.
     */
    public static Prover getInstance() {
        if (prover == null) {
            prover = new Prover();
        }

        return prover;
    }

    /**
     * Set the singleton prover. For testing purposes.
     * 
     * @param prover Prover mock to set as singleton.
     */
    public static void setProver(Prover prover) {
        Prover.prover = prover;
    }

    /**
     * The complete target with which to find the Haskell API.
     */
    private String target;

    /**
     * Private constructor for singleton pattern.
     */
    private Prover() {
        this.target = Settings.getHaskellTargetURL() + ":" + Settings.getHaskellPort() + "/" + Settings.getHaskellMethod();
    }

    /**
     * Compares the given methods, which should be formatted with
     * StringUtils.escapeJSON, using the Haskell API.
     * 
     * @param studentMethod Students method in comparison.
     * @param teacherMethod Teachers method in comparison.
     * @return Result of comparison of the methods.
     */
    public Response compare(String studentMethod, String teacherMethod) {
        String data = createParameters(studentMethod, teacherMethod);
        return queryAPI(data);
    }

    /**
     * Executes a query on the Haskell API.
     * 
     * @param query The query to be executed.
     * @return The parsed response of the Haskell API.
     */
    private Response queryAPI(String query) {
        HttpURLConnection connection = null;
        Response response = null;

        try {
            connection = startConnection(target);

            connection.setConnectTimeout(Settings.getHaskellTimeout());
            connection.setReadTimeout(Settings.getHaskellTimeout());

            LOGGER.debug("Query sent: {}", query);
            sendData(connection, query);

            int responseCode = connection.getResponseCode();

            if (responseCode == 200) {
                String data = readData(connection);
                LOGGER.debug("Response received: {}", data);
                response = Response.fromString(data);
            } else {
                LOGGER.warn("Response received (not ok), response code: {}", responseCode);
                response = new Response(responseCode);
            }
        } catch (IOException e) {
            LOGGER.error("Connection to the Haskell API has failed", e);
            response = new Response(500);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        return response;
    }

    /**
     * Starts a connection with the given URL.
     * 
     * @param targetUrl The URL with which to start a connection.
     * @return The connection with the given URL.
     * @throws IOException If an I/O exception occurs.
     */
    private HttpURLConnection startConnection(String targetUrl) throws IOException {
        HttpURLConnection connection = null;

        // XXX
        URL url = Factory.constructURL(targetUrl);

        connection = (HttpURLConnection) url.openConnection();

        // Required to send data
        connection.setDoOutput(true);

        connection.setRequestMethod("POST");

        // Set input and output data types
        connection.setRequestProperty("Accept-Charset", "UTF-8");
        connection.setRequestProperty("Content-type", "application/json");
        connection.setRequestProperty("Accept", "application/json");

        return connection;
    }

    /**
     * Send data over a connection.
     * 
     * @param connection The connection over which to send data.
     * @param data       The data to send over the connection.
     * @throws IOException If an I/O exception occurs.
     */
    private void sendData(HttpURLConnection connection, String data) throws IOException {
        OutputStreamWriter writer = Factory.constructOutputStreamWriter(connection.getOutputStream());
        writer.write(data);
        writer.flush();
        writer.close();
    }

    /**
     * Read a line of data from a connection.
     * 
     * @param connection The connection from which to read data.
     * @return The data read from the connection.
     * @throws IOException If an I/O exception occurs.
     */
    private String readData(HttpURLConnection connection) throws IOException {
        InputStream response = connection.getInputStream();
        Scanner scanner = Factory.constructScanner(response);
        scanner.useDelimiter("\\A");
        String resp = scanner.next();
        scanner.close();
        return resp;
    }

    /**
     * Format two methods as parameters to send to the Haskell API as a compare
     * query.
     * 
     * @param method1 The first method to compare.
     * @param method2 The second method to compare.
     * @return The compare query to send to the Haskell API.
     */
    private String createParameters(String method1, String method2) {
        return "{\"sourceA\": \"" + method1 + "\",\"sourceB\": \"" + method2 + "\"}";
    }
}
