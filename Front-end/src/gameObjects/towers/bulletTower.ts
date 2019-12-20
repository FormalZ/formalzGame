/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University
 * within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
import Effect from '../../effects/effect';
import Level from '../../states/level';
import Spark from '../sparks/spark';
import Bullet from './bullet';
import Tower from './tower';

/**
 * A BulletTower is a tower that shoots sparks that were marked incorrect by a ConditionScanner with a bullet.
 */
export default abstract class BulletTower extends Tower {
    // Add additional necessary variables here.
    protected bullets: Set<Bullet>;

    /**
     * The constructor for a BulletTower.
     * @param level The Phaser.Level object
     * @param x The x-position of the Tower
     * @param y The y-position of the Tower
     * @param width The width of the Bullet's sprite.
     * @param height The height of the Bullet's sprite.
     * @param spriteName The name of the Bullet's sprite.
     * @param anchorX The horizontal offset of the Bullet's sprite. This should be a number between 0 and 1 (inclusive).
     * @param anchorY The vertical offset of the Bullet's sprite. This should be a number between 0 and 1 (inclusive).
     */
    constructor(level: Level, x: number, y: number, width: number, height: number, spriteName: string, anchorX: number, anchorY: number) {
        super(level, x, y, width, height, spriteName);

        this.bullets = new Set<Bullet>();

        this.anchor.set(anchorX, anchorY);
    }

    /**
     * For this tower, create another shot if possible, and update shot.
     */
    public updateObject(): void {
        super.updateObject();

        const target: Spark = this.getTarget();

        if (target) {
            this.shoot(target);
        }

        this.bullets.forEach(bullet => {
            if (bullet.updateBullet()) {
                this.bullets.delete(bullet);
                bullet.destroy();
            }
        });
    }

    public abstract effectUpgrade(): void;

    public abstract boostUpgrade(): void;

    /**
     * Remove this tower, removing it from the game and refunding some of the cost.
     */
    public removeTower(): void {
        this.level.setMoney(this.level.getMoney() + this.getRefundAmount());

        // Remove any bullets shot by this tower currently active in the level.
        for (const bullet of this.bullets) {
            bullet.destroy();
        }
    }

    /**
    * Creates a certain amount of Bullets.
    * The first bullet is aimed at the Spark that was found by getInRange().
    */
    public shoot(spark: Spark): void {
        if (!this.getVirusAffected()) {
            const currentTime: number = this.level.game.time.totalElapsedSeconds();

            if ((currentTime - this.lastShootTime) > this.reloadTime) {
                let effects: Effect<Spark>[] = null;

                if (this.isEffectUpgraded) {
                    effects = [];
                    this.useEffect(effects);
                }

                // Calculate the angle with which to shoot at the marked Spark.
                const angle: number = Math.atan2(spark.y - this.y, spark.worldPosition.x - this.x);

                this.createBullet(effects, angle);

                this.lastShootTime = currentTime;

                this.playShootSound();
            }
        }
    }

    /**
     * An abstract function defining how the effect should affect a group of sparks
     * @param effects The group of sparks to apply an effect to
     */
    public abstract useEffect(effects: Effect<Spark>[]): Effect<Spark>[];

    /**
     * An abstract function defining how to create a bullet
     * @param effects The group of 0 or more effects to add to the bullet
     * @param angle The angle at which to shoot the bullet
     */
    public abstract createBullet(effects: Effect<Spark>[], angle: number): void;
}
