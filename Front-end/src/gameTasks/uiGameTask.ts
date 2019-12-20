/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University
 * within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
import * as Assets from '../assets';
import ConditionScanner from '../gameObjects/conditionScanner';
import CircleTower from '../gameObjects/towers/circleTower';
import MultishotTower from '../gameObjects/towers/multishotTower';
import PierceTower from '../gameObjects/towers/pierceTower';
import ShootingMode from '../gameObjects/towers/shootingMode';
import SingleshotTower from '../gameObjects/towers/singleshotTower';
import SniperTower from '../gameObjects/towers/sniperTower';
import Tower from '../gameObjects/towers/tower';
import Level from '../states/level';
import LevelState from '../states/levelState';
import Menu from '../userInterface/menu';
import MenuButton from '../userInterface/menuButton';
import MenuSlideButton from '../userInterface/menuSlideButton';
import MenuTextAlert from '../userInterface/menuTextAlert';
import ShopButton from '../userInterface/shopButton';
import SoundManager from '../soundManager';
import GameTask from './gameTask';

const buttonScale: number = 0.25;
const emptyButtonWidth: number = Assets.Spritesheets.SpritesheetsEmptyButton3691923.getFrameWidth() * buttonScale;
const emptyButtonHeight: number = Assets.Spritesheets.SpritesheetsEmptyButton3691923.getFrameHeight() * buttonScale;

export default class UiGameTask extends GameTask {
    private lastHealth: number;
    private lastMoney: number;
    private lastScore: number;
    private lastSelectedTower: Tower;
    private lastDescription: string;
    private lastHint: string;
    private lastPreFeedback: string;
    private lastPostFeedback: string;
    private progression: number;

    // UI - TextAlerts
    private moneyDisplay: MenuTextAlert;

    // UI - SpriteAlerts
    private healthDisplay: Phaser.Sprite;
    private pauseDisplay: Phaser.Sprite;
    private progressionDisplay: Phaser.Sprite;

    // UI - Tower Info
    private towerInfo: Phaser.Text;
    private towerDamage: Phaser.Sprite;
    private towerDamageLabel: Phaser.Text;
    private towerRange: Phaser.Sprite;
    private towerRangeLabel: Phaser.Text;
    private towerShootingModeLabel: Phaser.Text;
    private towerSpeed: Phaser.Sprite;
    private towerSpeedLabel: Phaser.Text;
    private boostTower: MenuButton;
    private effectTower: MenuButton;
    private shootingModeTower: MenuButton;
    private towerShop: Set<ShopButton>;

    // UI - Containers and Buttons
    private playingMenu: Menu; // Playing menu objects are only enable in LevelState.PLAYING.
    private allStatesMenu: Menu; // All states menu objects are always enabled.

    private cancelPlacementButton: MenuButton;
    private slideUpPre: MenuSlideButton;
    private slideUpDesc: MenuSlideButton;
    private slideUpPost: MenuSlideButton;

    private betweenWaveTimeLeftDisplay: Phaser.Text;

    private uiText: object = null;
    private warningsText: object = null;

    private towerToolTip: object = null;
    private hotkeys: object;

