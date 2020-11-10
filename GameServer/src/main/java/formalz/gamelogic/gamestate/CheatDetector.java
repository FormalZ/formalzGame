/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
package formalz.gamelogic.gamestate;

import formalz.data.Problem;
import formalz.data.WaveData;

/**
 * The system checking if a player is cheating.
 * @author Ludiscite
 * @version 1.0
 */
public class CheatDetector
{
    private int cheatCounter = 0;
    private int maxCheatCounter = 3;

    private int previousScore = 0;

    private int availableScore = 0;
    private int scoreGained = 0;

    public void processNewProblem(Problem problem, int availableScore)
    {
        this.availableScore = availableScore;
        
        this.scoreGained = 0;
    }
    
    /**
     * Process data about the previous wave.
     * @param waveData
     */
    public void processWaveData(WaveData waveData, int availableScore)
    {
        this.scoreGained += waveData.getDeltaScore();
        
        // Received too much score during the problem.
        if(this.scoreGained > availableScore)
        {
            cheatCounter += maxCheatCounter;
        }
        
        // Score decreased during the problem.
        if(this.scoreGained < 0)
        {
            cheatCounter += maxCheatCounter;
        }
        
        // Score inconsistency.
        if(previousScore + waveData.getDeltaScore() != waveData.getScore())
        {
            cheatCounter++;
        }
        
        // Received too much money during a single wave.
        if(waveData.getMoney() > 1000000)
        {
            cheatCounter += maxCheatCounter;
        }
         
        // Negative money is not possible.
        if(waveData.getMoney() < 0)
        {
            cheatCounter += maxCheatCounter;
        }
        
        previousScore = waveData.getScore();
    }

    /**
     * Increase the amount of times the player cheated.
     * @param amount Increase the amount of times the player cheated.
     */
    public void increaseCheatCounter(int amount)
    {
        this.cheatCounter += amount;
    }

    /**
     * Returns whether the player has cheated.
     * @return Whether the player has cheated.
     */
    public boolean hasCheated()
    {
        return cheatCounter >= maxCheatCounter;
    }
}
