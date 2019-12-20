/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
package gamelogic.gametasks;

import connection.Client;
import connection.Connection;
import gamelogic.gamestate.GameState;
import logger.AbstractLogger;

/**
 * The GameTask for handling the end screen.
 * @author Ludiscite
 * @version 1.0
 */
public class EndTask extends GameTask
{
    /**
     * Constructor for a EndTask object.
     * @param client The client that is playing the game.
     */
    public EndTask(Client client)
    {
        super(client);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean tryCommand(String command, String arguments)
    {
        switch (command)
        {
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
    private void end()
    {
        AbstractLogger logger = client.getLogger();
        Connection connection = client.getConnection();
        GameState state = client.getState();

        if (state.end())
        {
            logger.log("Game ended");
            connection.sendEnd();
        }
        else
        {
            logger.log("Invalid end moment");
        }
    }
}
