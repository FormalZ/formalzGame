/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University
 * within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
import * as Assets from '../../assets';
import SoundManager from '../../soundManager';
import ErrorState from '../../states/errorState';
import Level from '../../states/level';
import LevelState from '../../states/levelState';
import Menu from '../../userInterface/menu';
import MenuButton from '../../userInterface/menuButton';
import { StringUtils } from '../../utils/utils';
import GameObject from '../gameObject';
import Grid from '../grid/grid';
import BlockBuildingButton from './blockBuildingButton';
import BlockCreator from './blockCreator';
import ArithmeticBlock, { ArithmeticOperator } from './blocks/arithmeticBlock';
import ArrayIndexBlock from './blocks/arrayIndexBlock';
import BeginBlock from './blocks/beginBlock';
import Block from './blocks/block';
import BooleanBlock, { Boolean } from './blocks/booleanBlock';
import EqualityBlock, { EqualityComparer } from './blocks/equalityBlock';
import ImplicationBlock from './blocks/implicationBlock';
import LengthBlock from './blocks/lengthBlock';
import LogicOperatorBlock, { LogicOperator } from './blocks/logicOperatorBlock';
import NotBlock from './blocks/notBlock';
import NullBlock from './blocks/nullBlock';
import QuantifierBlock, { Quantifier } from './blocks/quantifierBlock';
import QuantifierRangeBlock, { QuantifierRange } from './blocks/quantifierRangeBlock';
import RealBlock from './blocks/realBlock';
import RelationalBlock, { RelationalComparer } from './blocks/relationalBlock';
import VariableBlock from './blocks/variableBlock';
import WithBlock from './blocks/withBlock';
import CollapsedBlock from './blocks/collapsedBlock';
import Connector from './connector';
import ConnectorColours, { ArrayIndexToColour } from './connectorColours';

const blockScreenSpriteName: string = Assets.Images.SpritesBackgroundScreen.getName();
const textureSprite: string = Assets.Images.SpritesGeneralContainerBackground.getName();
const arrowDownSprite: string = Assets.Images.SpritesArrowDown.getName();
const arrowUpSprite: string = Assets.Images.SpritesArrowUp.getName();
const arrowLeftSprite: string = Assets.Images.SpritesArrowLeft.getName();
const arrowRightSprite: string = Assets.Images.SpritesArrowRight.getName();
const gridWidth: number = 100;
const gridHeight: number = 100;

const errorSound: string = Assets.Audio.AudioError.getName();
const normalizeSound: string = Assets.Audio.AudioNormalize.getName();

/**
 * Defines a screen in which the BlockBuilding game mechanic takes place.
 * This includes the blockbuilding grid and a UI that allows the player to select specific blocks.
 */
export default class BlockBuildScreen extends GameObject {
    private grid: Grid;

    private hotkeys: object;

    // UI
    private blockBuildingMenu: Menu;
    private blockUI: Menu;

    // UI Container
    private blocksContainer: Phaser.TileSprite;
    private prePostIndicatorContainer: Phaser.TileSprite;

    // UI Buttons
    private previousBlockButton: MenuButton;
    private nextBlockButton: MenuButton;
    private crossButton: MenuButton;
    private normalizeButton: MenuButton;

    // UI variables
    private validBlockButtons: Array<BlockBuildingButton>;
    private firstButtonIndex: number;
    private pageIndexDisplay: Phaser.Text;
    private prePostIndicatorText: Phaser.Text;

    // Grid movement indicators
    private leftIndicator: Phaser.Sprite;
    private rightIndicator: Phaser.Sprite;
    private upIndicator: Phaser.Sprite;
    private downIndicator: Phaser.Sprite;

    // Block variables
    private blocks: Set<Block>;
    private beginBlocks: Set<BeginBlock>;

    private largestNormalizedTree: number;

    private buttonsText: object = null;
    private warningsText: object = null;

    private localSRemove: boolean;

