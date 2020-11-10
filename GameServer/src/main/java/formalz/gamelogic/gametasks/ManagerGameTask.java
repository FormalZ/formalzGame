/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
package formalz.gamelogic.gametasks;

import java.util.Set;

import formalz.connection.Client;

/**
 * A GameTask object that combines multiple other GameTask objects.
 * 
 * @author Ludiscite
 * @version 1.0
 */
public class ManagerGameTask extends GameTask {
    /**
     * The tasks that need to be managed.
     */
    protected Set<GameTask> tasks;

    /**
     * Constructor for a ManagerGameTask.
     * 
     * @param client The client that is playing the game.
     * @param tasks  The tasks that need to be managed.
     */
    public ManagerGameTask(Client client, Set<GameTask> tasks) {
        super(client);
        this.tasks = tasks;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean tryCommand(String command, String arguments) {
        for (GameTask task : tasks)
            if (task.tryCommand(command, arguments))
                return true;
        return false;
    }
}
