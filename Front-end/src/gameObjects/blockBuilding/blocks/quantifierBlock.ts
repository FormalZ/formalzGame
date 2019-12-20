/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University
 * within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
import Level from '../../../states/level';
import BlockBuildScreen from '../blockBuildScreen';
import QuantifierBaseBlock from './quantifierBaseBlock';
import ConnectorColours from '../connectorColours';

export type Quantifier = 'forall' | 'exists';

const colours: ConnectorColours[] = [ConnectorColours.ARRAY, ConnectorColours.REAL, ConnectorColours.BOOLEAN];

/**
 * Block that defines a Quantifier over an array.
 * The left connector corresponds to the array in question
 * The right connector corresponds to the predicate that the quantifier checks over the array.
 */
export default class QuantifierBlock extends QuantifierBaseBlock {

    public className: string = 'QuantifierBlock';

    /**
     * Instantiates a new QuantifierBlock
     * @param level The Level that the Block exists in.
     * @param x The x coordinate in screen space of the Block.
     * @param y The y coordinate in screen space of the Block.
     * @param quantifier The quantifier that this Block represents.
     * @param screen The BlockBuildScreen that this Block belongs to.
     */
    public constructor(level: Level, x: number, y: number, quantifier: Quantifier, screen: BlockBuildScreen) {
        super(level, x, y, quantifier, screen);

        this.blockTypeString = 'quantifierBlock';

        this.arrayConnector = this.addLeftConnector(colours[0]);
        this.indexConnector = this.addBottomConnector(colours[1]);
        this.predicateConnector = this.addRightConnector(colours[2]);
    }

    public toString(addBrackets: boolean): string {
        return `${this.quantifier}(${this.arrayConnector.toString(addBrackets)}, ` +
            `${this.indexConnector.toString(addBrackets)} -> ${this.predicateConnector.toString(addBrackets)})`;
    }
}
