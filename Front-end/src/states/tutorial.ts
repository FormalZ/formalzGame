/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University
 * within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
import * as Assets from '../assets';
import MainGameTask from '../gameTasks/mainGameTask';
import Connection from '../connection';
import BlockCreator from '../gameObjects/blockBuilding/blockCreator';
import BeginBlock from '../gameObjects/blockBuilding/blocks/beginBlock';
import Block from '../gameObjects/blockBuilding/blocks/block';
import RealBlock from '../gameObjects/blockBuilding/blocks/realBlock';
import RelationalBlock from '../gameObjects/blockBuilding/blocks/relationalBlock';
import VariableBlock from '../gameObjects/blockBuilding/blocks/variableBlock';
import Grid from '../gameObjects/grid/grid';
import FireWallSpark from '../gameObjects/sparks/fireWallSpark';
import Spark from '../gameObjects/sparks/spark';
import VirusSpark from '../gameObjects/sparks/virusSpark';
import MultishotTower from '../gameObjects/towers/multishotTower';
import SingleshotTower from '../gameObjects/towers/singleshotTower';
import Tower from '../gameObjects/towers/tower';
import Menu from '../userInterface/menu';
import TutorialHint from '../userInterface/tutorialHint';
import Level from './level';
import LevelState from './levelState';
import SoundManager from '../soundManager';

// TODO: this is declared in multiple classes, maybe make these global constants
const buttonScale: number = 0.25;
const emptyButtonWidth: number = Assets.Spritesheets.SpritesheetsEmptyButton3691923.getFrameWidth() * buttonScale;
const emptyButtonHeight: number = Assets.Spritesheets.SpritesheetsEmptyButton3691923.getFrameHeight() * buttonScale;

const tutorialJSON: string = Assets.JSON.LocalizationEnTutorialText.getName();

export default class Tutorial extends Level {
    private tutorialHints: TutorialHint[];
    private tutorialIndex: number;

    private speechBubble: Phaser.Text;

    private graphicsFill: Phaser.Graphics;
    private graphicsOutLine: Phaser.Graphics;

    private spark: Spark;

    private hotkeys: object;

    private menu: Menu;

