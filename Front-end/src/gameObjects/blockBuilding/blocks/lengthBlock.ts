/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University
 * within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
import Level from '../../../states/level';
import BlockBuildScreen from '../blockBuildScreen';
import BlockType from '../blockType';
import TypeCheck from '../typeCheck';
import UnaryBlock from './unaryBlock';
import ConnectorColours from '../connectorColours';

const colours: ConnectorColours[] = [ConnectorColours.ARRAY];

/**
 * Block to access the length property of an array-type variable
 * This functions as a unary operation on the variable in question.
 * The variable Connector should connect to the corresponding variable
 */
export default class LengthBlock extends UnaryBlock {

    public className: string = 'LengthBlock';
    /**
     * Instantiates a new LengthBlock
     * @param level The Level that the Block exists in.
     * @param x The x coordinate in screen space of the Block.
     * @param y The y coordinate in screen space of the Block.
     * @param screen The BlockBuildScreen that this Block belongs to.
     */
    public constructor(level: Level, x: number, y: number, screen: BlockBuildScreen) {
        super(level, x, y, 'length', screen, colours, BlockType.REAL);

        this.blockTypeString = 'lengthBlock';
    }

    public toString(addBrackets: boolean): string {
        return `${this.childConnector.toString(addBrackets)}.length`;
    }

    public typeCheck(): boolean {
        return TypeCheck.isArray(this.childConnector.getTowardsBlockType());
    }
}
