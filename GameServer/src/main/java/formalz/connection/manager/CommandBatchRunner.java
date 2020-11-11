package formalz.connection.manager;

import java.util.Deque;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import formalz.connection.Client;
import formalz.data.GameSession;
import formalz.data.Queries;
import formalz.gamelogic.gametasks.GameTask;

public class CommandBatchRunner implements Runnable, Cloneable {
    public static final int DEFAULT_BATCH_SIZE = 5;
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandBatchRunner.class);

    private String id;

    private int batchSize;

    private CommandRunnerContext context;

    public CommandBatchRunner(CommandRunnerContext context) {
        this(UUID.randomUUID().toString(), DEFAULT_BATCH_SIZE, context);
    }

    public CommandBatchRunner(String id, int batchSize, CommandRunnerContext context) {
        Objects.requireNonNull(context, "state must not be null");
        this.id = id;
        this.batchSize = batchSize;
        this.context = context;
    }

    @Override
    public void run() {
        AtomicBoolean runningCommands= this.context.getAlreadyRunningCommands();
        if (!runningCommands.compareAndSet(false, true)) {
            LOGGER.debug("Already running commands");
            return;
        }
        
        java.util.Map<String, String> ctx = this.context.getContextMap();
        MDC.setContextMap(ctx);
        Deque<GameCommand> commandQueue = this.context.getCommands();
        AtomicBoolean authenticated = this.context.getAuthenticated();
        GameTask game = this.context.getGame();
        Client client= this.context.getClient();
        Queries queries = this.context.getQueries();

        GameCommand command = null;
        int commandsExecuted = 0;
        while ( (commandsExecuted < this.batchSize) && (command = commandQueue.peek()) != null ) {
            commandQueue.pop();

            if (authenticated.get()) {
                if (!game.tryCommand(command.command, command.arguments)) {
                    LOGGER.error("Can not execute command '{}' in current state {}", command, game);
                }
            } else if (GameCommand.STARTUP.equals(command.command)) {
                GameSession s = queries.getGameSession(command.arguments);
                if (s != null) {
                    if (s.checkDifference()) {
                        authenticated.set(true);
                        client.getState().updateGameSession(s);
                        client.getConnection().sendStartUpCorrect();
                        client.getConnection().sendSessionId(s.getProblemId(), s.getUserId());
                    } else {
                        client.getConnection().sendStartUpTimeOut();
                        context.stop();
                    }
                } else {
                    client.getConnection().sendStartUpWrong();
                    context.stop();
                }
            } else {
                LOGGER.warn("User not authenticated and not startup command: {}", command);
                // XXX
                // client.getConnection().sendError();
            }
            commandsExecuted++;
        }

        this.context.getContextMap().keySet().forEach((key)->{ MDC.remove(key); });
        runningCommands.set(false);
        if (! commandQueue.isEmpty()) {
            context.submitTask(clone());
        }
    }

    @Override
    public String toString() {
        return "ExecutePendingClientCommands [batchSize=" + batchSize + ", id=" + id + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CommandBatchRunner other = (CommandBatchRunner) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    @Override
    protected CommandBatchRunner clone() {
        try {
            CommandBatchRunner clone = (CommandBatchRunner)super.clone();
            clone.id = UUID.randomUUID().toString();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException( "Error during clone",  e);
        }
    }
}