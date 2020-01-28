/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
package integration;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.sql.Timestamp;
import java.util.List;

import org.java_websocket.WebSocket;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.ArgumentCaptor;

import connection.ConsoleCommandManager;
import connection.Server;
import data.GameSession;
import data.Queries;
import data.Settings;
import logger.AbstractLogger;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class _IntegrationTest2
{
    static Queries MQueries;
    static WebSocket MWebSocket;
    static AbstractLogger MLogger;
    static InetSocketAddress MInetSocketAddress;
    static InetAddress MInetAddress;

    static Server server;
    static ConsoleCommandManager consoleCommandManager;

    static String token = "123abc456DEF";
    static int userId = 101;
    static int problemId = 99;
    static Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    static GameSession gameSession = new GameSession(userId, problemId, timestamp);

    @BeforeClass
    public static void init()
    {
        Settings.setSettingsFile("integration_settings.json");
        IntegrationTestDatabaseFiller.clearDatabase();
        IntegrationTestDatabaseFiller.fillDatabase();

        if (Settings.getDatabaseName() == "impress")
        {
            Queries.setQueries(MQueries);
        }

        MWebSocket = mock(WebSocket.class);
        MLogger = mock(AbstractLogger.class);
        MInetSocketAddress = mock(InetSocketAddress.class);
        MInetAddress = mock(InetAddress.class);

        // Address getting
        when(MWebSocket.getRemoteSocketAddress()).thenReturn(MInetSocketAddress);
        when(MInetSocketAddress.getAddress()).thenReturn(MInetAddress);
        when(MInetAddress.getHostAddress()).thenReturn("Host Adress");

        server = new Server(400);
        consoleCommandManager = new ConsoleCommandManager(server);

        server.onOpen(MWebSocket, null);
    }

    @AfterClass
    public static void finish() throws Exception
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

        server.onMessage(MWebSocket, "startup " + token);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(MWebSocket, atLeastOnce()).send(captor.capture());
        List<String> list = captor.getAllValues();

        assertTrue(list.stream().anyMatch(s -> s.contains("startup done")));

        reset(MWebSocket);
    }

    @Test
    public void test2StartFirstQuestion()
    {
        System.out.println("\n---- Test 2 ---- Start ----");

        server.onMessage(MWebSocket, "start");

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(MWebSocket, atLeastOnce()).send(captor.capture());
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

        reset(MWebSocket);
    }

    @Test
    public void test3SetWrongPrecondition()
    {
        System.out.println("\n---- Test 3 ---- Set wrong precondition ----");

        server.onMessage(MWebSocket, "submitPre (a <= 0)");

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(MWebSocket, atLeastOnce()).send(captor.capture());
        List<String> list = captor.getAllValues();

        assertTrue(list.stream().anyMatch(s -> s.contains("resultPre")));

        reset(MWebSocket);

        server.onMessage(MWebSocket, "submitPre (a != 0)");

        captor = ArgumentCaptor.forClass(String.class);
        verify(MWebSocket, atLeastOnce()).send(captor.capture());
        list = captor.getAllValues();

        assertTrue(list.stream().anyMatch(s -> s.contains("resultPre")));

        reset(MWebSocket);
    }

    @Test
    public void test4SetPreAndPostCorrect()
    {
        System.out.println("\n---- Test 4 ---- Set correct pre and postconditions ----");

        server.onMessage(MWebSocket, "submitPre (a <= 0) && (b <= 0)");
        server.onMessage(MWebSocket, "submitPost c >= 0");

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(MWebSocket, atLeastOnce()).send(captor.capture());
        List<String> list = captor.getAllValues();

        assertTrue(list.stream().anyMatch(s -> s.contains("resultPre")));
        assertTrue(list.stream().anyMatch(s -> s.contains("resultPost")));

        reset(MWebSocket);
    }

    @Test
    public void test5FinishWave()
    {
        System.out.println("\n---- Test 5 ---- Complete wave ----");

        server.onMessage(MWebSocket,
                "waveDone 5;35;1536;3;1;[1, 1, 0, 0];[16, 4, 0, 3];[25, 6, 4, 0];[103, 72, 51, 666];[13, 60];[14, 15];[5, 33, 400];[0, 12]");

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(MWebSocket, atLeastOnce()).send(captor.capture());
        List<String> list = captor.getAllValues();

        assertTrue(list.stream().anyMatch(s -> s.contains("difficulty")));
        assertTrue(list.stream().anyMatch(s -> s.contains("description")));
        assertTrue(list.stream().anyMatch(s -> s.contains("variableTypes")));
        assertTrue(list.stream().anyMatch(s -> s.contains("validPreTokens")));
        assertTrue(list.stream().anyMatch(s -> s.contains("validPostTokens")));
        assertTrue(list.stream().anyMatch(s -> s.contains("spawnWave")));

        reset(MWebSocket);
    }

    @Test
    public void test6SetPreAndPostCorrect()
    {
        System.out.println("\n---- Test 6 ---- Set correct pre and postconditions ----");

        server.onMessage(MWebSocket, "submitPre (a <= 0) && (b <= 0)");
        server.onMessage(MWebSocket, "submitPost c >= 0");

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(MWebSocket, atLeastOnce()).send(captor.capture());
        List<String> list = captor.getAllValues();

        assertTrue(list.stream().anyMatch(s -> s.contains("resultPre")));
        assertTrue(list.stream().anyMatch(s -> s.contains("resultPost")));

        reset(MWebSocket);
    }

    @Test
    public void test7FinishWave()
    {
        System.out.println("\n---- Test 7 ---- Complete wave ----");

        server.onMessage(MWebSocket,
                "waveDone 5;35;1536;3;1;[1, 1, 0, 0];[16, 4, 0, 3];[25, 6, 4, 0];[103, 72, 51, 666];[13, 60];[14, 15];[5, 33, 400];[0, 12]");

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(MWebSocket, atLeastOnce()).send(captor.capture());
        List<String> list = captor.getAllValues();

        assertTrue(list.stream().anyMatch(s -> s.contains("difficulty")));
        assertTrue(list.stream().anyMatch(s -> s.contains("description")));
        assertTrue(list.stream().anyMatch(s -> s.contains("variableTypes")));
        assertTrue(list.stream().anyMatch(s -> s.contains("validPreTokens")));
        assertTrue(list.stream().anyMatch(s -> s.contains("validPostTokens")));
        assertTrue(list.stream().anyMatch(s -> s.contains("spawnWave")));

        reset(MWebSocket);
    }

    @Test
    public void test8SetPreAndPostCorrect()
    {
        System.out.println("\n---- Test 8 ---- Set correct pre and postconditions ----");

        server.onMessage(MWebSocket, "submitPre (a <= 0) && (b <= 0)");
        server.onMessage(MWebSocket, "submitPost c >= 0");

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(MWebSocket, atLeastOnce()).send(captor.capture());
        List<String> list = captor.getAllValues();

        assertTrue(list.stream().anyMatch(s -> s.contains("resultPre")));
        assertTrue(list.stream().anyMatch(s -> s.contains("resultPost")));

        reset(MWebSocket);
    }

    @Test
    public void test9FinishWave()
    {
        System.out.println("\n---- Test 9 ---- Complete wave ----");

        server.onMessage(MWebSocket,
                "waveDone 5;35;1536;3;1;[1, 1, 0, 0];[16, 4, 0, 3];[25, 6, 4, 0];[103, 72, 51, 666];[13, 60];[14, 15];[5, 33, 400];[0, 12]");

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(MWebSocket, atLeastOnce()).send(captor.capture());
        List<String> list = captor.getAllValues();

        assertTrue(list.stream().anyMatch(s -> s.contains("finish")));

        reset(MWebSocket);
    }

    @Test
    public void test10FinalScore()
    {
        System.out.println("\n---- Test 10 ---- Final score ----");

        server.onMessage(MWebSocket, "finalScore 9001");

        reset(MWebSocket);
    }
}
