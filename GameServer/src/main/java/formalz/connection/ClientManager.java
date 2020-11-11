/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
package formalz.connection;

import java.net.InetAddress;
import java.util.Deque;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

import org.java_websocket.WebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import formalz.connection.manager.CommandRunnerContext;
import formalz.connection.manager.CommandBatchRunner;
import formalz.connection.manager.GameCommand;
import formalz.data.Queries;
import formalz.data.Settings;
import formalz.gamelogic.gametasks.*;
import formalz.utils.Tracker;

/**
 * An object to manage a connection with a client.
 * 
 * @author Ludiscite
 * @version 1.0
 */
public class ClientManager {

    public enum DebugLogDestination {
        NONE, FILE, CONSOLE
    }

    static final Logger LOGGER = LoggerFactory.getLogger(ClientManager.class);

    private Client client;

    private GameTask game;

    /**
     * Whether the client has been authenticated.
     */
    private AtomicBoolean authenticated;

    private Deque<GameCommand> commandQueue;

    private AtomicBoolean runningCommands;

    private Timer timer;

    private ExecutorService commandRunner;

    private Queries queries;

    private InetAddress clientIp;

    private String sessionId;

    private String sessionName;

    private DebugLogDestination debugLog;

    /**
     * Constructor of a ClientManager object.
     * 
     * @param socket The WebSocket of the client to be managed.
     */
    public ClientManager(InetAddress clientIp, String sessionId, Client client, GameTask gameTask, Timer timer, ExecutorService commandRunner, Queries queries) {
        this.clientIp = clientIp;
        this.sessionId = sessionId;
        this.sessionName = this.sessionId;
        this.debugLog = DebugLogDestination.NONE;

        this.client = client;
        this.game = gameTask;
        this.timer = timer;
        this.authenticated = new AtomicBoolean(false);
        this.commandQueue = new ConcurrentLinkedDeque<>();
        this.runningCommands = new AtomicBoolean(false);
        this.commandRunner = commandRunner;
        this.queries = queries;
        setAuthTimeoutCheck();
    }

    /**
     * Runs a timer to wait for a timeout of the authentication.
     */
    private void setAuthTimeoutCheck() {
        this.timer.schedule(new TimerTask() {
            @Override
            public void run() {
                authTimeout();
            }
        }, Settings.getSessionWaitTime());
    }

    /**
     * Timeout the connection when the client has not yet been authenticated.
     */
    private void authTimeout() {
        if (!authenticated.get()) {
            client.getConnection().sendStartUpTimeOut();
            this.stop();
        }
    }

    /**
     * Processes a message from the client.
     * 
     * @param message The message from the client.
     */
    public void processMessage(String message) {

        // Syntax: command<blank>[<arguments>]
        int pos = message.indexOf(" ");
        if (pos == -1) {
            LOGGER.error("Empty command");
            return;
        }

        String command = message.substring(0, pos);
        String arguments = "";
        if (pos < message.length()) {
            arguments = message.substring(pos + 1);
        }
        this.commandQueue.add(new GameCommand(command, arguments));
        this.commandRunner.submit(new CommandBatchRunner(new BatchRunnerContext()));
    }

    /**
     * Stops the connection with the client.
     */
    public void stop() {
        Connection connection = client.getConnection();
        WebSocket socket = connection.getSocket();

        connection.sendStop();
        socket.close();

        shutdown();

        LOGGER.debug("Stop connection");
    }

    public void shutdown() {
        Tracker tracker = client.getTracker();
        if(tracker != null) {
            tracker.stop();
        }
        this.commandQueue.clear();
    }

    /**
     * Just for Unit testing
     * @return
     */
    Deque<GameCommand> getCommands() {
        return this.commandQueue;
    }

	public InetAddress getIp() {
		return this.clientIp;
    }

    public String getSessionName() {
        return this.sessionName;
    }
    
    public void setSessionName(String newSessionName) {
        Objects.requireNonNull(newSessionName);
        this.sessionName = newSessionName;
    }

    public String getSessionId() {
        return this.sessionId;
    }

    public DebugLogDestination getDebugLog() {
        return this.debugLog;
    }

    public void setDebugLog(DebugLogDestination debugLog) {
        this.debugLog = debugLog;
    }

	public void sendError(String message) {
        client.getConnection().sendError(message);
    }
    
    private class BatchRunnerContext implements CommandRunnerContext {

        private Map<String, String> ctx;

        public BatchRunnerContext() {
            this.ctx = MDC.getCopyOfContextMap();
        }

        @Override
        public Map<String, String> getContextMap() {
            return this.ctx;
        }

        @Override
        public Deque<GameCommand> getCommands() {
            return commandQueue;
        }

        @Override
        public AtomicBoolean getAuthenticated() {
            return authenticated;
        }

        @Override
        public GameTask getGame() {
            return game;
        }

        @Override
        public Client getClient() {
            return client;
        }

        @Override
        public void stop() {
            stop();
        }

        @Override
        public Queries getQueries() {
            return queries;
        }

        @Override
        public void submitTask(Runnable task) {
            commandRunner.submit(task);
        }

        @Override
        public AtomicBoolean getAlreadyRunningCommands() {
            return runningCommands;
        }
    }
}
