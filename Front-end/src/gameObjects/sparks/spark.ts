/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University
 * within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
import * as Assets from '../../assets';
import SoundManager from '../../soundManager';
import Level from '../../states/level';
import GameObject from '../gameObject';
import Cell from '../grid/cell';
import Grid from '../grid/grid';
import Path from '../path/path';

const width: number = 40;
const height: number = 40;
const anchorX: number = 0.5;
const anchorY: number = 0.5;

const correctUnmarkedSpriteName: string = Assets.Images.SpritesGoodSpark.getName();
const correctMarkedSpriteName: string = Assets.Images.SpritesGoodSparkMarked.getName();
const incorrectUnmarkedSpriteName: string = Assets.Images.SpritesBadSpark.getName();
const incorrectMarkedSpriteName: string = Assets.Images.SpritesBadSparkMarked.getName();

const sparkError: string = Assets.Audio.AudioSparkError.getName();

/**
 * A Spark is an incoming input, travelling along the Path.
 * If a Spark reaches the end of a Path, the player loses health.
 */
export default class Spark extends GameObject {
    private path: Path;
    private grid: Grid;

    private waypointIndex: number;
    private distance: number; // Current distance along the path, measured from the origin of the path.

    private lastCellX: number;
    private lastCellY: number;
    private currentCell: Cell;

    /**
     * true = good spark (green), false = bad spark (red).
     */
    protected isCorrect: boolean;
    /**
     * true = marked by a condition scanner, false = not marked by a condition scanner.
     */
    protected isMarked: boolean;
    /**
     * true = satisfies the condition of a condition scanner, false = does not satisfy condition of a condition scanner.
     */
    private satisfiesCondition: boolean;

    private sparkHealthLoss: number;

    private sparkMoneyGain: number;

    private speed: number = 64;
    private baseSpeed: number;
    private speedMult: number;

    private baseHealth: number;
    private damageMultiplier: number;

    private particleEmitter: Phaser.Particles.Arcade.Emitter;

    private isProtected: boolean;

    protected effectSettings: object;

    /**
    * The constructor for a Spark.
    * @param game The Phaser.Game object
    * @param grid The grid the Spark is placed in
    * @param health The health of the Spark
    * @param speed The speed of the Spark
    */
    constructor(level: Level, grid: Grid, health: number, speed: number) {
        super(level, 0, 0, null);

        const levelSettings: object = this.game.cache.getJSON('levelSettings');
        this.effectSettings = this.game.cache.getJSON('effectSettings');

        this.width = width;
        this.height = height;

        this.grid = grid;

        this.lastCellX = 0;
        this.lastCellY = 0;

        this.baseSpeed = speed * ((Math.random() * 0.4) + 0.8);
        this.speed = this.baseSpeed;
        this.speedMult = 1;

        this.health = health;
        this.baseHealth = health;

        this.sparkHealthLoss = levelSettings['sparkHealthLoss'];

        this.sparkMoneyGain = levelSettings['sparkMoneyGain'];

        this.damageMultiplier = 1;

        this.isProtected = false;

        this.anchor.set(anchorX, anchorY);
    }

    /**
     * Update position of this spark
     */
    public updateObject(): void {
        super.updateObject();

        this.speed = this.baseSpeed * ((Math.random() * 0.2) + 0.9) * this.speedMult;

        // Since the effects might kill the spark, only continue if the spark still lives.
        if (!this.alive) {
            return;
        }

        let lastDistance: number = this.path.getWaypointDistances()[this.waypointIndex];
        let nextDistance: number = this.path.getWaypointDistances()[this.waypointIndex + 1];

        if (this.speed >= 0) {
            // Forward movement
            while (this.distance >= nextDistance) {
                this.waypointIndex++;

                // If the spark reached the last waypoint call the path's onReachedEnd callback function
                if (this.distance >= this.path.getWaypointDistances()[this.path.getWaypointDistances().length - 1]) {
                    if (!this.isCorrect) {
                        SoundManager.playSoundEffect(sparkError);
                    }

                    this.path.getOnReachedEnd()(this);

                    return;
                }

                lastDistance = nextDistance;
                nextDistance = this.path.getWaypointDistances()[this.waypointIndex + 1];
            }
        } else {
            // Backwards movement
            while (this.distance <= lastDistance) {
                this.waypointIndex--;

                // If the spark reached the begin waypoint, do nothing
                if (this.distance < this.path.getWaypointDistances()[0]) {
                    this.waypointIndex = 0;

                    return;
                }

                nextDistance = lastDistance;
                lastDistance = this.path.getWaypointDistances()[this.waypointIndex - 1];
            }
        }

        // Get the position in screen space of the two relevant waypoints
        const lastWaypoint: Phaser.Point = this.path.getWaypoints()[this.waypointIndex];
        const nextWaypoint: Phaser.Point = this.path.getWaypoints()[this.waypointIndex + 1];

        // Calculate the vector from lastWaypoint to nextWaypoint
        const direction: Phaser.Point = Phaser.Point.subtract(nextWaypoint, lastWaypoint);
        // Calculate the distance along this vector that the spark is, based on this.distance
        const length: number = (this.distance - lastDistance) / (nextDistance - lastDistance);

        // The current position of the spark is based on the lastWaypoint + the distance travelled
        // along the vector towards the next waypoint
        const position: Phaser.Point = Phaser.Point.add(lastWaypoint, direction.multiply(length, length));
        this.x = position.x;
        this.y = position.y;

        this.distance += this.speed * this.game.time.physicsElapsed;

        this.updateCell();
    }

