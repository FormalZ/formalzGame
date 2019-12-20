/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University
 * within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
import Level from '../../../states/level';
import BlockBuildScreen from '../blockBuildScreen';
import BlockType from '../blockType';
import TypeCheck from '../typeCheck';
import BinaryBlock from './binaryBlock';
import ConnectorColours from '../connectorColours';

const colours: ConnectorColours[] = [ConnectorColours.BOOLEAN, ConnectorColours.BOOLEAN];

/**
 * An ImplicationBlock defines the logical implication p => q
 * This is equivalent to (not p or q)
 */
export default class ImplicationBlock extends BinaryBlock {

    public className: string = 'ImplicationBlock';
    /**
     * Instantiates a new ImplicationBlock
     * @param level The Level that the Block exists in.
     * @param x The x coordinate in screen space of the Block.
     * @param y The y coordinate in screen space of the Block.
     * @param screen The BlockBuildScreen that this Block belongs to.
     */
    public constructor(level: Level, x: number, y: number, screen: BlockBuildScreen) {
        super(level, x, y, 'imp', screen, colours, BlockType.BOOL);

        this.blockTypeString = 'implicationBlock';
    }

    public toString(addBrackets: boolean): string {
        return `imp(${this.leftConnector.toString(addBrackets)}, ${this.rightConnector.toString(addBrackets)})`;
    }

    public typeCheck(): boolean {
        return TypeCheck.isBoolean(this.leftConnector.getTowardsBlockType())
            && TypeCheck.isBoolean(this.rightConnector.getTowardsBlockType());
    }
}
