/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University
 * within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
import Level from '../../../states/level';
import BlockBuildScreen from '../blockBuildScreen';
import BlockType from '../blockType';
import Connector from '../connector';
import Precedence from '../precedence';
import Block from './block';
import ConnectorColours from '../connectorColours';

/**
 * A UnaryBlock is an operator that has one operand.
 * The operand should connect to the bottom Connector,
 */
export default abstract class UnaryBlock extends Block {
    protected childConnector: Connector;

    protected operator: string;

    /**
     * Instantiates a new UnaryBlock
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

        this.childConnector = this.addBottomConnector(colours[0]);

        this.operator = operator;

        this.precedence = Precedence.UNARY_PRE;
    }

    public toString(addBrackets: boolean): string {
        return `${this.operator}(${this.childConnector.toString(addBrackets)})`;
    }

    public getChildren(): Block[]{
        const child: Block = this.childConnector.getTowards().getBlock();
        return [child];
    }
}