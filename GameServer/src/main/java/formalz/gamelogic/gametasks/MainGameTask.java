/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
package formalz.gamelogic.gametasks;

import java.util.*;

import formalz.connection.Client;

/**
 * The main GameTask object of any game.
 * 
 * @author Ludiscite
 * @version 1.0
 */
public class MainGameTask extends ManagerGameTask {
    /**
     * Constructor of a MainGameTask object.
     * 
     * @param client The client that is playing the game.
     */
    public MainGameTask(Client client) {
        super(client, new HashSet<GameTask>());

        tasks.add(new MenuTask(client));
        tasks.add(new GameLogicTask(client));
        tasks.add(new EndTask(client));
    }
}
