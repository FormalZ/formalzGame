/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University
 * within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
import Level from '../../../states/level';
import BlockBuildScreen from '../blockBuildScreen';
import BlockType from '../blockType';
import TypeCheck from '../typeCheck';
import UnaryBlock from './unaryBlock';
import ConnectorColours from '../connectorColours';

const colours: ConnectorColours[] = [ConnectorColours.BOOLEAN];

/**
 * A NotBlock is block that takes in a boolean and inverts it.
 * The operand should connect to the bottom Connector,
 */
export default class NotBlock extends UnaryBlock {

    public className: string = 'NotBlock';
    /**
     * Instantiates a new NotBlock
     * @param level The Level that the Block exists in.
     * @param x The x coordinate in screen space of the Block.
     * @param y The y coordinate in screen space of the Block.
     * @param screen The BlockBuildScreen that this Block belongs to.
     */
    public constructor(level: Level, x: number, y: number, screen: BlockBuildScreen) {
        super(level, x, y, '!', screen, colours, BlockType.BOOL);

        this.blockTypeString = 'notBlock';
    }

    public typeCheck(): boolean {
        return TypeCheck.isBoolean(this.childConnector.getBlock().getType());
    }
}
