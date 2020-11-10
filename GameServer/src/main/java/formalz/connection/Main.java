/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
package formalz.connection;

import java.sql.SQLException;
import java.util.Scanner;
import java.util.Timer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.mariadb.jdbc.MariaDbPoolDataSource;
import org.mariadb.jdbc.internal.util.pool.Pool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import formalz.data.Database;
import formalz.data.Queries;
import formalz.data.Settings;

/**
 * The main class ran by the server.
 * 
 * @author Ludiscite
 * @version 1.0
 */
public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    private static MariaDbPoolDataSource pool;

    private static Thread dbStatsThread;

    /**
     * The main method. Starts up the server and starts reading commands from the
     * console.
     * 
     * @param args This is currently ignored.
     */
    public static void main(String[] args) {
        String jdbcUrl = Settings.getDatabaseURL();
        pool = new MariaDbPoolDataSource(jdbcUrl);
        try {
            pool.setPoolName("MariaDBPool");
            pool.setMinPoolSize(Settings.getDatabaseMinPoolSize());
            pool.setMaxPoolSize(Settings.getDatabaseMaxPoolSize());
        } catch (SQLException e) {
            LOGGER.error("Can not create DB connection pool", e);
            System.exit(1);
        }
        showDBPoolStats();

        Database db = Database.getInstance(pool);
        ExecutorService executorService = Executors.newFixedThreadPool(Math.max(1, Settings.getWorkerThreadNumber()));
        Timer timer = new Timer(Server.class.getSimpleName() + "-timer");
        Server server = new Server(ClientManagerFactory.DEFAULT, ClientFactory.DEFAULT, timer, executorService,
                Queries.getInstance());

        server.start();

        if (args.length > 0) {
            try (Scanner s = new Scanner(System.in)) {
                ConsoleCommandManager consoleCommandManager = new ConsoleCommandManager(server);

                while (true) {
                    String input = s.nextLine();
                    try {
                        consoleCommandManager.tryCommand(input);
                    } catch (Exception e) {
                        LOGGER.error("Error processing commands", e);
                    }
                }
            }
        }

    }

    private static void showDBPoolStats() {
        if (LOGGER.isTraceEnabled()) {
            dbStatsThread = new Thread(new Runnable(){

                @Override
                public void run() {
                    boolean interrupted = false;
                    Pool internalPool = pool.testGetPool();
                    while (!interrupted && (internalPool == null)) {
                        LOGGER.debug("DB pool not available yet");
                        try {
                            Thread.sleep(10*1000);
                        } catch (InterruptedException e) {
                            LOGGER.error("Thread interrupted", e);
                            interrupted = true;
                        }
                        internalPool = pool.testGetPool();
                    }
                    LOGGER.debug("DB pool available");
                    while (!interrupted) {
                        long totalConnections = -1;
                        long idleConnections = -1;
                        if (internalPool != null ) {
                            totalConnections = internalPool.getTotalConnections();
                            idleConnections = internalPool.getIdleConnections();
                        }
                        LOGGER.trace("DB pool stats: idle={}, total={}", idleConnections, totalConnections);
                        try {
                            Thread.sleep(5*1000);
                        } catch (InterruptedException e) {
                            LOGGER.error("Thread interrupted", e);
                            interrupted = true;
                        }
                    }
                }
                
            });
            dbStatsThread.setName("dbStatsThread");
            dbStatsThread.start();
        }
    }
}