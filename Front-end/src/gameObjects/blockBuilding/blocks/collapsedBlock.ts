/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University
 * within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
import Level from '../../../states/level';
import BlockBuildScreen from '../blockBuildScreen';
import NullaryBlock from './nullaryBlock';
import Block from './block';

/**
 * Collapsed block is a block in which a subtree is collapsed
 */
export default class CollapsedBlock extends NullaryBlock {

    public className: string = 'CollapsedBlock';
    /**
     * Instantiates a new BooleanBlock
     * @param level The Level that the Block exists in.
     * @param x The x coordinate in screen space of the Block.
     * @param y The y coordinate in screen space of the Block.
     * @param value The boolean value that this Block represents. Can be either true or false.
     * @param screen The BlockBuildScreen that this Block belongs to.
     */

    private begin: Block;
    public constructor(level: Level, x: number, y: number, value: string, screen: BlockBuildScreen, begin: Block) {
        super(level, x, y, value, screen, begin.getType(), 500);
        this.begin = begin;
        this.blockTypeString = 'collapsedBlock';
    }

    public toString(addBrackets: boolean): string {
        return this.begin.toString(addBrackets);
    }

    public setCost(cost: number): void{
        this.blockCost = cost;
    }

    public getBegin(): Block{
        return this.begin;
    }
}
