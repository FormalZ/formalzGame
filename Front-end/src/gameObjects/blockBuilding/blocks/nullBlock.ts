/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University
 * within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
import Level from '../../../states/level';
import BlockBuildScreen from '../blockBuildScreen';
import BlockType from '../blockType';
import NullaryBlock from './nullaryBlock';

/**
 * A Block that represents a null-pointer
 */
export default class NullBlock extends NullaryBlock {

    public className: string = 'NullBlock';
    /**
     * Instantiates a new NullBlock
     * @param level The Level that the Block exists in.
     * @param x The x coordinate in screen space of the Block.
     * @param y The y coordinate in screen space of the Block.
     * @param screen The BlockBuildScreen that this Block belongs to.
     */
    public constructor(level: Level, x: number, y: number, screen: BlockBuildScreen) {
        super(level, x, y, 'null', screen, BlockType.NULL);

        this.blockTypeString = 'nullBlock';
    }
}