    /**
     * Instantiates a new BlockBuildScreen.
     * @param level The Level that this BlockBuildScreen exists in.
     * @param title The title of this BlockBuildScreen. This will be displayed above the Screen.
     */
    constructor(level: Level, title: string) {
        super(level, level.game.width / 2, level.game.height / 2, blockScreenSpriteName);

        this.scale.set(0.25, 0.25);

        this.hotkeys = this.game.cache.getJSON('hotkeys')['blockBuilding'];

        this.buttonsText = this.game.cache.getJSON('buttonsText')['blockBuildingScreen'];
        this.warningsText = this.game.cache.getJSON('warningsText')['blockBuildingScreen'];

        this.anchor.set(0.5);

        const cellSize: number = 80;
        this.grid = new Grid(cellSize, gridWidth, gridHeight, new Phaser.Point(
            this.left + (this.width % cellSize) / 2,
            this.top + (this.width % cellSize) / 2)
        );
        this.grid.setOffset(new Phaser.Point(gridWidth / 2, gridHeight / 2));

        // UI
        this.blockBuildingMenu = new Menu(this.level.game);
        this.blockUI = new Menu(this.level.game);

        // UI helper variables
        const buttonScale: Phaser.Point = new Phaser.Point(0.2, 0.2);
        const emptyButtonWidth: number = Assets.Spritesheets.SpritesheetsEmptyButton3691923.getFrameWidth() * buttonScale.x;
        const emptyButtonHeight: number = Assets.Spritesheets.SpritesheetsEmptyButton3691923.getFrameHeight() * buttonScale.y;

        // UI Container for blockbuilding
        this.blocksContainer = this.blockBuildingMenu.textureContainer(
            this.right, this.top,
            textureSprite,
            emptyButtonWidth,
            this.height
        );

        // UI Buttons for blockbuilding
        this.previousBlockButton = this.blockBuildingMenu.gameButton(
            this.right + emptyButtonWidth * 0.5,
            this.top + emptyButtonHeight * 0.5,
            this.buttonsText['previous'],
            () => {
                // Only allow scrolling through the blocks if there are more then 4 of them available
                if (this.validBlockButtons.length > 4) {
                    this.firstButtonIndex -= 4;

                    this.drawBlockUI();
                }
            }
        );

        this.nextBlockButton = this.blockBuildingMenu.gameButton(
            this.right + emptyButtonWidth * 0.5,
            this.bottom - emptyButtonHeight * 0.5,
            this.buttonsText['next'],
            () => {
                // Only allow scrolling through the blocks if there are more then 4 of them available
                if (this.validBlockButtons.length > 4) {
                    this.firstButtonIndex += 4;

                    this.drawBlockUI();
                }
            }
        );

        this.pageIndexDisplay = this.blockBuildingMenu.text(
            this.right + emptyButtonWidth * 0.5,
            this.top + this.blocksContainer.height / 7.5,
            null, {
            fontSize: 12,
            fill: 'white'
        }
        );
        this.pageIndexDisplay.anchor.set(0.5);

        this.crossButton = this.blockBuildingMenu.button(
            this.right + emptyButtonWidth + 0.125 * Assets.Spritesheets.SpritesheetsCrossButton1001002.getFrameWidth(),
            this.top + 0.125 * Assets.Spritesheets.SpritesheetsCrossButton1001002.getFrameHeight(),
            Assets.Spritesheets.SpritesheetsCrossButton1001002.getName(),
            null,
            buttonScale,
            null,
            () => this.closeScreen(),
            1, 0, 2
        );

        this.blockBuildingMenu.add(this.blockUI);

        // UI variables
        this.validBlockButtons = new Array<BlockBuildingButton>();
        this.firstButtonIndex = 0;

        // Block variables
        this.blocks = new Set<Block>();
        this.beginBlocks = new Set<BeginBlock>();

        this.level.getUIScreenRenderGroup().add(this.blockBuildingMenu);

        this.largestNormalizedTree = null;

        // UI pre- or post indicator for the container
        this.prePostIndicatorContainer = this.blockBuildingMenu.textureContainer(
            this.centerX, this.top,
            textureSprite,
            this.width / 2 + emptyButtonWidth,
            35
        );
        this.prePostIndicatorContainer.anchor.set(0.4, 1);

        const style: Phaser.PhaserTextStyle = { font: '26px Arial', fill: '#47B744' };

        this.prePostIndicatorText = this.blockBuildingMenu.text(this.centerX, this.top + 5, title, style);
        this.prePostIndicatorText.anchor.setTo(0.5, 1);

        this.leftIndicator = this.blockBuildingMenu.sprite(this.x - this.width / 2 + 20, this.centerY, arrowLeftSprite);
        this.rightIndicator = this.blockBuildingMenu.sprite(this.x + this.width / 2 - 20, this.centerY, arrowRightSprite);
        this.upIndicator = this.blockBuildingMenu.sprite(this.x, this.y - this.height / 2 + 20, arrowUpSprite);
        this.downIndicator = this.blockBuildingMenu.sprite(this.x, this.y + this.height / 2 - 20, arrowDownSprite);

        this.normalizeButton = this.blockBuildingMenu.gameButton(
            this.prePostIndicatorContainer.right - emptyButtonWidth * 0.5,
            this.prePostIndicatorContainer.top + emptyButtonHeight * 0.5,
            this.buttonsText['normalize'],
            () => this.normalize(),
        );

        this.blockBuildingMenu.gameButton(
            this.prePostIndicatorContainer.left - emptyButtonWidth * 0.5,
            this.normalizeButton.y,
            this.buttonsText['deleteBlock'],
            () => this.onRemove()
        );

        this.localSRemove = false;
    }

    /**
     * Method that parses an array of tokens from the back-end that defines what blocks are valid this wave.
     * It clears all currently placed blocks in the grid, refunding the player for 100% of their costs.
     * It always makes the Begin Block available.
     * Valid blocks are stored in both an internal dictionary structure as well as a GUI structure.
     * @param tokens array of tokens that defines what blocks are available to the player in a certain wave.
     */
    public parseValidBlocks(tokens: string[]): void {
        if (!this.localSRemove) {
            // Remove all previously placed blocks
            this.blocks.forEach(block => this.removeBlock(block, true));
            this.blocks.clear();
            this.beginBlocks.clear();
            this.localSRemove = false;
        }

        this.largestNormalizedTree = null;

        // Add beginblock
        this.validBlockButtons.forEach(button => button.destroy());
        this.validBlockButtons = [
            new BlockBuildingButton(this.level, (x, y) => new BeginBlock(this.level, x, y, this))
        ];

        tokens.forEach(token => {
            const [block, argument] = token.split(' ');

            let creator: BlockCreator;

            switch (block) {
                case 'ArrayIndex':
                    creator = (x, y) => new ArrayIndexBlock(this.level, x, y, this);
                    break;
                case 'Arithmetic':
                    creator = (x, y) => new ArithmeticBlock(this.level, x, y, argument as ArithmeticOperator, this);
                    break;
                case 'RelationalComparer':
                    creator = (x, y) => new RelationalBlock(this.level, x, y, argument as RelationalComparer, this);
                    break;
                case 'EqualityComparer':
                    creator = (x, y) => new EqualityBlock(this.level, x, y, argument as EqualityComparer, this);
                    break;
                case 'LogicOperator':
                    creator = (x, y) => new LogicOperatorBlock(this.level, x, y, argument as LogicOperator, this);
                    break;
                case 'Boolean':
                    creator = (x, y) => new BooleanBlock(this.level, x, y, argument as Boolean, this);
                    break;
                case 'Number':
                case 'Real':
                    creator = (x, y) => new RealBlock(this.level, x, y, parseFloat(argument), this);
                    break;
                case 'Null':
                    creator = (x, y) => new NullBlock(this.level, x, y, this);
                    break;
                case 'Length':
                    creator = (x, y) => new LengthBlock(this.level, x, y, this);
                    break;
                case 'Quantifier':
                    creator = (x, y) => new QuantifierBlock(this.level, x, y, argument as Quantifier, this);
                    break;
                case 'QuantifierR':
                    creator = (x, y) => new QuantifierRangeBlock(this.level, x, y, argument as QuantifierRange, this);
                    break;
                case 'Not':
                    creator = (x, y) => new NotBlock(this.level, x, y, this);
                    break;
                case 'Variable':
                    creator = (x, y) => new VariableBlock(this.level, x, y, argument, this);
                    break;
                case 'With':
                    creator = (x, y) => new WithBlock(this.level, x, y, this);
                    break;
                case 'Implication':
                    creator = (x, y) => new ImplicationBlock(this.level, x, y, this);
                    break;
                case 'Syntax':
                    console.warn(this.warningsText['syntaxTokenReceived']);
                    return;
                default:
                    ErrorState.throw(this.level.game, StringUtils.interpolateString(this.warningsText['invalidToken'], token));
                    return;
            }

            this.validBlockButtons.push(new BlockBuildingButton(this.level, creator));

        }, this);

        this.firstButtonIndex = 0;
    }

