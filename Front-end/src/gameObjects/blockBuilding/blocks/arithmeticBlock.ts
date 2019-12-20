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
import { StringUtils } from '../../../utils/utils';
import ConnectorColours from '../connectorColours';

export type ArithmeticOperator = '*' | '/' | '%' | '+' | '-';

const colours: ConnectorColours[] = [ConnectorColours.REAL, ConnectorColours.REAL];

/**
 * An ArithmeticBlock is an operator that has two real number operands.
 * These include: *, /, %, + and -
 */
export default class ArithmeticBlock extends BinaryBlock {

    public className: string = 'ArithmeticBlock';
    /**
     * Instantiates a new ArithmeticBlock
     * @param level The Level that the Block exists in.
     * @param x The x coordinate in screen space of the Block.
     * @param y The y coordinate in screen space of the Block.
     * @param operator The arithmetic operator that this Block represents. Arithmetic operators are: *, /, %, + and -
     * @param screen The BlockBuildScreen that this Block belongs to.
     */
    public constructor(level: Level, x: number, y: number, operator: ArithmeticOperator, screen: BlockBuildScreen) {
        super(level, x, y, operator, screen, colours, BlockType.REAL);

        this.blockTypeString = 'arithmeticBlock';

        switch (operator) {
            case '*':
            case '/':
            case '%':
                this.precedence = Precedence.MULTIPLICATIVE;
                break;
            case '+':
            case '-':
                this.precedence = Precedence.ADDITIVE;
                break;
            default:
                const noPrecedence: string = this.game.cache.getJSON('warningsText')['arithmeticBlock']['noPrecedence'];

                ErrorState.throw(this.game, StringUtils.interpolateString(noPrecedence, operator));
                return;
        }

        // Division and subtraction are non-associative.
        // This information is needed to determine where to place parentheses in the expression.
        if (operator === '/' || operator === '-') {
            this.associative = false;
        }
    }

    public typeCheck(): boolean {
        return TypeCheck.isReal(this.leftConnector.getTowardsBlockType())
            && TypeCheck.isReal(this.rightConnector.getTowardsBlockType());
    }
}
