/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University
 * within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
import PathTurn from '../../gameObjects/path/pathTurn';
import Direction from './direction';

/**
 * PathData is a data structure that defines the flow of both the pre and the post condition Path.
 */
export default class PathData {
    private startAngle: number;

    private preStartPoint: Phaser.Point;
    private prePathTurns: PathTurn[];

    private postStartPoint: Phaser.Point;
    private postPathTurns: PathTurn[];

    constructor(startAngle: number, preStartPoint: Phaser.Point, prePathTurns: PathTurn[],
        postStartPoint: Phaser.Point, postPathTurns: PathTurn[]) {

        this.startAngle = startAngle;

        this.preStartPoint = preStartPoint;
        this.prePathTurns = prePathTurns;

        this.postStartPoint = postStartPoint;
        this.postPathTurns = postPathTurns;
    }

    public getStartAngle(): number { return this.startAngle; }
    public getPreStartPoint(): Phaser.Point { return this.preStartPoint; }
    public getPrePathTurns(): PathTurn[] { return this.prePathTurns; }
    public getPostStartPoint(): Phaser.Point { return this.postStartPoint; }
    public getPostPathTurns(): PathTurn[] { return this.postPathTurns; }

    /**
     * Parse the path from a string to a usable PathData.
     * @param path all the data for the path as a string.
     * @returns all the data for the path as a PathData.
     */
    public static makePath(path: string): PathData {
        const parts: string[] = path.split(';');
        const preStartPoint: Phaser.Point = this.makePoint(parts[0]);
        const prePathsTurns: PathTurn[] = this.makePathTurns(parts[1]);

        const startAngle: number = prePathsTurns[0].getDirection();
        prePathsTurns[0] = new PathTurn(Direction.FORWARD, prePathsTurns[0].getDistance());

        const postStartPoint: Phaser.Point = this.makePoint(parts[2]);
        const postPathTurns: PathTurn[] = this.makePathTurns(parts[3]);

        return new PathData(startAngle, preStartPoint, prePathsTurns, postStartPoint, postPathTurns);
    }

    /**
     * Make the starting point of the path into a Phaser.Point.
     * @param point the point with all the extraneous elements around it.
     * @returns the (x,y) coordinates of the starting point of the path.
     */
    private static makePoint(point: string): Phaser.Point {
        const [x, y] = point.substring(1, point.length - 1).split('.');

        return new Phaser.Point(parseInt(x), parseInt(y));
    }

    /**
     * Make the PathTurns into a usable format.
     * @param allTurns all the turns of a path with all the extraneous elements around it.
     * @returns the PathTurn[] for the current path.
     */
    private static makePathTurns(allTurns: string): PathTurn[] {
        const turns: string[] = allTurns.substring(1, allTurns.length - 1).split('.');
        const output: PathTurn[] = [];

        turns.forEach(turn => {
            const [len, dir] = turn.substring(1, turn.length - 1).split(',');

            output.push(new PathTurn(parseInt(dir), parseInt(len)));
        });

        return output;
    }
}