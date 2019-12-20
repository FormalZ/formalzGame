/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
package connection;

import java.util.Iterator;
import java.util.Map.Entry;

import org.java_websocket.WebSocket;

import logger.AbstractLogger;
import logger.EmptyLogger;
import logger.FileLogger;
import logger.Logger;
import logger.PrefixLogger;
import logger.PrintStreamLogger;

/**
 * Manages the processing of console commands.
 * @author Ludiscite
 * @version 1.0
 */
public class ConsoleCommandManager
{
    private Server server;
    private AbstractLogger logger = new PrintStreamLogger(System.out);

    /**
     * Constructor for a ConsoleCommandManager object.
     * @param server Server that manages the connections with the clients.
     */
    public ConsoleCommandManager(Server server)
    {
        this.server = server;
    }

    /**
     * Try a command.
     * @param input Full input string.
     */
    public void tryCommand(String input)
    {
        String[] command = input.split(" ");
        if (command.length < 1)
        {
            logger.log("Empty command");
            return;
        }
        switch (command[0])
        {
            case "stop": // Stop server
                shutdownServer();
                return;

            case "rename": // Rename prefix of connection [1] to [2]
                renameConnection(command);
                break;

            case "disablelog": // Disable the log of connection [1]
                disableLog(command);
                break;

            case "filelog": // Change the logger of connection [1] to log to [2]
                fileLog(command);
                break;

            case "senderror": // Send to connection [1] an error message [2]
                // Extract the error message by removing the first two parts
                // This assumes single space seperation
                sendError(command[1], input.substring(command[0].length() + command[1].length() + 2));
                break;

            case "senderrorall": // Send to all connections an error message [2]
                sendErrorAll(input.substring(command[0].length() + 1));
                break;

            case "disconnect": // Disconnect connection [1]
                disconnect(command);
                break;

            case "h": // List all commands
            case "help":
                logger.log("- help \n" + "- stop \n" + "- rename (ip address) (name) \n" + "- disablelog (ip address) \n"
                        + "- filelog (ip address) (file name) \n" + "- senderror (ip address) (message) \n" + "- senderrorall (message) \n"
                        + "- disconnect (ip address) \n");
                break;

            // Add new console commands here
            default:
                logger.log("Unknown command: " + command[0]);
                break;
        }
    }

    /**
     * Rename the prefix of a connection.
     * @param arguments [1] Ip address of the connection, [2] new prefix.
     */
    private void renameConnection(String[] arguments)
    {
        if (arguments.length != 3)
        {
            return;
        }
        Entry<WebSocket, ClientManager> entry = server.getEntry(arguments[1]);
        if (entry == null)
        {
            logger.log("Manager not found with ip: " + arguments[1]);
            return;
        }
        ClientManager manager = entry.getValue();

        Client client = manager.getClient();
        AbstractLogger logger = new PrefixLogger(Logger.getLogger(), arguments[2] + " : ");
        client.setLogger(logger);
    }

    /**
     * Disable the logger of a connection.
     * @param arguments [1] Ip address of connection.
     */
    private void disableLog(String[] arguments)
    {
        if (arguments.length != 2)
        {
            return;
        }
        Entry<WebSocket, ClientManager> entry = server.getEntry(arguments[1]);
        if (entry == null)
        {
            logger.log("Manager not found with ip: " + arguments[1]);
            return;
        }
        ClientManager manager = entry.getValue();
        Client client = manager.getClient();
        AbstractLogger logger = new EmptyLogger();
        client.setLogger(logger);
    }

    /**
     * Change the logger of a connection to log to a file.
     * @param arguments [1] Ip address of the connection, [2] name of the file to log to.
     */
    private void fileLog(String[] arguments)
    {
        if (arguments.length != 3)
        {
            return;
        }
        Entry<WebSocket, ClientManager> entry = server.getEntry(arguments[1]);
        if (entry == null)
        {
            logger.log("Manager not found with ip: " + arguments[1]);
            return;
        }
        ClientManager manager = entry.getValue();
        Client client = manager.getClient();
        AbstractLogger logger = new FileLogger(arguments[2] + ".txt");
        client.setLogger(logger);
    }

    /**
     * Send an error message to a specific connection.
     * @param ip Ip address of the connection to send the error over.
     * @param message Error message to send.
     */
    private void sendError(String ip, String message)
    {
        Entry<WebSocket, ClientManager> entry = server.getEntry(ip);
        if (entry == null)
        {
            logger.log("Manager not found with ip: " + ip);
            return;
        }
        ClientManager manager = entry.getValue();
        Client client = manager.getClient();
        client.getConnection().sendError(message);
    }

    /**
     * Send an error message to all connections.
     * @param message Error message to send.
     */
    private void sendErrorAll(String message)
    {
        Iterator<Entry<WebSocket, ClientManager>> iterator = server.getConnections().entrySet().iterator();
        while (iterator.hasNext())
        {
            System.out.println("NO");
            Entry<WebSocket, ClientManager> entry = (Entry<WebSocket, ClientManager>) iterator.next();
            ClientManager manager = entry.getValue();
            manager.getClient().getConnection().sendError(message);
        }
    }

    /**
     * Disconnect a connection.
     * @param arguments [1] ip address of the connection to disconnect.
     */
    private void disconnect(String[] arguments)
    {
        if (arguments.length >= 3)
        {
            return;
        }
        Entry<WebSocket, ClientManager> entry = server.getEntry(arguments[1]);
        if (entry == null)
        {
            logger.log("Manager not found with ip: " + arguments[1]);
            return;
        }
        WebSocket socket = entry.getKey();
        socket.close();
    }

    /**
     * Shuts down the server including all connections.
     */
    private void shutdownServer()
    {
        server.stopServer(0);
    }
}
