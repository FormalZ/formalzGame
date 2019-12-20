/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University
 * within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
import * as Assets from '../../../assets';
import SoundManager from '../../../soundManager';
import Level from '../../../states/level';
import LevelState from '../../../states/levelState';
import { StringUtils } from '../../../utils/utils';
import GameObject from '../../gameObject';
import Grid from '../../grid/grid';
import Hitbox from '../../grid/hitbox';
import BlockBuildScreen from '../blockBuildScreen';
import BlockType from '../blockType';
import Connector from '../connector';
import Precedence from '../precedence';
import ConnectorColours, { BlockTypeToColour } from '../connectorColours';

const spriteName: string = Assets.Images.SpritesChipBlock.getName();
const hitbox: Hitbox = new Hitbox(0, 0, 1, 1);

const errorSound: string = Assets.Audio.AudioError.getName();
const blockMovedSound: string = Assets.Audio.AudioBlockMoved.getName();

/**
 * Base class for all BlockBuilding Blocks
 * A Block has 1 or more Connectors that connect it to other blocks.
 * These connected blocks form an Abstract Syntax Tree that can be recursively evaluated to a string.
 * To do this every class that inherits from Block must implement it's own version of the abstract 'toString' function.
 */
export default abstract class Block extends GameObject {
    protected connectors: Connector[];

    private textDisplay: Phaser.Text;

    private screen: BlockBuildScreen;
    private oldGridPos: Phaser.Point;

    protected blockType: BlockType;
    protected blockTypeString: string;
    protected thingsToInterpolate: BlockType[] = [];

    protected precedence: Precedence;

    protected blockCost: number;

    private normalized: boolean;

    private toolTip: string;

    private scaledX: number;
    private scaledY: number;

    protected polymorphicConnectionCount: number;

    private isBegin: boolean;
    private collapsed: boolean;

    private id: string;

    public className: string;
    /**
     * Instantiates a new Block
     * @param level The Level that the Block exists in.
     * @param x The x coordinate in screen space of the Block.
     * @param y The y coordinate in screen space of the Block.
     * @param text The text that the Block should display.
     * @param screen The BlockBuildScreen that this Block belongs to.
     * @param blockType The return type of the expression that this Block represents.
     * @param isBegin Whether or not this block is a Begin Block, meaning it's the entry point of the block building tree.
     * @param blockCost The amount of money necessary to purchase this Block.
     */
    public constructor(level: Level, x: number, y: number, text: string, screen: BlockBuildScreen,
        blockType: BlockType = BlockType.ANY, isBegin: boolean = false, blockCost: number = 40) {
        super(level, x, y, spriteName);

        this.anchor.set(0.5);

        this.connectors = new Array<Connector>();
        if (!isBegin) {
            this.addConnector(0, -this.height / 2, BlockTypeToColour(blockType), 180, false);
        }

        const textStyle: Phaser.PhaserTextStyle = { font: 'bold 40px Arial', fill: 'white' };
        this.textDisplay = new Phaser.Text(this.level.game, 0, 0, text, textStyle);
        this.textDisplay.anchor.set(0.5);
        this.textDisplay.fontSize = 85;
        this.addChild(this.textDisplay);

        this.screen = screen;
        this.oldGridPos = this.screen.getGrid().screenSpaceToGridSpace(this.x, this.y);

        this.blockType = blockType;

        this.precedence = Precedence.NONE;

        this.blockCost = blockCost;

        this.normalized = false;

        this.toolTip = null;
        this.scale = new Phaser.Point(0.14, 0.14);

        this.scaledX = this.width / this.scale.x;
        this.scaledY = this.height / this.scale.y;

        this.polymorphicConnectionCount = 0;

        this.isBegin = isBegin;

        this.id = 'B0';
        this.collapsed = false;
    }

    public updateObject(): void {
        const mouseX: number = this.level.input.activePointer.x;
        const mouseY: number = this.level.input.activePointer.y;

        if (this.level.getMovingBlock() === this) {
            this.x = mouseX - this.level.getMovingMouseOffset().x;
            this.y = mouseY - this.level.getMovingMouseOffset().y;
        } else if (this.level.getMovingBlock() === null && this.level.getPlacingBlock() === null
            && this.getBounds().contains(mouseX, mouseY) && this.level.getLevelState() === LevelState.BLOCKBUILDING) {
            this.toolTip = this.getTooltipString();
            this.level.getTooltip().show(this.toolTip, this);
        }
    }

    public onMouseDown(pointer: Phaser.Pointer): boolean {
        // Check if any of this block's connections can handle the mouseDown event
        for (let connector of this.connectors) {
            if (connector.onMouseDown(pointer)) {
                return true;
            }
        }

        // Start dragging this Block
        if (this.containsMouse(pointer)) {
            this.level.getTooltip().hide();

            this.level.setMovingBlock(this);
            this.level.setMovingMouseOffset(new Phaser.Point(
                this.level.input.activePointer.x - this.x,
                this.level.input.activePointer.y - this.y));

            const grid: Grid = this.screen.getGrid();
            const halfCellSize: number = grid.getCellSize() / 2;
            this.oldGridPos = grid.screenSpaceToGridSpace(this.x, this.y);

            return true;
        }

        return false;
    }

