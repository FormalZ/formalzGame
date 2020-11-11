package formalz.connection.manager;

import java.util.Deque;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import formalz.connection.Client;
import formalz.data.Queries;
import formalz.gamelogic.gametasks.GameTask;

public interface CommandRunnerContext {
    public Map<String, String> getContextMap();
    public Deque<GameCommand> getCommands();
    public AtomicBoolean getAlreadyRunningCommands();
    public AtomicBoolean getAuthenticated();
    public GameTask getGame();
    public Client getClient();
    public void stop();
	public Queries getQueries();
	public void submitTask(Runnable task);
}