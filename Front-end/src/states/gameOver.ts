/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University
 * within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
import * as Assets from '../assets';
import Menu from '../userInterface/menu';
import GameState from './gameState';
import SoundManager from '../soundManager';

const loseSound: string = Assets.Audio.AudioLose.getName();

export default class GameOver extends GameState {
    private menu: Menu;
    private uiText: object = null;

    /**
     * Create is a built-in Phaser function that is called when the state is called
     */
    public create(): void {
        this.uiText = this.game.cache.getJSON('uiText')['gameOver'];

        this.game.add.button(this.world.centerX - (126 / 2), this.world.centerY - (63 / 2),
            Assets.Spritesheets.SpritesheetsQuitButton126633.getName(), () => this.onClick(), this, 1, 0, 2, 0);
        this.menu = new Menu(this.game);

        this.menu.text(
            this.world.centerX, this.world.centerY - 126,
            this.uiText['gameOver'] + '\n' + this.uiText['returnToMenu'],
            {
                font: '24px Arial',
                fill: '#F2F2F2'
            }
        ).anchor.set(0.5);

        this.game.sound.stopAll();
        SoundManager.playSoundEffect(loseSound);
    }

    /**
     * Callback method for a mouse down event
     */
    protected onMouseDown(): void {
        this.menu.onMouseDown(this.game.input.activePointer);
    }

    /**
    * Callback method for a mouse up event
    */
    protected onMouseUp(): void {
        this.menu.onMouseUp(this.game.input.activePointer);
    }

    /**
     * This gets called when the user clicked on the button.
     */
    private onClick(): void {
        if (!DEBUG) {
            const url: string = (<HTMLInputElement>document.getElementById('problemroute')).value;
            window.location.replace(url);
        }

        this.game.state.start('menu');
    }
}

