/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University
 * within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
import * as Assets from '../assets';
import Tower from '../gameObjects/towers/tower';
import SoundManager from '../soundManager';
import Effect from './effect';

const virusSound: string = Assets.Audio.AudioEffectVirus.getName();

export default class PoisonEffect extends Effect<Tower> {
    /**
     * Disable a tower via a Virus.
     * @param tower The effected tower.
     * @param duration The duration of the effect
     */
    constructor(tower: Tower, duration: number) {
        super(tower, 'virus', 25, duration / 25);
    }

    /**
     * @inheritDoc Sets the colour of the object to red.
     */
    public initializeEffect(): void {
        this.object.tint = Phaser.Color.RED;

        SoundManager.playSoundEffect(virusSound);
    }

    /**
     * @inheritDoc Reset the colour. Reset the tower to be free of virus.
     */
    public endEffect(): void {
        this.object.tint = 0xffffff;
        this.object.setVirusAffected(false);
    }

    /**
     * @inheritDoc Set the tower to be affected by the virus.
     */
    public applyEffect(): void {
        this.object.setVirusAffected(true);
    }
}