    /**
     * Initialize the UI. This done by initializing the separate containers, icons, text alerts, slide up bars and UI sprites
     * @param level The current level to initialize the UI in
     */
    public initialize(level: Level): void {
        super.initialize(level);

        this.playingMenu = new Menu(this.level.game);
        this.allStatesMenu = new Menu(this.level.game);

        this.progression = 1.00;

        this.uiText = this.level.game.cache.getJSON('uiText')['uiGameTask'];
        this.warningsText = this.level.game.cache.getJSON('warningsText')['uiGameTask'];
        const uiSettings: object = this.level.game.cache.getJSON('uiSettings');
        const buttonsText: object = this.level.game.cache.getJSON('buttonsText')['uiGameTask'];
        this.towerToolTip = this.level.game.cache.getJSON('towersText');

        const moneySprite: string = Assets.Images.SpritesIconMoney.getName();
        const overlayRight: string = Assets.Images.SpritesUIOverlayRight.getName();
        const uiSide: string = Assets.Images.SpritesUIShopSignSmall.getName();

        // Containers

        let basisRightContainer: Phaser.TileSprite = this.playingMenu.textureContainer(
            this.level.world.width * 0.82, this.level.world.height * 0.075,
            Assets.Images.SpritesUIBasisRight.getName(),
            this.level.world.width * uiSettings['rightSideUI'],
            this.level.world.height * 0.85
        );
        basisRightContainer.scale.set((this.level.world.width * uiSettings['rightSideUI']) / 71, this.level.world.height * 0.85 / 233);

        this.playingMenu.textureContainer(
            this.level.world.width * 0.82, this.level.world.height * 0.075 + 8 * ((this.level.world.height * 0.85) / 233),
            overlayRight,
            this.level.world.width * uiSettings['rightSideUI'],
            this.level.world.height * 0.93
        );

        const topContainer: Phaser.TileSprite = this.playingMenu.textureContainer(
            0, 0,
            Assets.Images.SpritesUITop.getName(),
            this.level.world.width,
            this.level.world.height * 0.095
        );
        topContainer.tileScale.set(1, topContainer.height / 29);


        const rightUISideTopContainer: Phaser.TileSprite = this.playingMenu.textureContainer(
            this.level.world.width * 0.82, this.level.world.height * 0.075 + 5 * (this.level.world.height * 0.85 / 233),
            uiSide,
            this.level.world.width * uiSettings['rightSideUI'],
            32
        );
        rightUISideTopContainer.tileScale = new Phaser.Point(this.level.world.width * uiSettings['rightSideUI'] / 68, 1);

        const topLabel: Phaser.Text = new Phaser.Text(this.level.game, this.level.world.width * 0.82,
            this.level.world.height * 0.075 + 5 * (this.level.world.height * 0.85 / 233),
            'Tower Info', { font: '26px Arial', fill: '#47B744' }
        );
        topLabel.x += ((this.level.world.width * uiSettings['rightSideUI']) - topLabel.width) / 2;
        this.level.game.add.existing(topLabel);

        this.towerInfo = new Phaser.Text(this.level.game, this.level.world.width * 0.83,
            this.level.world.height * 0.15 + this.level.world.height * 0.85 / 233, '', { font: '20px Arial', fill: 'white' }
        );
        this.level.game.add.existing(this.towerInfo);
        this.towerRange = new Phaser.Sprite(this.level.game, this.level.world.width * 0.83,
            this.level.world.height * 0.25 + this.level.world.height * 0.85 / 233, Assets.Images.SpritesIconTowerRange.getName());
        this.level.game.add.existing(this.towerRange);
        this.towerRange.visible = false;
        this.towerRangeLabel = new Phaser.Text(this.level.game, this.level.world.width * 0.86,
            this.level.world.height * 0.25 + this.level.world.height * 0.85 / 233, '', { font: '26px Arial', fill: 'white' }
        );
        this.level.game.add.existing(this.towerRangeLabel);
        this.towerDamage = new Phaser.Sprite(this.level.game, this.level.world.width * 0.83,
            this.level.world.height * 0.3 + this.level.world.height * 0.85 / 233, Assets.Images.SpritesIconTowerDamage.getName());
        this.level.game.add.existing(this.towerDamage);
        this.towerDamage.visible = false;
        this.towerDamageLabel = new Phaser.Text(this.level.game, this.level.world.width * 0.86,
            this.level.world.height * 0.3 + this.level.world.height * 0.85 / 233, '', { font: '26px Arial', fill: 'white' }
        );
        this.level.game.add.existing(this.towerDamageLabel);
        this.towerSpeed = new Phaser.Sprite(this.level.game, this.level.world.width * 0.83,
            this.level.world.height * 0.35 + this.level.world.height * 0.85 / 233, Assets.Images.SpritesIconTowerSpeed.getName());
        this.level.game.add.existing(this.towerSpeed);
        this.towerSpeed.visible = false;
        this.towerSpeedLabel = new Phaser.Text(this.level.game, this.level.world.width * 0.86,
            this.level.world.height * 0.35 + this.level.world.height * 0.85 / 233, '', { font: '26px Arial', fill: 'white' }
        );
        this.level.game.add.existing(this.towerSpeedLabel);

        this.towerShootingModeLabel = new Phaser.Text(this.level.game, this.level.world.width * 0.83,
            this.level.world.height * 0.15 + 20 * (this.level.world.height * 0.85 / 233), '', { font: '14px Arial', fill: 'white' }
        );
        this.level.game.add.existing(this.towerShootingModeLabel);

        this.boostTower = this.playingMenu.gameButton(
            this.level.world.width * 0.85,
            this.level.world.height * 0.43 + 8 * (this.level.world.height * 0.85 / 233),
            buttonsText['boostTower'],
            () => {
                if (this.level.getPlacingTower()) {
                    return;
                }
                this.upgradeTowerClick(false);

            }
        );
        this.boostTower.visible = false;


        this.effectTower = this.playingMenu.gameButton(
            this.level.world.width * 0.85 + emptyButtonWidth - 15,
            this.level.world.height * 0.43 + 8 * (this.level.world.height * 0.85 / 233),
            buttonsText['effectTower'],
            () => {
                if (this.level.getPlacingTower()) {
                    return;
                }
                this.upgradeTowerClick(true);
            }
        );

        this.effectTower.visible = false;

        this.shootingModeTower = this.playingMenu.gameButton(
            this.level.world.width * 0.85 + (emptyButtonWidth - 15) * 2,
            this.level.world.height * 0.43 + 8 * (this.level.world.height * 0.85 / 233),
            buttonsText['shootingModeTower'],
            () => {
                if (this.level.getPlacingTower()) {
                    return;
                }

                const prevMode: ShootingMode = this.level.getSelectedTower().getShootingMode();
                if (prevMode === ShootingMode.Closest) {
                    this.level.getSelectedTower().setShootingMode(ShootingMode.First);
                } else {
                    this.level.getSelectedTower().setShootingMode(prevMode + 1);
                }
            }
        );

        this.shootingModeTower.visible = false;

        let rightUISideBottomContainer: Phaser.TileSprite = this.playingMenu.textureContainer(
            this.level.world.width * 0.82, this.level.world.height * 0.5,
            uiSide,
            this.level.world.width * uiSettings['rightSideUI'],
            32
        );
        rightUISideBottomContainer.tileScale = new Phaser.Point(this.level.world.width * uiSettings['rightSideUI'] / 68, 1);

        const bottomLabel: Phaser.Text = new Phaser.Text(this.level.game, this.level.world.width * 0.82,
            this.level.world.height * 0.50,
            'Tower Shop', { font: '26px Arial', fill: '#47B744' }
        );
        bottomLabel.x += ((this.level.world.width * uiSettings['rightSideUI']) - topLabel.width) / 2;
        this.level.game.add.existing(bottomLabel);

        // ########### Sprite + Text Displays ############
        this.moneyDisplay = this.playingMenu.textAlert(this.level.world.width * 0.012, this.level.world.height * 0.015, moneySprite,
            this.level.getMoney().toString(), false, 0, 1.5, 16);

        // Sprite + Bar Displays
        this.pauseDisplay = this.level.game.add.sprite(this.level.world.width * 0.2, 10,
            Assets.Spritesheets.SpritesheetsPauseIcon323210.getName(), this.level.world.height * 0.015);
        this.pauseDisplay.scale.set(1.7, 1.7);
        this.healthDisplay = this.level.game.add.sprite(this.level.world.width * 0.4, 0,
            Assets.Spritesheets.SpritesheetsHealthIcon323210.getName(), this.level.world.height * 0.015);
        this.healthDisplay.scale.set(2, 2);
        this.progressionDisplay = this.level.game.add.sprite(this.level.world.width * 0.6, 10,
            Assets.Images.SpritesIconScore.getName(), this.level.world.height * 0.015);
        this.progressionDisplay.scale.set(1.5, 1.5);

        // Text Display
        const style: Phaser.PhaserTextStyle = { font: 'Arial', fontSize: 12, fill: 'white' };

        this.initTowerToPlaceSprites();

        this.betweenWaveTimeLeftDisplay = this.level.game.add.text(this.level.world.width * 0.83,
            this.level.world.height - emptyButtonHeight * 0.75 - 35, '', style);

        // Buttons
        const startWaveButton: MenuButton = this.playingMenu.gameButton(
            this.level.world.width - emptyButtonWidth * 0.5,
            this.level.world.height - emptyButtonHeight * 0.5,
            buttonsText['startWave'],
            () => this.level.startWave()
        );

        // Hints button
        this.playingMenu.gameButton(
            this.level.world.width - emptyButtonWidth * 0.5 - 75,
            this.level.world.height - emptyButtonHeight * 0.5,
            'Hints',
            () => this.level.getHintScreen().openScreen()
        );

        // Delete Tower Button
        this.playingMenu.gameButton(
            startWaveButton.x - emptyButtonWidth * 1.63,
            startWaveButton.y,
            'Sell Tower',
            () => this.removeTower()
        );

        this.cancelPlacementButton = this.playingMenu.button(
            this.level.world.width * 0.91,
            this.level.world.height - emptyButtonHeight * 4,
            Assets.Spritesheets.SpritesheetsCrossButton1001002.getName(),
            null,
            new Phaser.Point(2.2, 2.5),
            () => this.cancelPlacingTower(),
            null,
            1, 0, 2
        );
        this.cancelPlacementButton.alpha = 0.7;
        this.cancelPlacementButton.visible = false;

        this.allStatesMenu.switchButton(
            this.level.world.width - emptyButtonWidth * 0.5 - 150,
            emptyButtonHeight * 0.5 + 15,
            'SFX: On',
            'SFX: Off',
            (isOn: boolean) => SoundManager.switchSoundEffectsVolume(),
            SoundManager.getSoundEffectsVolume() !== 0
        );

        this.allStatesMenu.switchButton(
            this.level.world.width - emptyButtonWidth * 0.5 - 75,
            emptyButtonHeight * 0.5 + 15,
            'Music: On',
            'Music: Off',
            (isOn: boolean) => SoundManager.switchMusicVolume(),
            SoundManager.getMusicVolume() !== 0
        );

        this.allStatesMenu.gameButton(
            this.level.world.width - emptyButtonWidth * 0.5,
            emptyButtonHeight * 0.5 + 15,
            'Exit',
            () => this.level.game.state.start('menu')
        );

        this.lastHealth = this.level.getHealth();
        this.lastMoney = this.level.getMoney();
        this.lastScore = this.level.getScore();

        this.lastDescription = '';
        this.lastHint = '';
        this.lastPreFeedback = '';
        this.lastPostFeedback = '';
        this.initBottomSliders();

        this.level.getUIScreenRenderGroup().add(this.playingMenu);

        this.hotkeys = this.level.cache.getJSON('hotkeys')['playing'];

        this.level.captureKey(Phaser.KeyCode[this.hotkeys['sliderNext']]);
        this.level.captureKey(Phaser.KeyCode[this.hotkeys['sliderPrevious']]);
    }