    /**
     * Draws the Block selection UI menu.
     */
    public drawBlockUI(): void {
        if (this.firstButtonIndex < 0) {
            this.firstButtonIndex = this.validBlockButtons.length - this.validBlockButtons.length % 4;

            if (this.firstButtonIndex === this.validBlockButtons.length) {
                this.firstButtonIndex -= 4;
            }
        } else if (this.firstButtonIndex >= this.validBlockButtons.length) {
            this.firstButtonIndex = 0;
        }

        this.pageIndexDisplay.setText(`${Math.floor(this.firstButtonIndex / 4) + 1}/${Math.ceil(this.validBlockButtons.length / 4)}`, true);

        let xPosition: number = this.right + 0.5 * this.blocksContainer.width;
        let yPosition: number = this.top + this.blocksContainer.height / 12;

        // Make every block invisible
        for (let i: number = 0; i < this.validBlockButtons.length; i++) {
            this.validBlockButtons[i].visible = false;
        }

        // Make visible block buttons in the UI visible
        for (let i: number = this.firstButtonIndex; i < Math.min(this.firstButtonIndex + 4, this.validBlockButtons.length); i++) {
            const currentButton: BlockBuildingButton = this.validBlockButtons[i];

            yPosition += currentButton.height * 1.28;
            currentButton.position.x = xPosition;
            currentButton.position.y = yPosition;

            this.blockUI.add(currentButton);

            currentButton.visible = true;
        }
    }

    public updateObject(): void {
        const placingBlock: Block = this.level.getPlacingBlock();

        if (placingBlock) {
            const pointer: Phaser.Point = this.level.input.activePointer.position;
            const position: Phaser.Point = this.grid.screenSpaceToGridSpace(pointer.x, pointer.y);

            placingBlock.tint = (this.grid.isValidHitbox(position.x, position.y, Block.getHitbox())) &&
                ((this.level.getMoney() - placingBlock.getCost()) >= 0) ? Phaser.Color.WHITE : Phaser.Color.RED;
        }

        this.validBlockButtons.forEach(blockButton => {
            blockButton.updateObject();
        });

        this.blocks.forEach(block => block.updateObject());

        if (this.game.input.mouse.wheelDelta !== 0) {
            switch (this.game.input.mouse.wheelDelta) {
                case Phaser.Mouse.WHEEL_UP:
                    this.firstButtonIndex -= 4;

                    break;
                case Phaser.Mouse.WHEEL_DOWN:
                    this.firstButtonIndex += 4;

                    break;
            }

            this.drawBlockUI();

            this.game.input.mouse.wheelDelta = 0;
        }

        this.blockBuildingMenu.updateObject();
    }

    public onMouseDown(pointer: Phaser.Pointer): boolean {
        if (this.blockBuildingMenu.onMouseDown(pointer)) {
            return true;
        }

        if (this.level.getPlacingBlock()) {
            return true;
        }

        for (let block of this.blocks) {
            if (block.onMouseDown(pointer) && block.visible) {
                this.level.getBlockBuildingRenderGroup().bringToTop(block);

                return true;
            }
        }

        return false;
    }

    public onMouseUp(pointer: Phaser.Pointer): boolean {
        if (!this.level.getMovingBlock() && this.blockBuildingMenu.onMouseUp(pointer)) {
            return true;
        }

        const placingBlock: Block = this.level.getPlacingBlock();

        if (placingBlock) {
            const origin: Phaser.Point = this.grid.getOrigin();

            if (pointer.x > origin.x && pointer.x < origin.x + this.width &&
                pointer.y > origin.y && pointer.y < origin.y + this.height) {
                const position: Phaser.Point = this.grid.screenSpaceToGridSpace(pointer.x, pointer.y);

                if (this.grid.isValidHitbox(position.x, position.y, Block.getHitbox())) {
                    const idNum: number = this.level.getBlockId();
                    this.createBlock(placingBlock, position, true, 'B' + idNum);
                    this.level.setBlockID(idNum + 1);

                    this.level.setPlacingBlock(null);
                } else {
                    SoundManager.playSoundEffect(errorSound);
                }
            }

            return true;
        }

        for (let block of this.blocks) {
            if (block.onMouseUp(pointer)) {
                if (this.level.getDragging() && block !== this.level.getCurrentConnector().getBlock()) {
                    this.level.setDragging(false);
                }

                return true;
            }
        }

        this.level.setDragging(false);

        return false;
    }

