/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University
 * within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
import * as Assets from '../assets';
import GameObject from '../gameObjects/gameObject';
import Spark from '../gameObjects/sparks/spark';
import SoundManager from '../soundManager';
import Effect from './effect';

const spinSound: string = Assets.Audio.AudioEffectSpin.getName();

export default class SpinEffect extends Effect<GameObject> {
    /**
     * Let the object spin around.
     * @param spark The effected object.
     * @param duration For how long the effect gets applied.
     */
    constructor(spark: Spark, duration: number) {
        super(spark, 'spinToWin', 25, duration / 25);
    }

    /**
     * Initialize the spin effect. Play the sound effect for the spin effect
     */
    public initializeEffect(): void {
        SoundManager.playSoundEffect(spinSound);
    }

    /**
     * @inheritDoc Increase the angle of the object.
     */
    public applyEffect(): void {
        this.object.angle += 36;
    }
}