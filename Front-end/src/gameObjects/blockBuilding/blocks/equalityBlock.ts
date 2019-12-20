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

export type EqualityComparer = '==' | '!=';

const colours: ConnectorColours[] = [ConnectorColours.POLYMORPHIC, ConnectorColours.POLYMORPHIC];

/**
 * An EqualityBlock is an operator that checks the equality of two primitive operands
 * These include: == and !=
 */
export default class EqualityBlock extends BinaryBlock {

    public className: string = 'EqualityBlock';
    /**
     * Instantiates a new EqualityBlock
     * @param level The Level that the Block exists in.
     * @param x The x coordinate in screen space of the Block.
     * @param y The y coordinate in screen space of the Block.
     * @param operator The equality comparer that this Block represents. Can be either == or !=.
     * @param screen The BlockBuildScreen that this Block belongs to.
     */
    public constructor(level: Level, x: number, y: number, operator: EqualityComparer, screen: BlockBuildScreen) {
        super(level, x, y, operator, screen, colours, BlockType.BOOL);

        this.blockTypeString = 'equalityBlock';

        this.precedence = Precedence.EQUALITY;

        this.associative = false;
    }

    public typeCheck(): boolean {
        return TypeCheck.equals(this.leftConnector.getTowardsBlockType(), this.rightConnector.getTowardsBlockType());
    }
}
