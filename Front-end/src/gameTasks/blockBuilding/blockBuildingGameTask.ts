/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University
 * within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
import BlockBuildScreen from '../../gameObjects/blockBuilding/blockBuildScreen';
import Block from '../../gameObjects/blockBuilding/blocks/block';
import BlockType from '../../gameObjects/blockBuilding/blockType';
import Connector from '../../gameObjects/blockBuilding/connector';
import Grid from '../../gameObjects/grid/grid';
import ErrorState from '../../states/errorState';
import Level from '../../states/level';
import LevelState from '../../states/levelState';
import Dictionary from '../../utils/dictionary';
import { StringUtils } from '../../utils/utils';
import GameTask from '../gameTask';

/**
 * BlockBuildingGameTask is a GameTask that handles all BlockBuilding functionality.
 * When in block building mode, this GameTask will show the right BlockBuildScreen and update it.
 * It also takes care of rendering the connections between Connectors.
 */
export default class BlockBuildingGameTask extends GameTask {
    private currentBlockBuildScreen: BlockBuildScreen;

    private graphics: Phaser.Graphics;

    private blockbuildingSettings: object = null;
    private hotkeys: object = null;


    /**
     * Initializes the block building game task. This is done by initializing the two block building screens
     * as well as setting up the hotkey commands
     * @param level The current level to initialize the block building game task in
     */
    public initialize(level: Level): void {
        super.initialize(level);

        const preBlockBuildScreen: BlockBuildScreen = new BlockBuildScreen(this.level, 'Precondition');
        const postBlockBuildScreen: BlockBuildScreen = new BlockBuildScreen(this.level, 'Postcondition');

        this.hotkeys = this.level.cache.getJSON('hotkeys')['blockBuilding'];

        this.level.setPreBlockBuildScreen(preBlockBuildScreen);
        this.level.setPostBlockBuildScreen(postBlockBuildScreen);

        this.level.getBlockBuildScreenRenderGroup().add(preBlockBuildScreen);
        this.level.getBlockBuildScreenRenderGroup().add(postBlockBuildScreen);

        this.level.captureKey(Phaser.KeyCode[this.hotkeys['moveUp']]);                  // UP
        this.level.captureKey(Phaser.KeyCode[this.hotkeys['moveDown']]);                // DOWN
        this.level.captureKey(Phaser.KeyCode[this.hotkeys['moveLeft']]);                // LEFT
        this.level.captureKey(Phaser.KeyCode[this.hotkeys['moveRight']]);               // RIGHT
        this.level.captureKey(Phaser.KeyCode[this.hotkeys['cancelPlacingAndLeave']]);   // ESC
        this.level.captureKey(Phaser.KeyCode[this.hotkeys['removeObject1']]);           // R
        this.level.captureKey(Phaser.KeyCode[this.hotkeys['removeObject2']]);           // DELETE
        this.level.captureKey(Phaser.KeyCode[this.hotkeys['normalize']]);               // N

        const origin: Phaser.Point = preBlockBuildScreen.getGrid().getOrigin();

        // Define a mask for all blocks, so they won't be drawn outside of this BlockBuildScreen's bounds
        const mask: Phaser.Graphics = new Phaser.Graphics(this.level.game);
        mask.beginFill(0xffffff);
        mask.drawRect(origin.x, origin.y, preBlockBuildScreen.width, preBlockBuildScreen.height);
        this.level.setBlockBuildingMask(mask);

        this.graphics = this.level.game.make.graphics();
        this.graphics.mask = mask;
        this.level.getBlockBuildingRenderGroup().add(this.graphics);

        this.blockbuildingSettings = this.level.game.cache.getJSON('blockbuildingSettings');

        preBlockBuildScreen.placeBlocksFromStorage('Precondition');
        postBlockBuildScreen.placeBlocksFromStorage('Postcondition');
    }

