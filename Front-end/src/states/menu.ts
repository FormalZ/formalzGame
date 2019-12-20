/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University
 * within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
import * as Assets from '../assets';
import Connection from '../connection';
import SoundManager from '../soundManager';
import UIMenu from '../userInterface/menu';
import MenuButton from '../userInterface/menuButton';
import GameState from './gameState';

/**
 * The default path data. This is only used with no connection to the server.
 */
const path: string = '(7.24);[[3,45].[3,45].[3,0].[3,-45].[3,-45].[3,45].[3,45].[3,-45].[3,-45].[3,-45].[3,-45].[3,45].[3,0]];' +
    '(37.15);[[3,-45].[3,-45].[3,-45].[3,-45].[3,-45].[3,45].[3,-45].[3,45].[3,45].[3,0].[3,0].[3,-45].[3,0].[3,0]]';

const buttonScale: number = 0.25;
const emptyButtonWidth: number = Assets.Spritesheets.SpritesheetsEmptyButton3691923.getFrameWidth() * buttonScale;
const emptyButtonHeight: number = Assets.Spritesheets.SpritesheetsEmptyButton3691923.getFrameHeight() * buttonScale;

export default class Menu extends GameState {

    private menu: UIMenu;

    private startButton: MenuButton;
    private tutorialButton: MenuButton;
    private okButton: MenuButton;
    private cancelButton: MenuButton;
    private exitButton: MenuButton;
    private sfxButton: MenuButton;
    private musicButton: MenuButton;

    private infoText: Phaser.Text;
    private connectionInfoText: Phaser.Text;


    private uiText: object = null;
    private buttonsText: object = null;
    private warningsText: object = null;

