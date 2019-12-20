/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University
 * within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
import * as Assets from '../assets';
import Menu from '../userInterface/menu';
import GameState from './gameState';
import { Point } from 'phaser-ce';
import SoundManager from '../soundManager';

const winSound: string = Assets.Audio.AudioWin.getName();

export default class WinGame extends GameState {
    private menu: Menu;

    /**
     * init is the very first function called when your State starts up.
     * It's called before preload, create or anything else.
     * If you need to route the game away to another State you could do so here, or if you need to
     * prepare a set of variables or objects before the preloading starts.
     * @param args The optional arguments
     */
    public init(args?: any[]): void {
        this.menu = new Menu(this.game);

        this.menu.button(
            this.world.centerX,
            this.world.centerY,
            Assets.Spritesheets.SpritesheetsQuitButton126633.getName(),
            '',
            new Point(1, 1),
            null,
            () => this.onClick(),
            0, 2, 0
        );

        this.menu.text(
            this.world.centerX,
            this.world.centerY - 126,
            'You WIN! \nClick to return to Menu\nFinal score: ' + args[0],
            {
                font: '24px Arial',
                fill: '#F2F2F2'
            }
        ).anchor.set(0.5);

        this.game.sound.stopAll();
        SoundManager.playSoundEffect(winSound);
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
            window.location.replace((<HTMLInputElement>document.getElementById('problemroute')).value);
        }

        this.game.state.start('menu');
    }
}

