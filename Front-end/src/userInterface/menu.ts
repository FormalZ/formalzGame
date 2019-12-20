/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University
 * within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
import * as Assets from '../assets';
import Level from '../states/level';
import MenuButton, { MouseEvent } from './menuButton';
import MenuSlideButton from './menuSlideButton';
import MenuSwitchButton from './menuSwitchButton';
import MenuTextAlert from './menuTextAlert';
import ShopButton from './shopButton';

const gameButtonSprite: string = Assets.Spritesheets.SpritesheetsEmptyButton3691923.getName();
const menuButtonSprite: string = Assets.Spritesheets.SpritesheetsStartButton126633.getName();

/**
 * A container for the Menu Phaser.Group that also contains methods for
 * creating objects with the right arguments.
 */
export default class Menu extends Phaser.Group {
    private static tweenCounter: number = 0;
    private static doneTweenCounter: number = 0;
    /**
     * Creates the menu object container (Phaser.Group) that will contain all other menu objects
     * @param game The game object
     */
    public constructor(game: Phaser.Game) {
        super(game);

        game.add.existing(this);
    }

    /**
     *  Update this specific object
     *  This is done by calling the update function for every menu and tower button in the current menu
     */
    public updateObject(): void {
        for (let button of this.children) {
            if ((button instanceof MenuButton) || (button instanceof ShopButton)) {
                button.updateObject();
            }
        }
    }

