/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University
 * within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
import * as Assets from '../assets';
import Connection from '../connection';
import Level from '../states/level';
import LevelState from '../states/levelState';
import GameObject from './gameObject';
import Cell from './grid/cell';
import Hitbox from './grid/hitbox';
import Spark from './sparks/spark';

const width: number = 40;
const height: number = 40;
const anchorX: number = 0.5;
const anchorY: number = 0.75;

const spriteName: string = Assets.Images.SpritesScanner.getName();
const laser: string = Assets.Images.SpritesLaser.getName();

/**
 * A ConditionTower is a tower that marks incoming inputs as correct / incorrect,
 * based on the formal specification that the player provides.
 */
export default class ConditionScanner extends GameObject {
    private static hitbox: Hitbox = new Hitbox(-1, -1, 2, 2);

    private waveWeights: number[] = [0, 0, 0.5, 0.5];

    private checkCell: Cell;

    private conditionType: string;
    private code: string;

    private TTspawned: number = 0;
    private TFspawned: number = 0;
    private FTspawned: number = 0;
    private FFspawned: number = 0;

    private TTpassed: number = 0;
    private TFpassed: number = 0;
    private FTpassed: number = 0;
    private FFpassed: number = 0;

    private healthLost: number = 0;

    private startTime: number;
    private opened: boolean;

    /**
     * The constructor for a ConditionTower.
     * @param level The Level object
     * @param x the x-position of the Tower
     * @param y the y-position of the Tower
     */
    constructor(level: Level, x: number, y: number, checkCell: Cell, conditionType: string) {
        super(level, x, y, spriteName);

        this.checkCell = checkCell;

        this.conditionType = conditionType;
        this.code = 'true';

        this.anchor.set(anchorX, anchorY);

        this.width = width;
        this.height = height;

        this.smoothed = true;

        this.game.add.existing(this);

        const laserbeam: Phaser.Sprite = new Phaser.Sprite(this.level.game, x - width * 0.20, y - height * 0.5, laser);
        laserbeam.scale.set(0.2, 1);
        this.level.game.add.existing(laserbeam);
        this.level.getGameRenderGroup().add(laserbeam);

        this.startTime = this.game.time.time;
        this.opened = false;

    }
    public updateObject(): void {
        this.checkCell.getSparks().forEach((spark: Spark) => {
            if (!spark.getSatisfiesCondition() && !spark.getIsMarked()) {
                spark.mark();
            }
        });
    }

    /**
     * Generates a random Spark type based on the weights received from the back end.
     * @returns a tuple of two booleans: the first is if the spark is either good or bad, the second
     * is whether it satisfies this Condition Tower's condition.
     */
    public getRandomSparkType(): [boolean, boolean] {
        // TODO: should DEBUG actually be here? In RELEASE we probably also would want to return [false, false]
        if (DEBUG && this.waveWeights === null) {
            return [false, false];
        }

        // Sum the weights, and multiply that with a number between 0 and 1
        // To get the percentages that sparks will have.
        const sum: number = this.waveWeights.reduce((pre, post) => pre + post);
        const randomNumber: number = Math.random() * sum;

        // based on the random number, return which spark needs to be spawned
        if (randomNumber < this.waveWeights[0]) {
            this.TTspawned++;

            return [true, true];
        } else if (randomNumber < this.waveWeights[0] + this.waveWeights[1]) {
            this.FTspawned++;

            return [false, true];
        } else if (randomNumber < this.waveWeights[0] + this.waveWeights[1] + this.waveWeights[2]) {
            this.TFspawned++;

            return [true, false];
        } else {
            this.FFspawned++;

            return [false, false];
        }
    }

    /**
     * Change the condition that this Condition Tower checks for
     * @param codeClean The code for the condition with the least amount of brackets possible; readable form
     * @param codeBrackets The code for the condition with the most amount of brackets to ensure the back-end
     * gets the right condition.
     */
    public changeCondition(codeClean: string, codeBrackets: string, shouldSend: boolean = true): void {
        this.code = codeClean;
        const endTime: number = this.game.time.time;
        const deltaTime: number = endTime - this.startTime;
        if (DEBUG && this.opened) console.log(deltaTime);
        if(shouldSend){
        Connection.connection.sendCondition(this.conditionType, codeBrackets);
        if (this.opened)
            Connection.connection.sendTimeSpent(this.conditionType, deltaTime);
        }
        this.opened = false;
    }

    /**
     * Check if there was a click on this conditionScanner and show input UI if it was.
     * @param pointer Click position.
     */
    public onMouseUp(pointer: Phaser.Pointer): boolean {
        if (this.getBounds().contains(pointer.x, pointer.y)) {
            this.level.setCurrentScanner(this);
            this.level.setLevelState(LevelState.BLOCKBUILDING);

            this.opened = true;
            this.startTime = this.game.time.time;

            if (!this.level.getPause() && this.level.getIsWaveStarted()) {
                this.level.togglePause();
            }

            return true;
        }
        return false;
    }

    /**
     * Check what type of spark passed, and store the correct data so that we can send that at the end of the wave.
     * @param spark the spark that passed.
     * @param deltaHealth the actual change in health
     */
    public updateSparkPassed(spark: Spark, deltaHealth: number): void {
        if (spark.getIsCorrect() && !spark.getIsMarked()) {
            this.TTpassed++;
        } else if (spark.getIsCorrect() && spark.getIsMarked()) {
            this.TFpassed++;
        } else if (!spark.getIsCorrect() && !spark.getIsMarked()) {
            this.healthLost += deltaHealth;
            this.FTpassed++;
        } else if (!spark.getIsCorrect() && spark.getIsMarked()) {
            this.healthLost += deltaHealth;
            this.FFpassed++;
        }
    }

    /**
     * Resets the data that was collected in the previous wave.
     */
    public resetWaveData(): void {
        this.TTpassed = 0;
        this.TFpassed = 0;
        this.FTpassed = 0;
        this.FFpassed = 0;
        this.TTspawned = 0;
        this.TFspawned = 0;
        this.FTspawned = 0;
        this.FFspawned = 0;
        this.healthLost = 0;
    }

    public setWaveWeights(waveWeights: number[]): void { this.waveWeights = waveWeights; }

    public getSparksSpawned(): number[] { return [this.TTspawned, this.TFspawned, this.FTspawned, this.FFspawned]; }

    public getDeltaHealth(): number[] { return [this.healthLost]; }

    public getSparksPassed(): number[] { return [this.TTpassed, this.TFpassed, this.FTpassed, this.FFpassed]; }

    public getConditionType(): string { return this.conditionType; }

    public getCode(): string { return this.code; }

    /**
     * Gets the static hitbox of a conditionTower
     */
    public static getHitbox(): Hitbox { return ConditionScanner.hitbox; }
}
