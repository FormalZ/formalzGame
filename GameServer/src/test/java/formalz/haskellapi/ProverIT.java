package formalz.haskellapi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

import formalz.data.Settings;

public class ProverIT {
    @Rule
    public GenericContainer checker = new GenericContainer(DockerImageName.parse("eucm/formalz-fchecker:1.0.0"))
            .withCommand("/usr/local/bin/javawlp -p 8080 --runServer").withExposedPorts(8080);

    @Before
    public void setUp() {
        Settings.setSettingsFile("integration_settings.json");
        Settings.setHaskellTargetURL("http://" + checker.getHost());
        Settings.setHaskellPort(checker.getFirstMappedPort());
        Prover.setProver(null);
    }

    /**
     * Test whether the real connection handles wrong syntax as we expect.
     */
    @Test
    public void testRealConnectionWrongSyntax() {
        String sourceA = "public static float real1_1(float a) {\\npre(a >= (2 - 1 + 1));\\na += a;\\npost(a >= (4 - 3 + 3));}";
        String sourceB = "public static float real1_1(float a) {\\npre(a >= 2));\\na += a;\\npost(a >= 4);}";

        Prover prover = Prover.getInstance();
        Response resp = prover.compare(sourceA, sourceB);
        assertEquals(500, resp.getResponseCode());
    }

    /**
     * Test whether the real connection works with equivalent conditions.
     */
    @Test
    public void testRealConnectionEqual() {
        String sourceA = "public static float real1_1(float a) {\\npre(a >= (2 - 1 + 1));\\na += a;\\npost(a >= (4 - 3 + 3));}";
        String sourceB = "public static float real1_1(float a) {\\npre(a >= 2);\\na += a;\\npost(a >= 4);}";

        Prover prover = Prover.getInstance();
        Response resp = prover.compare(sourceA, sourceB);
        assertTrue(resp.isEquivalent());
    }
}