    /**
     * Separate method for the initialization of the bottom 'slide up/tray' elements
     */
    private initBottomSliders(): void {
        // Small workaround to get the width and height of a sprite that has not been created yet
        const slideUpLong: string = Assets.Images.SpritesSlideUpButtonLong.getName();
        const slideUpWidth: number = new Phaser.Sprite(this.level.game, 0, 0, slideUpLong).width;

        // ########## Slide Up UI Elements ###########
        this.slideUpPre = this.allStatesMenu.slideButton(this.level,
            slideUpWidth * 1.52,
            this.level.world.height + emptyButtonHeight * 0.25,
            'Pre',
            ''
        );
        this.level.setSlideUpPre(this.slideUpPre);

        this.slideUpDesc = this.allStatesMenu.slideButton(this.level,
            slideUpWidth * 0.51,
            this.level.world.height + emptyButtonHeight * 0.25,
            'Desc',
            this.uiText['description'] + ': ' + this.level.getDescription()
          );
        this.level.setSlideUpDesc(this.slideUpDesc);

        this.slideUpPost = this.allStatesMenu.slideButton(this.level,
            slideUpWidth * 2.53,
            this.level.world.height + emptyButtonHeight * 0.25,
            'Post',
            ''
        );
        this.level.setSlideUpPost(this.slideUpPost);
    }

