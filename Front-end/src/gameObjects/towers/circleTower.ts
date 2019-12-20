/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University
 * within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
import * as Assets from '../../assets';
import Effect from '../../effects/effect';
import SupportEffect from '../../effects/supportEffect';
import Level from '../../states/level';
import Cell from '../grid/cell';
import Grid from '../grid/grid';
import Spark from '../sparks/spark';
import Bullet from './bullet';
import BulletTower from './bulletTower';
import CircleBullet from './circleBullet';
import ShootingMode from './shootingMode';

const width: number = 36;
const height: number = 54;
const anchorX: number = 0.5;
const anchorY: number = 0.6;

const spriteName: string = Assets.Images.SpritesCircleTower.getName();

const explosionSound: string = Assets.Audio.AudioExplosion.getName();

/**
 * A CircleTower is a tower that shoots a circle that hits sparks that were marked incorrect by a ConditionScanner.
 */
export default class CircleTower extends BulletTower {
    // Add additional necessary variables here.
    protected bullets: Set<Bullet> = new Set<CircleBullet>();
    private bulletAmount: number; // Amount of bullets this tower shoots in a circle
    private effectCells: Cell[];

    /**
     * The constructor for a CircleTower.
     * @param level The Phaser.Level object
     * @param x The x-position of the Tower
     * @param y The y-position of the Tower
     */
    constructor(level: Level, x: number, y: number) {
        super(level, x, y, width, height, spriteName, anchorX, anchorY);

        this.effectCells = [];
        this.setTowerType('Circle tower; Default');
        this.bulletAmount = this.towerSettings[this.getTowerType()]['bulletAmount'];
        this.range = this.baseRange * this.rangeMultiplier;

        this.shootSound = explosionSound;

        this.setDescriptions('circleTower');
    }

    /**
     * For this tower, create another shot if possible, and update shot.
     */
    public updateObject(): void {
        if (this.isEffectUpgraded) {
            const support: object = this.effectSettings['Support'];
            this.applyEffect(new SupportEffect(this.level, this, support['duration'], support['rangeMultiplier']));
        }

        super.updateObject();
    }

    public effectUpgrade(): void {
        this.rangeMultiplier = this.effectSettings['Support']['rangeMultiplier'];
        // Effect: Support. Supports towers in this tower's range, increasing their base range.'
        this.isEffectUpgraded = true;
        // Update the tower's variables (if the effect is too weak or too strong).

        this.setTowerType('Circle tower; Effect upgrade');
        this.bulletAmount = this.towerSettings[this.getTowerType()]['bulletAmount'];
        this.updateObject();
        this.addEffectToCellsInRange();

        // add this circle tower to local storage. Indicate that it has no extra effects/boots with 'E'
        const localStorageString: string = localStorage.getItem(this.level.getSessionId() + 'circletowers');
        const localStorageBlocks: string[] = localStorageString.split('|');
        let newLocalStorageString: string = '';
        localStorageBlocks.forEach(storedTower => {
          let stringToAdd: string = '';
          if (storedTower.includes(this.getId())) {
            const gridPos: Phaser.Point = this.level.getGrid().screenSpaceToGridSpace(this.x, this.y);
            stringToAdd = this.getId() + ';' + gridPos.x  + ';' + gridPos.y + ';E';
          }
          else
            stringToAdd = storedTower;
          if (newLocalStorageString !== '')
            newLocalStorageString = newLocalStorageString + '|' + stringToAdd;
          else
            newLocalStorageString = stringToAdd;
        });
        localStorage.setItem(this.level.getSessionId() + 'circletowers', newLocalStorageString);

        this.level.sendHash();
    }

