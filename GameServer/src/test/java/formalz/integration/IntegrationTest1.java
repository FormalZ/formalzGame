/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
package formalz.integration;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.sql.Timestamp;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.ExecutorService;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.junit.*;
import org.junit.runners.MethodSorters;
import org.mariadb.jdbc.MariaDbPoolDataSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.utility.DockerImageName;

import formalz.connection.Client;
import formalz.connection.ClientFactory;
import formalz.connection.ClientManager;
import formalz.connection.ClientManagerFactory;
import formalz.connection.ConsoleCommandManager;
import formalz.connection.Server;
import formalz.connection.manager.CommandBatchRunner;
import formalz.data.Database;
import formalz.data.GameSession;
import formalz.data.Queries;
import formalz.data.Settings;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class IntegrationTest1 {
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    // XXX webhippie/mariadb:latest (nov 19) -> mariadb:10.4
    @Rule
    public MariaDBContainer mariadb = new MariaDBContainer<>(DockerImageName.parse("mariadb").withTag("10.4"))
            .withExposedPorts(3306)
            .withClasspathResourceMapping("formalz.sql", "/docker-entrypoint-initdb.d/formalz.sql", BindMode.READ_ONLY);

    Server server;
    ConsoleCommandManager consoleCommandManager;

    String token = IntegrationTestDatabaseFiller.TOKEN;
    int userId = 101;
    int problemId = 99;
    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    GameSession gameSession = new GameSession(userId, problemId, timestamp);

    @Mock
    WebSocket socket;

    @Mock
    ClientHandshake clientHandshake;

    InetAddress address;

    InetSocketAddress socketAddress;

    @Mock
    ClientManagerFactory clientManagerFactory;

    @Mock
    ClientManager clientManager;

    @Mock
    ClientFactory clientFactory;

    @Mock
    Client client;

    @Mock
    Timer timer;

    @Mock
    ExecutorService executorService;

    Database database;

    Queries queries;

    @Before
    public void setUp() throws Exception
    {
        Settings.setSettingsFile("integration_settings.json");
        Settings.setDatabaseName(mariadb.getDatabaseName());
        Settings.setDatabaseUsername(mariadb.getUsername());
        Settings.setDatabasePassword(mariadb.getPassword());
        Settings.setDatabaseURL(mariadb.getJdbcUrl());
        IntegrationTestDatabaseFiller.fillDatabase();

        address = InetAddress.getByName("127.0.0.1");
        socketAddress = new InetSocketAddress(address, 8080);

        when(socket.getRemoteSocketAddress()).thenReturn(socketAddress);

        when(clientManagerFactory.create(any(), any(), any(), any(), any(), any(), any())).thenReturn(clientManager);
        when(clientFactory.constructClient(any())).thenReturn(client);

        when(clientManager.getIp()).thenReturn(address);
        when(clientManager.getDebugLog()).thenReturn(ClientManager.DebugLogDestination.NONE);
        when(clientManager.getSessionName()).thenReturn("sessionName");

        MariaDbPoolDataSource pool = new MariaDbPoolDataSource(mariadb.getJdbcUrl());
        pool.setPoolName("MariaDBPool");
        pool.setMinPoolSize(2);
        pool.setMaxPoolSize(8);

        database = Database.getInstance(pool);

        Queries.setQueries(null);
        queries = Queries.getInstance();

        server = new Server(ClientManagerFactory.DEFAULT, ClientFactory.DEFAULT, timer, executorService, queries);
        server.onOpen(socket, clientHandshake);
    }

    @After
    public void tearDown() throws Exception
    {
        server.stop();
    }

    @Test
    public void test1Startup()
    {
        // Given
        System.out.println("-----------------------------");
        System.out.println("- First test - Fastest path -");
        System.out.println("-----------------------------");
        System.out.println("---- Test 1 ---- Startup ----");

        // When
        server.onMessage(socket, "startup " + token);


        ArgumentCaptor<Runnable> captureRunnable = ArgumentCaptor.forClass(Runnable.class);
        verify(executorService, times(1)).submit(captureRunnable.capture());
        CommandBatchRunner runner = (CommandBatchRunner)captureRunnable.getValue();
        runner.run();

        // Then
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(socket, atLeastOnce()).send(captor.capture());
        List<String> list = captor.getAllValues();

        assertTrue(list.stream().anyMatch(s -> s.contains("startup done")));
    }

    private void startup() {
        server.onMessage(socket, "startup " + token);

        ArgumentCaptor<Runnable> captureRunnable = ArgumentCaptor.forClass(Runnable.class);
        verify(executorService, atLeast(1)).submit(captureRunnable.capture());
        CommandBatchRunner runner = (CommandBatchRunner)captureRunnable.getValue();
        runner.run();

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(socket, atLeast(2)).send(captor.capture());
        List<String> list = captor.getAllValues();
        assertTrue(list.stream().anyMatch(s -> s.contains("startup done")));
        assertTrue(list.stream().anyMatch(s -> s.contains("sessionId 1 1")));
    }

    @Test
    public void test2StartFirstQuestion()
    {
        // Given
        System.out.println("\n---- Test 2 ---- Start ----");
        startup();

        // When
        server.onMessage(socket, "startGame "+Integer.valueOf(IntegrationTestDatabaseFiller.HASH));

        ArgumentCaptor<Runnable> captureRunnable = ArgumentCaptor.forClass(Runnable.class);
        verify(executorService, atLeast(2)).submit(captureRunnable.capture());
        CommandBatchRunner runner = (CommandBatchRunner)captureRunnable.getValue();
        runner.run();

        // Then
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(socket, atLeastOnce()).send(captor.capture());
        List<String> list = captor.getAllValues();

        assertTrue(list.stream().anyMatch(s -> s.contains("start")));
        assertTrue(list.stream().anyMatch(s -> s.contains("path")));
        assertTrue(list.stream().anyMatch(s -> s.contains("description")));
        assertTrue(list.stream().anyMatch(s -> s.contains("difficulty")));
        assertTrue(list.stream().anyMatch(s -> s.contains("variableTypes")));
        assertTrue(list.stream().anyMatch(s -> s.contains("validPreTokens")));
        assertTrue(list.stream().anyMatch(s -> s.contains("validPostTokens")));
        assertTrue(list.stream().anyMatch(s -> s.contains("spawnWave")));
        assertTrue(list.stream().anyMatch(s -> s.contains("hint")));
/*
*/
    }

    @Test
    public void test3SetPreAndPostCorrect()
    {
        System.out.println("\n---- Test 3 ---- Set correct pre and postconditions ----");

        server.onMessage(socket, "submitPre (a <= 0) && (b <= 0)");
        server.onMessage(socket, "submitPost c >= 0");

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(socket, atLeastOnce()).send(captor.capture());
        List<String> list = captor.getAllValues();

        assertTrue(list.stream().anyMatch(s -> s.contains("resultPre")));
        assertTrue(list.stream().anyMatch(s -> s.contains("resultPost")));

        reset(socket);
    }

    @Test
    public void test4FinishWave()
    {
        System.out.println("\n---- Test 4 ---- Complete wave ----");

        server.onMessage(socket,
                "waveDone 5;35;1536;3;1;[1, 1, 0, 0];[16, 4, 0, 3];[25, 6, 4, 0];[103, 72, 51, 666];[13, 60];[14, 15];[5, 33, 400];[0, 12]");

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(socket, atLeastOnce()).send(captor.capture());
        List<String> list = captor.getAllValues();

        assertTrue(list.stream().anyMatch(s -> s.contains("difficulty")));
        assertTrue(list.stream().anyMatch(s -> s.contains("description")));
        assertTrue(list.stream().anyMatch(s -> s.contains("variableTypes")));
        assertTrue(list.stream().anyMatch(s -> s.contains("validPreTokens")));
        assertTrue(list.stream().anyMatch(s -> s.contains("validPostTokens")));
        assertTrue(list.stream().anyMatch(s -> s.contains("spawnWave")));

        reset(socket);
    }

    @Test
    public void test5SetPreAndPostCorrect()
    {
        System.out.println("\n---- Test 5 ---- Set correct pre and postconditions ----");

        server.onMessage(socket, "submitPre (a <= 0) && (b <= 0)");
        server.onMessage(socket, "submitPost c >= 0");

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(socket, atLeastOnce()).send(captor.capture());
        List<String> list = captor.getAllValues();

        assertTrue(list.stream().anyMatch(s -> s.contains("resultPre")));
        assertTrue(list.stream().anyMatch(s -> s.contains("resultPost")));

        reset(socket);
    }

    @Test
    public void test6FinishWave()
    {
        System.out.println("\n---- Test 6 ---- Complete wave ----");

        server.onMessage(socket,
                "waveDone 5;35;1536;3;1;[1, 1, 0, 0];[16, 4, 0, 3];[25, 6, 4, 0];[103, 72, 51, 666];[13, 60];[14, 15];[5, 33, 400];[0, 12]");

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(socket, atLeastOnce()).send(captor.capture());
        List<String> list = captor.getAllValues();

        assertTrue(list.stream().anyMatch(s -> s.contains("finish")));

        reset(socket);
    }

    @Test
    public void test7FinalScore()
    {
        System.out.println("\n---- Test 7 ---- Final score ----");

        server.onMessage(socket, "finalScore 9001");

        reset(socket);
    }
}
