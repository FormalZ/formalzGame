/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
package formalz.utils;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import org.java_websocket.WebSocket;

import formalz.connection.Client;
import formalz.connection.ClientManager;
import formalz.gamelogic.gametasks.MainGameTask;

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

}