    public onKeyPressed(key: number): boolean {
        switch (key) {
            case Phaser.KeyCode[this.hotkeys['moveUp']]:                // UP
                this.moveGrid(0, 1);
                return true;
            case Phaser.KeyCode[this.hotkeys['moveDown']]:              // DOWN
                this.moveGrid(0, -1);
                return true;
            case Phaser.KeyCode[this.hotkeys['moveLeft']]:              // LEFT
                this.moveGrid(1, 0);
                return true;
            case Phaser.KeyCode[this.hotkeys['moveRight']]:             // RIGHT
                this.moveGrid(-1, 0);
                return true;
            case Phaser.KeyCode[this.hotkeys['cancelPlacingAndLeave']]: // ESC
                // If the player is placing a block, ESC should cancel that. Otherwise, it should close the blockbuilding screen.
                if (this.level.getPlacingBlock()) {
                    this.cancelPlacingBlock();
                } else {
                    this.closeScreen();
                }
                return true;
            case Phaser.KeyCode[this.hotkeys['removeObject1']]:         // R
            case Phaser.KeyCode[this.hotkeys['removeObject2']]:         // DELETE
                this.onRemove();
                return true;
            case Phaser.KeyCode[this.hotkeys['normalize']]:             // N
                this.normalize();
                return true;
            case Phaser.KeyCode['C']:
                this.onCollapse();
                return true;
            case Phaser.KeyCode['E']:
                this.onExpand();
                return true;
            default:
                return false;
        }
    }

    /**
     * Creates and places a Block.
     * @param block The block to be placed in the grid
     * @param position Position to place block in grid space
     * @param localS Indicator whether to store the block in local storage
     * @param blockid The id to give to the block
     */
    public createBlock(block: Block, position: Phaser.Point, localS: boolean, blockid: string, free?: boolean, begin?: string): void {
        block.setId(blockid);
        if (free === undefined) {
            free = false;
        }
        const halfCellSize: number = this.grid.getCellSize() / 2;
        const screenPos: Phaser.Point = this.grid.gridSpaceToScreenSpace(position.x, position.y);

        block.x = screenPos.x + halfCellSize;
        block.y = screenPos.y + halfCellSize;

        block.alpha = 1;

        const newMoney: number = this.level.getMoney() - block.getCost();

        // Check if the player has enough money
        if (newMoney < 0 && !free) {
            block.destroy();

            SoundManager.playSoundEffect(errorSound);
        } else {
            this.blocks.add(block);

            // If the player placed a BeginBlock, add it to the list of entry-points so we can start compiling from there later on.
            if (block instanceof BeginBlock) {
                this.beginBlocks.add(block);
            }

            // Update the grid
            this.grid.placeHitbox(position.x, position.y, Block.getHitbox(), block);
            block.setOldGridPos(position.clone());

            this.level.setSelectedBlockOrConnector(block);
            // Adding a block is a change to the condition
            this.level.setConditionChanged(true);
            if (!free) {
                this.level.setMoney(newMoney);
            }
        }
        // add this block to local storage
        if (localS) {
          const preOrPostIndicator: string = this.prePostIndicatorText.text;
          const localStorageString: string = localStorage.getItem(this.level.getSessionId() + preOrPostIndicator);
          if (localStorageString !== null) {
            localStorage.setItem(this.level.getSessionId() + preOrPostIndicator, localStorageString + '|' + block.getId() + ';' + block.className + ';' + block.getText() + ';' + position.x + ';' + position.y + ';' + block.cameraOffset.x + ';' + block.cameraOffset.y + ';' + block.worldPosition.x + ';' + block.worldPosition.y + ';' + block.previousPosition.x + ';' + block.previousPosition.y + ';' + begin);
          }
          else {
            localStorage.setItem(this.level.getSessionId() + preOrPostIndicator, block.getId() + ';' + block.className + ';' + block.getText() + ';' + position.x + ';' + position.y + ';' + block.cameraOffset.x + ';' + block.cameraOffset.y + ';' + block.worldPosition.x + ';' + block.worldPosition.y + ';' + block.previousPosition.x + ';' + block.previousPosition.y + ';' + begin);
          }
          this.level.sendHash();
        }
    }

    /**
     * If the player is currently dragging a block this method cancels it.
     */
    private cancelPlacingBlock(): void {
        const placingBlock: Block = this.level.getPlacingBlock();
        if (placingBlock) {
            placingBlock.destroy();

            this.level.setPlacingBlock(null);
        }
    }

    /**
     * Moves the grid in the specified direction.
     * @param x x-coordinate of the direction to move in.
     * @param y y-coordinate of the direction to move in.
     */
    public moveGrid(x: number, y: number): void {
        const oldOffset: Phaser.Point = this.grid.getOffset();
        const newOffset: Phaser.Point = new Phaser.Point(oldOffset.x - x, oldOffset.y - y);

        // Check if the move is valid
        if (newOffset.x < 0 || newOffset.x >= this.grid.getWidth() ||
            newOffset.y < 0 || newOffset.y >= this.grid.getHeight()) {
            return;
        }

        this.grid.setOffset(newOffset);

        const cellSize: number = this.grid.getCellSize();

        this.blocks.forEach(block => {
            block.x += x * cellSize;
            block.y += y * cellSize;
        });

        // Check if the new offset is on the edge of the grid.
        // If so, set the visibility of the corresponding indicator to false.
        // Also check if the new offset is leaving the edge.
        // If so, set the visibility of the corresponding inidcator to true.
        switch (newOffset.x) {
            case 0:
                this.leftIndicator.visible = false;
                break;
            case 1:
                this.leftIndicator.visible = true;
                break;
            case this.grid.getWidth() - 2:
                this.rightIndicator.visible = true;
                break;
            case this.grid.getWidth() - 1:
                this.rightIndicator.visible = false;
                break;
        }

        switch (newOffset.y) {
            case 0:
                this.upIndicator.visible = false;
                break;
            case 1:
                this.upIndicator.visible = true;
                break;
            case this.grid.getHeight() - 2:
                this.downIndicator.visible = true;
                break;
            case this.grid.getHeight() - 1:
                this.downIndicator.visible = false;
                break;
        }
    }

