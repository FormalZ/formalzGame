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
import ConnectorColours from '../connectorColours';

/**
 * BeginBlock is an entry point into the blockbuidling tree.
 * Multiple BeginBlocks can be added into 1 screen.
 */
export default class BeginBlock extends Block {
    private entryPoint: Connector;

    public className: string = 'BeginBlock';

    /**
     * Instantiates a new BeginBlock
     * @param level The Level that the Block exists in.
     * @param x The x coordinate in screen space of the Block.
     * @param y The y coordinate in screen space of the Block.
     * @param screen The BlockBuildScreen that this Block belongs to.
     */
    public constructor(level: Level, x: number, y: number, screen: BlockBuildScreen) {
        super(level, x, y, 'Begin', screen, BlockType.BOOL, true, 0);

        this.blockTypeString = 'beginBlock';

        this.entryPoint = this.addBottomConnector(ConnectorColours.BOOLEAN);
    }

    public toString(addBrackets: boolean): string {
        return this.entryPoint.toString(addBrackets);
    }

    public typeCheck(): boolean {
        return TypeCheck.isBoolean(this.entryPoint.getTowardsBlockType());
    }

    public getChildren(): Block[]{
        const child: Block = this.entryPoint.getTowards().getBlock();
        return [child];
    }
}
