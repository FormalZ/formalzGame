/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University
 * within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
import * as Assets from '../assets';
import Connection from '../connection';
import Menu from '../userInterface/menu';
import MenuButton from '../userInterface/menuButton';
import GameState from './gameState';
import { StringUtils } from '../utils/utils';

const errorSound: string = Assets.Audio.AudioError.getName();

export default class ErrorState extends GameState {
    private menu: Menu;
    private warningsText: object = null;

    /**
     * Intializes the error state and all the buttons for the error menu.
     */
    public init(args?: any[]): void {
        this.warningsText = this.game.cache.getJSON('warningsText')['errorState'];
        const error: string = args.length > 0 ? args[0] : 'Unknown Error';
        const stackTrace: string = args.length > 1 ? args[1] : 'No Stack Trace';
        const sendToBackEnd: boolean = args.length > 2 ? args[2] : true;

        this.menu = new Menu(this.game);

        const quitButton: MenuButton = this.menu.button(
            this.world.centerX,
            this.world.height - 20,
            Assets.Spritesheets.SpritesheetsQuitButton126633.getName(),
            '',
            new Phaser.Point(1, 1),
            null,
            () => this.game.state.start('menu'),
            1, 0, 2
        );
        quitButton.anchor.set(0.5, 1);

        const errorDisplay: Phaser.Text = this.menu.text(
            this.world.centerX,
            20,
            StringUtils.interpolateString(this.warningsText['errorMessage'], error),
            {
                font: '12px Arial',
                fill: '#F2F2F2'
            }
        );
        errorDisplay.anchor.set(0.5, 0);

        if (sendToBackEnd) {
            Connection.connection.sendError(error, stackTrace);
        }
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
     * Change the state to the error state, sending the error message with it.
     */
    public static throw(game: Phaser.Game, error: string, sendToBackEnd: boolean = true): void {
        // In DEBUG mode it's probably preferable for bug-hunting and bug fixing if the game state
        // doesn't change and instead the error gets logged to the console. When not in DEBUG mode, a player
        // will instead be redirected to the error state.

        if (DEBUG) {
            console.error(error);
        } else {
            game.state.start('error', true, false, [
                error,
                Error().stack.split('\n').slice(2).join('\n'),
                sendToBackEnd
            ]);
        }
    }
}

