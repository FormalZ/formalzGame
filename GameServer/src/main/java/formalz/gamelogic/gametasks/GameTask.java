/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
package formalz.gamelogic.gametasks;

import java.util.Objects;

import formalz.connection.Client;

/**
 * The abstract class to execute tasks regarding the game.
 * 
 * @author Ludiscite
 * @version 1.0
 */
public abstract class GameTask {
    /**
     * The client that is playing the game.
     */
    protected Client client;

    /**
     * Constructor for a GameTask object.
     * 
     * @param client The client that is playing the game.
     */
    public GameTask(Client client) {
        Objects.requireNonNull(client, "client must not be null");
        this.client = client;
    }

    /**
     * Tries to run a command with given arguments.
     * 
     * @param command   The command from the client.
     * @param arguments The arguments of the command, space separated in a single
     *                  string.
     * @return A boolean value representing whether the command was executed by the
     *         task.
     */
    public abstract boolean tryCommand(String command, String arguments);
}
