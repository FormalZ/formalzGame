/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
package connection;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.*;

import org.java_websocket.WebSocket;

import data.GameSession;
import data.Queries;
import data.Settings;
import gamelogic.gametasks.*;
import logger.AbstractLogger;
import utils.Factory;
import utils.Tracker;

/**
 * An object to manage a connection with a client.
 * @author Ludiscite
 * @version 1.0
 */
public class ClientManager
{
    private Client client;
    private GameTask game;

    /**
     * The lock to ensure only one message is ever processed at once.
     */
    private Lock lock;

    /**
     * Whether the client has been authenticated.
     */
    private boolean authenticated = false;

    /**
     * Constructor of a ClientManager object.
     * @param socket The WebSocket of the client to be managed.
     */
    public ClientManager(WebSocket socket)
    {
        client = Factory.constructClient(socket);
        game = Factory.constructMainGameTask(client);
        lock = new ReentrantLock();

        RunTimeOutCheck();
    }

    /**
     * Runs a timer to wait for a timeout of the authentication.
     */
    private void RunTimeOutCheck()
    {
        new Timer().schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                TimeOut();
            }
        }, Settings.getSessionWaitTime());
    }

    /**
     * Timeout the connection when the client has not yet been authenticated.
     */
    private void TimeOut()
    {
        try
        {
            if (!authenticated)
            {
                client.getConnection().sendStartUpTimeOut();
                this.stop();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Processes a message from the client.
     * @param message The message from the client.
     */
    public void processMessage(String message)
    {
        lock.lock();
        try
        {
            AbstractLogger logger = client.getLogger();

            logger.log("Received message: " + message);

            String[] args = message.split(" ");
            if (args.length < 1)
            {
                logger.log("Empty command");
                lock.unlock();
                return;
            }

            String command = args[0];
            String arguments = "";

            int index = message.indexOf(' ');
            if (index != -1)
            {
                arguments = message.substring(index + 1);
            }

            if (authenticated)
            {
                if (!game.tryCommand(command, arguments))
                {
                    logger.log("Unknown command: " + command);
                }
            }
            else if (command.equals("startup"))
            {
                Queries queries = Queries.getInstance();
                GameSession s = queries.getGameSession(arguments);
                if (s != null)
                {
                    if (s.checkDifference())
                    {
                        client.getState().updateGameSession(s);
                        authenticated = true;
                        client.getConnection().sendStartUpCorrect();
                        client.getConnection().sendSessionId(s.getProblemId(), s.getUserId());
                    }
                    else
                    {
                        client.getConnection().sendStartUpTimeOut();
                        this.stop();
                    }
                }
                else
                {
                    client.getConnection().sendStartUpWrong();
                    this.stop();
                }
            }
        }
        finally
        {
            lock.unlock();
        }
    }

    /**
     * Stops the connection with the client.
     */
    public void stop()
    {
        AbstractLogger logger = client.getLogger();
        WebSocket socket = client.getConnection().getSocket();
        Connection connection = client.getConnection();
        Tracker tracker = client.getTracker();

        logger.log("Stopped");
        connection.sendStop();
        tracker.stop();
        socket.close();
    }

    /**
     * Returns the client the client manager manages.
     * @return Client The client being managed.
     */
    public Client getClient()
    {
        return client;
    }
}
