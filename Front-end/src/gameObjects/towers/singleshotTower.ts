/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University
 * within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
import * as Assets from '../../assets';
import BombEffect from '../../effects/bombEffect';
import Effect from '../../effects/effect';
import Level from '../../states/level';
import Spark from '../sparks/spark';
import BasicBullet from '../towers/basicBullet';
import BulletTower from './bulletTower';

const width: number = 44;
const height: number = 44;
const anchorX: number = 0.5;
const anchorY: number = 0.6;

const spriteName: string = Assets.Images.SpritesShootingTowerLeft.getName();

/**
 * A Singleshot tower is a tower that shoots sparks that were marked incorrect by a Condition Scanner.
 */
export default class SingleshotTower extends BulletTower {
    /**
     * The constructor for a Singleshot.
     * @param level The Level object
     * @param x The x-position of the Tower
     * @param y The y-position of the Tower
     */
    constructor(level: Level, x: number, y: number) {
        super(level, x, y, width, height, spriteName, anchorX, anchorY);

        this.setTowerType('Singleshot tower; Default');

        this.range = this.baseRange * this.rangeMultiplier;
        this.setDescriptions('singleShotTower');

        this.scale.set(0.065, 0.065);
    }

    public effectUpgrade(): void {
        // Effect: Bomb. Bombed sparks deal damage to other marked sparks around it when it dies.
        this.isEffectUpgraded = true;

        // Update the tower's variables (if the effect is too weak or too strong).
        this.setTowerType('Singleshot tower; Effect upgrade');

        // update the info about this single shot tower in local storage with 'E', to indicate the effect upgrade
        const localStorageString: string = localStorage.getItem(this.level.getSessionId() + 'singleshottowers');
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
        localStorage.setItem(this.level.getSessionId() + 'singleshottowers', newLocalStorageString);
        this.level.sendHash();
    }

    public boostUpgrade(): void {
        // Upgrade (some of) the tower's stats.
        this.setTowerType('Singleshot tower; Boost upgrade');

        // update the info about this single shot tower in local storage with 'N', to indicate the boost upgrade
        const localStorageString: string = localStorage.getItem(this.level.getSessionId() + 'singleshottowers');
        const localStorageBlocks: string[] = localStorageString.split('|');
        let newLocalStorageString: string = '';
        localStorageBlocks.forEach(storedTower => {
          let stringToAdd: string = '';
          if (storedTower.includes(this.getId())) {
            const gridPos: Phaser.Point = this.level.getGrid().screenSpaceToGridSpace(this.x, this.y);
            stringToAdd = this.getId() + ';' + gridPos.x + ';' + gridPos.y + ';N';
          }
          else
            stringToAdd = storedTower;
          if (newLocalStorageString !== '')
            newLocalStorageString = newLocalStorageString + '|' + stringToAdd;
          else
            newLocalStorageString = stringToAdd;
        });
        localStorage.setItem(this.level.getSessionId() + 'singleshottowers', newLocalStorageString);
        this.level.sendHash();
    }

    public useEffect(effects: Effect<Spark>[]): Effect<Spark>[] {
        const bomb: object = this.effectSettings['Bomb'];
        effects.push(new BombEffect(null, bomb['duration'], bomb['explosionDamage'], bomb['explosionRange']));

        return effects;
    }

    public createBullet(effects: Effect<Spark>[], angle: number): void {
        this.bullets.add(new BasicBullet(this.level, this.x, this.y, this.range, angle, this.cells, effects, this.power));
    }

    public removeTower(): void {
      super.removeTower();

      // remove this tower from local storage
      const localStorageString: string = localStorage.getItem(this.level.getSessionId() + 'singleshottowers');
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
        localStorage.setItem(this.level.getSessionId() + 'singleshottowers', newLocalStorageString);
      else
        // if there are no more single shot towers, remove the singleshottowers item from local storage.
        localStorage.removeItem(this.level.getSessionId() + 'singleshottowers');
      this.level.sendHash();
    }
}
