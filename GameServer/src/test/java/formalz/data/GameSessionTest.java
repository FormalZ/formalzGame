/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
package formalz.data;

import static org.junit.Assert.*;

import java.sql.Timestamp;

import org.junit.Test;
import static org.mockito.Mockito.*;

public class GameSessionTest {
    /**
     * Test whether the time difference check works correctly.
     */
    @Test
    public void testCheckDifference() {
        Timestamp MTimestamp = mock(Timestamp.class);

        int userId = 1;
        int problemId = 2;

        GameSession session = new GameSession(userId, problemId, MTimestamp);

        assertEquals(userId, session.getUserId());
        assertEquals(problemId, session.getProblemId());
        assertSame(MTimestamp, session.getCreatedAt());

        long timeDif = 1000l;
        Settings.setMaxSessionCreatedDifference(timeDif);

        when(MTimestamp.getTime()).thenReturn(System.currentTimeMillis());
        assertTrue(session.checkDifference());

        when(MTimestamp.getTime()).thenReturn(System.currentTimeMillis() - timeDif * 10);
        assertFalse(session.checkDifference());
    }
}
