/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University
 * within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
import Level from '../../../states/level';
import Dictionary from '../../../utils/dictionary';
import BlockBuildScreen from '../blockBuildScreen';
import BlockType from '../blockType';
import NullaryBlock from './nullaryBlock';
import { StringUtils } from '../../../utils/utils';
import { BlockTypeToColour } from '../connectorColours';

/**
 * Nullary Block that defines a variable name
 */
export default class VariableBlock extends NullaryBlock {
    private variableName: string;

    public className: string = 'VariableBlock';

    /**
     * Instantiates a new VariableBlock
     * @param level The Level that the Block exists in.
     * @param x The x coordinate in screen space of the Block.
     * @param y The y coordinate in screen space of the Block.
     * @param name The name of the variable that this Block represents.
     * @param screen The BlockBuildScreen that this Block belongs to.
     */
    public constructor(level: Level, x: number, y: number, name: string, screen: BlockBuildScreen) {
        super(level, x, y, name, screen);

        this.blockTypeString = 'variableBlock';


        this.variableName = name;

        const validTypes: Dictionary<BlockType> = level.getValidTypes();
        if (validTypes.containsKey(name)) {
            this.blockType = validTypes.get(name);
        } else {
            const warning: string = this.game.cache.getJSON('warningsText')['variableBlock']['notReceivedType'];
            console.warn(StringUtils.interpolateString(warning, this.variableName));
        }

        this.thingsToInterpolate = [this.blockType];

        if (this.getTopConnector()) {
            this.getTopConnector().setColour(BlockTypeToColour(this.blockType));
        }
    }
}