    /**
     * Update UI aspects like Health, Money, Score, Tower info and condition descriptions
     */
    public update(): void {
        if (this.getInputEnabled()) {
            this.playingMenu.updateObject();
            this.allStatesMenu.updateObject();
        }

        const score: number = this.level.getScore();

        this.updateMoney();
        this.updateHealth();

        // Update visual indicators for health, pausetime, and progression
        this.pauseDisplay.frame = 9 - Math.floor(((this.level.getRemainingPauseTime() / this.level.getMaxPauseTime()) * 10));

        // Show the cancelButton or the towerShop
        if (this.level.getPlacingTower()) {
            this.cancelPlacementButton.visible = true;
            this.towerShop.forEach(element => element.setVisible(false));
        } else {
            this.cancelPlacementButton.visible = false;
            this.towerShop.forEach(element => element.setVisible(true));
        }

        // Update the visual for the amount of time between waves left
        if (this.level.getIsWaveEnded()) {
            const timeleft: number = Math.round(this.level.getTimeBetweenWavesLeft() / 1000);

            this.betweenWaveTimeLeftDisplay.setText(this.uiText['betweenWaveTime'] + ': ' + timeleft);
            this.betweenWaveTimeLeftDisplay.visible = true;
        } else {
            this.betweenWaveTimeLeftDisplay.visible = false;
        }

        // Update other aspects
        this.updateSelectedTowerUI();
        this.updateDescription();
        this.updateHints();
        this.updatePrePostDisplay();
    }

    /**
     * Callback method for a mouse down event. Calls the mouse down event for the current menu
     * @param pointer the mouse Phaser.Pointer
     */
    public onMouseDown(pointer: Phaser.Pointer): boolean {
        if (this.level.getLevelState() === LevelState.PLAYING && this.playingMenu.onMouseDown(pointer)) {
            return true;
        }

        return this.allStatesMenu.onMouseDown(pointer);
    }

