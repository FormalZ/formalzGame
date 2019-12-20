/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University
 * within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
import Level from '../../../states/level';
import BlockBuildScreen from '../blockBuildScreen';
import BlockType from '../blockType';
import NullaryBlock from './nullaryBlock';

export type Boolean = 'true' | 'false';

/**
 * BooleanBlock is a constant nullary Block that has a boolean value that never changes.
 */
export default class BooleanBlock extends NullaryBlock {

    public className: string = 'BooleanBlock';
    /**
     * Instantiates a new BooleanBlock
     * @param level The Level that the Block exists in.
     * @param x The x coordinate in screen space of the Block.
     * @param y The y coordinate in screen space of the Block.
     * @param value The boolean value that this Block represents. Can be either true or false.
     * @param screen The BlockBuildScreen that this Block belongs to.
     */
    public constructor(level: Level, x: number, y: number, value: Boolean, screen: BlockBuildScreen) {
        super(level, x, y, value, screen, BlockType.BOOL);

        this.blockTypeString = 'booleanBlock';
    }
}
