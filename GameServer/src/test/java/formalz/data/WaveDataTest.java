/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
package formalz.data;

import org.junit.Test;

import static org.junit.Assert.*;

public class WaveDataTest {
    /**
     * Test whether the constructor from a string is handled correctly.
     */
    @Test
    public void testStringConstructor() {
        WaveData data = new WaveData(
                "0;100;5050;40;5;[5, 0, 0, 5];[1, 2, 3, 4];[5, 6, 7, 8];[9, 10, 11, 12];[9, 12];[10, 11];[11, 12];[9, 10]");
        assertEquals(0, data.getScore());
        assertEquals(100, data.getDeltaScore());
        assertEquals(5050, data.getMoney());
        assertEquals(40, data.getHealth());
        assertEquals(5, data.getTowerCount());
        assertArrayEquals(new int[] { 5, 0, 0, 5 }, data.getPreSpawned());
        assertArrayEquals(new int[] { 1, 2, 3, 4 }, data.getPrePassed());
        assertArrayEquals(new int[] { 5, 6, 7, 8 }, data.getPostSpawned());
        assertArrayEquals(new int[] { 9, 10, 11, 12 }, data.getPostPassed());
        assertArrayEquals(new int[] { 9, 12 }, data.getPreDeltaHealth());
        assertArrayEquals(new int[] { 10, 11 }, data.getPostDeltaHealth());
        assertArrayEquals(new int[] { 11, 12 }, data.getMoneySpent());
        assertArrayEquals(new int[] { 9, 10 }, data.getTimeSpent());
    }

    /**
     * Test whether the constructor from all data seperate is handled correctly.
     */
    @Test
    public void testArgumentsConstructor() {
        WaveData data = new WaveData(0, 100, 5050, 40, 5, new int[] { 5, 0, 0, 5 }, new int[] { 1, 2, 3, 4 },
                new int[] { 5, 6, 7, 8 }, new int[] { 9, 10, 11, 12 }, new int[] { 9, 12 }, new int[] { 10, 11 },
                new int[] { 11, 12 }, new int[] { 9, 10 });
        assertEquals(0, data.getScore());
        assertEquals(100, data.getDeltaScore());
        assertEquals(5050, data.getMoney());
        assertEquals(40, data.getHealth());
        assertEquals(5, data.getTowerCount());
        assertArrayEquals(new int[] { 5, 0, 0, 5 }, data.getPreSpawned());
        assertArrayEquals(new int[] { 1, 2, 3, 4 }, data.getPrePassed());
        assertArrayEquals(new int[] { 5, 6, 7, 8 }, data.getPostSpawned());
        assertArrayEquals(new int[] { 9, 10, 11, 12 }, data.getPostPassed());
        assertArrayEquals(new int[] { 9, 12 }, data.getPreDeltaHealth());
        assertArrayEquals(new int[] { 10, 11 }, data.getPostDeltaHealth());
        assertArrayEquals(new int[] { 11, 12 }, data.getMoneySpent());
        assertArrayEquals(new int[] { 9, 10 }, data.getTimeSpent());
    }

}
