/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University
 * within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
import * as Assets from '../../assets';
import WeakenEffect from '../../effects/weakenEffect';
import Level from '../../states/level';
import Spark from '../sparks/spark';
import LaserTower from './laserTower';

const width: number = 44;
const height: number = 44;
const anchorX: number = 0.5;
const anchorY: number = 0.5;

const spriteName: string = Assets.Images.SpritesSniperTowerLeft.getName();

const zapSound: string = Assets.Audio.AudioZap.getName();

/**
 * SniperTower is a Tower that snipes a spark from long range with big damage.
 */
export default class SniperTower extends LaserTower {
    /**
     * The constructor for a SniperTower.
     * @param level The Level object
     * @param x The x-position of the Tower
     * @param y The y-position of the Tower
     */
    constructor(level: Level, x: number, y: number) {
        super(level, x, y, width, height, spriteName, anchorX, anchorY);

        this.setTowerType('Sniper tower; Default');

        this.shotFadeTime = 0.07;

        this.shootSound = zapSound;

        this.range = this.baseRange * this.rangeMultiplier;
        this.setDescriptions('sniperTower');
    }

    public effectUpgrade(): void {
        // Effect: Weaken. Weakened sparks take additional damage.
        this.isEffectUpgraded = true;
        // Update the tower's variables (if the effect is too weak or too strong).
        this.setTowerType('Sniper tower; Effect upgrade');

        // update the info abput this sniper tower in local storage with 'E', to indicate the effect upgrade
        const localStorageString: string = localStorage.getItem(this.level.getSessionId() + 'snipertowers');
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
        localStorage.setItem(this.level.getSessionId() + 'snipertowers', newLocalStorageString);
        this.level.sendHash();
    }

    public boostUpgrade(): void {
        // Upgrade (some of) the tower's stats.
        this.setTowerType('Sniper tower; Boost upgrade');

        // update the info of this sniper tower in local storage  with 'N', to indicate the boost upgrade
        const localStorageString: string = localStorage.getItem(this.level.getSessionId() + 'snipertowers');
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
        localStorage.setItem(this.level.getSessionId() + 'snipertowers', newLocalStorageString);
        this.level.sendHash();
    }

    public useEffect(spark: Spark): void {
        const weaken: object = this.effectSettings['Weaken'];
        spark.applyEffect(new WeakenEffect(spark, weaken['duration'], weaken['damageMultiplier']));
    }

    public removeTower(): void {
      super.removeTower();

      // remove this sniper tower from local storage
      const localStorageString: string = localStorage.getItem(this.level.getSessionId() + 'snipertowers');
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
        localStorage.setItem(this.level.getSessionId() + 'snipertowers', newLocalStorageString);
      else
        // if there are no more sniper towers left, remove the snipertowers item from local storage
        localStorage.removeItem(this.level.getSessionId() + 'snipertowers');
      this.level.sendHash();
    }
}
