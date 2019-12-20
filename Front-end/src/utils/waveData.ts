/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University
 * within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
/**
 * All the data that is saved for a wave. It's a class since there is no struct in TypeScript.
 * properties to set:
 * score,
 * deltaScore,
 * money,
 * health,
 * towerCount,
 * preSpawned: [TT,TF,FT,FF],
 * prePassed: [TT,TF,FT,FF],
 * postSpawned: [TT,TF,FT,FF],
 * postPassed: [TT,TF,FT,FF],
 * preDeltaHealth,
 * postDeltaHealth,
 * moneySpent: [towersSpent,preSpent,postSpent],
 * timeSpent: [beforeWave,paused]
 */
export default class WaveData {
    /**
     * @property {number} score - The current score
     */
    readonly score: number;
    /**
     * @property {number} deltaScore - The score earned this wave.
     */
    readonly deltaScore: number;
    /**
     * @property {number} money - The current money
     */
    readonly money: number;
    /**
     * @property {number} health - The current health
     */
    readonly health: number;
    /**
     * @property {number} towerCount - The total amount of towers
     */
    readonly towerCount: number;
    /**
     * @property {number[]} preSpawned - What kind of sparks spawned for the precondition:
     * [TT,TF,FT,FF]
     */
    readonly preSpawned: number[];
    /**
     * @property {number[]} prePassed - What kind of sparks passed for the precondition to the midwaypoint:
     * [TT,TF,FT,FF]
     */
    readonly prePassed: number[];
    /**
     * @property {number[]} postSpawned - What kind of sparks spawned for the postcondition:
     * [TT,TF,FT,FF]
     */
    readonly postSpawned: number[];
    /**
     * @property {number[]} postPassed - What kind of sparks passed for the postcondition to the endpoint:
     * [TT,TF,FT,FF]
     */
    readonly postPassed: number[];
    /**
     * @property {number[]} preDeltaHealth - How much health you gained and lost on the prePath.
     */
    readonly preDeltaHealth: number[];
    /**
     * @property {number[]} postDeltaHealth - How much health you gained and lost on the postPath.
     */
    readonly postDeltaHealth: number[];
    /**
     * @property {number[]} moneySpent - Where did you spend your money on:
     * [towersSpent,preConditionSpent,postConditionSpent]
     */
    readonly moneySpent: number[];
    /**
     * @property {number[]} timeSpent - Where did you spend your time on:
     * [beforeWave,paused]
     */
    readonly timeSpent: number[];
}