    /**
    * Callback method for a mouse up event. Calls the mouse up event for the current menu
    * @param pointer the mouse Phaser.Pointer
    */
    public onMouseUp(pointer: Phaser.Pointer): boolean {
        if (this.level.getLevelState() === LevelState.PLAYING && this.playingMenu.onMouseUp(pointer)) {
            return true;
        }

        return this.allStatesMenu.onMouseUp(pointer);
    }

    /**
     * Updates the UI aspect for the selected tower
     */
    private updateSelectedTowerUI(): void {
        const currentTower: Tower = this.level.getSelectedTower();
        if (this.lastSelectedTower !== currentTower ||
            this.level.getSelectedTower() && this.lastSelectedTower.getTowerType() === currentTower.getTowerType()) {

            this.boostTower.visible = false;
            this.effectTower.visible = false;
            this.shootingModeTower.visible = false;

            // If a tower is selected, display its information, otherwise don't
            if (this.level.getSelectedTower()) {
                this.towerInfo.text = this.level.getSelectedTower().getTowerType().replace(';', '\n');
                this.towerRangeLabel.text = (Math.round(currentTower.getRange() * 100) / 100).toString();
                this.towerDamageLabel.text = (Math.round(currentTower.getDamage() * 100) / 100).toString();
                this.towerSpeedLabel.text = (Math.round(currentTower.getAttackSpeed() * 100) / 100).toString();

                // Display shooting mode if this tower is not a multishot or a circletower
                if (!(currentTower instanceof MultishotTower || currentTower instanceof CircleTower)) {
                    this.towerShootingModeLabel.text = 'Current Shooting Mode: ' + currentTower.getShootingModeText();
                    this.shootingModeTower.visible = true;
                }

                // If the tower is not upgraded, display its upgrade buttons
                if (!this.level.getSelectedTower().getUpgraded()) {
                    this.boostTower.visible = true;
                    this.effectTower.visible = true;
                    this.towerDamage.visible = true;
                    this.towerRange.visible = true;
                    this.towerSpeed.visible = true;

                    const pointer: Phaser.Point = this.level.input.activePointer.position;

                    // If we are not blockbuilding, show desciprions of upgrades on hover
                    if (this.level.getLevelState() === LevelState.PLAYING) {
                        const boostToolTip: string = currentTower.getBoostDescription();
                        if (boostToolTip && this.boostTower.getBounds().contains(pointer.x, pointer.y)) {
                            this.level.getTooltip().show(boostToolTip, this.boostTower, 0);
                        }

                        const effectToolTip: string = currentTower.getEffectDescription();
                        if (effectToolTip && this.effectTower.getBounds().contains(pointer.x, pointer.y)) {
                            this.level.getTooltip().show(effectToolTip, this.effectTower, 0);
                        }

                        const modeToolTip: string = this.level.cache.getJSON('towersText')['shootingMode'];
                        if (modeToolTip !== null && this.shootingModeTower.getBounds().contains(pointer.x, pointer.y)
                            && !(currentTower instanceof MultishotTower || currentTower instanceof CircleTower)) {
                            this.level.getTooltip().show(modeToolTip, this.shootingModeTower, 0);
                        }
                    }
                }
            } else {
                this.towerDamage.visible = false;
                this.towerRange.visible = false;
                this.towerSpeed.visible = false;
                this.towerInfo.text = '';
                this.towerRangeLabel.text = '';
                this.towerDamageLabel.text = '';
                this.towerSpeedLabel.text = '';
                this.towerShootingModeLabel.text = '';
            }

            this.lastSelectedTower = currentTower;
        }
    }

    /**
     * Update description within the UI.
     */
    private updateDescription(): void {
        const description: string = this.level.getDescription();

        // If the description hasn't changed, do nothing
        // Otherwise, give a notification
        if (description === this.lastDescription) {
            return;
        } else {
            this.playingMenu.textAlert(
                0, (this.level.world.height / 11.5) * 2,
                Assets.Images.SpritesWarningIndicatorRed.getName(),
                this.warningsText['descriptionChanged'],
                true,
                this.level.world.height / 11.5
            );
        }

        this.lastDescription = description;

        this.slideUpDesc.setBodyText(this.uiText['description'] + ': ' + description);
    }

    /**
     * This method mainly exists to give a notification for when a new hint is received
     */
    public updateHints(): void {
        const hint: string = this.level.getHintScreen().getHintsString();

        // If the hint hasn't changed or is empty, do nothing
        // Otherwise, give a notification
        if (hint === this.lastHint || !hint) {
            return;
        } else {
            this.playingMenu.textAlert(
                0, (this.level.world.height / 11.5) * 2,
                Assets.Images.SpritesWarningIndicatorRed.getName(),
                this.warningsText['hintChanged'],
                true,
                this.level.world.height / 11.5
            );
        }

        this.lastHint = hint;
    }

