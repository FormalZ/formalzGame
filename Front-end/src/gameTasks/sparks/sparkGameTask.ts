/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University
 * within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
import * as Assets from '../../assets';
import Connection from '../../connection';
import Grid from '../../gameObjects/grid/grid';
import Path from '../../gameObjects/path/path';
import FireWallSpark from '../../gameObjects/sparks/fireWallSpark';
import Spark from '../../gameObjects/sparks/spark';
import VirusSpark from '../../gameObjects/sparks/virusSpark';
import SoundManager from '../../soundManager';
import Level from '../../states/level';
import LevelState from '../../states/levelState';
import WaveData from '../../utils/waveData';
import GameTask from '../gameTask';
import ErrorState from '../../states/errorState';

const specialSparkSpawnSound: string = Assets.Audio.AudioSpecialSparkSpawn.getName();

/**
 * SparkGameTask is a GameTask that manages all Sparks in the game.
 * It updates all Sparks.
 * It takes care of Spark spawning.
 */
export default class SparkGameTask extends GameTask {
    private lastSpawnTime: number;
    private spawnedSparks: number;

    /**
     * Initialize the spark game task. This is done by initializing the last spawn time and the spawned sparks counter
     * @param level The current level to initialize the game task with
     */
    public initialize(level: Level): void {
        super.initialize(level);

        this.lastSpawnTime = this.level.game.time.time;
        this.spawnedSparks = 0;
    }

    /**
     * The general update function to run every game loop.
     * This checks whether the wave is finished, spawns sparks and updates every spawned spark
     */
    public update(): void {
        const currentTime: number = this.level.game.time.time;

        // Update lastSpawnTime while the game is paused to keep sparks from spawning at the wrong time after pausing
        if (this.level.getPause()) {
            this.lastSpawnTime += this.level.game.time.elapsed;

            return;
        }

        // If all the sparks from the current wave are spawned and none of them are on
        // the path anymore and the wave is not yet ended, we end the wave.
        if (this.spawnedSparks === this.level.getSparksPerWave() && this.level.getSparks().size === 0 && !this.level.getIsWaveEnded()) {
            this.finishWave();
        }

        // If the spawnInterval has passed and the wave is started, spawn a spark as long as there are still sparks that need to be spawned.
        if (currentTime - this.lastSpawnTime >= this.level.getSparkSpawnTime() &&
            this.level.getIsWaveStarted() && this.level.getSparksPerWave() > this.spawnedSparks) {

            const path: Path = this.level.getPrePath();
            const [correct, satisfiesCondition] = path.getConditionScanner().getRandomSparkType();

            let newSpark: Spark = null;

            const grid: Grid = this.level.getGrid();
            const health: number = this.level.getSparkHealth();
            const speed: number = this.level.getSparkSpeed();

            // If the spark is not correct, there exist a small chance that it will spawn as a special spark.
            if (!correct) {
                const random: number = Math.random();
                const [virusChance, fireWallChance] = this.level.getSpecialSparkPercentages();

                if (random < virusChance) {
                    newSpark = new VirusSpark(this.level, grid, health, speed);
                } else if (random < virusChance + fireWallChance) {
                    newSpark = new FireWallSpark(this.level, grid, health, speed);
                }
            }

            // If the spark was spawned as a special spark, play a sound effect to notify the player.
            // Otherwise, spawn the spark as a normal spark.
            if (newSpark) {
                SoundManager.playSoundEffect(specialSparkSpawnSound);
            } else {
                newSpark = new Spark(this.level, grid, health, speed);
            }

            newSpark.placeOnPath(path, correct, satisfiesCondition);

            this.spawnedSparks++;

            this.level.getSparks().add(newSpark);
            this.level.getGameRenderGroup().add(newSpark);

            this.lastSpawnTime = currentTime;
        }

        this.level.getSparks().forEach(spark => spark.updateObject());
    }

    /**
     * Finish a wave, by sending all game data when the wave was completed to the server
     */
    private finishWave(): void {
        this.spawnedSparks = 0;

        const data: WaveData = {
            score: this.level.getScore(),
            deltaScore: this.level.getDeltaScore(),
            money: this.level.getMoney(),
            health: this.level.getHealth(),
            towerCount: this.level.getTowers().size,
            preSpawned: this.level.getPreScanner().getSparksSpawned(),
            prePassed: this.level.getPreScanner().getSparksPassed(),
            postSpawned: this.level.getPostScanner().getSparksSpawned(),
            postPassed: this.level.getPostScanner().getSparksPassed(),
            preDeltaHealth: this.level.getPreScanner().getDeltaHealth(),
            postDeltaHealth: this.level.getPostScanner().getDeltaHealth(),
            moneySpent: [
                this.level.getMoneySpentOnTowers(),
                this.level.getMoneySpentOnPreCondition(),
                this.level.getMoneySpentOnPostCondition()
            ],
            timeSpent: [this.level.getTimeSpentBeforeWave(), this.level.getTimeSpentPaused()]
        };

        Connection.connection.sendWaveDone(data);

        const deadline: number = this.level.getDeadline();
        const now: number = this.level.time.time;
        // Deadline passed, end game.
        if (deadline !== null && now > deadline) {
            ErrorState.throw(this.level.game, 'The deadline has passed', false);
        }

        this.level.endWave();
    }

    /**
    *  Change the input possibilities based on the current level state
    * @param levelState the current level state
    */
    public changeState(levelState: LevelState): void {
        this.setInputEnabled(levelState === this.enabledState);
        this.setUpdateEnabled(true);
    }
}