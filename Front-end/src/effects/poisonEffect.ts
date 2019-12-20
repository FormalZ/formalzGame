/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University
 * within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
import Spark from '../gameObjects/sparks/spark';
import Effect from './effect';

export default class PoisonEffect extends Effect<Spark> {
    private damage: number;

    /**
     * Poison the spark.
     * @param spark The effected spark.
     * @param applyInterval How many updates between each tick.
     * @param applyCount How often it should apply.
     * @param damage How much damage the poison does.
     */
    constructor(spark: Spark, applyInterval: number, applyCount: number, damage: number) {
        super(spark, 'poison', applyInterval, applyCount);

        this.damage = damage;
    }

    /**
     * @inheritDoc Sets the colour of the object to green.
     */
    public initializeEffect(): void {
        this.object.tint = 0x00ff00;

        // SoundManager.playSoundEffect(poisonSound);
    }

    /**
     * @inheritDoc Reset the colour.
     */
    public endEffect(): void {
        this.object.tint = 0xffffff;
    }

    /**
     * @inheritDoc Damage the spark, using the damageMultiplier.
     */
    public applyEffect(): void {
        this.object.damageSpark(this.damage);
    }

    /**
     * @inheritDoc Reapply the poison effect.
     * @param activeEffect The reapplied effect.
     */
    public reapply(activeEffect: Effect<Spark>): void {
        activeEffect.setApplyCount(this.applyCount);
    }
}