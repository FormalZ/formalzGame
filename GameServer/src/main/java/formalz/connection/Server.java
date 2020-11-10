/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
package formalz.connection;

import org.apache.commons.net.util.SubnetUtils.SubnetInfo;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import formalz.data.Queries;
import formalz.data.Settings;
import formalz.gamelogic.gametasks.MainGameTask;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import org.java_websocket.server.DefaultSSLWebSocketServerFactory;

/**
 * Custom implementation of a WebSocketServer. This keeps track of all client
 * connections and all communication with the clients.
 * 
 * @author Ludiscite
 * @version 1.0
 */
public class Server extends WebSocketServer {
    private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);

    private static final String X_FORWARDED_FOR = "X-Forwarded-For";

    private static final String X_REAL_IP = "X-Real-Ip";

    /**
     * The map from WebSockets to their corresponding ClientConnection objects.
     */
    private Map<WebSocket, ClientManager> connections;

    private ClientManagerFactory clientManagerFactory;

    private ClientFactory clientFactory;

    private Timer timer;

    private ExecutorService executorService;

    private Queries queries;

    /**
     * Constructor of a Server object.
     */
    public Server(ClientManagerFactory clientManagerFactory, ClientFactory clientFactory, Timer timer, ExecutorService executorService, Queries queries) {
        super(new InetSocketAddress(Settings.getConnectionPort()));

        Objects.requireNonNull(clientManagerFactory, "clientManagerFactory must not be null");
        Objects.requireNonNull(clientFactory, "clientFactory must not be null");
        Objects.requireNonNull(executorService, "queries must not be null");
        Objects.requireNonNull(queries, "queries must not be null");

        /*
         * XXX nginx by default has a proxy_read_timeout value of 60, after that the
         * connection is closed.
         */
        setConnectionLostTimeout(Settings.getConnectionLostTimeout());
        this.connections = new ConcurrentHashMap<WebSocket, ClientManager>();
        this.clientManagerFactory = clientManagerFactory;
        this.clientFactory = clientFactory;
        this.timer = timer;
        this.executorService = executorService;
        this.queries = queries;
    }

    @Override
    public void start() {
        try {
            if (Settings.isTLSEnabled()) {
                // load up the key store
                String STORETYPE = Settings.getServerStoreType();
                String KEYSTORE = Settings.getServerKeyStore();
                String STOREPASSWORD = Settings.getServerStorePassword();
                String KEYPASSWORD = Settings.getServerKeyPassword();

                KeyStore ks = KeyStore.getInstance(STORETYPE);
                File kf = new File(KEYSTORE);
                ks.load(new FileInputStream(kf), STOREPASSWORD.toCharArray());

                KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
                kmf.init(ks, KEYPASSWORD.toCharArray());
                TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
                tmf.init(ks);

                SSLContext sslContext = null;
                sslContext = SSLContext.getInstance("TLS");
                sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

                this.setWebSocketFactory(new DefaultSSLWebSocketServerFactory(sslContext));
            }
        } catch (Exception e) {
            LOGGER.error("Could not start the server", e);
        }
        super.start();
    }

    /**
     * Gets called when a new client connection is opened. Saves the connection and
     * initializes a corresponding ClientCommunication object.
     * 
     * @param connection The client connection that is opened.
     * @param handshake  The handshake with the client.
     */
    @Override
    public void onOpen(WebSocket connection, ClientHandshake handshake) {
        InetAddress clientIp = getConnectionAddress(connection, handshake);
        Client client = clientFactory.constructClient(connection);
        MainGameTask gameTask = new MainGameTask(client);
        ClientManager clientManager = clientManagerFactory.create(clientIp, UUID.randomUUID().toString(), client, gameTask, this.timer, this.executorService, this.queries);
        connections.put(connection, clientManager);
        
        MDC.put("clientIp", clientIp.getHostAddress());
        MDC.put("sessionName", clientManager.getSessionName());
        MDC.put("disabled", clientManager.getDebugLog() == ClientManager.DebugLogDestination.NONE ? "true" : "false");
        MDC.put("type", clientManager.getDebugLog() == ClientManager.DebugLogDestination.FILE ? "file" : "console");
        LOGGER.debug("New connection from: " + clientIp.getHostAddress());
        MDC.remove("clientIp");
        MDC.remove("sessionName");
        MDC.remove("disabled");
        MDC.remove("type");
    }

    private InetAddress getConnectionAddress(WebSocket connection, ClientHandshake handshake) {
        InetAddress address = connection.getRemoteSocketAddress().getAddress();

        Collection<SubnetInfo> trustedProxyCidrs = Settings.getTrustedProxyCidrs();
        if (!trustedProxyCidrs.isEmpty()) {
            final String ip = address.getHostAddress();
            Optional<SubnetInfo> cidr = trustedProxyCidrs.stream()
                .filter(e -> e.isInRange(ip))
                .findFirst();
            if (cidr.isPresent()) {
                if (handshake.hasFieldValue(X_REAL_IP)) {
                    String xRealIpHeader = handshake.getFieldValue(X_REAL_IP);
                    try {
                        address = InetAddress.getByName(xRealIpHeader);
                    } catch (UnknownHostException e) {
                        LOGGER.warn(String.format("'%s' is not a valid InetAddress", xRealIpHeader), e);
                    }
                } else if (handshake.hasFieldValue(X_FORWARDED_FOR)) {
                    String[] ips = handshake.getFieldValue(X_FORWARDED_FOR).replaceAll(" ", "").split(",");
                    if (ips.length > 0) {
                        try {
                            address = InetAddress.getByName(ips[ips.length - 1]);
                        } catch (UnknownHostException e) {
                            LOGGER.warn(String.format("'%s' is not a valid InetAddress", ips[ips.length - 1]), e);
                        }                                
                    }
                }
            }
        }

        return address;
    }

    /**
     * Gets called when a client connection is closed. Removes the connection from
     * the list of connections.
     * 
     * @param connection The client connection that is closed.
     * @param code       This parameter currently gets ignored.
     * @param reason     The reason the connection is closed.
     * @param remote     This parameter currently gets ignored.
     */
    @Override
    public void onClose(WebSocket connection, int code, String reason, boolean remote) {
        ClientManager clientManager = connections.remove(connection);
        if (clientManager != null) {
            MDC.put("clientIp", clientManager.getIp().getHostAddress());
            MDC.put("sessionName", clientManager.getSessionName());
            MDC.put("disabled", clientManager.getDebugLog() == ClientManager.DebugLogDestination.NONE ? "true" : "false");
            MDC.put("type", clientManager.getDebugLog() == ClientManager.DebugLogDestination.FILE ? "file" : "console");
            LOGGER.debug("Closed connection: {}", reason);
        } else {
            MDC.put("clientIp", "-");
            MDC.put("sessionName", "-");
            MDC.put("disabled", "false");
            MDC.put("type", "console");
            LOGGER.error("Oops, no clientManager during connection close");
        }
        MDC.remove("clientIp");
        MDC.remove("sessionName");
        MDC.remove("disabled");
        MDC.remove("type");
    }

    /**
     * Gets called when a message is received from a client. Lets the corresponding
     * ClientConnection object handle the message.
     * 
     * @param connection The client from which the message is received.
     * @param message    The message that is received.
     */
    @Override
    public void onMessage(WebSocket connection, String message) {
        ClientManager clientManager = connections.get(connection);
        if (clientManager != null) {
            MDC.put("clientIp", clientManager.getIp().getHostAddress());
            MDC.put("sessionName", clientManager.getSessionName());
            MDC.put("disabled", clientManager.getDebugLog() == ClientManager.DebugLogDestination.NONE ? "true" : "false");
            MDC.put("type", clientManager.getDebugLog() == ClientManager.DebugLogDestination.FILE ? "file" : "console");
            LOGGER.trace("New message: {}", message);
            clientManager.processMessage(message);
        } else {
            MDC.put("clientIp", "-");
            MDC.put("sessionName", "-");
            MDC.put("disabled", "false");
            MDC.put("type", "console");
            LOGGER.error("Oops, no clientManager receiving message");
        }
        MDC.remove("clientIp");
        MDC.remove("sessionName");
        MDC.remove("disabled");
        MDC.remove("type");
    }

    /**
     * Gets called when an error occurs in a connection with a client. Removes the
     * connection from the list of connections.
     * 
     * @param connection The connection in which the error occurred.
     * @param e          The exception that occurred.
     */
    @Override
    public void onError(WebSocket connection, Exception e) {
        ClientManager clientManager = connections.remove(connection);
        String clientIp = "-";
        String sessionName = "-";
        String disabled="";
        String type="";
        if (clientManager != null) {
            clientIp = clientManager.getIp().getHostAddress();
            sessionName = clientManager.getSessionName();
            disabled = clientManager.getDebugLog() == ClientManager.DebugLogDestination.NONE ? "true" : "false";
            type = clientManager.getDebugLog() == ClientManager.DebugLogDestination.FILE ? "file" : "console";
        }
        MDC.put("clientIp", clientIp);
        MDC.put("sessionName", sessionName);
        MDC.put("disabled", disabled);
        MDC.put("type", type);

        if (clientManager != null) {
            clientManager.shutdown();
        }
        LOGGER.error(String.format("Close connection to %s due to error", clientIp), e);

        MDC.remove("clientIp");
        MDC.remove("sessionName");
        MDC.remove("disabled");
        MDC.remove("type");
    }

    /**
     * Gets called when a new connection is started.
     */
    @Override
    public void onStart() {
        LOGGER.info("Server has started");
    }

    /**
     * Stops the server including all its connections.
     * 
     * @param timeout The time out in milliseconds. If timeout is 0, there will be
     *                no time out.
     */
    public void stopServer(int timeout) {
        connections.values().forEach((clientManager) -> clientManager.stop());
        connections.clear();

        try {
            this.stop(timeout);
        } catch (InterruptedException e) {
            LOGGER.error("Error while closing the server", e);
        }
        LOGGER.info("Server closed");
    }

    /**
     * Returns the connections the server currently has.
     * 
     * @return Map indexed on WebSockets containing ClientManagers.
     */
    Map<WebSocket, ClientManager> getServerConnections() {
        return Collections.unmodifiableMap(connections);
    }

    /**
     * Returns the ClientManager and WebSocket for a given ip address.
     * 
     * @param ip Ip address of the connection to search for.
     * @return ClientManager and Websocket of the connection to the given ip
     *         address.
     */
    Map.Entry<WebSocket, ClientManager> getEntry(final String ip) {
        Entry<WebSocket, ClientManager> result = null;
        Optional<Entry<WebSocket, ClientManager>> posibleResult = connections.entrySet().stream()
            .filter(e -> e.getValue().getIp().getHostAddress().equalsIgnoreCase(ip))
            .findFirst();
        if (posibleResult.isPresent()) {
            result = posibleResult.get();
        }
        return result;
    }

	public void renameSession(String clientIp, String newSessionName) {
        Entry<WebSocket, ClientManager> entry = getEntry(clientIp);
        if (entry == null) {
            throw new ClientNotFoundException(String.format("Manager not found with ip: %s", clientIp));
        }
        ClientManager manager = entry.getValue();
        manager.setSessionName(newSessionName);
	}

	public void disableSessionLog(String clientIp) {
        Entry<WebSocket, ClientManager> entry = getEntry(clientIp);
        if (entry == null) {
            throw new ClientNotFoundException(String.format("Manager not found with ip: %s", clientIp));
        }
        ClientManager manager = entry.getValue();
        manager.setDebugLog(ClientManager.DebugLogDestination.NONE);
    }

	public void enableFileSessionLog(String clientIp) {
        Entry<WebSocket, ClientManager> entry = getEntry(clientIp);
        if (entry == null) {
            throw new ClientNotFoundException(String.format("Manager not found with ip: %s", clientIp));
        }
        ClientManager manager = entry.getValue();
        manager.setDebugLog(ClientManager.DebugLogDestination.FILE);
	}

	public void sendError(String clientIp, String message) {
        Entry<WebSocket, ClientManager> entry = getEntry(clientIp);
        if (entry == null) {
            throw new ClientNotFoundException(String.format("Manager not found with ip: %s", clientIp));
        }
        ClientManager manager = entry.getValue();
        manager.sendError(message);
	}

	public void sendErrorToAll(String message) {
        connections.values().forEach((clientManager) -> clientManager.sendError(message));
	}

	public void disconnectSession(String clientIp) {
        Entry<WebSocket, ClientManager> entry = getEntry(clientIp);
        if (entry == null) {
            throw new ClientNotFoundException(String.format("Manager not found with ip: %s", clientIp));
        }
        entry.getValue().stop();
        connections.remove(entry.getKey());
    }

}