    /**
     * Initialize the tutorial state, including cutting the connection to the server
     * and setting the description and conditions for the tutorial problem
     * @param args the optional arguments. Currently this only includes path data for when there is no connection
     */
    public init(args: any): void {
        super.init(args);

        this.isTutorial = true;

        // Make sure the tutorial runs without a server connection.
        Connection.connection.setLevel(this);
        Connection.connection.closeConnection(false);

        this.setDescription('Multiply a non-negative integer a by 2');
        this.getPreScanner().setWaveWeights([0.5, 0, 0, 0.5]);

        MainGameTask.mainTask.tryCommand('validPreTokens', '[[Variable a], [RelationalComparer >=], [Number 0]]');
        MainGameTask.mainTask.tryCommand('validPostTokens', '[[Variable a], [RelationalComparer >=], [Number 0]]');

        // Spawn a wave where the pre condition is correct, as this will be demonstrated in the tutorial.
        MainGameTask.mainTask.tryCommand('spawnWave', '10;[0.5, 0.0, 0.0, 0.5];[0.5, 0.5, 0.0, 0.0];20;80;500;[0.01, 0.01]');

        // This is necessary for getBounds() to work later when highlighting the positions of the Condition Scanners.
        this.getPreScanner().updateTransform();
        this.getPostScanner().updateTransform();

        this.setSparkSpeed(64);

        let beginBlock: Block;
        let relationalBlock: Block;
        let variableBlock: Block;
        let constBlock: Block;

        // TODO: move the JSON into the TutorialHint object so we don't have to deal with it here
        const json: object = this.game.cache.getJSON(tutorialJSON);

        const preBlockBuildingGridCenterX: number = this.getPreBlockBuildScreen().getGrid().getWidth() / 2;
        const preBlockBuildingGridCenterY: number = this.getPreBlockBuildScreen().getGrid().getHeight() / 2;

        this.tutorialHints = [
            // Introduction
            new TutorialHint(json['Welcome']),
            new TutorialHint(json['Purpose']),

            // Resources
            new TutorialHint(json['Resources']),
            new TutorialHint(json['Money'], [new PIXI.Rectangle(10, 5, 275, 64)]),
            new TutorialHint(json['Pause'], [new PIXI.Rectangle(285, 5, 265, 64)]),
            new TutorialHint(json['Health'], [new PIXI.Rectangle(545, 5, 275, 64)]),
            new TutorialHint(json['Score'], [new PIXI.Rectangle(820, 5, 275, 64)]),

            // UI
            new TutorialHint(json['Description'], [new PIXI.Rectangle(0, 620, 374, 145)], () => {
                this.getSlideUpDesc().slide();
            }),
            new TutorialHint(json['Scanners'], [this.getPreScanner().getBounds(), this.getPostScanner().getBounds()]),
            new TutorialHint(json['EndUI']),

            // BlockBuilding
            new TutorialHint(json['BlockBuilding'], [], () => this.setLevelState(LevelState.BLOCKBUILDING)),
            new TutorialHint(json['BeginBlock'], [], () => {
                beginBlock = this.placeBlock(
                    (x: number, y: number) => new BeginBlock(this, x, y, this.getPreBlockBuildScreen()),
                    preBlockBuildingGridCenterX + 3,
                    preBlockBuildingGridCenterY + 1
                );
            }),
            new TutorialHint(json['Relational'], [], () => {
                relationalBlock = this.placeBlock(
                    (x: number, y: number) => new RelationalBlock(this, x, y, '>=', this.getPreBlockBuildScreen()),
                    preBlockBuildingGridCenterX + 3,
                    preBlockBuildingGridCenterY + 3
                );
            }),
            new TutorialHint(json['VarAndConst'], [], () => {
                variableBlock = this.placeBlock(
                    (x: number, y: number) => new VariableBlock(this, x, y, 'a', this.getPreBlockBuildScreen()),
                    preBlockBuildingGridCenterX,
                    preBlockBuildingGridCenterY + 3
                );
                constBlock = this.placeBlock(
                    (x: number, y: number) => new RealBlock(this, x, y, 0, this.getPreBlockBuildScreen()),
                    preBlockBuildingGridCenterX + 4,
                    preBlockBuildingGridCenterY + 4
                );
            }),
            new TutorialHint(json['Connecting'], [], () => {
                beginBlock.getConnectors()[0].setTowards(relationalBlock.getConnectors()[0]);
                relationalBlock.getConnectors()[1].setTowards(variableBlock.getConnectors()[0]);
                relationalBlock.getConnectors()[2].setTowards(constBlock.getConnectors()[0]);
            }),
            new TutorialHint(json['Normalizing'], [], () => this.getPreBlockBuildScreen().normalize()),
            new TutorialHint(json['DeletingBlocks']),
            new TutorialHint(json['CancellingBlocks']),

            // Conditions
            new TutorialHint(json['PreCondition'], [new PIXI.Rectangle(374, 680, 374, 85)], () => {
                this.getPreBlockBuildScreen().closeScreen();
                this.getSlideUpPre().slide();
            }),
            new TutorialHint(json['PostCondition'], [new PIXI.Rectangle(748, 680, 374, 85)], () => {
                this.getSlideUpPost().slide();
            }),

            // Spark types
            new TutorialHint(json['Sparks']),
            new TutorialHint(json['GoodSpark'], [], () => this.spawnSpark(Spark, true, true)),
            new TutorialHint(json['BadSpark'], [], () => this.spawnSpark(Spark, false, false)),
            new TutorialHint(json['VirusSpark'], [], () => this.spawnSpark(VirusSpark, false, false)),
            new TutorialHint(json['FireWallSpark'], [], () => this.spawnSpark(FireWallSpark, false, false)),

            // Prepare for wave
            new TutorialHint(json['PrepareWave'], [], () => this.spark.destroyObject()),

            // Towers
            new TutorialHint(json['TowerMenu'], [new PIXI.Rectangle(1122, 430, 240, 290)]),
            new TutorialHint(json['SingleShotTower'], [new PIXI.Rectangle(290, 535, 60, 50)], () =>
                this.placeTower(SingleshotTower, 12, 26)
            ),
            new TutorialHint(json['TowerStats'], [new PIXI.Rectangle(1122, 105, 240, 275)]),
            new TutorialHint(json['RangeStat'], [new PIXI.Rectangle(1127, 190, 230, 40)]),
            new TutorialHint(json['PowerStat'], [new PIXI.Rectangle(1127, 230, 230, 40)]),
            new TutorialHint(json['SpeedStat'], [new PIXI.Rectangle(1127, 270, 230, 40)]),
            new TutorialHint(json['BoostButton'], [new PIXI.Rectangle(1120, 330, 82, 44)]),
            new TutorialHint(json['EffectButton'], [new PIXI.Rectangle(1197, 330, 82, 44)]),
            new TutorialHint(json['ModeButton'], [new PIXI.Rectangle(1275, 330, 82, 44)]),
            new TutorialHint(json['MultiShotTower'], [new PIXI.Rectangle(290, 455, 60, 60)], () =>
                this.placeTower(MultishotTower, 12, 21)
            ),
            new TutorialHint(json['TowerStrategy']),

            // Wave
            new TutorialHint(json['NextWave'], [new PIXI.Rectangle(1285, 720, 70, 50)], () =>
                this.setSelectedTower(null)
            ),
            new TutorialHint(json['Wave'], [], () => this.startWave()),

            // End
            new TutorialHint(json['End'])
        ];
        this.tutorialIndex = 0;

        this.speechBubble = this.game.add.text(560, 680, '', {
            font: '18px Arial',
            fill: 'white',
            stroke: 'black',
            strokeThickness: 3
        });
        this.speechBubble.anchor.set(0.5, 1);
        this.speechBubble.boundsAlignV = 'bottom';
        this.speechBubble.wordWrap = true;
        this.speechBubble.wordWrapWidth = 1120;

        this.graphicsFill = this.game.add.graphics(0, 0);
        this.graphicsFill.blendMode = Phaser.blendModes.ADD;
        this.graphicsOutLine = this.game.add.graphics(0, 0);

        this.game.add.tween(this.graphicsFill).to({ alpha: 0 }, 750, Phaser.Easing.Linear.None, true, 0, -1, true);

        // TODO: right now these buttons are functional buttons that are laid over the normal non-functional buttons.
        // This is not a nice nor scalable solution. It can be fixed by adding a reference to these buttons in Level.
        this.menu = new Menu(this.game);
        this.menu.switchButton(
            this.world.width - emptyButtonWidth * 0.5 - 150,
            emptyButtonHeight * 0.5 + 15,
            'SFX: On',
            'SFX: Off',
            (isOn: boolean) => SoundManager.switchSoundEffectsVolume(),
            SoundManager.getSoundEffectsVolume() !== 0
        );

        this.menu.switchButton(
            this.world.width - emptyButtonWidth * 0.5 - 75,
            emptyButtonHeight * 0.5 + 15,
            'Music: On',
            'Music: Off',
            (isOn: boolean) => SoundManager.switchMusicVolume(),
            SoundManager.getMusicVolume() !== 0
        );

        this.hotkeys = this.cache.getJSON('hotkeys')['tutorial'];
        this.captureKey(Phaser.KeyCode[this.hotkeys['next']]);

        this.menu.gameButton(
            this.world.width - emptyButtonWidth * 0.5,
            emptyButtonHeight * 0.5 + 15,
            'Exit',
            () => this.game.state.start('menu')
        );

        this.updateTutorialHint();
    }

