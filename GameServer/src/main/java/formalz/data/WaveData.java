/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
package formalz.data;

import formalz.utils.TryParser;

/**
 * The class that keeps track of data about a wave.
 * @author Ludiscite
 * @version 1.0
 */
public class WaveData
{
    private int score;
    private int deltaScore;
    private int money;
    private int health;
    private int towerCount;
    private int[] preSpawned;
    private int[] prePassed;
    private int[] postSpawned;
    private int[] postPassed;
    private int[] preDeltaHealth;
    private int[] postDeltaHealth;
    private int[] moneySpent;
    private int[] timeSpent;

    /**
     * Create WaveData from the command arguments.
     * @param data Command arguments.
     */
    public WaveData(String data)
    {
        String[] split = data.split(";");
        this.score = TryParser.parseInt(split[0]);
        this.deltaScore = TryParser.parseInt(split[1]);
        this.money = TryParser.parseInt(split[2]);
        this.health = TryParser.parseInt(split[3]);
        this.towerCount = TryParser.parseInt(split[4]);
        this.preSpawned = TryParser.parseStringToIntArray(split[5]);
        this.prePassed = TryParser.parseStringToIntArray(split[6]);
        this.postSpawned = TryParser.parseStringToIntArray(split[7]);
        this.postPassed = TryParser.parseStringToIntArray(split[8]);
        this.preDeltaHealth = TryParser.parseStringToIntArray(split[9]);
        this.postDeltaHealth = TryParser.parseStringToIntArray(split[10]);
        this.moneySpent = TryParser.parseStringToIntArray(split[11]);
        this.timeSpent = TryParser.parseStringToIntArray(split[12]);
    }

    /**
     * Create WaveData.
     * @param score Score after the wave.
     * @param deltaScore Score gained during wave.
     * @param money Money after the wave.
     * @param health Health after the wave.
     * @param towerCount TowerCount after the wave.
     * @param preSpawned sparks spawned for preconditions during wave.
     * @param prePassed sparks that passed the towers for preconditions during wave.
     * @param postSpawned sparks spawned for postconditions during wave.
     * @param postPassed sparks that passed the towers for postconditions during wave.
     * @param preDeltaHealth Health gained or lost through preconditions.
     * @param postDeltaHealth Health gained or lost through postconditions.
     * @param moneySpent Money spent on different parts of the game.
     * @param timeSpent Time spent on different parts of the game.
     */
    public WaveData(int score, int deltaScore, int money, int health, int towerCount, int[] preSpawned, int[] prePassed, int[] postSpawned,
            int[] postPassed, int[] preDeltaHealth, int[] postDeltaHealth, int[] moneySpent, int[] timeSpent)
    {
        this.score = score;
        this.deltaScore = deltaScore;
        this.money = money;
        this.health = health;
        this.towerCount = towerCount;
        this.preSpawned = preSpawned;
        this.prePassed = prePassed;
        this.postSpawned = postSpawned;
        this.postPassed = postPassed;
        this.preDeltaHealth = preDeltaHealth;
        this.postDeltaHealth = postDeltaHealth;
        this.moneySpent = moneySpent;
        this.timeSpent = timeSpent;
    }

    /**
     * Returns the score after the wave.
     * @return Score after the wave.
     */
    public int getScore()
    {
        return score;
    }

    /**
     * Returns score gained during the wave.
     * @return Score gained during wave.
     */
    public int getDeltaScore()
    {
        return deltaScore;
    }

    /**
     * Returns the money after the wave.
     * @return Money after the wave.
     */
    public int getMoney()
    {
        return money;
    }

    /**
     * Returns the health after the wave.
     * @return Health after the wave.
     */
    public int getHealth()
    {
        return health;
    }

    /**
     * Returns the tower count after the wave.
     * @return TowerCount after the wave.
     */
    public int getTowerCount()
    {
        return towerCount;
    }

    /**
     * Returns the sparks that got spawned for the precondition during the wave.
     * @return Precondition spawned sparks.
     */
    public int[] getPreSpawned()
    {
        return preSpawned;
    }

    /**
     * Returns the sparks that passed the towers for the precondition.
     * @return Precondition passed sparks.
     */
    public int[] getPrePassed()
    {
        return prePassed;
    }

    /**
     * Returns the sparks that got spawned for the postcondition during the wave.
     * @return Postcondition spawned sparks.
     */
    public int[] getPostSpawned()
    {
        return postSpawned;
    }

    /**
     * Returns the sparks that passed the towers for the postcondition.
     * @return Postcondition passed sparks.
     */
    public int[] getPostPassed()
    {
        return postPassed;
    }

    /**
     * Returns the health gained and lost through preconditions.
     * @return Health gained and lost through preconditions.
     */
    public int[] getPreDeltaHealth()
    {
        return preDeltaHealth;
    }

    /**
     * Returns the health gained and lost through postconditions.
     * @return Health gained and lost through postconditions.
     */
    public int[] getPostDeltaHealth()
    {
        return postDeltaHealth;
    }

    /**
     * Returns money spent on different components.
     * @return Money spent on different components.
     */
    public int[] getMoneySpent()
    {
        return moneySpent;
    }

    /**
     * Returns time spent before wave and time spent with paused game.
     * @return Time spent.
     */
    public int[] getTimeSpent()
    {
        return timeSpent;
    }
}
