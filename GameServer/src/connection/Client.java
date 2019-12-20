/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
package connection;

import data.Queries;
import es.eucm.tracker.formalz.FormalZDemo;
import org.java_websocket.WebSocket;

import gamelogic.gamestate.GameState;
import logger.AbstractLogger;
import logger.Logger;
import logger.PrefixLogger;
import utils.Tracker;

/**
 * An object to combine the communication socket, the logger and the game state of a client.
 * @author Ludiscite
 * @version 1.0
 */
public class Client
{
    private AbstractLogger logger;
    private GameState state;
    private Connection connection;
    private Tracker tracker;

    /**
     * Constructor for a Client object.
     * @param socket The socket of the client.
     */
    public Client(WebSocket socket)
    {
        this.connection = new Connection(socket);
        logger = new PrefixLogger(Logger.getLogger(), socket.getRemoteSocketAddress().getAddress().getHostAddress() + ": ");
        state = new GameState(logger);
        tracker = new Tracker(this);
    }

    /**
     * Gets the Connection that can send messages to the client.
     * @return The Connection that can send messages to the client.
     */
    public Connection getConnection()
    {
        return connection;
    }

    /**
     * Gets the logger with which to log messages about the client.
     * @return The logger with which to log messages about the client.
     */
    public AbstractLogger getLogger()
    {
        return logger;
    }

    /**
     * Gets the Tracker with which to send traces to the analytics.
     * @return The tracker with which to send traces to the analytics.
     */
    public Tracker getTracker() { return tracker; }

    /**
     * Set the logger with which to log messages about the client.
     * @param logger The logger with which to log messages about the client.
     */
    public void setLogger(AbstractLogger logger)
    {
        this.logger = logger;
    }

    /**
     * Gets the state of the game that the client is playing.
     * @return The state of the game that the client is playing.
     */
    public GameState getState()
    {
        return state;
    }
}
