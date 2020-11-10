/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
package formalz.connection;

import org.java_websocket.WebSocket;

import formalz.gamelogic.gamestate.GameState;
import formalz.utils.Tracker;

/**
 * An object to combine the communication socket, the logger and the game state
 * of a client.
 * 
 * @author Ludiscite
 * @version 1.0
 */
public class Client {


    private GameState state;

    private Connection connection;

    private Tracker tracker;

    /**
     * Constructor for a Client object.
     * 
     * @param socket The socket of the client.
     */
    public Client(WebSocket socket) {
        this.connection = new Connection(socket);
        this.state = new GameState();
        this.tracker = new Tracker(this);
    }

    /**
     * Gets the Connection that can send messages to the client.
     * 
     * @return The Connection that can send messages to the client.
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * Gets the Tracker with which to send traces to the analytics.
     * 
     * @return The tracker with which to send traces to the analytics.
     */
    public Tracker getTracker() {
        return tracker;
    }

    /**
     * Gets the state of the game that the client is playing.
     * 
     * @return The state of the game that the client is playing.
     */
    public GameState getState() {
        return state;
    }
}
