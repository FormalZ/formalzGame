/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University
 * within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
import * as Assets from '../../assets';
import PoisonEffect from '../../effects/poisonEffect';
import SpinEffect from '../../effects/spinEffect';
import Level from '../../states/level';
import Spark from '../sparks/spark';
import LaserTower from './laserTower';
import ShootingMode from './shootingMode';

const width: number = 36;
const height: number = 64;
const anchorX: number = 0.5;
const anchorY: number = 0.5;

const spriteName: string = Assets.Images.SpritesAoeTower.getName();

/**
 * A Multishot tower is a tower that shoots ALL sparks in its range that were marked incorrect by a Condition Scanner.
 */
export default class MultishotTower extends LaserTower {
    // Add additional necessary variables here.
    private previousShotSprites: Set<Phaser.Sprite> = new Set<Phaser.Sprite>();

    /**
     * The constructor for a Multishot tower.
     * @param level The Phaser.Level object
     * @param x The x-position of the Tower
     * @param y The y-position of the Tower
     */
    constructor(level: Level, x: number, y: number) {
        super(level, x, y, width, height, spriteName, anchorX, anchorY);

        this.setTowerType('Multishot tower; Default');

        this.shotFadeTime = 0.07;

        this.range = this.baseRange * this.rangeMultiplier;
        this.setDescriptions('multiShotTower');
    }

    // Updates the targets to shoot
    public updateTarget(currentTime: number): void {
        const targets: Set<Spark> = this.getTargets();

        if (targets && (currentTime - this.lastShootTime) > this.reloadTime) {
            for (let target of targets) {
                this.shoot(target);
                this.lastShootTime = currentTime;
            }

            this.playShootSound();
        }
    }

    /**
     * Gets ALL of the Sparks in the tower's effective range; when there are no Sparks in range, it returns null
     * @returns All the Sparks present in range of the tower.
     */
    public getTargets(): Set<Spark> {
        const targets: Set<Spark> = new Set<Spark>();

        for (let i: number = this.cells.length - 1; i >= 0; i--) {
            this.cells[i].getSparks().forEach(spark => {
                if (spark.getIsMarked() && !spark.getProtected()) {
                    targets.add(spark);
                }
            });
        }

        return targets.size > 0 ? targets : null;
    }

    /**
     * A Multishot tower shot consists of multiple sprites
     */
    public destroyShot(): void {
        this.previousShotSprites.forEach(sprite => sprite.destroy());
        this.previousShotSprites.clear();
    }

    /**
     * Multishot tower only has one mode
     */
    public setShootingMode(shootingMode: ShootingMode): void { console.warn('Multishot Tower only has one mode'); }

    public effectUpgrade(): void {
        // Effect: Poison. Poisoned sparks take damage over time.
        this.isEffectUpgraded = true;
        // Update the tower's variables (if the effect is too weak or too strong).
        this.setTowerType('Multishot tower; Effect upgrade');

        // Update the info of this multi shot tower in local storage with 'E', to indicate the effect upgrade

        const localStorageString: string = localStorage.getItem(this.level.getSessionId() + 'multishottowers');
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
        localStorage.setItem(this.level.getSessionId() + 'multishottowers', newLocalStorageString);
        this.level.sendHash();
    }

    public boostUpgrade(): void {
        // Upgrade (some of) the tower's stats.
        this.setTowerType('Multishot tower; Boost upgrade');

        // update the info of this multi shot tower in local storage with 'N', to indicate the boost upgrade
        const localStorageString: string = localStorage.getItem(this.level.getSessionId() + 'multishottowers');
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
        localStorage.setItem(this.level.getSessionId() + 'multishottowers', newLocalStorageString);
        this.level.sendHash();
    }

    public useEffect(spark: Spark): void {
        const poison: object = this.effectSettings['Poison'];
        const spin: object = this.effectSettings['Spin'];

        spark.applyEffect(new PoisonEffect(spark, poison['applyInterval'], poison['applyCount'], poison['damagePerTick']));
        spark.applyEffect(new SpinEffect(spark, spin['duration']));
    }

    public updateSprite(shotSprite: Phaser.Sprite): void {
        this.previousShotSprites.add(shotSprite);
    }

    public removeTower(): void {
      super.removeTower();

      // remove this tower from local storage
      const localStorageString: string = localStorage.getItem(this.level.getSessionId() + 'multishottowers');
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
        localStorage.setItem(this.level.getSessionId() + 'multishottowers', newLocalStorageString);
      else
      // if there are no more multi shot towers left, remove the multishottowers item from local storage
        localStorage.removeItem(this.level.getSessionId() + 'multishottowers');
      this.level.sendHash();
    }
}