    /**
     * General update function. This is called for every loop in the game loop.
     */
    public update(): void {
        // Prevent scrolling in the tutorial
        this.game.input.mouse.wheelDelta = 0;

        super.update();

        // Set the cursor to the default cursor.
        // Buttons are non-interactive in the tutorial, so showing the hand cursor would be a bit weird.
        this.game.canvas.style.cursor = 'default';
    }

    /**
     * Callback method for a mouse down event
     */
    protected onMouseDown(): void {
        this.menu.onMouseDown(this.input.activePointer);
    }

    /**
    * Callback method for a mouse down event
    */
    protected onMouseUp(): void {
        if (this.menu.onMouseUp(this.input.activePointer)) {
            return;
        }

        this.nextTutorialHint();
    }

    /**
    * Callback method for a key press event
    */
    protected onKeyPressed(key: number): void {
        if (key === Phaser.KeyCode[this.hotkeys['next']]) {
            this.nextTutorialHint();
        }
    }

    /**
     * Set the hint to the next hint of the tutorial
     */
    private nextTutorialHint(): void {
        this.tutorialIndex++;

        if (this.tutorialIndex === this.tutorialHints.length) {
            this.game.state.start('menu');

            return;
        }

        this.updateTutorialHint();
    }

    /**
     * Update the hint of the tutorial to the current hint
     */
    private updateTutorialHint(): void {
        const hint: TutorialHint = this.tutorialHints[this.tutorialIndex];

        // Show the text associated with the hint
        this.speechBubble.text = hint.getText();

        const fillColour: number = Phaser.Color.WHITE;
        const outlineColour: number = Phaser.Color.RED;

        this.graphicsFill.clear();
        this.graphicsFill.beginFill(fillColour, 0.5);

        this.graphicsOutLine.clear();
        this.graphicsOutLine.lineStyle(4, outlineColour);

        // Draw the rectangles associated with the hint
        hint.getRectangles().forEach(rect => {
            this.graphicsFill.drawRect(rect.x, rect.y, rect.width, rect.height);
            this.graphicsOutLine.drawRect(rect.x, rect.y, rect.width, rect.height);
        });

        // Perform the action associated with the hint
        hint.getAction()();
    }

