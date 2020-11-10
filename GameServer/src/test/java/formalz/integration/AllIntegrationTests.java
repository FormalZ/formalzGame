/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
package formalz.integration;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import formalz.haskellapi.ProverIT;

@RunWith(Suite.class)
@SuiteClasses({ IntegrationTest1.class, _IntegrationTest2.class, ProverIT.class })
public class AllIntegrationTests
{

}
