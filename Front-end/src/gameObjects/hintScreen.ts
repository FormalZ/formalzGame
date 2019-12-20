/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University
 * within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
import * as Assets from '../assets';
import Level from '../states/level';
import Menu from '../userInterface/menu';
import MenuButton from '../userInterface/menuButton';
import GameObject from './gameObject';

const hintScreenSpriteName: string = Assets.Images.SpritesBackgroundScreen.getName();
const textureSprite: string = Assets.Images.SpritesGeneralContainerBackground.getName();

/**
 * Screen to display hints
 */
export default class HintScreen extends GameObject {
    private menu: Menu;
    private crossButton: MenuButton;
    private titleContainer: Phaser.TileSprite;
    private titleText: Phaser.Text;
    private nextHintButton: MenuButton;
    private contentText: Phaser.Text;

    private hints: string[];
    private hintsString: string;
    private currIndex: number;
    private currAmountOfHints: number;

    private uiText: object;

    /**
     * Constructs a hint screen and sets its visibility to false
     * @param level The level to construct the hint screen in
     */
    constructor(level: Level) {
        super(level, level.game.width / 2, level.game.height / 2, hintScreenSpriteName);

        this.level.add.existing(this);

        this.uiText = this.level.game.cache.getJSON('uiText')['hints'];

        // UI creation and logic

        this.scale.set(0.25, 0.25);
        this.anchor.set(0.5);

        this.menu = new Menu(this.level.game);

        // UI helper variables
        const buttonScale: Phaser.Point = new Phaser.Point(0.2, 0.2);
        const emptyButtonWidth: number = Assets.Spritesheets.SpritesheetsEmptyButton3691923.getFrameWidth() * buttonScale.x;
        const emptyButtonHeight: number = Assets.Spritesheets.SpritesheetsEmptyButton3691923.getFrameHeight() * buttonScale.y;

        // Close button
        this.crossButton = this.menu.button(
            this.right + 0.125 * Assets.Spritesheets.SpritesheetsCrossButton1001002.getFrameWidth(),
            this.top + 0.125 * Assets.Spritesheets.SpritesheetsCrossButton1001002.getFrameHeight(),
            Assets.Spritesheets.SpritesheetsCrossButton1001002.getName(),
            null,
            buttonScale,
            null,
            () => this.closeScreen(),
            1, 0, 2
        );

        // Container UI at the top
        this.titleContainer = this.menu.textureContainer(
            this.centerX, this.top,
            textureSprite,
            this.width / 2 + emptyButtonWidth,
            35
        );
        this.titleContainer.anchor.set(0.4, 1);

        // Title text
        const style: Phaser.PhaserTextStyle = { font: '26px Arial', fill: 'white' };

        this.titleText = this.menu.text(this.centerX, this.top + 5, this.uiText['hints'], style);
        this.titleText.anchor.setTo(0.5, 1);

        const padding: number = 15;

        // Hint content text
        this.contentText = this.menu.text(this.left + padding, this.top + padding, '', {
            font: '18px Arial',
            fill: 'white',
            stroke: 'black',
            strokeThickness: 7
        });
        this.contentText.wordWrap = true;
        this.contentText.wordWrapWidth = this.width - 2 * padding;

        // Next hint button
        this.nextHintButton = this.menu.gameButton(
            this.titleContainer.right - emptyButtonWidth * 0.5,
            this.titleContainer.top + emptyButtonHeight * 0.5,
            this.uiText['nextHint'],
            () => this.nextHint(),
        );

        this.level.getUIScreenRenderGroup().add(this);
        this.level.getUIScreenRenderGroup().add(this.menu);

        // Other creation and logic
        this.hints = [];
        this.hintsString = '';
        this.currAmountOfHints = 0;
        this.currIndex = 0;

        this.closeScreen();
    }

    /**
     * Display the next hint in the sequence
     */
    private nextHint(): void {
        // If we have no hints, display that and stop the execution of this method
        if (this.currAmountOfHints === 0) {
            this.contentText.setText(this.uiText['noHint']);

            return;
        }

        // Update hint index
        this.currIndex++;
        if (this.currIndex >= this.currAmountOfHints) {
            this.currIndex = 0;
        }

        // Update the hint text
        this.contentText.setText(this.hints[this.currIndex]);
    }

    /**
     * Clear all hints currently present
     */
    public resetHints(): void {
        this.hints = [];
        this.hintsString = '';
        this.currAmountOfHints = 0;
        this.currIndex = 0;
    }

    /**
     * Add a hint to the collection of hints
     * @param hint The hint to add
     */
    public addHint(hint: string): void {
        // Add the hint
        this.currAmountOfHints++;
        this.hints.push(hint);

        // Concatenate the hint to the string version, for quicker processing for UIGameTask
        if (this.hintsString) {
            this.hintsString = this.hintsString + '\n\n' + hint;
        } else {
            this.hintsString = hint;
        }
    }


    public updateObject(): void {
        this.menu.updateObject();
    }

    public onMouseDown(pointer: Phaser.Pointer): boolean {
        return this.menu.onMouseDown(pointer);
    }

    public onMouseUp(pointer: Phaser.Pointer): boolean {
        return this.menu.onMouseUp(pointer);
    }

    /**
     * Open the hintScreen
     */
    public openScreen(): void {
        this.menu.visible = true;
        this.visible = true;

        this.nextHint();
    }

    /**
     * Close the hintScreen.
     */
    public closeScreen(): void {
        this.menu.visible = false;
        this.visible = false;
    }

    public getHints(): string[] {
        return this.hints;
    }

    public getHintsString(): string {
        return this.hintsString;
    }
}
