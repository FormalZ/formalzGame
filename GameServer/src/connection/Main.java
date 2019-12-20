/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
package connection;

import java.util.Scanner;

import data.Settings;

/**
 * The main class ran by the server.
 * @author Ludiscite
 * @version 1.0
 */
public class Main
{
    private static final Server server = new Server(Settings.getConnectionPort());
    private static final Scanner s = new Scanner(System.in);
    private static final ConsoleCommandManager consoleCommandManager = new ConsoleCommandManager(server);

    private static final boolean debug = false;
    
    /**
     * The main method. Starts up the server and starts reading commands from the console.
     * @param args This is currently ignored.
     */
    public static void main(String[] args)
    {
        server.start();

        if(debug)
        {
            while (true)
            {
                String input = s.nextLine();
                try
                {
                    consoleCommandManager.tryCommand(input);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }

    }
}