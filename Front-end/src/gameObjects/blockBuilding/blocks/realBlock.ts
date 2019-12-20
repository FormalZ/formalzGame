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
 * RealBlock is a constant nullary Block that has a real number value that never changes.
 */
export default class RealBlock extends NullaryBlock {

    public className: string = 'RealBlock';
    /**
     * Instantiates a new RealBlock
     * @param level The Level that the Block exists in.
     * @param x The x coordinate in screen space of the Block.
     * @param y The y coordinate in screen space of the Block.
     * @param value The real number that this Block represents.
     * @param screen The BlockBuildScreen that this Block belongs to.
     */
    public constructor(level: Level, x: number, y: number, value: number, screen: BlockBuildScreen) {
        super(level, x, y, value.toString(), screen, BlockType.REAL);
        this.blockTypeString = 'realBlock';
    }
}
