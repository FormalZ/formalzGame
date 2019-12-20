/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University
 * within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
import Level from '../../../states/level';
import BlockBuildScreen from '../blockBuildScreen';
import BlockType from '../blockType';
import Precedence from '../precedence';
import TypeCheck from '../typeCheck';
import BinaryBlock from './binaryBlock';
import ConnectorColours from '../connectorColours';

const colours: ConnectorColours[] = [ConnectorColours.ARRAY_POLYMORPHIC, ConnectorColours.REAL];

/**
 * Defines a block that indexes an array.
 * The left Connector is the array that is to be indexed.
 * The right Connector is the index itself.
 */
export default class ArrayIndexBlock extends BinaryBlock {

    public className: string = 'ArrayIndexBlock';
    /**
     * Instantiates a new ArrayIndexBlock
     * @param level The Level that the Block exists in.
     * @param x The x coordinate in screen space of the Block.
     * @param y The y coordinate in screen space of the Block.
     * @param screen The BlockBuildScreen that this Block belongs to.
     */
    public constructor(level: Level, x: number, y: number, screen: BlockBuildScreen) {
        super(level, x, y, '[]', screen, colours);

        this.blockTypeString = 'arrayIndexBlock';
        this.thingsToInterpolate = [this.blockType];

        this.precedence = Precedence.ARRAY_INDEX;
    }

    public toString(addBrackets: boolean): string {
        return `${this.leftConnector.toString(addBrackets)}[${this.rightConnector.toString(addBrackets)}]`;
    }

    public typeCheck(): boolean {
        // The output type of an array index is determined by the type of the array that is indexed.
        switch (this.leftConnector.getTowardsBlockType()) {
            case BlockType.REAL_ARRAY:
                this.blockType = BlockType.REAL;
                break;
            case BlockType.REAL_ARRAY_ARRAY:
                this.blockType = BlockType.REAL_ARRAY;
                break;
            case BlockType.BOOL_ARRAY:
                this.blockType = BlockType.BOOL;
                break;
            case BlockType.BOOL_ARRAY_ARRAY:
                this.blockType = BlockType.BOOL_ARRAY;
                break;
            default:
                this.blockType = BlockType.ANY;
        }

        return TypeCheck.isArray(this.leftConnector.getTowardsBlockType())
            && TypeCheck.isReal(this.rightConnector.getTowardsBlockType());
    }
}
