/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
package formalz.data;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import formalz.JsonRunnables.JsonAccumulatorRunnableTest;
import formalz.connection.ClientManagerTest;
import formalz.connection.ConnectionTest;
import formalz.connection.ConsoleCommandManagerTest;
import formalz.connection.ServerTest;
import formalz.connection.manager.CommandBatchRunnerTest;
import formalz.gamelogic.GameLogicTaskTest;
import formalz.gamelogic.GameStateTest;
import formalz.gamelogic.HintSystemTest;
import formalz.gamelogic.gametasks.EndTaskTest;
import formalz.gamelogic.gametasks.ManagerGameTaskTest;
import formalz.gamelogic.gametasks.MenuTaskTest;
import formalz.haskellapi.ProverTest;
import formalz.haskellapi.ResponseTest;
import formalz.haskellapi.StringUtilsTest;
import formalz.utils.LexUtilsTest;

@RunWith(Suite.class)
@SuiteClasses({ DatabaseTest.class, 
	    ProverTest.class, 
	    ResponseTest.class, StringUtilsTest.class, LexUtilsTest.class,
        GameStateTest.class, 
        ClientManagerTest.class, CommandBatchRunnerTest.class,
	    ResponseTest.class, ConnectionTest.class, MenuTaskTest.class,
        EndTaskTest.class, ManagerGameTaskTest.class, GameLogicTaskTest.class,
        GameSessionTest.class, WaveDataTest.class, ServerTest.class, JsonAccumulatorRunnableTest.class,
        ConsoleCommandManagerTest.class, QueriesTest.class, HintSystemTest.class ,
        SignatureTransformerTest.class })
public class AllTests
{

}
