/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University
 * within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
import * as Assets from '../../assets';
import SoundManager from '../../soundManager';
import Level from '../../states/level';
import LevelState from '../../states/levelState';
import { StringUtils } from '../../utils/utils';
import GameObject from '../gameObject';
import Cell from '../grid/cell';
import Grid from '../grid/grid';
import Hitbox from '../grid/hitbox';
import FireWallSpark from '../sparks/fireWallSpark';
import Spark from '../sparks/spark';
import VirusSpark from '../sparks/virusSpark';
import ShootingMode from './shootingMode';

const shootSound: string = Assets.Audio.AudioLaser.getName();
const errorSound: string = Assets.Audio.AudioError.getName();
const upgradeSound: string = Assets.Audio.AudioUpgrade.getName();

/**
 * Base class for all Tower objects.
 */
export default abstract class Tower extends GameObject {
    protected cells: Cell[]; // The path cells in range.
    protected allTowersInRange: Set<Tower>; // All towers in range.
    protected hitbox: Hitbox; // The hitbox of the tower.
    protected power: number; // Damage that is dealt per shot on the spark.
    protected range: number; // The current effective range of the tower.
    protected shootingMode: ShootingMode; // The current mode of the tower.
    protected baseRange: number; // The base range of the tower.
    protected reloadTime: number; // Time it takes for the tower to reload and be able to fire another shot.
    protected towerCost: number; // The money cost of the tower.
    protected upgradeCost: number; // The money required to upgrade the tower.
    protected rangeMultiplier: number; // A multiplier which increases the range of a tower.
    protected upgradedTower: boolean; // A boolean which tracks if the tower is upgraded or not.
    protected isEffectUpgraded: boolean; // A boolean which represents whether a tower will be upgraded with an effect or not.
    protected lastShootTime: number; // The time since the last fired shot.

    private virusAffected: boolean;

    protected towerSettings: object = null;
    protected effectSettings: object = null;
    protected static towersText: object = null;

    protected boostDescription: string;
    protected effectDescription: string;
    protected upgradeCostString: string;
    protected tooltip: string;
    private towerType: string;

    protected shootSound: string;


    private upgradeVisual: Phaser.Text;

    private id: string;

    /**
    * The constructor for the Tower GameObject, once the constructor is called, the tower will
    * automatically be drawn and added to the world. The different variables are predefined, and
    * are altered in each separate tower.
    * @param level The Level object
    * @param x the x-position of the Tower
    * @param y the y-position of the Tower
    * @param width the width of the tower object
    * @param height the height of the tower object
    * @param image the name of the image to be displayed
    * @param cells the path cells in range of the tower
    * @param power the damage that is dealt per shot fired
    * @param range the effective range of the tower
    * @param reloadTime the time it takes for the tower to reload and fire another shot
    * @param towerCost the cost of placing this tower in the level
    * @param hitbox the Hitbox of the tower
    */
    constructor(level: Level, x: number, y: number, width: number, height: number, image: string, cells: Cell[] = [],
        power: number = 10, range: number = 80, reloadTime: number = 0.4, towerCost: number = 1000,
        hitbox: Hitbox = new Hitbox(-1, -1, 2, 2)) {
        super(level, x, y, image);

        this.width = width;
        this.height = height;

        this.cells = cells;
        this.allTowersInRange = new Set<Tower>();

        this.power = power;
        this.baseRange = range;
        this.rangeMultiplier = 1;
        this.range = this.baseRange * this.rangeMultiplier;
        this.shootingMode = ShootingMode.First;
        this.reloadTime = reloadTime;
        this.towerCost = towerCost;
        this.hitbox = hitbox;
        this.upgradedTower = false;
        this.isEffectUpgraded = false;
        this.lastShootTime = null;

        this.shootSound = shootSound;

        this.towerSettings = this.game.cache.getJSON('towerSettings');
        this.effectSettings = this.game.cache.getJSON('effectSettings');

        this.scale.set(0.1, 0.1);

        const towerid: number = this.level.getTowerId();
        this.id = 'T' + towerid;
        this.level.setTowerID(towerid + 1);

        Tower.towersText = this.game.cache.getJSON('towersText');
    }

