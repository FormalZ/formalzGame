/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University
 * within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
import * as Assets from '../../assets';
import Level from '../../states/level';
import Spark from '../sparks/spark';
import Tower from './tower';

const shotSpriteName: string = Assets.Images.SpritesLaser.getName();

/**
 * A LaserTower is a tower that shoots sparks that were marked incorrect by a ConditionScanner with a laser.
 */
export default abstract class LaserTower extends Tower {
    protected previousShotSprite: Phaser.Sprite = null; // The Graphics module (necessary to draw lines with).
    protected shotFadeTime: number; // The time it takes for the laser to fade.

    /**
     * The constructor for a LaserTower.
     * @param level The Level object
     * @param x The x-position of the Tower
     * @param y The y-position of the Tower
     * @param width The width of the Tower's sprite.
     * @param height The height of the Tower's sprite.
     * @param spriteName The name of the Tower's sprite.
     * @param anchorX The horizontal offset of the Bullet's sprite. This should be a number between 0 and 1 (inclusive).
     * @param anchorY The vertical offset of the Bullet's sprite. This should be a number between 0 and 1 (inclusive).
     */
    constructor(level: Level, x: number, y: number, width: number, height: number, spriteName: string, anchorX: number, anchorY: number) {
        super(level, x, y, width, height, spriteName);

        this.anchor.set(anchorX, anchorY);
        this.range = this.baseRange * this.rangeMultiplier;
    }

    /**
     * For this tower, destroy a previous shot if necessary.
     * Get a target spark, and shoot it.
     */
    public updateObject(): void {
        super.updateObject();

        const currentTime: number = this.level.game.time.totalElapsedSeconds();

        if (this.lastShootTime !== null && (currentTime - this.lastShootTime) > this.shotFadeTime) {
            this.destroyShot();
        }

        this.updateTarget(currentTime);
    }

    // Updates the targets to shoot
    public updateTarget(currentTime: number): void {
        const target: Spark = this.getTarget();
        if (target && (currentTime - this.lastShootTime) > this.reloadTime) {
            this.shoot(target);
            this.lastShootTime = currentTime;

            this.playShootSound();
        }
    }

    /**
    * Draws a line between the tower and the spark furthest from the starting point and destroys the spark
    */
    public shoot(spark: Spark): void {
        if (!this.getVirusAffected()) {
            const shotSprite: Phaser.Sprite = this.game.add.sprite(this.x, this.y, shotSpriteName);
            // shotSprite.anchor = new Phaser.Point(0.5, 0);

            const angle: number = Math.atan2(spark.centerY - this.y, spark.centerX - this.x);
            shotSprite.rotation = angle;

            const distance: number = Math.sqrt(
                (spark.centerX - this.x) * (spark.centerX - this.x) +
                (spark.centerY - this.y) * (spark.centerY - this.y)
            );
            const scaleValue: number = distance / shotSprite.width;
            shotSprite.scale = new Phaser.Point(scaleValue, scaleValue);

            // Deal damage to the spark, destroy the spark when its health reaches 0 or lower.
            spark.damageSpark(this.power);

            if (this.isEffectUpgraded) {
                this.useEffect(spark);
            }

            if (spark.health <= 0) {
                spark.destroyObject();
            }

            this.updateSprite(shotSprite);
        }
    }

    /**
    * Remove this tower, removing it from the game and refunding some of the cost.
    */
    public removeTower(): void {
        this.level.setMoney(this.level.getMoney() + this.getRefundAmount());
        // Remove any sprites drawn when Remove was called.
        if (this.previousShotSprite !== null) {
            this.destroyShot();
        }
    }

    /**
     * Destroys the previous shot object
     */
    public destroyShot(): void {
        this.previousShotSprite.destroy();
    }

    /**
     * Abstract function defining how a effect should be applied to a spark
     * @param spark The spark to be affected by the effect
     */
    public abstract useEffect(spark: Spark): void;

    /**
     * Update the sprite of the current shot
     * @param shotSprite The new shot to be updated to
     */
    public updateSprite(shotSprite: Phaser.Sprite): void {
        this.level.getGameRenderGroup().add(shotSprite);
        this.previousShotSprite = shotSprite;
    }
}