    public boostUpgrade(): void {
        // Upgrade (some of) the tower's stats.
        this.setTowerType('Circle tower; Boost upgrade');
        this.bulletAmount = this.towerSettings[this.getTowerType()]['bulletAmount'];

        // update the info of this circle tower in local storage with a N to indicate the boost upgrade
        const localStorageString: string = localStorage.getItem(this.level.getSessionId() + 'circletowers');
        const localStorageBlocks: string[] = localStorageString.split('|');
        let newLocalStorageString: string = '';
        localStorageBlocks.forEach(storedTower => {
          let stringToAdd: string = '';
          if (storedTower.includes(this.getId())) {
            const gridPos: Phaser.Point = this.level.getGrid().screenSpaceToGridSpace(this.x, this.y);
            stringToAdd = this.getId() + ';' + gridPos.x  + ';' + gridPos.y + ';N';
          }
          else
            stringToAdd = storedTower;
          if (newLocalStorageString !== '')
            newLocalStorageString = newLocalStorageString + '|' + stringToAdd;
          else
            newLocalStorageString = stringToAdd;
        });
        localStorage.setItem(this.level.getSessionId() + 'circletowers', newLocalStorageString);

        this.level.sendHash();
    }

    /**
     * Remove this tower, removing it from the game and refunding some of the cost.
     */
    public removeTower(): void {
        super.removeTower();

        this.effectCells.forEach(cell => {
            cell.removeRangeEffect();
        });

        if (this.isEffectUpgraded) {
            this.destroyObject();
        }

        // remove this tower from local storage
        const localStorageString: string = localStorage.getItem(this.level.getSessionId() + 'circletowers');
        const localStorageTowers: string[] = localStorageString.split('|');
        let newLocalStorageString: string = '';
        localStorageTowers.forEach(tower => {
          if (!tower.includes(this.getId())) {
            if (newLocalStorageString === '')
              newLocalStorageString += tower;
            else
              newLocalStorageString += '|' + tower;
          }
        });
        if (newLocalStorageString !== '')
          localStorage.setItem(this.level.getSessionId() + 'circletowers', newLocalStorageString);
        // if there are no circle towers left, remove the item circletowers from local storage
        else
          localStorage.removeItem(this.level.getSessionId() + 'circletowers');
        this.level.sendHash();
      }

    /**
     * CircleTower does not apply an effect to its bullets.
     */
    public useEffect(effects: Effect<Spark>[]): Effect<Spark>[] {
        return null;
    }

    /**
    * Creates a certain amount of CircleBullets, based on bulletAmount.
    * The first bullet is aimed at the Spark that was found by getInRange().
    */
    public createBullet(effects: Effect<Spark>[], angle: number): void {
        for (let i: number = 0; i < this.bulletAmount; i++) {
            this.bullets.add(new CircleBullet(
                this.level,
                this.x, this.y,
                this.range,
                angle + 2 * i * Math.PI / this.bulletAmount,
                this.cells,
                this.power
            ));
        }
    }

    /**
     * Affects the cells in range of the tower, so that they get the range increase effect.
     */
    public addEffectToCellsInRange(): void {
        const grid: Grid = this.level.getGrid();

        // The range of the tower in Screen Space
        const towerRange: number = this.baseRange * this.rangeMultiplier;

        // Left and Top possible range cell in Grid Space
        const leftTop: Phaser.Point = grid.screenSpaceToGridSpace(this.x - towerRange, this.y - towerRange);
        const rightBottom: Phaser.Point = grid.screenSpaceToGridSpace(this.x + towerRange, this.y + towerRange);

        // Iterate over all possible cells in range and update them if they are in tower range
        for (let x: number = leftTop.x; x <= rightBottom.x; x++) {
            for (let y: number = leftTop.y; y <= rightBottom.y; y++) {
                const currentCell: Cell = grid.getCell(x, y);

                // The difference between the position of the middle of the current cell and the tower in Screen Space
                const xDiff: number = grid.gridSpaceToScreenSpace(x + 0.5, y + 0.5).x - this.x;
                const yDiff: number = grid.gridSpaceToScreenSpace(x + 0.5, y + 0.5).y - this.y;

                // If x^2 + y^2 <= r^2, then the current cell is in range.
                if (xDiff * xDiff + yDiff * yDiff <= towerRange * towerRange) {
                    currentCell.addRangeEffect();
                    this.effectCells.push(currentCell);
                }
            }
        }
    }

    /**
     * Circle Tower only has one mode
     */
    public setShootingMode(shootingMode: ShootingMode): void { console.warn('Circle Tower only has one mode'); }
}
