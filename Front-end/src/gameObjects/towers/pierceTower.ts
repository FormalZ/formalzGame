/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University
 * within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
import * as Assets from '../../assets';
import Effect from '../../effects/effect';
import SlowEffect from '../../effects/slowEffect';
import Spark from '../../gameObjects/sparks/spark';
import Level from '../../states/level';
import Bullet from './bullet';
import BulletTower from './bulletTower';
import PierceBullet from './pierceBullet';

const width: number = 36;
const height: number = 54;
const anchorX: number = 0.5;
const anchorY: number = 0.8;

const spriteName: string = Assets.Images.SpritesPierceTower.getName();

/**
 * A PierceTower is a tower that shoots a bullet in a straight line that hits sparks that were marked incorrect by a ConditionScanner.
 * The bullet continues moving until it has reached the maximum range of the tower.
 */
export default class PierceTower extends BulletTower {
    // Add additional necessary variables here.
    protected bullets: Set<Bullet> = new Set<PierceBullet>();

    /**
     * The constructor for a PierceTower.
     * @param level The Phaser.Level object
     * @param x The x-position of the Tower
     * @param y The y-position of the Tower
     */
    constructor(level: Level, x: number, y: number) {
        super(level, x, y, width, height, spriteName, anchorX, anchorY);

        this.setTowerType('Pierce tower; Default');

        this.range = this.baseRange * this.rangeMultiplier;
        this.setDescriptions('pierceTower');
    }

    public effectUpgrade(): void {
        // Effect: Slow. Slowed sparks move slower.
        this.isEffectUpgraded = true;

        // Update the tower's variables (if the effect is too weak or too strong).
        this.setTowerType('Pierce tower; Effect upgrade');

        // Update the info on this pierce tower in local storage with 'E', to indicate the effect upgrade
        const localStorageString: string = localStorage.getItem(this.level.getSessionId() + 'piercetowers');
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
        localStorage.setItem(this.level.getSessionId() + 'piercetowers', newLocalStorageString);
        this.level.sendHash();
    }

    public boostUpgrade(): void {
        // Upgrade (some of) the tower's stats.
        this.setTowerType('Pierce tower; Boost upgrade');

        // update the info of this pierce tower in local storage with 'N', to indicate the boost upgrade
        const localStorageString: string = localStorage.getItem(this.level.getSessionId() + 'piercetowers');
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
        localStorage.setItem(this.level.getSessionId() + 'piercetowers', newLocalStorageString);
        this.level.sendHash();
    }

    public useEffect(effects: Effect<Spark>[]): Effect<Spark>[] {
        const slow: object = this.effectSettings['Slow'];
        effects.push(new SlowEffect(null, slow['duration'], slow['slowMultiplier']));

        return effects;
    }

    public createBullet(effects: Effect<Spark>[], angle: number): void {
        this.bullets.add(new PierceBullet(this.level, this.x, this.y, this.range, angle, this.cells, effects, this.power));
    }

    public removeTower(): void {
      super.removeTower();

      // remove this tower from local storage
      const localStorageString: string = localStorage.getItem(this.level.getSessionId() + 'piercetowers');
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
        localStorage.setItem(this.level.getSessionId() + 'piercetowers', newLocalStorageString);
      else
        // if there are no more pierce towers to upgrade, remvoe the 'piercetowers' item from local storage
        localStorage.removeItem(this.level.getSessionId() + 'piercetowers');
      this.level.sendHash();
    }
}
