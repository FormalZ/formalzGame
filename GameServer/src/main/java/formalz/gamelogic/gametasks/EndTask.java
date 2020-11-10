/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
package formalz.gamelogic.gametasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import formalz.connection.Client;
import formalz.connection.Connection;
import formalz.gamelogic.gamestate.GameState;

/**
 * The GameTask for handling the end screen.
 * 
 * @author Ludiscite
 * @version 1.0
 */
public class EndTask extends GameTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(EndTask.class);

    /**
     * Constructor for a EndTask object.
     * 
     * @param client The client that is playing the game.
     */
    public EndTask(Client client) {
        super(client);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean tryCommand(String command, String arguments) {
        switch (command) {
            case "end":
                end();
                return true;
            default:
                return false;
        }
    }

    /**
     * End the game session.
     */
    private void end() {
        Connection connection = client.getConnection();
        GameState state = client.getState();

        if (state.end()) {
            LOGGER.debug("Game ended");
            connection.sendEnd();
        } else {
            LOGGER.error("Invalid end moment");
        }
    }
}