    /**
     * Adds the path cells in range of the tower to the towerRange
     * @param pathCells The path cells (prePath or postPath)
     */
    public addPathRange(pathCells: Cell[]): void {
        const cellSize: number = this.level.getGrid().getCellSize();

        // The position of the middle of the tower in Screen Space
        const xTowerMiddle: number = this.x;
        const yTowerMiddle: number = this.y;

        const gridOrigin: Phaser.Point = this.level.getGrid().getOrigin();

        // Iterate over the path cells
        for (let i: number = 0; i < pathCells.length; i++) {
            // The position of middle of the current cell defined in Screen Space
            const xCellMiddle: number = ((pathCells[i].getX()) + 0.5) * cellSize + gridOrigin.x;
            const yCellMiddle: number = ((pathCells[i].getY()) + 0.5) * cellSize + gridOrigin.y;

            // The difference between the position of the current cell and the tower
            const xDiff: number = xCellMiddle - xTowerMiddle;
            const yDiff: number = yCellMiddle - yTowerMiddle;

            // If x^2 + y^2 <= r^2, then the pathCell is in range. But only add it if it's not already in there.
            if (!this.cells.find(cell => cell === pathCells[i]) &&
                (xDiff * xDiff + yDiff * yDiff) <= ((this.baseRange * this.rangeMultiplier) * (this.baseRange * this.rangeMultiplier))) {
                this.cells.push(pathCells[i]);
            }
        }
    }

    /**
     * Adds all the towers in range of the tower.
     */
    public addTowersInRange(): void {
        const grid: Grid = this.level.getGrid();
        const towers: Set<Tower> = this.level.getTowers();
        const cellSize: number = grid.getCellSize();
        const origin: Phaser.Point = grid.getOrigin();

        // The position of the middle of the tower in Screen Space
        const xTowerMiddle: number = this.x;
        const yTowerMiddle: number = this.y;

        // Iterate over all the cells of the whole grid.
        towers.forEach(tower => {
            // The position of middle of the current cell defined in Screen Space
            const cellMiddle: Phaser.Point = this.level.getGrid().screenSpaceToGridSpace(tower.x, tower.y);
            const xCellMiddle: number = (cellMiddle.x + 0.5) * cellSize + origin.x;
            const yCellMiddle: number = (cellMiddle.y + 0.5) * cellSize + origin.y;

            // The difference between the position of the current cell and the tower
            const xDiff: number = xCellMiddle - xTowerMiddle;
            const yDiff: number = yCellMiddle - yTowerMiddle;

            // If x^2 + y^2 <= r^2, then the cell is in range
            if ((xDiff * xDiff + yDiff * yDiff) <= ((this.baseRange * this.rangeMultiplier) * (this.baseRange * this.rangeMultiplier))) {
                this.allTowersInRange.add(tower);
            }
        });
    }