    /**
     * Change the input recognition based on the current level state
     * @param levelState the current level state
     */
    public changeState(levelState: LevelState): void {
        this.setInputEnabled(true);
        this.setUpdateEnabled(true);
    }

    /**
     * If the towers gets upgraded, change the visuals as well for the tower info.
     * @param isEffect if the tower upgrade is an effect.
     */
    private upgradeTowerClick(isEffect: boolean): void {
        const towerToUpgrade: Tower = this.level.getSelectedTower();

        const oldRange: number = towerToUpgrade.getRange();
        const oldDamage: number = towerToUpgrade.getDamage();
        const oldAS: number = towerToUpgrade.getAttackSpeed();

        towerToUpgrade.upgradeTower(isEffect);

        const newRange: number = towerToUpgrade.getRange();
        const newDamage: number = towerToUpgrade.getDamage();
        const newAS: number = towerToUpgrade.getAttackSpeed();

        this.updateLabel(this.towerRangeLabel, oldRange, newRange);
        this.updateLabel(this.towerDamageLabel, oldDamage, newDamage);
        this.updateLabel(this.towerSpeedLabel, oldAS, newAS);
    }

    /**
     * Change the visual of pre and post condition displays.
     * X,Y setting happens here because Scanners don't exist yet on initialization.
     */
    private updatePrePostDisplay(): void {
        const preF: string = this.level.getPreFeedback();
        const postF: string = this.level.getPostFeedback();

        // If the preF is '', then there is no feedback as the precondition is correct.
        // If the preF is undefined, then it hasn't been initialized as the level has just started.
        if (!preF || this.level.getIsWaveStarted()) {
            this.slideUpPre.setBodyText(this.uiText['preconditionText'] + ': ' + this.level.getPreScanner().getCode());
        } else {
            this.slideUpPre.setBodyText(this.uiText['preconditionText'] + ': ' + this.level.getPreScanner().getCode()
                + '\n\n' + this.uiText['preconditionFeedback'] + ': ' + preF);
        }

        // If the postF is '', then there is no feedback as the precondition is correct.
        // If the postF is undefined, then it hasn't been initialized as the level has just started.
        if (!postF || this.level.getIsWaveStarted()) {
            this.slideUpPost.setBodyText(this.uiText['postconditionText'] + ': ' + this.level.getPostScanner().getCode());
        } else {
            this.slideUpPost.setBodyText(this.uiText['postconditionText'] + ': ' + this.level.getPostScanner().getCode()
                + '\n\n' + this.uiText['postconditionFeedback'] + ': ' + postF);
        }

        // Feedback has changed, give an alert.
        if ((preF && preF !== this.lastPreFeedback) || (postF && postF !== this.lastPostFeedback)) {
            this.playingMenu.textAlert(
                0, (this.level.world.height / 11.5) * 2,
                Assets.Images.SpritesWarningIndicatorRed.getName(),
                this.warningsText['feedbackChanged'],
                true,
                this.level.world.height / 11.5
            );
            this.lastPreFeedback = preF;
            this.lastPostFeedback = postF;
        }
    }

    /**
     * Update the visuals for the money.
     * This also shows a little tween indicating that the player gained / lost money.
     */
    private updateMoney(): void {
        const money: number = this.level.getMoney();
        // If the money didn't change, do nothing.
        if (money ===  this.lastMoney) {
            return;
        }
        this.moneyDisplay.setText(money.toString());
        const newPos: Phaser.Point = new Phaser.Point(this.moneyDisplay.x + this.moneyDisplay.width + this.level.world.width / 17,
            this.moneyDisplay.y + this.moneyDisplay.height / 2);

        // Make a tween, and add some text to this. Both the direction and colour are based on if you gained money or lost it.
        const tweenSprite: Phaser.Sprite = new Phaser.Sprite(this.level.game, newPos.x, newPos.y,
            Assets.Images.SpritesIconMoney.getName());
            tweenSprite.addChild(new Phaser.Text(this.level.game, tweenSprite.width, 0,
                String(money - this.lastMoney),  {fill: (money < this.lastMoney) ? 'red' : 'green'}));
            this.level.game.add.existing(tweenSprite);
        const dY: number = (money < this.lastMoney) ? 10 : -10;

        this.level.game.add.tween(tweenSprite).to({ x: newPos.x, y: newPos.y + dY, alpha: 0 },
            1000, Phaser.Easing.Linear.None, true, 0, 0, false).onComplete.add( (sprite: Phaser.Sprite) => sprite.destroy());
        this.lastMoney = money;
    }

