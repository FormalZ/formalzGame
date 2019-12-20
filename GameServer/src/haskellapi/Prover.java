/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
package haskellapi;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import data.Settings;
import logger.AbstractLogger;
import logger.FileLogger;
import logger.Logger;
import utils.Factory;

/**
 * The class with the main functionality corresponding to executing queries on the Haskell API.
 * @author Ludiscite
 * @version 1.0
 */
public class Prover
{
    /**
     * Singleton prover.
     */
    private static Prover prover;

    /**
     * Get an instance of the singleton prover.
     * @return Singleton prover.
     */
    public static Prover getInstance()
    {
        if (prover == null)
        {
            prover = new Prover();
        }

        return prover;
    }

    /**
     * Set the singleton prover. For testing purposes.
     * @param prover Prover mock to set as singleton.
     */
    public static void setProver(Prover prover)
    {
        Prover.prover = prover;
    }

    private static int counter = 0;
    
    /**
     * Private constructor for singleton pattern.
     */
    private Prover()
    {

    }

    private static AbstractLogger fileLogger = new FileLogger("proverResponses.txt");

    /**
     * The complete target with which to find the Haskell API.
     */
    private static final String target = Settings.getHaskellTargetURL() + ":" + Settings.getHaskellPort() + "/"
            + Settings.getHaskellMethod();

    /**
     * Compares the given methods, which should be formatted with StringUtils.escapeJSON, using the Haskell API.
     * @param studentMethod Students method in comparison.
     * @param teacherMethod Teachers method in comparison.
     * @return Result of comparison of the methods.
     */
    public Response compare(String studentMethod, String teacherMethod)
    {
        String data = createParameters(studentMethod, teacherMethod);
        return queryAPI(data);
    }

    /**
     * Executes a query on the Haskell API.
     * @param query The query to be executed.
     * @return The parsed response of the Haskell API.
     */
    private static Response queryAPI(String query)
    {
        HttpURLConnection connection = null;
        Response response = null;

        int currentIndex = counter++;
        
        try
        {
            connection = startConnection(target);

            connection.setConnectTimeout(Settings.getHaskellTimeout());
            connection.setReadTimeout(Settings.getHaskellTimeout());

            fileLogger.log((new SimpleDateFormat("").format(new Date())) + "index " + currentIndex + " ; Query send : " + query + "\n");
            sendData(connection, query);

            int responseCode = connection.getResponseCode();

            if (responseCode == 200)
            {
                String data = readData(connection);
                fileLogger.log((new SimpleDateFormat("").format(new Date())) + "index " + currentIndex + " ; Response received : " + data + "\n");
                response = Response.fromString(data);
            }
            else
            {
                response = new Response(responseCode);
            }
        }
        catch (IOException e)
        {
            Logger.log("Connection to the Haskell API has failed");
            fileLogger.log((new SimpleDateFormat("").format(new Date())) + "index " + currentIndex + " ; Connection failed with query : " + query + "\n");
            e.printStackTrace();
            response = new Response(400);
        }
        finally
        {
            if (connection != null)
            {
                connection.disconnect();
            }
        }

        return response;
    }

    /**
     * Starts a connection with the given URL.
     * @param targetUrl The URL with which to start a connection.
     * @return The connection with the given URL.
     * @throws IOException If an I/O exception occurs.
     */
    private static HttpURLConnection startConnection(String targetUrl) throws IOException
    {
        HttpURLConnection connection = null;

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
     * @param connection The connection over which to send data.
     * @param data The data to send over the connection.
     * @throws IOException If an I/O exception occurs.
     */
    private static void sendData(HttpURLConnection connection, String data) throws IOException
    {
        OutputStreamWriter writer = Factory.constructOutputStreamWriter(connection.getOutputStream());
        writer.write(data);
        writer.flush();
        writer.close();
    }

    /**
     * Read a line of data from a connection.
     * @param connection The connection from which to read data.
     * @return The data read from the connection.
     * @throws IOException If an I/O exception occurs.
     */
    private static String readData(HttpURLConnection connection) throws IOException
    {
        InputStream response = connection.getInputStream();
        Scanner scanner = Factory.constructScanner(response);
        scanner.useDelimiter("\\A");
        String resp = scanner.next();
        scanner.close();
        return resp;
    }

    /**
     * Format two methods as parameters to send to the Haskell API as a compare query.
     * @param method1 The first method to compare.
     * @param method2 The second method to compare.
     * @return The compare query to send to the Haskell API.
     */
    private static String createParameters(String method1, String method2)
    {
        return "{\"sourceA\": \"" + method1 + "\",\"sourceB\": \"" + method2 + "\"}";
    }
}
