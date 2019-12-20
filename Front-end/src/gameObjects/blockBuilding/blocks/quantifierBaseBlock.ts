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
import { Quantifier } from './quantifierBlock';
import { QuantifierRange } from './quantifierRangeBlock';
import VariableBlock from './variableBlock';
import ConnectorColours from '../connectorColours';

/**
 * Base class that abstracts away code that both the QuantifierBlock and the QuantifierRangeBlock classes share.
 */
export default abstract class QuantifierBaseBlock extends Block {
    protected arrayConnector: Connector;
    protected indexConnector: Connector;
    protected predicateConnector: Connector;

    protected quantifier: Quantifier | QuantifierRange;
    /**
     * Instantiates a new QuantifierBaseBlock
     * @param level The Level that the Block exists in.
     * @param x The x coordinate in screen space of the Block.
     * @param y The y coordinate in screen space of the Block.
     * @param quantifier The quantifier that this Block represents.
     * @param screen The BlockBuildScreen that this Block belongs to.
     * @param blockCost The amount of money necessary to purchase this Block.
     */
    public constructor(level: Level, x: number, y: number, quantifier: Quantifier | QuantifierRange, screen: BlockBuildScreen,
        blockCost: number = 40) {

        super(level, x, y, quantifier.replace('forall', '∀').replace('exists', '∃'), screen, BlockType.BOOL, false, blockCost);

        this.quantifier = quantifier;
    }

    public typeCheck(): boolean {
        return TypeCheck.isArray(this.arrayConnector.getTowardsBlockType())
            && TypeCheck.isReal(this.indexConnector.getTowardsBlockType())
            // Assert that the index block is actually a variable, instead of an expression:
            && (!this.indexConnector.getTowards() || this.indexConnector.getTowards().getBlock() instanceof VariableBlock)
            && TypeCheck.isBoolean(this.predicateConnector.getTowardsBlockType());
    }
    public getChildren(): Block[]{
        const arrayBlock: Block = this.arrayConnector.getTowards().getBlock();
        const indexBlock: Block = this.indexConnector.getTowards().getBlock();
        const predBlock: Block = this.predicateConnector.getTowards().getBlock();
        return [arrayBlock, indexBlock, predBlock];
    }
}