    /**
     * Closes this screen.
     * This will also try to compile the blockbuilding tree, starting at all Begin blocks.
     * The compiled code will be send to the server.
     */
    public closeScreen(shouldSend: boolean = true): void {
        let conditionClean: string = '';
        let conditionBrackets: string = '';
        let codeClean: string;
        let codeBrackets: string;

        this.beginBlocks.forEach(beginBlock => {
            let valid: boolean;

            // Try to compile the block-tree into a string, this can fail if connectors are not connected and thus have null-pointers.
            try {
                codeClean = `(${beginBlock.toString(false)})`;
                codeBrackets = `(${beginBlock.toString(true)})`;
                valid = true;
            } catch (e) {
                valid = false;
            }

            // If the code was valid, append it to the current condition.
            if (valid) {
                if (conditionClean !== '') {
                    conditionClean += ' && ';
                    conditionBrackets += ' && ';
                }

                conditionClean += codeClean;
                conditionBrackets += codeBrackets;
            }
        });

        // If no valid conditions were added, the conditions becomes 'true'
        if (conditionClean === '') {
            conditionClean = 'true';
            conditionBrackets = 'true';
        }

        if (this.level.getPause()) {
            this.level.togglePause();
        }

        this.cancelPlacingBlock();

        this.level.setDragging(false);
        this.level.setCurrentConnector(null);
        this.level.setSelectedBlockOrConnector(null);
        this.level.setCurrentConnector(null);


        if (this.level.getMovingBlock()) {
            this.level.getMovingBlock().stopMoving();
        }
        this.level.getCurrentScanner().changeCondition(conditionClean, conditionBrackets, shouldSend);
        this.level.setLevelState(LevelState.PLAYING);
    }

    /**
     * CallBack method to handle deletion of a block.
     */
    private onRemove(): void {
        const selected: Block | Connector = this.level.getSelectedBlockOrConnector();

        if (selected instanceof Block) {

            let parent: Block = selected.getParent();
            if (parent !== null) {
                let parentConnector: Connector = selected.getParentConnector();
                if (parentConnector.getOriginalColour() === ConnectorColours.POLYMORPHIC) {
                    parent.decreasePolymorphismCount(parentConnector);
                }
                if (parentConnector.getOriginalColour() === ConnectorColours.ARRAY_POLYMORPHIC) {
                    parentConnector.getBlock().getTopConnector().resetColour();
                }

                // remove the connections from this block from the local storage
                const localStorageString: string = localStorage.getItem(this.level.getSessionId() + 'connections');
                const localStorageConnections: string[] = localStorageString.split('|');
                let newLocalStorageString: string = '';
                localStorageConnections.forEach(storedConnection => {
                  if (!storedConnection.includes(parentConnector.getTowards().getBlock().getId())) {
                    if (newLocalStorageString === '')
                      newLocalStorageString += storedConnection;
                    else
                      newLocalStorageString += '|' + storedConnection;
                  }
                });
                if (newLocalStorageString !== '')
                  localStorage.setItem(this.level.getSessionId() + 'connections', newLocalStorageString);
                // if there are no more connections, remove the connections item from local storage
                else
                  localStorage.removeItem(this.level.getSessionId() + 'connections');
                parentConnector.setTowards(null);
            }

            this.removeBlock(selected);
        } else if (selected instanceof Connector) {
            if (selected.getOriginalColour() === ConnectorColours.POLYMORPHIC)
                selected.getBlock().decreasePolymorphismCount(selected);
            if (selected.getOriginalColour() === ConnectorColours.ARRAY_POLYMORPHIC)
                selected.getBlock().getTopConnector().resetColour();

            if (selected.getTowards().getOriginalColour() === ConnectorColours.POLYMORPHIC)
                selected.getTowards().getBlock().decreasePolymorphismCount(selected.getTowards());
            if (selected.getTowards().getOriginalColour() === ConnectorColours.ARRAY_POLYMORPHIC)
                selected.getTowards().getBlock().getTopConnector().resetColour();

            // remove the connections from this block from the local storage
            const localStorageString: string = localStorage.getItem(this.level.getSessionId() + 'connections');
            const localStorageConnections: string[] = localStorageString.split('|');
            let newLocalStorageString: string = '';
            localStorageConnections.forEach(storedConnection => {
              let secondConnectorCheck: boolean;
              if (!selected.getIsInput()) {
                secondConnectorCheck = storedConnection.includes(selected.getFrom().getBlock().getId());
              }
              else {
                secondConnectorCheck = storedConnection.includes(selected.getTowards().getBlock().getId());
              }
              if (! (storedConnection.includes(selected.getBlock().getId()) && secondConnectorCheck)) {
                if (newLocalStorageString === '')
                  newLocalStorageString += storedConnection;
                else
                  newLocalStorageString += '|' + storedConnection;
              }
            });
            if (newLocalStorageString !== '')
              localStorage.setItem(this.level.getSessionId() + 'connections', newLocalStorageString);
              // if there are no more connections, remove the connections item from local storage
            else
              localStorage.removeItem(this.level.getSessionId() + 'connections');
            selected.setTowards(null);
        }

        this.level.setDragging(false);
        this.level.setCurrentConnector(null);
        this.level.setSelectedBlockOrConnector(null);

        if (this.level.getTooltip().getObject() === selected) {
            this.level.getTooltip().hide();
        }
        this.level.sendHash();
    }

