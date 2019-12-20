/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
package connection;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import data.Settings;
import logger.Logger;
import utils.Factory;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import org.java_websocket.server.DefaultSSLWebSocketServerFactory;

/**
 * Custom implementation of a WebSocketServer. This keeps track of all client connections and all communication with the clients.
 * @author Ludiscite
 * @version 1.0
 */
public class Server extends WebSocketServer
{
    /**
     * The map from WebSockets to their corresponding ClientConnection objects.
     */
    private Map<WebSocket, ClientManager> connections;

    /**
     * Constructor of a Server object.
     * @param port Port the listen to for connections.
     */
    public Server(int port)
    {
        super(new InetSocketAddress(port));
        connections = new ConcurrentHashMap<WebSocket, ClientManager>();
    }

    @Override
    public void start()
    {
        try
        {
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
        catch (Exception e)
        {
            Logger.log(e);
        }
        super.start();
    }

    /**
     * Gets called when a new client connection is opened. Saves the connection and initializes a corresponding ClientCommunication object.
     * @param connection The client connection that is opened.
     * @param handshake The handshake with the client.
     */
    @Override
    public void onOpen(WebSocket connection, ClientHandshake handshake)
    {
        connections.put(connection, Factory.constructClientManager(connection));
        Logger.log("New connection from " + connection.getRemoteSocketAddress().getAddress().getHostAddress());
    }

    /**
     * Gets called when a client connection is closed. Removes the connection from the list of connections.
     * @param connection The client connection that is closed.
     * @param code This parameter currently gets ignored.
     * @param reason The reason the connection is closed.
     * @param remote This parameter currently gets ignored.
     */
    @Override
    public void onClose(WebSocket connection, int code, String reason, boolean remote)
    {
        connections.remove(connection);
        Logger.log("Closed connection to " + connection.getRemoteSocketAddress().getAddress().getHostAddress());
        Logger.log("Reason: " + reason);
    }

    /**
     * Gets called when a message is received from a client. Lets the corresponding ClientConnection object handle the message.
     * @param connection The client from which the message is received.
     * @param message The message that is received.
     */
    @Override
    public void onMessage(WebSocket connection, String message)
    {
        connections.get(connection).processMessage(message);
    }

    /**
     * Gets called when an error occurs in a connection with a client. Removes the connection from the list of connections.
     * @param connection The connection in which the error occurred.
     * @param e The exception that occurred.
     */
    @Override
    public void onError(WebSocket connection, Exception e)
    {
        if (connection != null)
        {
            Logger.log("Error from " + connection.getRemoteSocketAddress().getAddress().getHostAddress());
        }
        Logger.log(e);
        if (connection != null)
        {
            connections.remove(connection);
            Logger.log("Closed connection to " + connection.getRemoteSocketAddress().getAddress().getHostAddress());
        }
    }

    /**
     * Gets called when a new connection is started.
     */
    @Override
    public void onStart()
    {
        Logger.log("Server has started");
    }

    /**
     * Stops the server including all its connections.
     * @param timeout The time out in milliseconds. If timeout is 0, there will be no time out.
     */
    public void stopServer(int timeout)
    {
        for (ClientManager connection : connections.values())
        {
            connection.stop();
        }
        connections.clear();

        try
        {
            this.stop(timeout);
            Logger.log("Server closed");
        }
        catch (InterruptedException e)
        {
            Logger.log("Error while closing the server:");
            Logger.log(e);
        }
    }

    /**
     * Returns the connections the server currently has.
     * @return Map indexed on WebSockets containing ClientManagers.
     */
    public Map<WebSocket, ClientManager> getConnections()
    {
        return connections;
    }

    /**
     * Returns the ClientManager and WebSocket for a given ip address.
     * @param ip Ip address of the connection to search for.
     * @return ClientManager and Websocket of the connection to the given ip address.
     */
    public Entry<WebSocket, ClientManager> getEntry(String ip)
    {
        ip = ip.replaceAll(" ", "");
        Iterator<Entry<WebSocket, ClientManager>> iterator = connections.entrySet().iterator();
        while (iterator.hasNext())
        {
            Entry<WebSocket, ClientManager> entry = (Entry<WebSocket, ClientManager>) iterator.next();
            WebSocket socket = entry.getKey();
            String socketIp = socket.getRemoteSocketAddress().getAddress().getHostAddress();
            socketIp = socketIp.replaceAll(" ", "");
            if (socketIp.equalsIgnoreCase(ip))
            {
                return entry;
            }
        }
        return null;
    }
}