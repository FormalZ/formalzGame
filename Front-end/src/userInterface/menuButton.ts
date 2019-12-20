/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University
 * within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
import * as Assets from '../assets';
import SoundManager from '../soundManager';

export type MouseEvent = () => void;

const clickDownSound: string = Assets.Audio.AudioClickDown.getName();
const clickUpSound: string = Assets.Audio.AudioClickUp.getName();

/**
 * Moves the button UI logic into a separate class in order to add functionality later
 */
export default class MenuButton extends Phaser.Sprite {
    private mouseDown: MouseEvent;
    private mouseUp: MouseEvent;

    private overFrame: number;
    private outFrame: number;
    private downFrame: number;

    private label: Phaser.Text;

    /**
     * Creates a menu button
     * @param game the current game object to initialize the button width
     * @param x The x-coordinate of the button
     * @param y The y-coordinate of the button
     * @param image The asset associated with the button's sprite
     * @param text The text of the button
     * @param scaleFactor A number that can scale the width and the height of the button
     * @param mouseUp The callback function to use when the button is pressed
     * @param mouseDown The callback function to use when the button is released
     * @param overFrame The index of the frame in the sprite sheet that should show when the button contains the mouse
     * @param outFrame The index of the frame in the sprite sheet that should show when the button does not contain the mouse
     * @param downFrame The index of the frame in the sprite sheet that should show when the button is pressed
     */
    public constructor(game: Phaser.Game, x: number, y: number, image: string, text: string,
        scaleFactor: Phaser.Point = new Phaser.Point(1, 1), mouseDown: MouseEvent = null, mouseUp: MouseEvent = null,
        overFrame: number = 0, outFrame: number = 0, downFrame: number = 0) {

        super(game, x, y, image);

        this.mouseDown = mouseDown;
        this.mouseUp = mouseUp;

        this.overFrame = overFrame;
        this.outFrame = outFrame;
        this.downFrame = downFrame;

        this.inputEnabled = true;
        this.input.useHandCursor = true;

        // Sprite related
        this.anchor.set(0.5);
        this.scale = scaleFactor;

        // Text related
        this.label = new Phaser.Text(game, 0, 0, text, {
            font: '64px Arial',
            fill: 'white'
        });
        this.label.anchor.set(0.5, 0.5);
        this.addChild(this.label);
    }

    /**
     * Updates this button.
     * This is done by changing the button sprite based on whether the pointer is on the button or not
     */
    public updateObject(): void {
        const pointer: Phaser.Point = this.game.input.activePointer.position;

        if (this.game.input.activePointer.isUp) {
            this.frame = this.getBounds().contains(pointer.x, pointer.y) ? this.overFrame : this.outFrame;
        }
    }

    /**
     * Handle the mouse down event.
     * If the pointer is on this button, call the given mouse down function. Also play the click down sound effect
     * @param pointer The position where the mouse down event happened
     */
    public onMouseDown(pointer: Phaser.Pointer): boolean {
        if (!this.visible) {
            return false;
        }

        if (this.getBounds().contains(pointer.x, pointer.y)) {
            this.frame = this.downFrame;

            SoundManager.playSoundEffect(clickDownSound);

            if (this.mouseDown) {
                this.mouseDown();
            }

            return true;
        }

        return false;
    }

    /**
     * Handle the mouse up event.
     * If the pointer is on this button, call the given mouse up function. Also play the click up sound effect
     * @param pointer The position where the mouse up event happened
     */
    public onMouseUp(pointer: Phaser.Pointer): boolean {
        if (!this.visible) {
            return false;
        }

        if (this.getBounds().contains(pointer.x, pointer.y)) {
            this.frame = this.overFrame;

            SoundManager.playSoundEffect(clickUpSound);

            if (this.mouseUp) {
                this.mouseUp();
            }

            return true;
        }

        return false;
    }

    /**
     * Set the text of this button
     * @param text The text to set for this button
     * @param immediate Whether the text should be changed immediately or not
     */
    public setText(text: string, immediate: boolean): void {
        this.label.setText(text, immediate);
    }
}