    /**
     * Perform damage to the spark object
     * @param damage The amount of damage
     */
    public damageSpark(damage: number): void {
        this.damage(damage * this.damageMultiplier);
    }

    /**
    * Updates what cell the Spark is currently in.
    */
    private updateCell(): void {
        if (this.satisfiesCondition) {
            // If a Spark satisfies the current condition, it can never get shot
            // so it doesn't need to update it's current Cell.
            return;
        }

        const currentCell: Phaser.Point = this.level.getGrid().screenSpaceToGridSpace(this.position.x, this.position.y);

        // Update the cell if the Spark position is in another cell than it was previously.
        if ((this.lastCellX !== currentCell.x) || (this.lastCellY !== currentCell.y)) {
            // Update the cell in the grid with an extra spark, and remove the spark from the last position
            this.currentCell = this.grid.getCell(currentCell.x, currentCell.y);
            this.currentCell.getSparks().add(this);

            this.grid.getCell(this.lastCellX, this.lastCellY).getSparks().delete(this);
        }

        // Save the position of the last cell the spark was in.
        this.lastCellX = currentCell.x;
        this.lastCellY = currentCell.y;
    }

    /**
     * Places the Spark at the start of the specified Path.
     * @param path the Path the Spark will be placed on.
     */
    public placeOnPath(path: Path, correct: boolean, satisfiesCondition: boolean): void {
        this.path = path;

        this.waypointIndex = 0;
        this.distance = 0;

        const position: Phaser.Point = path.getWaypoints()[0];
        this.x = position.x;
        this.y = position.y;

        // Check if this spark is correct and satisfies the relevant condition
        this.isCorrect = correct;
        this.satisfiesCondition = satisfiesCondition;

        this.isMarked = false;

        this.health = this.baseHealth;

        this.loadTexture(this.isCorrect ? correctUnmarkedSpriteName : incorrectUnmarkedSpriteName);
        this.width = width;
        this.height = height;
    }

    /**
     * Marks the Spark so it can be shot by Shooting Towers.
     */
    public mark(): void {
        this.loadTexture(this.isCorrect ? correctMarkedSpriteName : incorrectMarkedSpriteName);
        this.width = width;
        this.height = height;

        this.isMarked = true;
    }

    /**
     * Destroys the Spark.
     * Takes care of all cleanup necessary to remove the Spark from the game world.
     */
    public destroyObject(): void {
        if (this.isCorrect) {
            SoundManager.playSoundEffect(sparkError);
        }

        this.level.getSparks().delete(this);
        this.level.getGameRenderGroup().remove(this);

        if (this.getCell()) {
            this.getCell().getSparks().delete(this);
        }

        super.destroyObject();
    }

    /**
     * Determine what to do when a spark reaches the end of its current path
     * This entails updating the score of the game, updating the money and health of the game, and optionally destroying the spark
     * @param destroy Whether to destroy the spark or not
     */
    public endOfPath(destroy: boolean): void {
        if (this.isCorrect) {
            this.level.setMoney(this.level.getMoney() + this.sparkMoneyGain);
        } else {
            this.level.setHealth(this.level.getHealth() - this.sparkHealthLoss);
        }

        if (destroy) {
            this.level.getSparks().delete(this);
            this.level.getGameRenderGroup().remove(this);

            this.destroy();
        }
    }

    public getPath(): Path { return this.path; }
    public setPath(path: Path): void { this.path = path; }

    public getWaypointIndex(): number { return this.waypointIndex; }
    public setWaypointIndex(waypointIndex: number): void { this.waypointIndex = waypointIndex; }

    public getDistance(): number { return this.distance; }
    public setDistance(distance: number): void { this.distance = distance; }

    public getIsCorrect(): boolean { return this.isCorrect; }
    public getIsMarked(): boolean { return this.isMarked; }
    public getSatisfiesCondition(): boolean { return this.satisfiesCondition; }

    public getCell(): Cell { return this.currentCell; }

    public getSparkHealthLoss(): number { return this.sparkHealthLoss; }

    public setSpeedMultiplier(speedMult: number): void { this.speedMult = speedMult; }

    public setDamageMultiplier(damageMultiplier: number): void { this.damageMultiplier = damageMultiplier; }

    /**
     * Get all the cells that are on the path that are in range.
     * If it goes off the path, then the cell doesn't get added.
     * @param range the range in which to check.
     * @returns the cells on the path that are in the given range.
     */
    public getSurroundingCells(range: number): Set<Cell> {
        const allCellsOnPath: Cell[] = this.path.getCells();

        const index: number = allCellsOnPath.findIndex((cell: Cell) => cell === this.currentCell);
        const output: Set<Cell> = new Set<Cell>();

        for (let i: number = -range; i <= range; i++) {
            if (index + i < 0 || index + i > allCellsOnPath.length - 1) {
                continue;
            }

            output.add(allCellsOnPath[index + i]);
        }

        return output;
    }

    public getProtected(): boolean { return this.isProtected; }
    public setProtected(isProtected: boolean): void { this.isProtected = isProtected; }
}