    /**
     * The general update function to run every game loop. This calls the update function for the currently selected block building screen,
     * as well as drawing blocks or connections that are being currently placed
     */
    public update(): void {
        if (this.currentBlockBuildScreen) {
            const placingBlock: Block = this.level.getPlacingBlock();

            if (placingBlock) {
                placingBlock.x = this.level.game.input.activePointer.position.x;
                placingBlock.y = this.level.game.input.activePointer.position.y;
            }

            this.currentBlockBuildScreen.updateObject();

            if (!this.level.getMovingBlock()) {
                this.level.getBlockBuildingRenderGroup().bringToTop(this.graphics);
            }

            this.graphics.clear();
            this.graphics.lineStyle(this.blockbuildingSettings['connectorLineWidth'], 0x111111);

            // Draw lines between all connected Connectors
            this.currentBlockBuildScreen.getBlocks().forEach(block => {
                if (block.typeCheck()) {
                    block.tint = block === this.level.getSelectedBlockOrConnector() ? 0x9e9e9e : 0xffffff;
                } else {
                    block.tint = block === this.level.getSelectedBlockOrConnector() ? 0x9e9e9e : 0xff0000;
                }

                if (block === this.level.getMovingBlock()) {
                    const grid: Grid = this.currentBlockBuildScreen.getGrid();
                    const gridPos: Phaser.Point = grid.screenSpaceToGridSpace(block.x, block.y);

                    block.tint = grid.isValidHitbox(gridPos.x, gridPos.y, Block.getHitbox()) || gridPos.equals(block.getOldGridPos()) ?
                        Phaser.Color.WHITE :
                        Phaser.Color.RED;
                }

                block.getConnectors().forEach(connector => {

                    this.graphics.lineStyle(this.blockbuildingSettings['connectorLineWidth'],
                        connector === this.level.getSelectedBlockOrConnector() ? 0x233240 : 0x64768d);

                    if (connector.getTowards() && connector.visible) {
                        this.graphics.moveTo(connector.worldPosition.x, connector.worldPosition.y);
                        this.graphics.lineTo(connector.getTowards().worldPosition.x, connector.getTowards().worldPosition.y);
                    }
                });
            });

            this.graphics.lineStyle(this.blockbuildingSettings['connectorLineWidth'], 0x111111);

            // If the player is currently dragging a connection line, draw that line
            if (this.level.getDragging()) {
                const connector: Connector = this.level.getCurrentConnector();
                const pointer: Phaser.Point = this.level.game.input.activePointer.position;

                this.graphics.moveTo(connector.worldPosition.x, connector.worldPosition.y);
                this.graphics.lineTo(pointer.x, pointer.y);
            }
        }
    }

    /**
     * Callback method for a mouse down event. Calls the mouse down event for the currently selected block building screen
     * @param pointer the position where the mouse down event was triggered
     */
    public onMouseDown(pointer: Phaser.Pointer): boolean {
        return this.currentBlockBuildScreen.onMouseDown(pointer);
    }

    /**
     * Callback method for a mouse up event. Calls the mouse up event for the currently selected block building screen
     * @param pointer the position where the mouse up event was triggered
     */
    public onMouseUp(pointer: Phaser.Pointer): boolean {
        return this.currentBlockBuildScreen.onMouseUp(pointer);
    }

    /**
    * Callback method for a key press event. Calls the key even for the currently selected block building screen
    * @param key the key code for the key that was pressed
    */
    public onKeyPressed(key: number): boolean {
        if (this.currentBlockBuildScreen) {
            return this.currentBlockBuildScreen.onKeyPressed(key);
        }

        return false;
    }

    /**
     * Handle a command from the server.
     * For the 'variableTypes' command, call the parseTypes function.
     * For the 'validPreTokens' command, reset the pre block building screen and parse the new block tokens
     * For the 'validPostTokens' command, reset the post block building screen and parse the new block tokens
     * @param keyWord the key word describing what command is triggered
     * @param args the possible arguments for a command
     */
    public tryCommand(command: string, args: string): boolean {
        switch (command) {
            case 'variableTypes':
                this.parseTypes(args);
                return true;
            case 'validPreTokens':
                const preBlockBuildScreen: BlockBuildScreen = this.level.getPreBlockBuildScreen();
                preBlockBuildScreen.parseValidBlocks(this.splitTokens(args));
                preBlockBuildScreen.drawBlockUI();
                return true;
            case 'validPostTokens':
                const postBlockBuildScreen: BlockBuildScreen = this.level.getPostBlockBuildScreen();
                postBlockBuildScreen.parseValidBlocks(this.splitTokens(args));
                postBlockBuildScreen.drawBlockUI();
                return true;
            default:
                return false;
        }
    }

