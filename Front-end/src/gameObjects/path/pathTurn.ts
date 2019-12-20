/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University
 * within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
import Direction from './direction';

/**
 * A PathTurn describes a single turn in the path, along with the distance to travel after that turn.
 * PathTurn makes use of the Direction enum to describe it's turn.
 * This ensures that only 45 degree angles can appear in the path.
 * Because PathTurns define relative angles and distances,
 * the path can be started at 8 starting-orientations and any origin position within the grid.
 */
export default class PathTurn {
    private direction: Direction;
    private distance: number;

    /**
     * Constructor for a PathTurn.
     * PathTurns describes the flow of a path.
     * @param direction the current turn the path should take.
     * @param distance the distance after the turn.
     */
    constructor(direction: Direction, distance: number) {
        this.direction = direction;
        this.distance = distance;
    }

    public getDirection(): Direction { return this.direction; }
    public getDistance(): number { return this.distance; }
}
