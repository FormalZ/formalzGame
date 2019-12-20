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

export type RelationalComparer = '>=' | '>' | '<' | '<=';

const colours: ConnectorColours[] = [ConnectorColours.REAL, ConnectorColours.REAL];

/**
 * A RelationalBlock is a binary operator that operates on two reals and compares their size.
 * These operators include >=, >, <= and <.
 */
export default class RelationalBlock extends BinaryBlock {

    public className: string = 'RelationalBlock';
    /**
     * Instantiates a new RelationalBlock
     * @param level The Level that the Block exists in.
     * @param x The x coordinate in screen space of the Block.
     * @param y The y coordinate in screen space of the Block.
     * @param operator The relational comparer that this Block represents. This includes >= > < and <=.
     * @param screen The BlockBuildScreen that this Block belongs to.
     */
    public constructor(level: Level, x: number, y: number, operator: RelationalComparer, screen: BlockBuildScreen) {
        super(level, x, y, operator, screen, colours, BlockType.BOOL);

        this.blockTypeString = 'relationalBlock';

        this.precedence = Precedence.RELATIONAL;
    }

    public typeCheck(): boolean {
        return TypeCheck.isReal(this.leftConnector.getTowardsBlockType())
            && TypeCheck.isReal(this.rightConnector.getTowardsBlockType());
    }
}
