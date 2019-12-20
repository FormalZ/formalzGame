/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University
 * within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
import Level from '../../../states/level';
import BlockBuildScreen from '../blockBuildScreen';
import BlockType from '../blockType';
import Connector from '../connector';
import TypeCheck from '../typeCheck';
import Block from './block';
import VariableBlock from './variableBlock';
import ConnectorColours from '../connectorColours';

const colours: ConnectorColours[] = [ConnectorColours.ANY, ConnectorColours.ANY, ConnectorColours.BOOLEAN];

export default class WithBlock extends Block {
    private genericConnector: Connector;
    private variableConnector: Connector;
    private predicateConnector: Connector;

    public className: string = 'WithBlock';

    /**
     * Instantiates a new WithBlock
     * @param level The Level that the Block exists in.
     * @param x The x coordinate in screen space of the Block.
     * @param y The y coordinate in screen space of the Block.
     * @param screen The BlockBuildScreen that this Block belongs to.
     */
    public constructor(level: Level, x: number, y: number, screen: BlockBuildScreen) {
        super(level, x, y, 'with', screen, BlockType.BOOL);

        this.blockTypeString = 'withBlock';
        this.genericConnector = this.addLeftConnector(colours[0]);
        this.variableConnector = this.addBottomConnector(colours[1]);
        this.predicateConnector = this.addRightConnector(colours[2]);

        this.thingsToInterpolate = [this.genericConnector.getTowardsBlockType(), this.variableConnector.getTowardsBlockType()];
    }

    public toString(addBrackets: boolean): string {
        return `with(${this.genericConnector.toString(addBrackets)}, ` +
            `${this.variableConnector.toString(addBrackets)} -> ${this.predicateConnector.toString(addBrackets)})`;
    }

    public typeCheck(): boolean {
        // The left connector is a generic type so it doesn't need to be type-checked
        return TypeCheck.equals(this.genericConnector.getTowardsBlockType(), this.variableConnector.getTowardsBlockType())
            // Assert that the index block is actually a variable, instead of an expression:
            && (!this.variableConnector.getTowards() || this.variableConnector.getTowards().getBlock() instanceof VariableBlock)
            && TypeCheck.isBoolean(this.predicateConnector.getTowardsBlockType());
    }

    public getChildren(): Block[]{
        const genericBlock: Block = this.genericConnector.getTowards().getBlock();
        const variableBlock: Block = this.variableConnector.getTowards().getBlock();
        const predBlock: Block = this.predicateConnector.getTowards().getBlock();
        return [genericBlock, variableBlock, predBlock];
    }
}
