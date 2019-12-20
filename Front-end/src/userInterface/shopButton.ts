/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University
 * within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
import * as Assets from '../assets';
import Level from '../states/level';
import LevelState from '../states/levelState';
import { StringUtils } from '../utils/utils';
import MenuButton, { MouseEvent } from './menuButton';

const style: Phaser.PhaserTextStyle = { font: 'Arial', fontSize: 14, fill: 'white' };

export default class ShopButton extends MenuButton {

    private iconSprite: Phaser.Sprite;
    private towerName: Phaser.Text;
    private goldSprite: Phaser.Sprite;
    private towerCost: Phaser.Text;

    private level: Level;
    private toolTipText: string;
    private towerSettings: object;

    /**
     * Construct a shop button
     * @param level The current level to initialize the shop button in
     * @param x The x-coordinate of the button
     * @param y The y-coordinate of the button
     * @param iconSprite The name of the sprite of the button
     * @param name The text of the button
     * @param towerSettings The settings info of the tower for this button  TODO: Remove all tower specific things
     * @param mouseDown The mouseDown event definition for this button
     * @param toolTipText The text for the tooltip of this button
     */


    public constructor(level: Level, x: number, y: number, iconSprite: string,
        name: string, towerSettings: object, mouseDown: MouseEvent, toolTipText: string) {
            super(level.game, x, y, iconSprite, '', new Phaser.Point(1, 1), mouseDown);
            this.iconSprite = new Phaser.Sprite(level.game, x, y, iconSprite);
            this.iconSprite.anchor.set(0.5, 0.5);
            this.game.add.existing(this.iconSprite);

            this.towerName = new Phaser.Text(level.game, x + 25, y - 10, name, style);
            level.game.add.existing(this.towerName);

            this.goldSprite = new Phaser.Sprite(level.game, x + 137,
                y - 10, Assets.Images.SpritesIconMoney.getName());
            this.goldSprite.scale.set(0.5, 0.5);

            level.game.add.existing(this.goldSprite);
            this.towerCost = new Phaser.Text(level.game, x + 139 + this.goldSprite.width,
                y - 10, towerSettings['towerCost'], style);
            level.game.add.existing(this.towerCost);

            this.x -= this.iconSprite.width / 2;
            this.anchor.set(0, 0.5);
            this.width = 139 + this.goldSprite.width + this.towerCost.width + this.iconSprite.width / 2;
            this.alpha = 0;

            this.level = level;
            this.toolTipText = toolTipText;
            this.towerSettings = towerSettings;
    }

    /**
     * Update the tower selection button
     * This entails updating the tooltip if necessary
     */
    public updateObject(): void {
        const pointer: Phaser.Point = this.level.input.activePointer.position;
        if (this.toolTipText !== null && this.getBounds().contains(pointer.x, pointer.y)
            && this.level.getLevelState() === LevelState.PLAYING) {
            this.level.getTooltip().show(this.getTooltipString(), this);
        }
    }

    private getTooltipString(): string {
        return StringUtils.interpolateString(
            this.toolTipText,
            this.towerSettings['power'],
            this.towerSettings['range'],
            this.towerSettings['reloadTime'],
            this.towerSettings['towerCost'],
            this.towerSettings['upgradeCost']
        );
    }

    /**
     * Change the visibility of this towerButton and all attached text to the given parameter
     * @param visible Whether the object should be visible or not
     */
    public setVisible(visible: boolean): void {
        this.visible = visible;
        this.iconSprite.visible = visible;
        this.goldSprite.visible = visible;
        this.towerCost.visible = visible;
        this.towerName.visible = visible;
    }
}