/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
package formalz.integration;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.ExecutorService;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import formalz.connection.ClientFactory;
import formalz.connection.ClientManagerFactory;
import formalz.connection.ConsoleCommandManager;
import formalz.connection.Server;
import formalz.data.GameSession;
import formalz.data.Queries;
import formalz.data.Settings;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class _IntegrationTest2 {
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    Queries queries;

    @Mock
    WebSocket socket;

    @Mock
    ClientHandshake clientHandshake;

    InetSocketAddress socketAddress;
    InetAddress address;

    Server server;
    ConsoleCommandManager consoleCommandManager;

    String token = "123abc456DEF";
    int userId = 101;
    int problemId = 99;
    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    GameSession gameSession = new GameSession(userId, problemId, timestamp);

    @Mock
    Timer timer;

    @Mock
    ExecutorService executorService;

    @Before
    public void setUp() throws SQLException
    {
        Settings.setSettingsFile("integration_settings.json");
        IntegrationTestDatabaseFiller.clearDatabase();
        IntegrationTestDatabaseFiller.fillDatabase();

        if (Settings.getDatabaseName() == "impress")
        {
            Queries.setQueries(queries);
        }

        socket = mock(WebSocket.class);
        socketAddress = mock(InetSocketAddress.class);
        address = mock(InetAddress.class);

        // Address getting
        when(socket.getRemoteSocketAddress()).thenReturn(socketAddress);
        when(socketAddress.getAddress()).thenReturn(address);
        when(address.getHostAddress()).thenReturn("Host Adress");

        server = new Server(ClientManagerFactory.DEFAULT, ClientFactory.DEFAULT, timer, executorService, queries);
        server.onOpen(socket, clientHandshake);
    }

    @After
    public void finish() throws Exception
    {
        server.stop();
    }

    @Test
    public void test1Startup()
    {
        System.out.println("-----------------------------");
        System.out.println("--- Second test - mistake ---");
        System.out.println("-----------------------------");
        System.out.println("---- Test 1 ---- Startup ----");

        server.onMessage(socket, "startup " + token);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(socket, atLeastOnce()).send(captor.capture());
        List<String> list = captor.getAllValues();

        assertTrue(list.stream().anyMatch(s -> s.contains("startup done")));

        reset(socket);
    }

    @Test
    public void test2StartFirstQuestion()
    {
        System.out.println("\n---- Test 2 ---- Start ----");

        server.onMessage(socket, "start");

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

        reset(socket);
    }

    @Test
    public void test3SetWrongPrecondition()
    {
        System.out.println("\n---- Test 3 ---- Set wrong precondition ----");

        server.onMessage(socket, "submitPre (a <= 0)");

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(socket, atLeastOnce()).send(captor.capture());
        List<String> list = captor.getAllValues();

        assertTrue(list.stream().anyMatch(s -> s.contains("resultPre")));

        reset(socket);

        server.onMessage(socket, "submitPre (a != 0)");

        captor = ArgumentCaptor.forClass(String.class);
        verify(socket, atLeastOnce()).send(captor.capture());
        list = captor.getAllValues();

        assertTrue(list.stream().anyMatch(s -> s.contains("resultPre")));

        reset(socket);
    }

    @Test
    public void test4SetPreAndPostCorrect()
    {
        System.out.println("\n---- Test 4 ---- Set correct pre and postconditions ----");

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
    public void test5FinishWave()
    {
        System.out.println("\n---- Test 5 ---- Complete wave ----");

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
    public void test6SetPreAndPostCorrect()
    {
        System.out.println("\n---- Test 6 ---- Set correct pre and postconditions ----");

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
    public void test7FinishWave()
    {
        System.out.println("\n---- Test 7 ---- Complete wave ----");

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
    public void test8SetPreAndPostCorrect()
    {
        System.out.println("\n---- Test 8 ---- Set correct pre and postconditions ----");

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
    public void test9FinishWave()
    {
        System.out.println("\n---- Test 9 ---- Complete wave ----");

        server.onMessage(socket,
                "waveDone 5;35;1536;3;1;[1, 1, 0, 0];[16, 4, 0, 3];[25, 6, 4, 0];[103, 72, 51, 666];[13, 60];[14, 15];[5, 33, 400];[0, 12]");

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(socket, atLeastOnce()).send(captor.capture());
        List<String> list = captor.getAllValues();

        assertTrue(list.stream().anyMatch(s -> s.contains("finish")));

        reset(socket);
    }

    @Test
    public void test10FinalScore()
    {
        System.out.println("\n---- Test 10 ---- Final score ----");

        server.onMessage(socket, "finalScore 9001");

        reset(socket);
    }
}
