/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University
 * within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
import * as Assets from '../../assets';
import Effect from '../../effects/effect';
import Level from '../../states/level';
import Cell from '../grid/cell';
import Spark from '../sparks/spark';
import Bullet from './bullet';

const circleBulletZeroSprite: string = Assets.Images.SpritesZero.getName();

const width: number = 6;
const height: number = 6;
const speed: number = 10;

/**
 * A PierceBullet is a bullet shot by the PierceTower.
 * It is a bullet that keeps moving in a straight line, damaging all Sparks it encounters in its trajectory.
 * It is removed when the PierceTower's maximum range is reached.
 */
export default class PierceBullet extends Bullet {
    private sparksHit: Set<Spark> = new Set<Spark>(); // A set that keeps track of Sparks hit by this bullet.

    /**
     * Creates a new instance of PierceBullet
     * @param level The Level that the Bullet exists in.
     * @param x The x coordinate in screen space of the Bullet.
     * @param y The y coordinate in screen space of the Bullet.
     * @param range The maximum range that the Bullet can reach. After the Bullet has travelled this distance it will self-destruct.
     * @param angle The angle at which the Bullet travels.
     * @param cells An array of all the Cells within the Bullet's range that are Path cells.
     * @param effects The effects that the Bullet will apply to any Spark it hits.
     * @param power The amount of damage the Bullet will to to any Spark it hits.
     */
    constructor(level: Level, x: number, y: number, range: number, angle: number, cells: Cell[], effects: Effect<Spark>[], power: number) {
        super(level, x, y, range, angle, speed, power, width, height, cells, circleBulletZeroSprite, effects);
    }

    /**
     * Collide this bullet with the sprites of all sparks in maximum range
     */
    protected collideSparks(): void {
        for (let i: number = this.cells.length - 1; i >= 0; i--) {
            for (let spark of this.cells[i].getSparks()) {
                // Check if the Spark is marked, if the bullet overlaps it, and if this bullet has not damaged the spark yet.
                if (spark.getIsMarked() && this.overlap(spark) && !this.sparksHit.has(spark)) {
                    spark.damageSpark(this.power);
                    // If the bullet has effects, apply them.
                    if (this.effects) {
                        this.effects.forEach(effect => {
                            effect.setObject(spark);
                            spark.applyEffect(effect);
                        });
                    }

                    if (spark.health <= 0) {
                        spark.destroyObject();
                    } else {
                        this.sparksHit.add(spark);
                    }
                }
            }
        }
    }
}
