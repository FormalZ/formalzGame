/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University
 * within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
import Level from '../../../states/level';
import BlockBuildScreen from '../blockBuildScreen';
import BlockType from '../blockType';
import Connector from '../connector';
import Block from './block';
import VariableBlock from './variableBlock';
import ConnectorColours from '../connectorColours';

/**
 * A BinaryBlock is a block that has a left and a right input connector.
 */
export default abstract class BinaryBlock extends Block {
    protected leftConnector: Connector;
    protected rightConnector: Connector;

    protected operator: string;

    protected associative: boolean;

    /**
     * Instantiates a new BinaryBlock
     * @param level The Level that the Block exists in.
     * @param x The x coordinate in screen space of the Block.
     * @param y The y coordinate in screen space of the Block.
     * @param operator The binary operator that this Block represents.
     * @param screen The BlockBuildScreen that this Block belongs to.
     * @param blockType The return type of the expression that this Block represents.
     * @param blockCost The amount of money necessary to purchase this Block.
     */
    public constructor(level: Level, x: number, y: number, operator: string, screen: BlockBuildScreen, colours: ConnectorColours[],
        blockType: BlockType = BlockType.ANY, blockCost: number = 40) {
        super(level, x, y, operator, screen, blockType, false, blockCost);

        this.leftConnector = this.addLeftConnector(colours[0]);
        this.rightConnector = this.addRightConnector(colours[1]);

        this.operator = operator;

        this.associative = true;
    }

    /**
     * A binary block needs to look at operator precedence
     */
    public toString(addBrackets: boolean): string {
        // NOTE: the parentheses placement algorithm assumes operators that are left-associative when parsed.
        // Assignment operators are the only binary right associative operators in Java, but they are not supported by the
        // Haskell back-end.
        const leftBlock: Block = this.leftConnector.getTowards().getBlock();
        const rightBlock: Block = this.rightConnector.getTowards().getBlock();

        let leftString: string = leftBlock.toString(addBrackets);
        let rightString: string = rightBlock.toString(addBrackets);

        // Add parentheses if the left child binds less tightly than this block's operator
        if ((addBrackets || leftBlock.getPrecedence() < this.precedence) && !(leftBlock instanceof VariableBlock))  {
            leftString = `(${leftString})`;
        }

        // Add parentheses if the right child binds less tightly than this block's operator
        // If this operator is non-associative, also add parentheses if there right child binds as tight as this block's operator
        if ((addBrackets || rightBlock.getPrecedence() < this.precedence ||
            (!this.associative && rightBlock.getPrecedence() === this.precedence)) &&
        ! (rightBlock instanceof VariableBlock)) {
            rightString = `(${rightString})`;
        }

        return `${leftString} ${this.operator} ${rightString}`;
    }

    public getChildren(): Block[]{
        const leftBlock: Block = this.leftConnector.getTowards().getBlock();
        const rightBlock: Block = this.rightConnector.getTowards().getBlock();
        return [leftBlock, rightBlock];
    }
}