    /**
     * CallBack method to handle collapsing of a block.
     */
    private onCollapse(localSId?: string): void {
        const selected: Block | Connector = this.level.getSelectedBlockOrConnector();
        if (selected instanceof Block && !(selected instanceof CollapsedBlock || selected instanceof BeginBlock)) {
            var collapsed: CollapsedBlock = this.collapse(selected);
            var connectedBlock: Block = selected.getParent();
            var location: Phaser.Point = selected.getOldGridPos();
            collapsed.previousPosition = this.grid.gridSpaceToScreenSpace(location.x, location.y);
            collapsed.cameraOffset = selected.cameraOffset;
            collapsed.worldPosition = selected.worldPosition;
            if (localSId !== undefined) {
              collapsed.setId(localSId);
            }
            else {
              collapsed.setId('B' + this.level.getBlockId());
              this.level.setBlockID(this.level.getBlockId() + 1);
            }
            this.level.getBlockBuildingRenderGroup().add(collapsed);
            var value: number = this.recursiveRemove(selected);
            collapsed.setCost(value);
            this.createBlock(collapsed, location, true, collapsed.getId(), true, collapsed.getBegin().getId());
            if (connectedBlock != null) {
                connectedBlock.getConnectors().forEach(connector => {
                  if (connector.getTowards() !== null) {
                    if (connector.getTowards().getBlock() == selected) {
                        connector.setTowards(collapsed.getConnectors()[0]);
                    }
                  }
                });
            }
            this.level.sendHash();
            // Recursively remove the block and children. Replace currently selected block with this one
        }
    }

    public collapse(selected: Block): CollapsedBlock {
        return new CollapsedBlock(this.level, selected.getOldGridPos().x, selected.getOldGridPos().y, selected.toString(false), this, selected);
    }

    private recursiveRemove(start: Block): number {
        var blocksToRemove: Block[] = [start];
        var index: number = 0;
        var value: number = 0;
        while (index < blocksToRemove.length) {
            var currentBlock = blocksToRemove[index];
            blocksToRemove = blocksToRemove.concat(currentBlock.getChildren());
            value += currentBlock.getCost();
            currentBlock.visible = false;
            currentBlock.setCollapsed(true);
            const oldGridPos: Phaser.Point = currentBlock.getOldGridPos();
            this.grid.removeHitbox(oldGridPos.x, oldGridPos.y, Block.getHitbox());
            currentBlock.getConnectors().forEach(connector => {
                connector.visible = false;
            }, this);
            index++;
        }
        return value;
    }

    /**
     * CallBack method to expanding a collapsed block.
     */
    private onExpand(): void {
        const selected: Block | Connector = this.level.getSelectedBlockOrConnector();

        if (selected instanceof CollapsedBlock) {
            var begin: Block = selected.getBegin();
            var connectedBlock: Block = selected.getParent();
            var location: Phaser.Point = selected.getOldGridPos();
            var row: number = this.findHighestOpenRow();
            var difference: number = row - location.y;
            this.incrementPosition(begin, difference);
            if (connectedBlock != null) {
                connectedBlock.getConnectors().forEach(connector => {
                  if (connector.getTowards() !== null) {
                    if (connector.getTowards().getBlock() == selected) {
                        connector.setTowards(begin.getConnectors()[0]);
                    }
                  }
                });
            }
            this.removeBlock(selected, false, false);
            this.level.setSelectedBlockOrConnector(begin);
            this.level.sendHash();
        }
    }

    /**
     * Looks for the highest row that has no blocks in it
     */

    private findHighestOpenRow(): number {
        var row: number = 0;
        this.blocks.forEach(block => { if (block.getOldGridPos().y > row && block.visible) { row = block.getOldGridPos().y } });
        return row;
    }

    private incrementPosition(begin: Block, incrementBy: number): void {

        var blocksToIncrement: Block[] = [begin];
        var index: number = 0;
        while (index < blocksToIncrement.length) {
            var currentBlock = blocksToIncrement[index];
            blocksToIncrement = blocksToIncrement.concat(currentBlock.getChildren());
            currentBlock.visible = true;
            currentBlock.setCollapsed(false);
            const oldGridPos: Phaser.Point = currentBlock.getOldGridPos();
            currentBlock.changePosition(oldGridPos.x, oldGridPos.y + incrementBy);
            currentBlock.getConnectors().forEach(connector => {
                connector.visible = true;
            }, this);
            index++;
        }
    }

    /**
     * Removes a specified block from this BlockBuildingScreen
     * @param block the block to be removed. If no block is given, the default is 'this.selected'
     * @param completed whether the player has finished the challenge. If so, refund the whole cost.
     */
    private removeBlock(block: Block, completed?: boolean, returnMoney?: boolean): void {
        // Check if the block is instantiated and has not been removed yet
        if (returnMoney === undefined) {
            returnMoney = true;
        }
        if (block && this.blocks.has(block)) {
            // Remove all connections the block has
            block.getConnectors().forEach(connector => {
                if (connector.getFrom()) {
                    connector.getFrom().setTowards(null);
                } else if (connector.getTowards()) {
                    connector.setTowards(null);
                }
            }, this);

            // Remove the block's hitbox from the grid
            const oldGridPos: Phaser.Point = block.getOldGridPos();
            this.grid.removeHitbox(oldGridPos.x, oldGridPos.y, Block.getHitbox());

            // Refund the player for this block
            // Check if the player completed the level. If so, refund the whole cost.
            if (completed && returnMoney) {
                this.level.setMoney(this.level.getMoney() + block.getCost());
            } else if (returnMoney) {
                // If not, refund the block normally.
                this.level.setMoney(this.level.getMoney() + (block.getCost() * this.level.getRefundBlockModifier()));
            }

            if (block instanceof BeginBlock && this.beginBlocks.has(block)) {
                this.beginBlocks.delete(block);
            }

            // remove this block from local storage
            const localStorageString: string = localStorage.getItem(this.level.getSessionId() + this.prePostIndicatorText.text);
            const localStorageBlocks: string[] = localStorageString.split('|');
            let newLocalStorageString: string = '';
            localStorageBlocks.forEach(storedBlock => {
              if (!storedBlock.includes(block.getId())) {
                if (newLocalStorageString === '')
                  newLocalStorageString += storedBlock;
                else
                  newLocalStorageString += '|' + storedBlock;
              }
            });
            if (newLocalStorageString !== '')
              localStorage.setItem(this.level.getSessionId() + this.prePostIndicatorText.text, newLocalStorageString);
              // if there are no more blocks, remove the blocks item from local storage
            else
              localStorage.removeItem(this.level.getSessionId() + this.prePostIndicatorText.text);

            // Remove the block
            this.blocks.delete(block);
            block.destroy(true);

            // Deleting a block is a change to the condition
            this.level.setConditionChanged(true);

            this.level.sendHash();
        }
    }

