/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
package formalz.gamelogic.gametasks;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import formalz.connection.Client;

public class ManagerGameTaskTest {
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    GameTask MGameTask1;
    @Mock
    GameTask MGameTask2;
    @Mock
    Client MClient;

    /**
     * Test whether a correct command is processed correctly.
     */
    @Test
    public void testTryCommandCorrect() {
        Set<GameTask> tasks = new HashSet<GameTask>();
        tasks.add(MGameTask1);
        tasks.add(MGameTask2);

        String command = "command";
        String arguments = "arguments";

        when(MGameTask1.tryCommand(eq(command), eq(arguments))).thenReturn(true);

        GameTask task = new ManagerGameTask(MClient, tasks);

        assertTrue(task.tryCommand(command, arguments));
    }

    /**
     * Test whether a wrong command is processed correctly.
     */
    @Test
    public void testTryCommandIncorrect() {
        Set<GameTask> tasks = new HashSet<GameTask>();
        tasks.add(MGameTask1);
        tasks.add(MGameTask2);

        String command = "command";
        String arguments = "arguments";

        when(MGameTask1.tryCommand(eq(command), eq(arguments))).thenReturn(false);
        when(MGameTask2.tryCommand(eq(command), eq(arguments))).thenReturn(false);

        GameTask task = new ManagerGameTask(MClient, tasks);

        assertFalse(task.tryCommand(command, arguments));
    }

}