    /**
     * Handle the mouse down event.
     * This is done by calling the onMouseDown function for every menuButton in the current menu
     * @param pointer The position where mouse down event was triggered
     */
    public onMouseDown(pointer: Phaser.Pointer): boolean {
        for (let button of this.children) {
            if ((button instanceof MenuButton || button instanceof Menu) && button.onMouseDown(pointer)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Handle the mouse up event.
     * This is done by calling the onMouseUp function for every menuButton in the current menu
     * @param pointer The position where mouse up event was triggered
     */
    public onMouseUp(pointer: Phaser.Pointer): boolean {
        for (let button of this.children) {
            if ((button instanceof MenuButton || button instanceof Menu) && button.onMouseUp(pointer)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Creates a button and adds it to the menu container
     * @param x The x-coordinate of the button
     * @param y The y-coordinate of the button
     * @param image The asset associated with the button's sprite
     * @param text The text of the button
     * @param scaleFactor A number that can scale the width and the height of the button
     * @param mouseDown The callback function to use when the button is pressed
     * @param mouseUp The callback function to use when the button is released
     * @param overFrame The index of the frame in the sprite sheet that should show when the button contains the mouse
     * @param outFrame The index of the frame in the sprite sheet that should show when the button does not contain the mouse
     * @param downFrame The index of the frame in the sprite sheet that should show when the button is pressed
     */
    public button(x: number, y: number, image: string, text: string, scaleFactor?: Phaser.Point, mouseDown?: MouseEvent,
        mouseUp?: MouseEvent, overFrame?: number, outFrame?: number, downFrame?: number): MenuButton {

        return this.add(new MenuButton(this.game, x, y, image, text, scaleFactor, mouseDown, mouseUp, overFrame, outFrame, downFrame));
    }

    /**
     * Creates a new button using the standard game button style
     * @param x The x-coordinate of the button
     * @param y The y-coordinate of the button
     * @param text The text of the button
     * @param onClick The callback function to use after the button is clicked
     */
    public gameButton(x: number, y: number, text: string, onClick: MouseEvent): MenuButton {
        return this.button(
            x, y,
            gameButtonSprite,
            text,
            new Phaser.Point(0.2, 0.2),
            null,
            onClick,
            1, 0, 2
        );
    }

    /**
     * Creates a new button using the standard menu button style
     * @param x The x-coordinate of the button
     * @param y The y-coordinate of the button
     * @param text The text of the button
     * @param onClick The callback function to use after the button is clicked
     */
    public menuButton(x: number, y: number, text: string, onClick: MouseEvent): MenuButton {
        return this.button(
            x, y,
            menuButtonSprite,
            text,
            new Phaser.Point(1, 1),
            null,
            onClick,
            1, 0, 2
        );
    }


    /**
     * Creates a new tower selection button
     * @param level The current level to initialize the tower button in
     * @param x The x-coordinate of the button
     * @param y The y-coordinate of the button
     * @param spriteName The name of the sprite of the button
     * @param text The text of the button
     * @param towerSettings The settings info of the tower for this button
     * @param mouseDown The mouseDown event definition for this button
     * @param toolTipText The text for the tooltip of this button
     */
    public towerButton(level: Level, x: number, y: number, spriteName: string, text: string,
        towerSettings: object, mouseDown: MouseEvent, toolTipText: string): ShopButton {
        return this.add(new ShopButton(level, x, y, spriteName, text, towerSettings, mouseDown, toolTipText));
    }

    public slideButton(level: Level, x: number, y: number, title: string, body: string): MenuSlideButton {
        return this.add(new MenuSlideButton(level, x, y, title, body));
    }

    /**
     * Creates a new switchable button
     * @param x The x-coordinate of the button
     * @param y The y-coordinate of the button
     * @param textOn The text of the button when the button is switched on
     * @param textOff The text of the button when the button is switched off
     * @param onSwitch The function definition of what the button should when switched on/off
     * @param isOn Whether the button should be initially on or off
     */
    public switchButton(x: number, y: number, textOn: string, textOff: string,
        onSwitch: (isOn: boolean) => void, isOn: boolean): MenuSwitchButton {
        return this.add(new MenuSwitchButton(
            this.game,
            x, y,
            gameButtonSprite,
            textOn, textOff,
            new Phaser.Point(0.2, 0.2),
            onSwitch,
            isOn,
            1, 0, 2
        ));
    }

    /**
     * Add a text field to the menu
     * @param x The x-coordinate of the button
     * @param y The y-coordinate of the button
     * @param text The text of the text field
     * @param style The style of the text
     */
    public text(x: number, y: number, text: string, style: Phaser.PhaserTextStyle): Phaser.Text {
        return this.add(new Phaser.Text(this.game, x, y, text, style));
    }

    /**
     * Adds a MenuTextAlert object to the menu container
     * @param game The Phaser.Game object
     * @param x The x-coordinate of the text alert, recommended: 0
     * @param y The y-coordinate of the text alert, recommended: 0
     * @param image The key of the asset associated with the alert
     * @param text The text you want to display in the alert
     */
    public textAlert(x: number, y: number, image: string, text: string, tween: boolean, tweenY: number = 0,
        scale: number = 0.25, fontSize: number = 64): MenuTextAlert {
        return this.add(new MenuTextAlert(this.game, x, y, image, text, tween, tweenY, scale, fontSize));
    }

    /**
     * Adds a container for a texture to the menu
     * @param x The x-coordinate of the text alert
     * @param y The y-coordinate of the text alert
     * @param image The image of the container
     * @param width The width of the container
     * @param height The height of the container
     */
    public textureContainer(x: number, y: number, image: string, width: number, height: number): Phaser.TileSprite {
        return this.add(new Phaser.TileSprite(this.game, x, y, width, height, image));
    }

    /**
     * Adds a generic Sprite to the menu group with an 0.5 anchor
     * @param x The x-coordinate of the sprite
     * @param y The y-coordinate of the sprite
     * @param key The asset key of the sprite
     * @param frame The specific frame (optional, only used for SpriteSheets)
     */
    public sprite(x: number, y: number, key: string, frame?: string | number): Phaser.Sprite {
        const sprite: Phaser.Sprite = new Phaser.Sprite(this.game, x, y, key, frame);
        sprite.anchor.set(0.5);
        return this.add(sprite);
    }

    /**
     * Removes a child from the Phaser.Group menu
     * @param child the object to remove
     */
    public killChild(child: any): void {
        this.remove(child, true, false);
    }

    /**
     * Removes all the elements of a menu
     */
    public killAll(): void {
        Menu.resetTweenCounters();
        this.destroy();
    }

    /**
     * Increment the tween counter. Should only be called by the constructor of an object using a tween.
     */
    public static incrementTweenCounter(): void { Menu.tweenCounter++; }

    /**
     * Increment the done tween counter. Should be called when an object using a tween destroys itself.
     * Resets counters when every active tween is done.
     */
    public static incrementDoneTweenCounter(): void {
        Menu.doneTweenCounter++;
        // Every tween is done, so reset
        if (Menu.tweenCounter === Menu.doneTweenCounter) {
            Menu.resetTweenCounters();
        }
    }

    /**
     * Reset Tween counters.
     * Should only be called by killAll for state changes or by incrementDoneTweenCounter.
     */
    private static resetTweenCounters(): void {
        Menu.tweenCounter = 0;
        Menu.doneTweenCounter = 0;
    }
    public static getTweenCounter(): number { return this.tweenCounter; }
}