    /**
    * Gets the Spark in range of the tower that needs to be shot depending on the mode
    * If the range does not have any marked sparks, it returns null.
    * @returns The Spark in range of the tower depending on the mode, or null if none found.
    */
    public getTarget(): Spark {
        let target: Spark = null;

        // Local function that calculates the strength of a value, where VirusSparks and FireWallSparks are allays considered strongest.
        const getStrength: (spark: Spark) => number = (spark: Spark) => {
            if (spark instanceof VirusSpark || spark instanceof FireWallSpark) {
                return Number.MAX_SAFE_INTEGER;
            }

            return spark.health;
        };

        switch (this.shootingMode) {
            case ShootingMode.First:
                for (let i: number = this.cells.length - 1; i >= 0; i--) {
                    const curSpark: Spark = this.getSpark(i);
                    if (curSpark !== null && !curSpark.getProtected()) {
                        return curSpark;
                    }
                }

                break;

            case ShootingMode.Last:
                for (let i: number = 0; i < this.cells.length; i++) {
                    const curSpark: Spark = this.getSpark(i);
                    if (curSpark !== null && !curSpark.getProtected()) {
                        return curSpark;
                    }
                }

                break;

            case ShootingMode.Strongest:
                for (let i: number = this.cells.length - 1; i >= 0; i--) {
                    for (let spark of this.cells[i].getSparks()) {
                        if (spark.getIsMarked()) {
                            if ((target === null || getStrength(spark) > getStrength(target)) && !spark.getProtected()) {
                                target = spark;
                            }
                        }
                    }
                }

                break;

            case ShootingMode.Weakest:
                for (let i: number = this.cells.length - 1; i >= 0; i--) {
                    for (let spark of this.cells[i].getSparks()) {
                        if (spark.getIsMarked()) {
                            if ((target === null || getStrength(spark) > getStrength(target)) && !spark.getProtected()) {
                                target = spark;
                            }
                        }
                    }
                }

                break;

            case ShootingMode.Closest:
                let closestDistance: number = Number.MAX_SAFE_INTEGER;
                for (let i: number = this.cells.length - 1; i >= 0; i--) {
                    for (let spark of this.cells[i].getSparks()) {
                        if (spark.getIsMarked()) {
                            let distance: number = Math.abs(spark.x - this.x) + Math.abs(spark.y - this.y);
                            distance *= distance;

                            if (distance < closestDistance && !spark.getProtected()) {
                                closestDistance = distance;
                                target = spark;
                            }
                        }
                    }
                }

                break;
        }

        return target;
    }

    /**
    * Gets the Spark on this cell index, if present, otherwise returns null
    * @param i index of the cell
    * @returns the Spark on this cell index, if present, otherwise returns null.
    */
    public getSpark(i: number): Spark {
        for (let spark of this.cells[i].getSparks()) {
            if (spark.getIsMarked()) {
                return spark;
            }
        }

        return null;
    }

    public updateObject(): void {
        this.range = this.baseRange * this.rangeMultiplier;

        super.updateObject();

        const pointer: Phaser.Point = this.level.input.activePointer.position;
        const toolTip: string = this.getTooltipString();
        if (toolTip && this.getBounds().contains(pointer.x, pointer.y) && this.level.getLevelState() === LevelState.PLAYING) {
            this.level.getTooltip().show(toolTip, this);
        }

        // Compensate for paused game time
        if (this.level.getPause() && this.lastShootTime !== null) {
            this.lastShootTime += this.level.game.time.elapsed;
        }
    }

    /**
     * Upgrade this tower (for instance, increasing its DPS or effective range).
     */
    public upgradeTower(isUpgradeEffect: boolean): void {
        // Check if the player has enough money to upgrade, and if the tower is not upgraded already.
        if (this.level.getMoney() >= this.upgradeCost && !this.upgradedTower) {
            // Decrease the money of the player.
            this.level.setMoney(this.level.getMoney() - this.upgradeCost);

            this.upgradedTower = true;

            if (isUpgradeEffect) {
                this.effectUpgrade();
            } else {
                this.boostUpgrade();
            }
            this.level.setSelectedTower(this);

            SoundManager.playSoundEffect(upgradeSound);

            this.upgradeVisual = this.game.add.text(this.left, this.top, '+', {
                fill: isUpgradeEffect ? '#00ccff' : '#ffff00', font: '26px Arial'
            });

            this.updateObject();
        } else {
            SoundManager.playSoundEffect(errorSound);
        }
    }

    /**
     * Abstract method that forces subclasses of Tower to define their behaviour when they receive an effect upgrade.
     */
    public abstract effectUpgrade(): void;

    /**
     * Abstract method that forces subclasses of Tower to define their behaviour when they receive an boost upgrade.
     */
    public abstract boostUpgrade(): void;

    /**
     * Remove the tower.
     */
    public abstract removeTower(): void;

    public playShootSound(): void {
        SoundManager.playSoundEffect(this.shootSound);
    }

