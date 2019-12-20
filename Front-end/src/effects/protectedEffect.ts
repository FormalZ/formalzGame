/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University
 * within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
import Spark from '../gameObjects/sparks/spark';
import Effect from './effect';

export default class ProtectedEffect extends Effect<Spark> {

    /**
     * Protect sparks by a firewall.
     * @param spark The effected spark.
     */
    constructor(spark: Spark) {
        super(spark, 'protected', 10, 10);
    }

    /**
     * @inheritDoc Sets the colour of the object to Gray.
     */
    public initializeEffect(): void {
        this.object.tint = Phaser.Color.GRAY;
    }

    /**
     * @inheritDoc Reset the colour. Set the spark to be unprotected.
     */
    public endEffect(): void {
        this.object.tint = 0xffffff;
        this.object.setProtected(false);
    }

    /**
     * @inheritDoc Set the Spark to be protected.
     */
    public applyEffect(): void {
        this.object.setProtected(true);
    }

    /**
     * @inheritDoc Reapply the protect effect.
     * @param activeEffect The reapplied effect.
     */
    public reapply(activeEffect: Effect<Spark>): void {
        activeEffect.setApplyCount(this.applyCount);
    }
}