    /**
     * Places a bock at the specified coordinates (in grid space)
     * @param creator Creator function for a block.
     * @param x The x coordinate of the block to place in grid space
     * @param y The y coordinate of the block to place in grid space
     */
    private placeBlock(creator: BlockCreator, x: number, y: number): Block {
        const block: Block = creator(0, 0);
        block.mask = this.getBlockBuildingMask();

        this.game.add.existing(block);
        this.getBlockBuildingRenderGroup().add(block);

        this.getPreBlockBuildScreen().createBlock(block, new Phaser.Point(x, y), false, 'B0');

        return block;
    }

    /**
     * Places a new tower at the specified coordinates (in grid space).
     * @param towerConstructor Constructor of a Tower type. This will be used to create a new instance of a Tower.
     * @param x The x coordinate of the Tower in grid space.
     * @param y The y coordinate of the Tower in grid space.
     */
    private placeTower(towerConstructor: new (level: Level, x: number, y: number) => Tower, x: number, y: number): void {
        const grid: Grid = this.getGrid();

        const gridPos: Phaser.Point = new Phaser.Point(x, y);
        const screenPos: Phaser.Point = grid.gridSpaceToScreenSpace(gridPos.x, gridPos.y);

        const tower: Tower = new towerConstructor(this, screenPos.x, screenPos.y);

        this.game.add.existing(tower);
        this.getGameRenderGroup().add(tower);

        // Add the range from the different paths
        tower.addPathRange(this.getPrePath().getCells());
        tower.addPathRange(this.getPostPath().getCells());
        tower.addTowersInRange();

        // Update the grid
        grid.placeHitbox(gridPos.x, gridPos.y, tower.getHitbox(), tower);

        tower.updateObject();
        this.setSelectedTower(tower);
        this.getTowers().add(tower);

        this.setMoney(this.getMoney() - tower.getTowerCost());
        this.setMoneySpentOnTowers(this.getMoneySpentOnTowers() + tower.getTowerCost());
    }

    /**
     * Spawns a single spark of the specified type at the start of the pre path.
     * @param sparkConstructor Constructor of a Spark type. This will be used to create a new instance of a Spark.
     * @param correct Is the spark good or evil?
     * @param satisfiesCondition Does the spark satisfy the pre-condition.
     */
    private spawnSpark(sparkConstructor: new (level: Level, grid: Grid, health: number, speed: number) => Spark,
        correct: boolean, satisfiesCondition: boolean): void {

        if (this.spark) {
            this.spark.destroyObject();
        }

        this.spark = new sparkConstructor(this, this.getGrid(), this.getSparkHealth(), this.getSparkSpeed());
        this.spark.placeOnPath(this.getPrePath(), correct, satisfiesCondition);

        this.getSparks().add(this.spark);
        this.getGameRenderGroup().add(this.spark);
    }
}
