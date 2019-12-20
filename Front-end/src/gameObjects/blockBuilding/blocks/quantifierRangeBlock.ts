/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University
 * within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
import Level from '../../../states/level';
import BlockBuildScreen from '../blockBuildScreen';
import Connector from '../connector';
import TypeCheck from '../typeCheck';
import QuantifierBaseBlock from './quantifierBaseBlock';
import ConnectorColours from '../connectorColours';

export type QuantifierRange = 'forallr' | 'existsr';

const colours: ConnectorColours[] = [ConnectorColours.ARRAY, ConnectorColours.REAL,
    ConnectorColours.REAL, ConnectorColours.REAL, ConnectorColours.BOOLEAN];

/**
 * Block that defines a Quantifier over a limited range of an array.
 * The left connector corresponds to the array in question
 * The bottom-left connector corresponds to the begin of the range
 * The bottom connector corresponds to the end of the range
 * The bottom-right connector corresponds to the variable that is the argument of the predicate
 * The right connector corresponds to the predicate that the quantifier checks over the array.
 */
export default class QuantifierRangeBlock extends QuantifierBaseBlock {
    private beginConnector: Connector;
    private endConnector: Connector;

    public className: string = 'QuantifierRangeBlock';

    /**
     * Instantiates a new QuantifierRangeBlock
     * @param level The Level that the Block exists in.
     * @param x The x coordinate in screen space of the Block.
     * @param y The y coordinate in screen space of the Block.
     * @param quantifier The quantifier that this Block represents.
     * @param screen The BlockBuildScreen that this Block belongs to.
     */
    public constructor(level: Level, x: number, y: number, quantifier: QuantifierRange, screen: BlockBuildScreen) {
        super(level, x, y, quantifier, screen);

        this.blockTypeString = 'quantifierRangeBlock';

        // The order in which these Connector are added matters for the normalization algorithm
        // This also means that the array, index and predicate Connectors cannot be initialized inside the super class, sadly.
        this.arrayConnector = this.addLeftConnector(colours[0]);
        this.beginConnector = this.addBottomLeftConnector(colours[1]);
        this.endConnector = this.addBottomConnector(colours[2]);
        this.indexConnector = this.addBottomRightConnector(colours[3]);
        this.predicateConnector = this.addRightConnector(colours[4]);
    }

    public toString(addBrackets: boolean): string {
        return `${this.quantifier}(${this.arrayConnector.toString(addBrackets)}, ` +
            `${this.beginConnector.toString(addBrackets)}, ${this.endConnector.toString(addBrackets)}, ` +
            `${this.indexConnector.toString(addBrackets)} -> ${this.predicateConnector.toString(addBrackets)})`;
    }

    public typeCheck(): boolean {
        return super.typeCheck()
            && TypeCheck.isReal(this.beginConnector.getTowardsBlockType())
            && TypeCheck.isReal(this.endConnector.getTowardsBlockType());
    }
}