    public onMouseUp(pointer: Phaser.Pointer): boolean {
        // Check if any of this block's connections can handle the mouseUp event
        for (let connector of this.connectors) {
            if (connector.onMouseUp(pointer)) {
                return true;
            }
        }

        // Stop dragging this Block
        if (this.containsMouse(pointer)) {
            this.level.setSelectedBlockOrConnector(this);

            if (this.level.getMovingBlock() === this) {
                this.stopMoving();

                return true;
            }
        }

        return false;
    }

    /**
     * Stop moving this Block.
     * This means that the Block will try to snap to the nearest position in the Grid.
     * If this position is already occupied, it will instead snap back to its last valid position in the Grid.
     */
    public stopMoving(): void {
        this.level.setMovingBlock(null);

        const grid: Grid = this.screen.getGrid();
        const newGridPos: Phaser.Point = grid.screenSpaceToGridSpace(this.x, this.y);

        const origin: Phaser.Point = grid.getOrigin();

        if (this.x >= origin.x && this.x < origin.x + this.screen.width &&
            this.y >= origin.y && this.y < origin.y + this.screen.height &&
            grid.isValidHitbox(newGridPos.x, newGridPos.y, Block.getHitbox())) {
            this.changePosition(newGridPos.x, newGridPos.y);

            SoundManager.playSoundEffect(blockMovedSound);
        } else {
            this.changePosition(this.oldGridPos.x, this.oldGridPos.y);

            SoundManager.playSoundEffect(errorSound);
        }
    }

    /**
     * Attaches a new Connector to this Block.
     * @param x X coordinate of the Connector relative to this block's x coordinate.
     * @param y Y coordinate of the Connector relative to this block's y coordinate.
     * @param rotate Rotation of the Connector in degrees. Rotation is counter clockwise.
     * @param isInput Whether the Connector is an input Connector or an output Connector.
     * @returns the newly created Connector.
     */
    private addConnector(x: number, y: number, colour: ConnectorColours, rotate: number = 0, isInput: boolean = true): Connector {
        const connector: Connector = new Connector(this.level, x, y, this, isInput, colour);
        connector.angle = -rotate;
        connector.scale = new Phaser.Point(0.75, 0.75);
        this.addChild(connector);
        this.connectors.push(connector);

        return connector;
    }

    /**
     * change the position of the block to an x and y position in grid space
     * @param x x position in grid space
     * @param y y position in grid space
     */
    public changePosition(x: number, y: number): void {
        const grid: Grid = this.screen.getGrid();

        grid.removeHitbox(this.oldGridPos.x, this.oldGridPos.y, Block.getHitbox());
        grid.placeHitbox(x, y, Block.getHitbox(), this);

        const halfCellSize: number = grid.getCellSize() / 2;
        const newScreenPos: Phaser.Point = grid.gridSpaceToScreenSpace(x, y);

        this.x = newScreenPos.x + halfCellSize;
        this.y = newScreenPos.y + halfCellSize;

        this.oldGridPos.x = x;
        this.oldGridPos.y = y;

        const localStorageString: string = localStorage.getItem(this.level.getSessionId() + this.screen.getPrePostIndicatorText());
        const localStorageBlocks: string[] = localStorageString.split('|');
        let newLocalStorageString: string = '';
        // update the position of this block in local storage
        localStorageBlocks.forEach(storedBlock => {
          let stringToAdd: string = '';
          if (storedBlock.includes(this.id))
            stringToAdd = this.id + ';' + this.className + ';' + this.textDisplay.text + ';' + x + ';' + y + ';' + this.cameraOffset.x + ';' + this.cameraOffset.y + ';' + this.worldPosition.x + ';' + this.worldPosition.y + ';' + this.previousPosition.x + ';' + this.previousPosition.y;
          else
            stringToAdd = storedBlock;
          if (newLocalStorageString !== '')
            newLocalStorageString = newLocalStorageString + '|' + stringToAdd;
          else
            newLocalStorageString = stringToAdd;
        });
        localStorage.setItem(this.level.getSessionId() + this.screen.getPrePostIndicatorText(), newLocalStorageString);

        this.level.sendHash();
    }

    /**
     * Adds a new input Connector on the left side of this Block.
     */
    protected addLeftConnector(colour: ConnectorColours): Connector { return this.addConnector(-this.scaledX / 2, 0, colour, -90); }
    /**
     * Adds a new input Connector on the right side of this Block.
     */
    protected addRightConnector(colour: ConnectorColours): Connector { return this.addConnector(this.scaledX / 2, 0, colour, 90); }
    /**
     * Adds a new input Connector on the bottom of this Block.
     */
    protected addBottomConnector(colour: ConnectorColours): Connector { return this.addConnector(0, this.scaledY / 2, colour); }