    /**
     * Update the visuals for the health.
     * This also shows a little tween indicating that the player gained / lost health.
     */
    private updateHealth(): void {
        const health: number = this.level.getHealth();
        if (health === this.lastHealth) {
            return;
        }
        this.healthDisplay.frame = 9 - Math.floor(((this.level.getHealth() / this.level.getFullHealth()) * 10));

        const newPos: Phaser.Point = new Phaser.Point(this.healthDisplay.x + this.healthDisplay.width,
            this.healthDisplay.y + this.healthDisplay.height / 2);
        // Make a tween, and add some text to this. Both the direction and colour are based on if you gained health or lost it.
        const tweenSprite: Phaser.Sprite = new Phaser.Sprite(this.level.game, newPos.x, newPos.y,
            Assets.Spritesheets.SpritesheetsHealthIcon323210.getName(), 0);
            tweenSprite.addChild(new Phaser.Text(this.level.game, tweenSprite.width, 0,
                String((health < this.lastHealth) ? '-' : '+' ),  {fill: (health < this.lastHealth) ? 'red' : 'green'}));
            this.level.game.add.existing(tweenSprite);
        const dY: number = (health < this.lastHealth) ? 10 : -10;

        this.level.game.add.tween(tweenSprite).to({ x: newPos.x, y: newPos.y + dY, alpha: 0 },
            1000, Phaser.Easing.Linear.None, true, 0, 0, false).onComplete.add( (sprite: Phaser.Sprite) => sprite.destroy());

        this.lastHealth = health;
    }

    /**
     * If a tower gets upgraded, then all the labels of the stats that are in the towerinfo part can have a tween.
     */
    private updateLabel(label: Phaser.Text, oldStat: number, newStat: number): void {
        if (oldStat === newStat) {
            return;
        }
        const newPos: Phaser.Point = new Phaser.Point(label.x + label.width + 20,
            label.y + label.height / 2 - 10);

        // Make a tween, based on if the stat increased or decreased.
        const tweenText: Phaser.Text = new Phaser.Text(this.level.game, newPos.x, newPos.y,
            (oldStat > newStat) ? '-' : '+',  {fill: (oldStat > newStat) ? 'red' : 'green'});
            this.level.game.add.existing(tweenText);
        const dY: number = (oldStat > newStat) ? 10 : -10;

        this.level.game.add.tween(tweenText).to({ x: newPos.x, y: newPos.y + dY, alpha: 0 },
            1000, Phaser.Easing.Linear.None, true, 0, 0, false).onComplete.add( (text: Phaser.Text) => text.destroy());

    }

    /**
     * Handle the key event.
     * @param key The key code of the pressed key
     */
    public onKeyPressed(key: number): boolean {
        switch (key) {
            case Phaser.KeyCode[this.hotkeys['removeObject1']]:         // R
            case Phaser.KeyCode[this.hotkeys['removeObject2']]:         // DELETE
                this.removeTower();
                return true;
            case Phaser.KeyCode[this.hotkeys['cancelPlacing']]:         // ESC
                this.cancelPlacingTower();
                return true;
            case Phaser.KeyCode[this.hotkeys['sliderNext']]:       // D
                if(this.slideUpDesc.getActive())
                  this.slideUpDesc.changePageIndex(true);
                if(this.slideUpPre.getActive())
                  this.slideUpPre.changePageIndex(true);
                if(this.slideUpPost.getActive())
                  this.slideUpPost.changePageIndex(true);
                return true;
            case Phaser.KeyCode[this.hotkeys['sliderPrevious']]:   // A
                if(this.slideUpDesc.getActive())
                  this.slideUpDesc.changePageIndex(false);
                if(this.slideUpPre.getActive())
                  this.slideUpPre.changePageIndex(false);
                if(this.slideUpPost.getActive())
                  this.slideUpPost.changePageIndex(false);
                return true;
        }
    }

