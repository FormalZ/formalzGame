/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University
 * within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
import * as Assets from '../assets';
import Spark from '../gameObjects/sparks/spark';
import SoundManager from '../soundManager';
import Effect from './effect';

const explosionSound: string = Assets.Audio.AudioExplosion.getName();

export default class BombEffect extends Effect<Spark> {
    private explodeDamage: number;
    private range: number;

    /**
     * Put a bomb on the spark, that explodes on death.
     * @param spark The affected spark.
     * @param duration How many updates the effect lasts.
     * @param explodeDamage How much damage the effect does.
     * @param range How much range the explosion has
     */
    constructor(spark: Spark, duration: number, explodeDamage: number, range: number) {
        super(spark, 'bomb', duration, 0);

        this.explodeDamage = explodeDamage;
        this.range = range;
    }

    /**
     * @inheritDoc change the colour of the object.
     */
    public initializeEffect(): void {
        this.object.tint = 0xfc5883;
    }

    /**
     * Reset the colour of the object.
     */
    public endEffect(): void {
        this.object.tint = 0xFFFFFF;
    }

    /**
     * @inheritDoc For all marked sparks in range, damage them.
     */
    public onObjectDeath(): void {
        this.object.getSurroundingCells(this.range).forEach(cell => {
            cell.getSparks().forEach(spark => {
                if (spark.getIsMarked()) {
                    spark.damageSpark(this.explodeDamage);

                    if (spark.health < 0) {
                        spark.destroyObject();
                    }
                }
            });
        });

        SoundManager.playSoundEffect(explosionSound);
    }
}