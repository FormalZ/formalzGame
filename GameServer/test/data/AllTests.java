/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
package data;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import JsonRunnables._JsonAccumulatorRunnableTest;
import connection._ConnectionManagerTest;
import connection._ConnectionTest;
import connection._ConsoleCommandManagerTest;
import connection._ServerTest;
import gamelogic._GameLogicTaskTest;
import gamelogic._GameStateTest;
import gamelogic._HintSystemTest;
import gamelogic.gametasks._EndTaskTest;
import gamelogic.gametasks._ManagerGameTaskTest;
import gamelogic.gametasks._MenuTaskTest;
import haskellapi._ProverTest;
import haskellapi._ResponseTest;
import haskellapi._StringUtilsTest;
import logger._FileLoggerTest;
import logger._PrefixLoggerTest;
import logger._PrintStreamLoggerTest;
import utils._LexUtilsTest;

@RunWith(Suite.class)
@SuiteClasses({ _DatabaseTest.class, 
	    _ProverTest.class, 
	    _ResponseTest.class, _StringUtilsTest.class, _LexUtilsTest.class,
        _GameStateTest.class, 
        _ConnectionManagerTest.class, 
	    _ResponseTest.class, _ConnectionTest.class, _MenuTaskTest.class,
        _EndTaskTest.class, _PrintStreamLoggerTest.class, _PrefixLoggerTest.class, _ManagerGameTaskTest.class, _GameLogicTaskTest.class,
        _GameSessionTest.class, _WaveDataTest.class, _ServerTest.class, _JsonAccumulatorRunnableTest.class, _FileLoggerTest.class,
        _ConsoleCommandManagerTest.class, _QueriesTest.class, _HintSystemTest.class ,
        _SignatureTransformerTest.class })
public class AllTests
{

}