    /**
     * Sets the visibility of this screen and it's substructures (i.e. the blocks and UI)
     * @param visible boolean that indicates whether or not the screen should be visible.
     */
    public setVisible(visible: boolean): void {
        this.visible = visible;
        this.blockBuildingMenu.visible = visible;
        this.blockUI.visible = visible;
        this.blocksContainer.visible = visible;
        this.previousBlockButton.visible = visible;
        this.nextBlockButton.visible = visible;
        this.crossButton.visible = visible;

        this.blocks.forEach(block => { if (!block.getCollapsed()) { block.visible = visible; } });
    }

    /**
     * Reformats the current tree to a nicely formated tree.
    */
    public normalize(): void {
        // Reset the window to the center of the grid
        this.grid.setOffset(new Phaser.Point(20, 20));
        let x: number = 0;

        // Apply dfs to each tree.
        // The x value sets the offset for each tree, making the trees nicely aligned to each other
        for (let block of this.beginBlocks) {
            x += this.dfs(block, x, 0, true) + 1;
        }

        // Reset the x value for non-complete trees
        x = 0;
        // Check for all blocks
        for (let block of this.blocks) {
            if(!block.visible)
                continue;
            if (block.getNormalized()) {
                // Reset all normalized values
                block.setNormalized(false);
            } else {
                // If this block is the start of a incomplete tree, normalize this tree underneath the completed trees
                if (!block.getParent()) {
                    x += this.dfs(block, x, this.largestNormalizedTree + 1, false);
                }
            }
        }

        SoundManager.playSoundEffect(normalizeSound);
    }

    /**
     * Format a single tree , by performing a single depth first search to determine its width and height.
     * Also move the actual blocks in the screen when all its children have been evaluated
     * @param block The block currently being evaluated
     * @param x The starting x position of this (sub)tree
     * @param y The starting y position of this (sub)tree
     * @param complete If the tree is a complete tree or not
     */
    private dfs(block: Block, x: number, y: number, complete: boolean): number {
        // Set the width of this subtree
        let width: number = -1;

        for (let connection of block.getConnectors()) {
            if(!connection.visible)
                continue;
            const childConnector: Connector = connection.getTowards();
            // If the connector has a child connected
            if (childConnector) {
                const childBlock: Block = childConnector.getBlock();

                // Add a space between each child node
                width++;
                width += this.dfs(childBlock, x + width, y + 1, complete);
            }
        }

        // With no children, set the width of the subtree to at least 1
        width = Math.max(width, 1);

        // Change the position of the block
        const newRelativeGridPos: Phaser.Point = new Phaser.Point(Math.floor(x + width / 2), y);
        const offset: Phaser.Point = this.grid.getOffset();
        block.changePosition(newRelativeGridPos.x + offset.x, newRelativeGridPos.y + offset.y);

        // If this block is in a completed tree, check if this is the lowest node, and set the normalized value
        if (complete) {
            if (this.largestNormalizedTree === null || y > this.largestNormalizedTree) {
                this.largestNormalizedTree = y;
            }

            block.setNormalized(true);
        }

        return width;
    }