    /**
     * Adds a new input Connector on the bottom left side of this Block.
     */
    protected addBottomLeftConnector(colour: ConnectorColours): Connector {
        return this.addConnector(-this.scaledX / 2, this.scaledY / 2, colour);
    }

    /**
     * Adds a new input Connector on the bottom right side of this Block.
     */
    protected addBottomRightConnector(colour: ConnectorColours): Connector {
        return this.addConnector(this.scaledX / 2, this.scaledY / 2, colour);
    }

    /**
     * Converts this block to a string so it can be compiled to java code.
     */
    public abstract toString(addBrackets: boolean): string;

    /**
     * Checks if the types of the connected Blocks are type-correct.
     */
    public abstract typeCheck(): boolean;

    /**
     * Get the string of the tooltip, depending on which block it was
     * @returns The tooltip as a multiline string
     */
    public getTooltipString(): string {
        // Get the correct tooltip from the JSON file.
        let tooltip: string = this.game.cache.getJSON('blockBuildingText')[this.blockTypeString]['tooltip'].join('\n');
        // Interpolate where needed
        tooltip = this.thingsToInterpolate.length > 0 ? StringUtils.interpolateString(tooltip, this.thingsToInterpolate) : tooltip;
        return tooltip;
    }

    /**
     * @returns a list of all Connectors attached to the Block.
     * The first element in the list will always be the output Connector (unless it's a Begin Block as it has no output Connector).
     * The next elements in the list will be all other Connectors attached to this Block, going from left to right.
     */
    public getConnectors(): Connector[] { return this.connectors; }

    public getOldGridPos(): Phaser.Point { return this.oldGridPos; }
    public setOldGridPos(oldGridPos: Phaser.Point): void { this.oldGridPos = oldGridPos; }

    public getType(): BlockType { return this.blockType; }

    public getPrecedence(): Precedence { return this.precedence; }

    public getCost(): number { return this.blockCost; }

    public getText(): string { return this.textDisplay.text; }

    /**
     * @returns the parent Block of this Block.
     * The parent Block is the Block that is connected to this Block's output Connector.
     * If this Block has no output Connector or no Block is connected to it, null will be returned.
     */
    public getParent(): Block {
        if (this.connectors.length === 0 || !this.connectors[0].getFrom()) {
            return null;
        }

        return this.connectors[0].getFrom().getBlock();
    }

    /**
     * @returns the connector of the parent which this block is connected to.
     */
    public getParentConnector(): Connector {
        if (this.connectors.length === 0 || !this.connectors[0].getFrom()) {
            return null;
        }

        return this.connectors[0].getFrom();
    }

    /**
     * @returns the output connector
     */
    public getTopConnector(): Connector {
        if (this.connectors.length === 0 || !this.connectors[0] || this.isBegin) {
            return null;
        }

        return this.connectors[0];
    }

    public getNormalized(): boolean { return this.normalized; }
    public setNormalized(normalized: boolean): void { this.normalized = normalized; }

    public getCollapsed(): boolean { return this.collapsed; }
    public setCollapsed(collapsed: boolean) { this.collapsed = collapsed; }

    public abstract getChildren(): Block[];

    public static getHitbox(): Hitbox { return hitbox; }

    private setAllColours(output: Connector, deleteBlock: boolean): void {

        // If a block got deleted, check if the types actually were all the same.
        // If not, 'steal' that colour to make all the other polymorphic colours correct again.
        if (this.getPolymorphismCount() === 1 && deleteBlock) {
            for (let connector of this.getConnectors()) {
                if (connector.getOriginalColour() === ConnectorColours.POLYMORPHIC && output !== connector) {
                    output = connector.getTowards();
                    break;
                }
            }
        }

        // Set all the polymorphic colours correct on adding the 1st block
        // Or deleting a block so that only 1 connection is alive.
        if (this.getPolymorphismCount() === 1) {
            for (let connector of this.getConnectors()) {
            if (connector.getColour() === ConnectorColours.POLYMORPHIC ||
                (connector.getOriginalColour() === ConnectorColours.POLYMORPHIC)) {
                    connector.setColour(output.getColour());
                }
            }
        }
    }

    private resetAllColours(output: Connector, deleteBlock: boolean): void {
        if (this.getPolymorphismCount()  === 0) {
            for (let connector of this.getConnectors()) {
                connector.resetColour();
            }
        }
        this.setAllColours(output, deleteBlock);
    }

    public getPolymorphismCount(): number { return this.polymorphicConnectionCount; }
    public increasePolymorphismCount(output: Connector): void {
        this.polymorphicConnectionCount++;
        this.resetAllColours(output, false);
    }
    public decreasePolymorphismCount(output: Connector): void {
        this.polymorphicConnectionCount--;
        this.resetAllColours(output, true);
    }

    public getId(): string { return this.id; }
    public setId(id: string): void { this.id = id; }
}
