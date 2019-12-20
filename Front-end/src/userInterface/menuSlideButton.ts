/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University
 * within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
import * as Assets from '../assets';
import Level from '../states/level';
import MenuButton from './menuButton';

const slideFontStyle: Phaser.PhaserTextStyle = { font: '32px Arial', fill: '#47B744' };

const slideSprite: string = Assets.Images.SpritesSlideUpButtonLong.getName();
const arrowRightSprite: string = Assets.Images.SpritesArrowRight.getName();
const arrowLeftSprite: string = Assets.Images.SpritesArrowLeft.getName();

/**
 * MenuSlideButton is a MenuButton that can slide up and down when clicked.
 */
export default class MenuSlideButton extends MenuButton {
    private level: Level;

    private initialY: number;

    private titleDisplay: Phaser.Text;
    private bodyDisplay: Phaser.Text;

    // A page is defined as what body of text can be displayed at once in a slide button
    private bodyPages: string[];
    private pageIndex: number;
    // The maximum number of lines per page
    private maxPageLength: number;
    // The maximum number of characters per line
    private maxLineLength: number;
    private completeBody: string;

    private arrowRight: MenuButton;
    private arrowLeft: MenuButton;

    private active: boolean;

    public constructor(level: Level, x: number, y: number, text: string, body: string) {
        super(level.game, x, y, slideSprite, '', new Phaser.Point(1.02, 0.8), null);

        this.level = level;

        this.initialY = this.y;

        this.titleDisplay = this.level.game.add.text(x, y - 35, text, slideFontStyle);
        this.titleDisplay.anchor.setTo(0.5);

        this.bodyDisplay = this.level.game.add.text(x, y - 15, '', {
            font: 'Arial',
            fontSize: 24,
            fill: 'white',
            align: 'left',
            wordWrap: true,
            wordWrapWidth: this.width * 0.85
        });
        this.arrowRight = new MenuButton(this.game, this.x + this.width / 2 - 20, this.y - 35, arrowRightSprite, '');
        this.game.add.existing(this.arrowRight);
        this.arrowLeft = new MenuButton(this.game, this.x - this.width / 2 + 20, this.y - 35, arrowLeftSprite, '');
        this.game.add.existing(this.arrowLeft);

        this.bodyPages = [];
        this.pageIndex = 0;
        this.maxPageLength = 6;
        this.maxLineLength = 26;
        this.completeBody = '';
        this.setBodyText(body);

        this.bodyDisplay.anchor.set(0.5, 0);

        this.active = false;

        this.anchor.set(0.5, 0.1);
    }

    public onMouseDown(pointer: Phaser.Pointer): boolean {
        if (!this.visible) {
            return false;
        }

        if (this.arrowRight.getBounds().contains(pointer.x, pointer.y) && this.arrowRight.visible === true) {
            this.changePageIndex(true);
            return true;
        }
        else if (this.arrowLeft.getBounds().contains(pointer.x, pointer.y) && this.arrowLeft.visible === true) {
            this.changePageIndex(false);
            return true;
        }
        else if (this.getBounds().contains(pointer.x, pointer.y)) {
            this.slide();
            return true;
        }

        return false;
    }

    /**
     * Slides the Button up or down based on its current y position.
     */
    public slide(): void {
        const time: number = 500;

        // Disable if the button is already tweening
        if (!this.level.game.tweens.isTweening(this)) {
            // Switch between what tween to use
            const ypos: number = Math.max(this.level.world.height * 0.095 + 150, this.initialY - this.bodyDisplay.height);

            if (this.y !== ypos) {
                this.game.add.tween(this).to({ y: ypos }, time, Phaser.Easing.Linear.None, true);
                this.game.add.tween(this.titleDisplay).to({ y: ypos - 35 }, time, Phaser.Easing.Linear.None, true);
                this.game.add.tween(this.bodyDisplay).to({ y: ypos - 15 }, time, Phaser.Easing.Linear.None, true);
                this.active = true;
            } else {
                this.game.add.tween(this).to({ y: this.initialY }, time, Phaser.Easing.Linear.None, true);
                this.game.add.tween(this.titleDisplay).to({ y: this.initialY - 35 }, time, Phaser.Easing.Linear.None, true);
                this.game.add.tween(this.bodyDisplay).to({ y: this.initialY - 15 }, time, Phaser.Easing.Linear.None, true);
                this.active = false;
            }
        }
    }

    public setBodyText(text: string): void {
        if (text !== this.completeBody) {
            this.pageIndex = 0;
            this.arrowLeft.visible = false;
            this.completeBody = text;
            this.formatText(text);
            this.bodyDisplay.setText(this.bodyPages[this.pageIndex]);
            if (this.bodyPages.length === 1) {
              this.arrowRight.visible = false;
            }
            else {
              this.arrowRight.visible = true;
            }
        }
    }

    /**
     * Takes the body of text, and splits it into pages to be displayed in the slide button
     * @param body the complete text for this body of the slide button
     */
    public formatText(body: string): void {
        this.bodyPages = [];
        let characterCounter: number = 0;
        let lineCounter: number = 0;
        let newPage: string = '';
        let newLine: string = '';
        for (let word of body.split(' ')) {
            // Count the characters of a word, where a new line sets it to the maximum of the line
            let wordCharacterCounter: number = 0;
            for (let character of word) {
                if (character === '\n') {
                    wordCharacterCounter = this.maxLineLength;
                }
                else {
                    wordCharacterCounter++;
                }
            }
            characterCounter += wordCharacterCounter + 1;
            // if the line length has been reached
            if (characterCounter >= this.maxLineLength) {
                // If the last word in a line does not contain the new line command, this word does not fit on the line
                // Add the current line to the page with a newline, and add the word to the next line
                if (!word.includes('\n')) {
                    newPage += newLine + '\n';
                    newLine = word + ' ';
                    characterCounter = wordCharacterCounter + 1;
                }
                // If the last word in a line does contain the new line command, this word ends the current line
                // Add the current line to the page and start a empty new line
                else {
                    newPage += newLine + word;
                    newLine = '';
                    characterCounter = 0;
                }
                lineCounter += 1;
            }
            else {
                newLine += word + ' ';
            }
            if (lineCounter >= this.maxPageLength) {
                this.bodyPages.push(newPage);
                newPage = '';
                lineCounter = 0;
            }
        }
        newPage += newLine;
        if (newPage !== '')
        this.bodyPages.push(newPage);
    }

    public changePageIndex(next: boolean): void {
        if (next && this.pageIndex !== this.bodyPages.length - 1)
                this.pageIndex++;
        else if (!next && this.pageIndex !== 0)
            this.pageIndex--;
        this.bodyDisplay.setText(this.bodyPages[this.pageIndex]);
        if (this.pageIndex === 0) {
            this.arrowLeft.visible = false;
        }
        else if (this.pageIndex === 1) {
            this.arrowLeft.visible = true;
        }
        if (this.pageIndex === this.bodyPages.length - 2) {
            this.arrowRight.visible = true;
        }
        else if (this.pageIndex === this.bodyPages.length - 1) {
            this.arrowRight.visible = false;
        }
    }

    public getActive(): boolean { return this.active;}
}