    /**
     * Changes the visibility of both the block build screens, dependent on the state of the game and which blockbuild screen is selected.
     * @param levelState This describes to which state the game is being changed to.
     *                   This is a boolean value, where true means that the game is changing to block building
     *                   and false means that the game is being changed to gameplay.
     */

    public changeState(levelState: LevelState): void {
        const preBlockBuildScreen: BlockBuildScreen = this.level.getPreBlockBuildScreen();
        const postBlockBuildScreen: BlockBuildScreen = this.level.getPostBlockBuildScreen();

        const isBlockBuilding: boolean = levelState === LevelState.BLOCKBUILDING;

        // On initialization of blockbuilding we reset the conditionchanged variable to false
        if (isBlockBuilding) {
            this.level.setConditionChanged(false);
        }

        this.setInputEnabled(isBlockBuilding);
        this.setUpdateEnabled(isBlockBuilding);

        this.graphics.visible = isBlockBuilding;

        const isPre: boolean = isBlockBuilding && this.level.getCurrentScanner().getConditionType() === 'Pre';
        const isPost: boolean = isBlockBuilding && this.level.getCurrentScanner().getConditionType() === 'Post';

        preBlockBuildScreen.setVisible(isPre);
        postBlockBuildScreen.setVisible(isPost);

        if (isPre) {
            this.currentBlockBuildScreen = preBlockBuildScreen;
        } else if (isPost) {
            this.currentBlockBuildScreen = postBlockBuildScreen;
        } else {
            this.currentBlockBuildScreen = null;
        }
    }

    /**
     * Split the raw token data into usable strings. All garbage around it has been removed as well.
     * @param tokens the pre- or postcondition tokens to be split
     */
    private splitTokens(tokens: string): string[] {
        return tokens.substring(1, tokens.length - 1).split(', ').map(x => x.substring(1, x.length - 1));
    }

    /**
     * Parses an array of type tokens and stores each tuple (variable name, type) in a Dictionary with the variable's name as key.
     * This allows a variable block to retrieve its type from the same Dictionary later on.
     * @param args Array of comma separated tokens, surrounded by square brackets.
     *             The tokens must consist of the type and the name of a variable, separated by a space.
     */
    private parseTypes(args: string): void {
        const tokens: string[] = args.substring(1, args.length - 1).split(', ');

        const validTypes: Dictionary<string> = this.level.getValidTypes();
        validTypes.clear();

        tokens.forEach(token => {
            token = token.trim();
            let splitTokens: string[] = token.split(' ').filter(x => x !== '');
            const type: string = splitTokens[0];
            const name: string = splitTokens[splitTokens.length - 1];
            // If both are '' or undefined, continue
            if ((type !== '' && name !== '') && (type !== undefined && name !== undefined)) {
                let blockType: BlockType;
                switch (type) {
                    case 'int':
                    case 'float':
                    case 'double':
                        blockType = BlockType.REAL;
                        break;
                    case 'boolean':
                        blockType = BlockType.BOOL;
                        break;
                    case 'int[]':
                    case 'float[]':
                    case 'double[]':
                        blockType = BlockType.REAL_ARRAY;
                        break;
                    case 'boolean[]':
                        blockType = BlockType.BOOL_ARRAY;
                        break;
                    case 'int[][]':
                    case 'float[][]':
                    case 'double[][]':
                        blockType = BlockType.REAL_ARRAY_ARRAY;
                        break;
                    case 'boolean[][]':
                        blockType = BlockType.BOOL_ARRAY_ARRAY;
                        break;
                    default:
                        const unknownBlock: string =
                            this.level.game.cache.getJSON('warningsText')['blockBuildingGameTask']['invalidBlockType'];
                        ErrorState.throw(this.level.game, StringUtils.interpolateString(unknownBlock, type));
                }

                validTypes.add(name, blockType);
            }
        });
    }
}
