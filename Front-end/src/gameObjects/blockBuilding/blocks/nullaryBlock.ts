/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University
 * within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
import Level from '../../../states/level';
import BlockBuildScreen from '../blockBuildScreen';
import BlockType from '../blockType';
import Block from './block';

/**
 * NullaryBlock is a Block that has zero input Connectors and one output Connector.
 */
export default abstract class NullaryBlock extends Block {
    protected value: string;

    /**
     * Instantiates a new NullaryBlock
     * @param level The Level that the Block exists in.
     * @param x The x coordinate in screen space of the Block.
     * @param y The y coordinate in screen space of the Block.
     * @param value The value that the Block represents.
     * @param screen The BlockBuildScreen that this Block belongs to.
     * @param blockType The return type of the expression that this Block represents.
     * @param blockCost The amount of money necessary to purchase this Block.
     */
    public constructor(level: Level, x: number, y: number, value: string, screen: BlockBuildScreen,
        blockType: BlockType = BlockType.ANY, blockCost: number = 40) {
        super(level, x, y, value, screen, blockType, false, blockCost);

        this.value = value;
    }

    public toString(addBrackets: boolean): string {
        return this.value;
    }

    public typeCheck(): boolean {
        return true; // A nullary block has no inputs. As such it's type-correct by default.
    }

    public getChildren(): Block[] {
        return [];
    }
}