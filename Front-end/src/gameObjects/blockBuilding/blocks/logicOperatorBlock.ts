/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University
 * within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
import ErrorState from '../../../states/errorState';
import Level from '../../../states/level';
import BlockBuildScreen from '../blockBuildScreen';
import BlockType from '../blockType';
import Precedence from '../precedence';
import TypeCheck from '../typeCheck';
import BinaryBlock from './binaryBlock';
import ConnectorColours from '../connectorColours';

export type LogicOperator = '&&' | '||';

const colours: ConnectorColours[] = [ConnectorColours.BOOLEAN, ConnectorColours.BOOLEAN];

/**
 * A Logic Operator is an operator that has two boolean operands.
 * These include: && and ||.
 */
export default class LogicOperatorBlock extends BinaryBlock {

    public className: string = 'LogicOperatorBlock';
    /**
     * Instantiates a new LogicOperatorBlock
     * @param level The Level that the Block exists in.
     * @param x The x coordinate in screen space of the Block.
     * @param y The y coordinate in screen space of the Block.
     * @param operator The logical operator that this Block represents. Can be either && or ||.
     * @param screen The BlockBuildScreen that this Block belongs to.
     */
    public constructor(level: Level, x: number, y: number, operator: LogicOperator, screen: BlockBuildScreen) {
        super(level, x, y, operator, screen, colours, BlockType.BOOL);

        this.blockTypeString = 'logicOperator';

        switch (operator) {
            case '&&':
                this.precedence = Precedence.LOGICAL_AND;
                break;
            case '||':
                this.precedence = Precedence.LOGICAL_OR;
                break;
            default:
                ErrorState.throw(this.game, 'Precedence not defined for operator ' + operator);
                return;
        }
    }

    public typeCheck(): boolean {
        return TypeCheck.isBoolean(this.leftConnector.getTowardsBlockType())
            && TypeCheck.isBoolean(this.rightConnector.getTowardsBlockType());
    }
}