    /**
     * Initializes all the different towers with the same position and their corresponding sprites
     * and adds them to the Phaser.Group of the UI.Menu
     */
    private initTowerToPlaceSprites(): void {
        const x: number = this.level.world.width * 0.84;
        const y: number = this.level.world.height;
        const towerSettings: object = this.level.game.cache.getJSON('towerSettings');

        this.towerShop = new Set<ShopButton>();

        // Initialize each item in the towershop
        this.towerShop.add(this.playingMenu.towerButton(this.level, x, y - emptyButtonHeight * 6,
            Assets.Images.SpritesIconSingle.getName(), 'Singleshot Tower',
            towerSettings['Singleshot tower; Default'],
            () => this.placeTower(new SingleshotTower(this.level, x, y - emptyButtonHeight * 6)),
            this.towerToolTip['singleShotTower']['tooltip'].join('\n')));

        this.towerShop.add(this.playingMenu.towerButton(this.level, x, y - emptyButtonHeight * 5,
            Assets.Images.SpritesIconCircle.getName(), 'Circle Tower',
            towerSettings['Circle tower; Default'],
            () => this.placeTower(new CircleTower(this.level, x, y - emptyButtonHeight * 5)),
            this.towerToolTip['circleTower']['tooltip'].join('\n')));

        this.towerShop.add(this.playingMenu.towerButton(this.level, x, y - emptyButtonHeight * 4,
            Assets.Images.SpritesIconMulti.getName(), 'Multishot Tower',
            towerSettings['Multishot tower; Default'],
            () => this.placeTower(new MultishotTower(this.level, x, y - emptyButtonHeight * 4)),
            this.towerToolTip['multiShotTower']['tooltip'].join('\n')));

        this.towerShop.add(this.playingMenu.towerButton(this.level, x, y - emptyButtonHeight * 3,
            Assets.Images.SpritesIconPierce.getName(), 'Pierce Tower',
            towerSettings['Pierce tower; Default'],
            () => this.placeTower(new PierceTower(this.level, x, y - emptyButtonHeight * 3)),
            this.towerToolTip['pierceTower']['tooltip'].join('\n')));

        this.towerShop.add(this.playingMenu.towerButton(this.level, x, y - emptyButtonHeight * 2,
            Assets.Images.SpritesIconSniper.getName(), 'Sniper Tower',
            towerSettings['Sniper tower; Default'],
            () => this.placeTower(new SniperTower(this.level, x, y - emptyButtonHeight * 2)),
            this.towerToolTip['sniperTower']['tooltip'].join('\n')));
    }

    /**
     * When a tower button has been clicked, create a tower to hover with the mouse to signify where and what tower will be placed.
     * @param tower The tower to be placed
     */
    private placeTower(tower: Tower): void {
        const placingTower: Tower = this.level.getPlacingTower();
        if (placingTower) {
            this.cancelPlacingTower();
        } else {
            this.level.game.add.existing(tower);
            this.level.setPlacingTower(tower);

            this.level.getGameRenderGroup().add(tower);

            tower.alpha = 0.65;
        }
    }

    /**
     * Cancels the current placing of a tower.
     */
    private cancelPlacingTower(): void {
        const placingTower: Tower = this.level.getPlacingTower();

        if (placingTower) {
            placingTower.destroy();

            this.level.setPlacingTower(null);
            this.level.setSelectedTower(null);
            this.level.getSelectedTowerGraphics().clear();

            if (this.level.getTooltip().getObject() === placingTower) {
                this.level.getTooltip().hide();
            }
        }
    }

    /**
     * @inheritDoc
     */
    public tryCommand(command: string, args: string): boolean {
        switch (command) {
            case 'progression':
                this.handleProgression(args);
                return true;
            case 'warning':
                this.handleWarning(args);
                return true;
            default:
                return false;
        }
    }

    /**
     * If the command 'progression' is called:
     * Set how far the player has progressed in the current level.
     * @param args the data received from the server.
     */
    private handleProgression(args: string): void {
        this.progression = parseInt(args);
    }


    private handleWarning(args: string): void{
        this.playingMenu.textAlert(
            0, (this.level.world.height / 11.5) * 2,
            Assets.Images.SpritesWarningIndicatorRed.getName(),
            args,
            true,
            this.level.world.height / 11.5
        );
    }

    /**
    * Removes the tower the player has selected.
    */
    private removeTower(): void {
        const towerToRemove: Tower = this.level.getSelectedTower();
        const placingTower: Tower = this.level.getPlacingTower();
        if (towerToRemove && towerToRemove !== placingTower) {
            // Perform what is necessary for the removal of this specific tower (refunds, for instance).
            towerToRemove.removeTower();

            // If the tower was upgraded, remove the upgrade sprite as well.
            if (towerToRemove.getUpgraded()) {
                towerToRemove.getUpgradeVisual().destroy();
            }

            // For all the towers in range of the tower to remove
            towerToRemove.getTowersInRange().forEach(tower => {
                // Get all their towers in range, and remove the tower to remove out of the set.
                tower.getTowersInRange().delete(towerToRemove);
            });

            const gridPos: Phaser.Point = this.level.getGrid().screenSpaceToGridSpace(towerToRemove.position.x, towerToRemove.position.y);

            // Afterwards, remove it from the level.
            this.level.getGrid().removeHitbox(gridPos.x, gridPos.y, towerToRemove.getHitbox());
            this.level.getTowers().delete(towerToRemove);
            this.level.setSelectedTower(null);

            towerToRemove.destroy();

            if (this.level.getTooltip().getObject() === towerToRemove) {
                this.level.getTooltip().hide();
            }
        }
    }
}
