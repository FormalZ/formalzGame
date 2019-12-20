/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University
 * within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
import * as Assets from '../../assets';
import Level from '../../states/level';
import Cell from '../grid/cell';
import Bullet from './bullet';

const circleBulletOneSprite: string = Assets.Images.SpritesOne.getName();
const circleBulletZeroSprite: string = Assets.Images.SpritesZero.getName();

const width: number = 6;
const height: number = 6;
const speed: number = 3;

/**
 * A CircleBullet is a bullet shot by the CircleTower.
 * It keeps going until it collides with a Spark, or when it reaches the range of the CircleTower.
 */
export default class CircleBullet extends Bullet {
    /**
     * Creates a new instance of BasicBullet
     * @param level The Level that the Bullet exists in.
     * @param x The x coordinate in screen space of the Bullet.
     * @param y The y coordinate in screen space of the Bullet.
     * @param range The maximum range that the Bullet can reach. After the Bullet has travelled this distance it will self-destruct.
     * @param angle The angle at which the Bullet travels.
     * @param cells An array of all the Cells within the Bullet's range that are Path cells.
     * @param power The amount of damage the Bullet will do to any Spark it hits.
     */
    constructor(level: Level, x: number, y: number, range: number, angle: number, cells: Cell[], power: number) {
        super(level, x, y, range, angle, speed, power, width, height, cells,
            Math.random() < 0.5 ? circleBulletOneSprite : circleBulletZeroSprite, []
        );
    }

    /**
     * Move bullet and check collision
     * @returns whether this Bullet has collided with a Spark or has reached its maximum distance or the edges of the screen.
     */
    public updateBullet(): boolean {
        this.distance += this.bulletSpeed;
        this.x = this.originX + this.distance * Math.cos(this.anglePhi);
        this.y = this.originY + this.distance * Math.sin(this.anglePhi);

        return this.collideSparks() || this.distance >= this.maxDistance || this.checkOutOfBounds();
    }

    /**
     * Collide this bullet with the sprites of all sparks in maximum range
     * @returns Whether the bullet collided with a spark or not.
     */
    protected collideSparks(): boolean {
        // Loop over all Path cells within this Bullet's range.
        for (let i: number = this.cells.length - 1; i >= 0; i--) {
            for (let spark of this.cells[i].getSparks()) {
                // Check if the Spark is marked and if the bullet overlaps it.
                if (spark.getIsMarked() && this.overlap(spark)) {
                    spark.damageSpark(this.power);

                    if (spark.health <= 0) {
                        spark.destroyObject();
                    }

                    return true;
                }
            }
        }

        return false;
    }
}