    /**
     * Get the cost of the base tower.
     */
    public getTowerCost(): number { return this.towerCost; }

    /**
     * Get the cost to upgrade this tower.
     */
    public getUpgradeCost(): number { return this.upgradeCost; }

    /**
     * Get the refund amount when the tower is sold.
     */
    public getRefundAmount(): number {
        return this.level.getRefundTowerModifier() * this.getTowerCost() + (
            this.upgradedTower ? this.level.getRefundTowerModifier() * this.getUpgradeCost() : 0
        );
    }

    /**
     * Get the damage of each shot.
     */
    public getDamage(): number { return this.power; }

    /**
     * Get the range of the tower.
     */
    public getRange(): number { return this.range; }

    public setShootingMode(shootingMode: ShootingMode): void { this.shootingMode = shootingMode; }
    public getShootingMode(): ShootingMode { return this.shootingMode; }
    public getShootingModeText(): string {
        switch (this.shootingMode) {
            case 0:
                return 'First';
            case 1:
                return 'Last';
            case 2:
                return 'Strongest';
            case 3:
                return 'Weakest';
            case 4:
                return 'Closest';
        }
    }

    /**
     * Get the attack speed of the tower.
     */
    public getAttackSpeed(): number { return 1 / this.reloadTime; }

    /**
     * Gets the static hitbox for the tower.
     */
    public getHitbox(): Hitbox { return this.hitbox; }

    /**
     * Gets the description of the Boost Upgrade.
     */
    public getBoostDescription(): string { return this.boostDescription + '\n' + this.upgradeCostString; }

    /**
     * Gets the description of the Effect Upgrade.
     */
    public getEffectDescription(): string { return this.effectDescription + '\n' + this.upgradeCostString; }

    /**
     * Gets the tooltip of the tower.
     */
    public getTooltipString(): string {
        return StringUtils.interpolateString(this.tooltip, this.power, this.baseRange, this.reloadTime, this.towerCost, this.upgradeCost);
    }

    protected setDescriptions(towerType: string): void {
        this.boostDescription = Tower.towersText[towerType]['boostDescription'];
        this.effectDescription = Tower.towersText[towerType]['effectDescription'];
        this.tooltip = Tower.towersText[towerType]['tooltip'].join('\n');
        this.upgradeCostString = StringUtils.interpolateString(Tower.towersText['upgradeCost'], this.upgradeCost);
    }

    /**
     * Update all the ranges of the tower. This also should be used if you decrease the range of the tower.
     * @param prePathCells the cells of the prePath
     * @param postPathCells the cells of the postPath
     */
    public updateCellsInRange(prePathCells: Cell[], postPathCells: Cell[]): void {
        this.cells = [];

        this.addPathRange(prePathCells);
        this.addPathRange(postPathCells);
    }

    /**
     * Update the stats of a tower, based on a certain type.
     * @param towerType the type of tower to be updated.
     */
    private updateTowerType(): void {
        const type: object = this.towerSettings[this.towerType];
        this.power = type['power'];
        this.baseRange = type['range'];
        this.reloadTime = type['reloadTime'];
        this.towerCost = type['towerCost'];
        this.upgradeCost = type['upgradeCost'];
    }

    public getRangeMultiplier(): number { return this.rangeMultiplier; }
    public setRangeMultiplier(multiplier: number): void { this.rangeMultiplier = multiplier; }

    public getVirusAffected(): boolean { return this.virusAffected; }
    public setVirusAffected(virusAffected: boolean): void { this.virusAffected = virusAffected; }

    public getTowersInRange(): Set<Tower> { return this.allTowersInRange; }

    public getTowerType(): string { return this.towerType; }
    protected setTowerType(towerType: string): void {
        this.towerType = towerType;
        this.updateTowerType();
    }

    public getUpgraded(): boolean { return this.upgradedTower; }
    public getUpgradeVisual(): Phaser.Text { return this.upgradeVisual; }

    public getId(): string { return this.id; }
    public setId(id: string): void { this.id = id; }
}