    /**
     * Create is a built-in Phaser function that is called when the state is called
     * TODO: adapt for multiple create calls
     */
    public create(): void {
        this.createMenu();

        this.connectionInfoText = this.game.add.text(
            this.world.centerX + this.world.width * 0.2,
            this.world.centerY - this.world.height * 0.225,
            'Connecting to server...',
            {
                font: '14px Arial',
                fill: 'white'
            }
        );
        this.connectionInfoText.anchor.x = Math.round(this.connectionInfoText.width * 0.5) / this.connectionInfoText.width;
        this.connectionInfoText.anchor.y = Math.round(this.connectionInfoText.height * 0.5) / this.connectionInfoText.height;
        this.uiText = this.game.cache.getJSON('uiText')['menu'];
        this.buttonsText = this.game.cache.getJSON('buttonsText')['menu'];
        this.warningsText = this.game.cache.getJSON('warningsText')['menu'];

        this.game.sound.stopAll();

        Connection.initializeConnection(this.game);
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
     * This gets called when the user clicked on the start button.
     * It destroys the start button and, based on the config, creates buttons accordingly
     */
    private onClickStart(): void {
        this.menu.killChild(this.startButton);
        this.menu.killChild(this.tutorialButton);

        if (Connection.connection.hasSucceeded()) {
            // The connection is open, so it doesn't matter if we're in debug mode.
            this.onStart(false);
        } else if (DEBUG) {
            // Otherwise, if we actually are in debug, ask if we want to ignore the connect and start the game
            this.infoText = this.game.add.text(
                this.world.centerX + this.world.width * 0.2,
                this.world.centerY * 0.7,
                this.uiText['debugStart'],
                {
                    font: '14px Arial',
                    fill: 'white'
                }
            );

            // Set the anchor manually, since the built-in anchor.set does not work properly.
            this.infoText.anchor.x = Math.round(this.infoText.width * 0.5) / this.infoText.width;
            this.infoText.anchor.y = Math.round(this.infoText.height * 0.5) / this.infoText.height;

            // OK button
            this.okButton = this.menu.gameButton(
                this.world.centerX + this.world.width * 0.175,
                this.world.centerY * 0.8,
                this.buttonsText['confirm'],
                () => this.onStart(true),
            );

            // Cancel button, if this button is clicked, we show an error message by calling this.onCancel
            this.cancelButton = this.menu.gameButton(
                this.world.centerX + this.world.width * 0.25,
                this.world.centerY * 0.8,
                this.buttonsText['cancel'],
                () => this.onCancel(),
            );
        } else {
            // not in debug, but no connection was possible
            // TODO: retrieve websocket error message from connection.ts
            this.menu.textAlert(0, 100, Assets.Images.SpritesWarningIndicatorRed.getName(),
                this.warningsText['noConnection'], true);
            this.onCancel();
        }
    }

    /**
     * Destroys the OK Button and Cancel Button and creates a textAlert. It then calls the create function again.
     */
    private onCancel(): void {
        if (DEBUG) {
            this.infoText.destroy();
            this.menu.killChild(this.okButton);
            this.menu.killChild(this.cancelButton);
        }
        this.createMenu();
    }

    /**
     * Close the menu, sends the hash check for local storage and starts the game
     * @param withoutConnection if there is a correct connection set up.
     */
    private onStart(withoutConnection: boolean): void {
        this.menu.killAll();
        this.menu.destroy(true);

        if (DEBUG) {
            console.log('without Connection? ' + withoutConnection);
        }
        // send the hash of all the items in local storage for the game server to check
        let hash: number = 0;
        let sessionId: string = Connection.connection.getSessionId();
        let keys: string[] = Object.keys(localStorage);
        let i: number = keys.length;
        let keyCheck: boolean = false;
        while ( i-- ) {
          if (keys[i].match(sessionId) !== null)
            keyCheck = true;
        }
        if (keyCheck) {
            hash = this.makeHash();
        }
        Connection.connection.sendStartGame(hash);

        if (withoutConnection) {
            this.game.state.start('level', true, false, [path]);
        }
    }

    /**
     * General update function. This is called for every loop in the game loop.
     */
    public update(): void {
        super.update();

        if (Connection.connection.hasSucceeded()) {
            this.connectionInfoText.text = 'Connected';
        }

        this.menu.updateObject();
    }

    /**
     * Create the menu and all its buttons
     */
    private createMenu(): void {

        this.menu = new UIMenu(this.game);

        const background: Phaser.Sprite = this.menu.sprite(this.world.centerX,this.world.centerY,Assets.Images.SpritesBackground.getName());
        background.scale.set(1.3, 1.05);

        this.startButton = this.menu.gameButton(
            this.world.centerX - this.world.width * 0.1,
            this.world.centerY * 0.7,
            'START',
            () => this.onClickStart(),
        );

        this.startButton.scale.set(0.4, 0.3);

        this.exitButton = this.menu.gameButton(
            this.world.centerX - this.world.width * 0.1,
            this.world.centerY * 0.9,
            'EXIT',
            () => {
                if (!DEBUG) {
                    window.location.replace((<HTMLInputElement>document.getElementById('problemroute')).value);
                }
            }
        );

        this.exitButton.scale.set(0.4, 0.3);

        // Add the game logo, this.menu.sprite has an 0.5 anchor by default
        this.menu.sprite(this.world.centerX, this.world.centerY * 0.75, Assets.Images.SpritesLogo.getName());


        this.tutorialButton = this.menu.gameButton(
            this.world.centerX - this.world.width * 0.1,
            this.world.centerY * 1.1,
            'TUTORIAL',
            () => { this.game.state.start('tutorial', true, false, [path]); }
        );

        this.tutorialButton.scale.set(0.4, 0.3);

        this.sfxButton = this.menu.switchButton(
            this.world.centerX - this.world.width * 0.1,
            this.world.centerY * 1.3,
            'SFX: On',
            'SFX: Off',
            (isOn: boolean) => SoundManager.switchSoundEffectsVolume(),
            SoundManager.getSoundEffectsVolume() !== 0
        );

        this.sfxButton.scale.set(0.4, 0.3);

        this.musicButton = this.menu.switchButton(
            this.world.centerX - this.world.width * 0.1,
            this.world.centerY * 1.5,
            'Music: On',
            'Music: Off',
            (isOn: boolean) => SoundManager.switchMusicVolume(),
            SoundManager.getMusicVolume() !== 0
        );

        this.musicButton.scale.set(0.4, 0.3);
    }

    public makeHash(): number {

      // Get all the locally stored information in one concattenated string
      let totalString: string = '';
      let keys: string[] = Object.keys(localStorage);
      let i: number = keys.length;

      while ( i-- ) {
        if (keys[i].match(Connection.connection.getSessionId()) !== null)
        totalString = totalString + localStorage.getItem(keys[i]);
      }

      // convert this string to a hash value
      let hash: number = 0;
      for (i = 0; i < totalString.length; i++) {
        let chr: number   = totalString.charCodeAt(i);
        hash  = ((hash << 5) - hash) + chr;
        hash |= 0; // Convert to 32bit integer
      }
      return hash;
    }
}
