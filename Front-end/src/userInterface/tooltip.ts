/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University
 * within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
import Level from '../states/level';
import GeometryUtils from '../utils/geometryUtils';

/**
 * A ToolTip is a text box that appears when hovering over an object.
 * It can be used to display helpful information about the object.
 * It disappears when the mouse is no longer over the object.
 */
export default class Tooltip extends Phaser.Text {
    private level: Level;
    private object: Phaser.Sprite;
    private active: boolean;
    private time: number;

    /**
     * Instantiates a new Tooltip.
     * NOTE: A new Tooltip should not be instantiated each time a tooltip is shown!
     * Instead Level.getTooltip().show should be used, which is automatically instantiated.
     * @param level The Level that this Tooltip exists in.
     */
    public constructor(level: Level) {
        super(level.game, 0, 0, null, {
            font: '16px Arial',
            fill: 'white',
            backgroundColor: '#043d04'
        });

        this.level = level;
        this.object = null;

        this.visible = false;
        this.active = false;

        this.time = 0;

        this.alpha = 0.9;
        this.resolution = 1.5;

        this.level.game.add.existing(this);
    }

    /**
     * Updates the Tooltip.
     * This means the Tooltip follows the mouse pointer.
     * It also automatically hides the Tooltip once the mouse is no longer over the object that the Tooltip belongs to.
     */
    public updateObject(): void {
        // If the tooltip isn't active, don't do anything
        if (!this.active) {
            return;
        }

        // Check if the tooltip should appear
        if (!this.visible && (this.time -= this.game.time.elapsedMS) <= 0) {
            this.visible = true;
        }

        const mousePos: Phaser.Point = this.level.input.activePointer.position;
        this.x = GeometryUtils.clamp(mousePos.x + 6, 0, this.game.width - this.width);
        this.y = GeometryUtils.clamp(mousePos.y + 12, 0, this.game.height - this.height);

        // If object became null or undefined, or the mouse is no longer over the object, the tooltip should disappear
        if (!this.object || !this.object.visible || !this.object.getBounds().contains(mousePos.x, mousePos.y)) {
            this.hide();
        }
    }

    /**
     * Shows the tooltip
     * @param text The text to be displayed on the tooltip
     * @param object The object over which the tooltip should appear
     * @param time The time in milliseconds it should take for the tooltip to appear, after hovering over the object.
     */
    public show(text: string, object: Phaser.Sprite, time: number = 500): void {
        if (!this.active) {
            this.object = object;

            this.active = true;
            this.visible = false;

            this.time = time;

            this.bringToTop();
        } else if (object !== this.object) {
            this.object = object;

            if (!this.visible) {
                this.time = time;
            }
        }
        this.setText(text, text !== this.text);
    }

    /**
     * Hides the tooltip.
     */
    public hide(): void {
        this.active = false;
        this.visible = false;
    }

    /**
     * Update the text of the tooltip when the object off which the tooltip is displayed is upgraded
     * @param text The new updated text
     * @param object The object which is upgraded
     */
    public upgradeUpdate(text: string, object: Phaser.Sprite): void {
        this.hide();
        this.show(text, object, 0);
    }

    public getObject(): Phaser.Sprite {
        return this.object;
    }
}