    /**
    * Places all the blocks from local storage
    * @param preOrPost Indicator whether this screen needs to retrieve from pre or post storage
    */
    public placeBlocksFromStorage(preOrPost: string): void {
      // a list of all colapsed blocks with their corresponding begin block
      let toCollapseArray: string[][] = [];
      if (preOrPost === 'Precondition') {
        this.level.setCurrentScanner(this.level.getPreScanner());
      }
      else
        this.level.setCurrentScanner(this.level.getPostScanner());
      const blocksStorage: string = localStorage.getItem(this.level.getSessionId() + preOrPost);
      if (blocksStorage !== null) {
        const infoStrings: string[] = blocksStorage.split('|');
        // loop over all blocks in local storage, check what type they are and place the corresponding
        // block type on the given position
        infoStrings.forEach(infoString => {
          // a boolean check if the block to place is a collapsed block
          let collapsed: boolean = false;
          const infoStrings: string[] = infoString.split(';');
          const blockid: string = infoStrings[0];
          const blockType: string = infoStrings[1];
          const blockText: string = infoStrings[2];
          const gridPosition: Phaser.Point = new Phaser.Point(Number(infoStrings[3]), Number(infoStrings[4]));
          const position: Phaser.Point = this.grid.gridSpaceToScreenSpace(gridPosition.x, gridPosition.y);
          const cameraOff: Phaser.Point = new Phaser.Point(Number(infoStrings[5]), Number(infoStrings[6]));
          const worldPos: Phaser.Point = new Phaser.Point(Number(infoStrings[7]), Number(infoStrings[8]));
          const prevPos: Phaser.Point = new Phaser.Point(Number(infoStrings[9]), Number(infoStrings[10]));
          const begin: string = infoStrings[11];
          let block: Block = null;
          switch (blockType) {
              case 'BeginBlock':
                  block = new BeginBlock(this.level, position.x, position.y, this);
                  break;
              case 'ArrayIndexBlock':
                  block = new ArrayIndexBlock(this.level, position.x, position.y, this);
                  break;
              case 'ArithmeticBlock':
                  block = new ArithmeticBlock(this.level, position.x, position.y, blockText as ArithmeticOperator, this);
                  break;
              case 'RelationalBlock':
                  block = new RelationalBlock(this.level, position.x, position.y, blockText as RelationalComparer, this);
                  break;
              case 'EqualityBlock':
                  block = new EqualityBlock(this.level, position.x, position.y, blockText as EqualityComparer, this);
                  break;
              case 'LogicOperatorBlock':
                  block = new LogicOperatorBlock(this.level, position.x, position.y, blockText as LogicOperator, this);
                  break;
              case 'BooleanBlock':
                  block = new BooleanBlock(this.level, position.x, position.y, blockText as Boolean, this);
                  break;
              case 'RealBlock':
                  block = new RealBlock(this.level, position.x, position.y, parseFloat(blockText), this);
                  break;
              case 'NullBlock':
                  block = new NullBlock(this.level, position.x, position.y, this);
                  break;
              case 'LengthBlock':
                  block = new LengthBlock(this.level, position.x, position.y, this);
                  break;
              case 'QuantifierBlock':
                  block = new QuantifierBlock(this.level, position.x, position.y, blockText as Quantifier, this);
                  break;
              case 'QuantifierRangeBlock':
                  block = new QuantifierRangeBlock(this.level, position.x, position.y, blockText as QuantifierRange, this);
                  break;
              case 'NotBlock':
                  block = new NotBlock(this.level, position.x, position.y, this);
                  break;
              case 'VariableBlock':
                  block = new VariableBlock(this.level, position.x, position.y, blockText, this);
                  break;
              case 'WithBlock':
                  block = new WithBlock(this.level, position.x, position.y, this);
                  break;
              case 'ImplicationBlock':
                  block = new ImplicationBlock(this.level, position.x, position.y, this);
              case 'CollapsedBlock':
                  toCollapseArray.push([begin, blockid]);
                  collapsed = true;
        }
        const idNum: number = Number(blockid.replace('B', '')) + 1;
        if (idNum >= this.level.getBlockId()) {
          this.level.setBlockID(idNum);
        }
        // only if the block is not collapsed, place the actual block.
        // collapsed blocks are handled below
        if (!collapsed) {
          block.worldPosition = worldPos;
          block.cameraOffset = cameraOff;
          block.previousPosition = prevPos;
          block.mask = this.level.getBlockBuildingMask();
          this.createBlock(block, gridPosition, false, blockid);
          this.level.game.add.existing(block);
          this.level.getBlockBuildingRenderGroup().add(block);
        }
      });
      this.localSRemove = true;

      const connectionsStorage: string = localStorage.getItem(this.level.getSessionId() + 'connections');
      if (connectionsStorage !== null) {
        const blockArray: Block[] = Array.from(this.blocks);
        const infoStrings: string[] = connectionsStorage.split('|');
        // loop over all connections, and place connections between blocks based on block id
        infoStrings.forEach(infoString => {
        const infoStrings: string[] = infoString.split(';');
        const inputBlockId: string = infoStrings[0];
        const inputBlockIndex: number = blockArray.findIndex(x => x.getId() === inputBlockId);
        if (inputBlockIndex !== -1) {
          const inputBlock: Block = blockArray[inputBlockIndex];
          const outputBlockId: string = infoStrings[2];
          const outputBlockIndex: number = blockArray.findIndex(x => x.getId() === outputBlockId);
          const outputBlock: Block = blockArray[outputBlockIndex];
          const input: Connector = inputBlock.getConnectors()[Number(infoStrings[1])];
          const output: Connector = outputBlock.getConnectors()[Number(infoStrings[3])];
          input.setTowards(output);
          this.level.setSelectedBlockOrConnector(input);

          if (input.getOriginalColour() === ConnectorColours.POLYMORPHIC) {
             for (let connector of input.getBlock().getConnectors()) {
                  if (connector.getColour() === ConnectorColours.POLYMORPHIC) {
                  connector.setColour(output.getColour());
                  }
              }
              input.getBlock().increasePolymorphismCount(input);
          }
          if (input.getOriginalColour() === ConnectorColours.ARRAY_POLYMORPHIC) {
              input.getBlock().getTopConnector().setColour(ArrayIndexToColour(output.getBlock().getType()));
          }
        }
      });
    }
  }
  // for each collapsed block read from local storage, collapse the corresponding begin block
  toCollapseArray.forEach( (item) => {
    let beginid: string = item[0];
    let blockid: string = item[1];
    // remove the collapsed block from local storage, to prevent copies in local storage
    let localStorageString: string = localStorage.getItem(this.level.getSessionId() + this.prePostIndicatorText.text);
    const localStorageBlocks: string[] = localStorageString.split('|');
    let newLocalStorageString: string = '';
    localStorageBlocks.forEach(storedBlock => {
      if (!storedBlock.includes(blockid)) {
        if (newLocalStorageString === '')
          newLocalStorageString += storedBlock;
        else
          newLocalStorageString += '|' + storedBlock;
      }
    });
    localStorage.setItem(this.level.getSessionId() + this.prePostIndicatorText.text, newLocalStorageString);

    // select the correct begin block
    this.blocks.forEach((block) => {
      if (block.getId() === beginid)
        this.level.setSelectedBlockOrConnector(block);
    });
    // collapse this begin block
    this.onCollapse(blockid);
  });
  this.closeScreen(false);
}

    public getBlocks(): Set<Block> { return this.blocks; }

    public getGrid(): Grid { return this.grid; }

    public getPrePostIndicatorText(): string { return this.prePostIndicatorText.text; }
}
