/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University
 * within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
import * as Assets from '../assets';
import Spark from '../gameObjects/sparks/spark';
import SoundManager from '../soundManager';
import Effect from './effect';

const weakenSound: string = Assets.Audio.AudioEffectWeaken.getName();

export default class WeakenEffect extends Effect<Spark> {
    private weakenMultiplier: number;
    /**
     * Make the spark vulnerable for more damage.
     * @param spark The effected spark.
     * @param duration How many updates the effect should last.
     * @param weakenMultiplier What the damage multiplier for the spark will be.
     */
    constructor(spark: Spark, duration: number, weakenMultiplier: number) {
        super(spark, 'weaken', 25, duration / 25);

        this.weakenMultiplier = weakenMultiplier;
    }

    /**
     * @inheritDoc Sets the damage multiplier of the spark, and recolour it.
     */
    public initializeEffect(): void {
        this.object.setDamageMultiplier(this.weakenMultiplier);
        this.object.tint = 0x2e5f91;

        SoundManager.playSoundEffect(weakenSound);
    }

    /**
     * @inheritDoc Remove the damage multiplier and the colour.
     */
    public endEffect(): void {
        this.object.setDamageMultiplier(1);
        this.object.tint = 0xffffff;
    }
}