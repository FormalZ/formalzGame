/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University
 * within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
import * as Assets from '../../assets';
import Level from '../../states/level';
import GeometryUtils from '../../utils/geometryUtils';
import GameObject from '../gameObject';
import Block from './blocks/block';
import BlockType from './blockType';
import SoundManager from '../../soundManager';
import ConnectorColours, { ArrayIndexToColour } from './connectorColours';

const spriteName: string = Assets.Images.SpritesConnector.getName();

const errorSound: string = Assets.Audio.AudioError.getName();

/**
 * A Connector is a little box that is attached to a Block that is able to form connections to other Connectors on other Blocks
 */
export default class Connector extends GameObject {
    private block: Block;

    private towards: Connector;
    private from: Connector;

    private isInput: boolean;

    private blockbuildingSettings: object = null;

    private colour: ConnectorColours;
    private originalColour: ConnectorColours;

    /**
     * Instantiates a new Connector.
     * @param level The Level that this Connector exists in.
     * @param x The x coordinate of the Connector in screen space.
     * @param y The y coordinate of the Connector in screen space.
     * @param block The Block that this Connector is attached to.
     * @param isInput Whether this is an input Connector or an output Connector.
     */
    public constructor(level: Level, x: number, y: number, block: Block, isInput: boolean, colour: ConnectorColours) {
        super(level, x, y, spriteName);

        this.tint = colour;
        this.colour = colour;
        this.originalColour = colour;

        this.block = block;

        this.towards = null;
        this.from = null;

        this.isInput = isInput;

        this.anchor.set(0.5);

        this.blockbuildingSettings = this.level.game.cache.getJSON('blockbuildingSettings');
    }

    public onMouseDown(pointer: Phaser.Pointer): boolean {
        if (this.containsMouse(pointer)) {
            if (!this.level.getDragging()) {
                this.level.setDragging(true);
                this.level.setCurrentConnector(this);
            }

            return true;
        }

        return this.connectionContainsMouse(pointer.position);
    }

    public onMouseUp(pointer: Phaser.Pointer): boolean {
        if (this.containsMouse(pointer) && this.visible) {
            if (this.level.getDragging()) {
                const currentConnector: Connector = this.level.getCurrentConnector();

                if (currentConnector && currentConnector !== this) {
                    let valid: boolean = false;

                    if (currentConnector.block !== this.block) {
                        let input: Connector, output: Connector;

                        // It only makes sense to connect 1 input Connector to 1 output Connector.
                        // When both are input or both are output, no connection is made.
                        if (this.isInput && !currentConnector.isInput) {
                            input = this;
                            output = currentConnector;
                        } else if (!this.isInput && currentConnector.isInput) {
                            input = currentConnector;
                            output = this;
                        }

                        if (input && output) {
                            // If the output Connector has a previous incoming connection, remove that connection
                            if (output.from) {
                                if (output.from.originalColour === ConnectorColours.POLYMORPHIC)
                                    output.from.getBlock().decreasePolymorphismCount(output.from);
                                if (output.from.originalColour === ConnectorColours.ARRAY_POLYMORPHIC)
                                    output.from.getBlock().getTopConnector().resetColour();
                                output.from.towards = null;
                            }

                            // If the input Connector has a previous outgoing connection, remove that connection
                            if (input.towards) {
                                if (input.towards.from.originalColour === ConnectorColours.POLYMORPHIC)
                                    input.towards.from.getBlock().decreasePolymorphismCount(input.towards.from);
                                input.towards.from = null;
                            }

                            input.setTowards(output);
                            this.level.setSelectedBlockOrConnector(input);

                            // place this connection in local storage, based on block ids
                            const localStorageString: string = localStorage.getItem(this.level.getSessionId() + 'connections');
                            const inputBlock: Block = input.getBlock();
                            const outputBlock: Block = output.getBlock();
                            if (localStorageString !== null) {
                              localStorage.setItem(this.level.getSessionId() + 'connections', localStorageString + '|' + inputBlock.getId() + ';' + inputBlock.getConnectors().findIndex(x => x === input) + ';' + outputBlock.getId() + ';' + outputBlock.getConnectors().findIndex(x => x === output));
                            }
                            else {
                              localStorage.setItem(this.level.getSessionId() + 'connections', inputBlock.getId() + ';' + inputBlock.getConnectors().findIndex(x => x === input) + ';' + outputBlock.getId() + ';' + outputBlock.getConnectors().findIndex(x => x === output));
                            }

                            this.level.sendHash();

                            if (input.originalColour === ConnectorColours.POLYMORPHIC) {
                               for (let connector of input.getBlock().getConnectors()) {
                                    if (connector.colour === ConnectorColours.POLYMORPHIC) {
                                    connector.setColour(output.colour);
                                    }
                                }
                                input.getBlock().increasePolymorphismCount(input);
                            }
                            if (input.originalColour === ConnectorColours.ARRAY_POLYMORPHIC) {
                                input.getBlock().getTopConnector().setColour(ArrayIndexToColour(output.getBlock().getType()));
                            }

                            valid = true;
                        }
                    }

                    if (!valid) {
                        SoundManager.playSoundEffect(errorSound);
                    }

                    this.level.setDragging(false);
                    this.level.setCurrentConnector(null);
                }
            }

            // Changing a connector is changing the condition
            this.level.setConditionChanged(true);

            return true;
        }

        if (this.connectionContainsMouse(pointer.position)) {
            this.level.setSelectedBlockOrConnector(this);

            const movingBlock: Block = this.level.getMovingBlock();
            if (movingBlock) {
                movingBlock.stopMoving();
            }

            // Changing a connector is changing the condition
            this.level.setConditionChanged(true);

            return true;
        }

        return false;
    }

    /**
     * Checks if the connection line going from this Connector to the 'this.towards' Connector contains the mouse pointer.
     * @param mousePosition The position of the mouse pointer.
     * @returns True if the connection contained the mouse, false otherwise.
     */
    public connectionContainsMouse(mousePosition: Phaser.Point): boolean {
        if (this.towards) {
            const distSquared: number = GeometryUtils.squaredDistancePointLineSegment(mousePosition,
                new Phaser.Line(
                    this.worldPosition.x, this.worldPosition.y,
                    this.towards.worldPosition.x, this.towards.worldPosition.y
                )
            );

            return distSquared < (this.blockbuildingSettings['connectorLineWidth'] * 0.5) *
                (this.blockbuildingSettings['connectorLineWidth'] * 0.5);
        }

        return false;
    }

    public toString(addBrackets: boolean): string {
        return this.getTowards().getBlock().toString(addBrackets);
    }

    public getBlock(): Block { return this.block; }

    public getTowardsBlockType(): BlockType {
        if (this.towards) {
            return this.towards.getBlock().getType();
        }

        return BlockType.ANY;
    }

    public getTowards(): Connector { return this.towards; }
    public setTowards(towards: Connector): void {
        if (towards) {
            if (this.towards) {
                this.towards.from = null;
            }
            this.towards = towards;
            this.towards.from = this;
        } else {
            if (this.towards) {
                this.towards.from = null;
            }

            this.towards = null;
        }
    }

    public getFrom(): Connector { return this.from; }

    public setColour(colour: ConnectorColours): void {
        this.colour = colour;
        this.tint = colour;
    }

    public getColour(): ConnectorColours { return this.colour; }
    public getOriginalColour(): ConnectorColours { return this.originalColour; }

    public resetColour(): void {
        this.colour = this.originalColour;
        this.tint = this.originalColour;
    }

    public getIsInput(): boolean { return this.isInput; }
}
