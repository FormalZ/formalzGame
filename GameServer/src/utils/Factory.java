/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
package utils;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import org.java_websocket.WebSocket;

import connection.Client;
import connection.ClientManager;
import gamelogic.gametasks.MainGameTask;

/**
 * The class used to mock constructors.
 * @author Ludiscite
 * @version 1.0
 */
public class Factory
{

    private static URL mURL = null;

    /**
     * Set a mock URL object.
     * @param url Mock URL object.
     */
    public static void setMockURL(URL url)
    {
        mURL = url;
    }

    /**
     * Construct a URL object or return a mock.
     * @param targetUrl Target URL of the URL object.
     * @return URL object.
     * @throws MalformedURLException Incorrect URL exception.
     */
    public static URL constructURL(String targetUrl) throws MalformedURLException
    {
        if (mURL == null)
        {
            return new URL(targetUrl);
        }
        else
        {
            return mURL;
        }
    }

    private static OutputStreamWriter mOutputStreamWriter = null;

    /**
     * Set a mock OutputStreamWriter.
     * @param outputStreamWriter Mock OutputStreamWriter.
     */
    public static void setMockOutputStreamWriter(OutputStreamWriter outputStreamWriter)
    {
        mOutputStreamWriter = outputStreamWriter;
    }

    /**
     * Construct a OuputStreamWriter object or return a mock.
     * @param outputStream OutputStream to write to.
     * @return OutputStreamWriter object.
     */
    public static OutputStreamWriter constructOutputStreamWriter(OutputStream outputStream)
    {
        if (mOutputStreamWriter == null)
        {
            return new OutputStreamWriter(outputStream);
        }
        else
        {
            return mOutputStreamWriter;
        }
    }

    private static Scanner mScanner = null;

    /**
     * Set a mock Scanner.
     * @param scanner Mock Scanner.
     */
    public static void setMockScanner(Scanner scanner)
    {
        mScanner = scanner;
    }

    /**
     * Construct a scanner object or return a mock.
     * @param inputStream InputStream for the scanner.
     * @return Scanner object.
     */
    public static Scanner constructScanner(InputStream inputStream)
    {
        if (mScanner == null)
        {
            return new Scanner(inputStream);
        }
        else
        {
            return mScanner;
        }
    }

    private static Client mClient = null;

    /**
     * Set a mock Client.
     * @param client Mock Client.
     */
    public static void setMockClient(Client client)
    {
        mClient = client;
    }

    /**
     * Construct a client object or return a mock.
     * @param socket Socket for the client object.
     * @return Client object.
     */
    public static Client constructClient(WebSocket socket)
    {
        if (mClient == null)
        {
            return new Client(socket);
        }
        else
        {
            return mClient;
        }
    }

    private static MainGameTask mMainGameTask = null;

    /**
     * Set a mock MainGameTask.
     * @param mainGameTask Mock MainGameTask.
     */
    public static void setMockMainGameTask(MainGameTask mainGameTask)
    {
        mMainGameTask = mainGameTask;
    }

    /**
     * Construct a MainGameTask object or return a mock.
     * @param client Client for the MainGameTask.
     * @return MainGameTask object.
     */
    public static MainGameTask constructMainGameTask(Client client)
    {
        if (mMainGameTask == null)
        {
            return new MainGameTask(client);
        }
        else
        {
            return mMainGameTask;
        }
    }

    private static ClientManager mClientManager = null;

    /**
     * Set a mock ClientManager.
     * @param clientManager Mock ClientManager.
     */
    public static void setMockClientManager(ClientManager clientManager)
    {
        mClientManager = clientManager;
    }

    /**
     * Construct a ClientManager object or return a mock.
     * @param webSocket WebSocket to manage.
     * @return ClientManager object.
     */
    public static ClientManager constructClientManager(WebSocket webSocket)
    {
        if (mClientManager == null)
        {
            return new ClientManager(webSocket);
        }
        else
        {
            return mClientManager;
        }
    }
}
