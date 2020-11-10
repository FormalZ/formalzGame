package formalz.connection;

import java.net.InetAddress;
import java.util.Timer;
import java.util.concurrent.ExecutorService;

import formalz.data.Queries;
import formalz.gamelogic.gametasks.GameTask;

public interface ClientManagerFactory {
    public static final ClientManagerFactory DEFAULT = new ClientManagerFactory() {
        public ClientManager create(InetAddress clientIp, String sessionId, Client client, GameTask gameTask, Timer timer, ExecutorService commandRunner, Queries queries) {
            return new ClientManager(clientIp, sessionId, client, gameTask, timer, commandRunner, queries);
        }
    };

    public ClientManager create(InetAddress clientIp, String sessionId, Client client, GameTask gameTask, Timer timer, ExecutorService commandRunner, Queries queries);
}