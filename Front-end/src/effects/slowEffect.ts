/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University
 * within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
import * as Assets from '../assets';
import Spark from '../gameObjects/sparks/spark';
import SoundManager from '../soundManager';
import Effect from './effect';

const slowSound: string = Assets.Audio.AudioEffectSlow.getName();

export default class SlowEffect extends Effect<Spark> {
    private a: number;
    private b: number;
    private c: number;

    /**
     * Slow the spark.
     * @param spark The effected spark.
     * @param duration For how long the effect lasts in updates. This cannot be 0.
     * @param beginMultiplier How strong is the slow effect.
     */
    constructor(spark: Spark, duration: number, beginMultiplier: number ) {
        super(spark, 'slow', 25, duration / 25);

        this.a = (1 - beginMultiplier) / (duration * duration);
        this.b = 2 * (beginMultiplier - 1) / duration;
        this.c = 1;
    }

    /**
     * @inheritDoc Set the speedMultiplier of the spark, based on the multiplier gotten.
     */
    public applyEffect(): void {
        const t: number = this.applyCount * this.applyInterval;
        this.object.setSpeedMultiplier(this.a * t * t + this.b * t + this.c);
    }

    /**
     * Initialize the slow effect. Also play the sound effect for the slow effect
     */
    public initializeEffect(): void {
        this.object.tint = 0x00FF00;

        SoundManager.playSoundEffect(slowSound);
    }

    /**
     * @inheritDoc Set the applyCount of this to the applyCount of the reapplied effect.
     * @param effect The reapplied effect.
     */
    public reapply(effect: Effect<Spark>): void {
        effect.setApplyCount(this.applyCount);
    }

    public endEffect(): void {
        this.object.setSpeedMultiplier(1);
    }
}