/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University
 * within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
import MenuButton from './menuButton';

export type MouseEvent = () => void;

/**
 * MenuSwitchButton is a special MenuButton that switches between an 'on' and an 'off' state.
 * When the Button is clicked the state is swapped and a user defined onSwitch callback function is called with isOn as argument.
 */
export default class MenuSwitchButton extends MenuButton {
    private textOn: string;
    private textOff: string;

    private isOn: boolean;
    private onSwitch: (isOn: boolean) => void;

    /**
     * Creates a switch button
     * @param game the current game object to initialize the button width
     * @param x The x-coordinate of the button
     * @param y The y-coordinate of the button
     * @param image The asset associated with the button's sprite
     * @param textOn The text of the button when the button is on
     * @param textOff The text of the button when the button is off
     * @param scaleFactor A number that can scale the width and the height of the button
     * @param onSwitch The function defining what to do when the button is switched on/off
     * @param isOn Whether the button should be on when initialized
     * @param overFrame The index of the frame in the sprite sheet that should show when the button contains the mouse
     * @param outFrame The index of the frame in the sprite sheet that should show when the button does not contain the mouse
     * @param downFrame The index of the frame in the sprite sheet that should show when the button is pressed
     */
    public constructor(game: Phaser.Game, x: number, y: number, image: string, textOn: string, textOff: string,
        scaleFactor: Phaser.Point = new Phaser.Point(1, 1), onSwitch: (isOn: boolean) => void, isOn: boolean = true,
        overFrame: number = 0, outFrame: number = 0, downFrame: number = 0) {

        super(
            game,
            x, y,
            image,
            '',
            scaleFactor,
            null,
            () => {
                this.onSwitch(this.isOn = !this.isOn);

                this.updateText();
            },
            overFrame, outFrame, downFrame
        );

        this.textOn = textOn;
        this.textOff = textOff;

        this.isOn = isOn;
        this.onSwitch = onSwitch;

        this.updateText();
    }

    /**
     * Updates the text of the button
     */
    private updateText(): void {
        this.setText(this.isOn ? this.textOn : this.textOff, true);
    }
}