/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University
 * within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
import Level from '../../states/level';
import GeometryUtils from '../../utils/geometryUtils';
import ConditionScanner from '../conditionScanner';
import GameObject from '../gameObject';
import Cell from '../grid/cell';
import Grid from '../grid/grid';
import Spark from '../sparks/spark';
import PathTurn from './pathTurn';

// Helper variables to improve the readability of the code
const pi: number = Math.PI;
const piOver2: number = Math.PI / 2;
const piOver4: number = Math.PI / 4;

/**
 * A Path is a collection of waypoints that define a path.
 */
export default class Path extends GameObject {
    private origin: Phaser.Point;

    private waypoints: Phaser.Point[];
    private waypointDistances: number[];

    private cells: Cell[];

    private conditionScanner: ConditionScanner;

    /**
     * Callback function, gets called when a spark reaches this path's end.
     * Should be set to actual implementation in pathGameTask.
     */
    private onReachedEnd: (spark: Spark) => void;

    /**
    * The constructor for a Path.
    * @param game The Phaser.Game object
    * @param origin The starting point of the path in grid space;
    */
    constructor(level: Level, origin: Phaser.Point) {
        super(level, 0, 0, null);

        this.origin = origin;

        this.waypoints = [];
        this.waypointDistances = [];

        this.cells = [];

        this.game.add.existing(this);
    }

    /**
    * Draws the path to the background tilemap
    * @param pathTurns the instructions for how the path should flow
    * @param startingAngle the angle that the path begins with
    */
    public draw(pathTurns: PathTurn[], startingAngle: number = 0): void {
        const grid: Grid = this.level.getGrid();
        const tilemap: Phaser.Tilemap = grid.getTilemap();
        const cellSize: number = grid.getCellSize();

        // Cursor is the position of the current cell in the grid
        let cursor: Phaser.Point = this.origin.clone();
        // Angle is the current angle that the path is on
        let angle: number = startingAngle;
        let oldAngle: number = startingAngle;

        // Add the first waypoint
        this.waypoints.push(grid.gridSpaceToScreenSpace(cursor.x, cursor.y).add(cellSize / 2, cellSize / 2));
        this.waypointDistances.push(0);


        for (let i: number = 0; i < pathTurns.length; i++) {
            const pathTurn: PathTurn = pathTurns[i];
            oldAngle = angle;
            // Turn the angle based on the current pathTurn
            angle = (angle + pathTurn.getDirection()) % 360;
            if (angle < 0) {
                angle += 360;
            }

            let distance: number = pathTurn.getDistance();
            let deltaAngle: number = GeometryUtils.degreesToRadians(pathTurn.getDirection());

            // Add tiles in the current direction, based on the distance defined in the current PathTurn
            while (distance > 0) {
                // Calculate the next cursor based on the angle.
                const radians: number = GeometryUtils.degreesToRadians(angle);
                const next: Phaser.Point = new Phaser.Point(Math.round(Math.cos(radians)), -Math.round(Math.sin(radians)));

                // Draw the tile
                // If delta angle is zero, it means we're going straight ahead, otherwise it's a corner.
                const linePart: boolean = distance % 3 === 0;
                const startingLine: boolean = i === 0 && distance % 3 === 2;
                const endingLine: boolean = i === pathTurns.length - 1 && distance % 3 === 2;
                if (deltaAngle === 0) {
                    this.putStraight(tilemap, cursor.x, cursor.y, radians, linePart, startingLine, endingLine);
                } else {
                    this.putCorner(tilemap, cursor.x, cursor.y, GeometryUtils.degreesToRadians(oldAngle), deltaAngle);
                }
                // If we're going diagonal, add two extra tiles on both sides of the path.
                // if (GeometryUtils.almostEquals(radians % piOver2, piOver4)) {
                //     this.putTile(tilemap, cursor.x, cursor.y + next.y, 3, -radians + piOver4);
                //     this.putTile(tilemap, cursor.x + next.x, cursor.y, 3, -radians - piOver4 - piOver2);
                // }

                // Update the cell to mark it as occupied
                const cell: Cell = grid.getCell(cursor.x, cursor.y);
                cell.setObject(this);
                this.cells.push(cell);

                // Update the cursor
                cursor.add(next.x, next.y);

                distance--;
                deltaAngle = 0;
            }

            // Add the newly calculated waypoint
            this.waypoints.push(grid.gridSpaceToScreenSpace(cursor.x, cursor.y).add(cellSize / 2, cellSize / 2));

            // Calculate the actual distance from the origin to this waypoint along the path,
            // based on the previous distance plus the distance between the newest two waypoints.
            this.waypointDistances.push(this.waypointDistances[this.waypointDistances.length - 1] +
                Phaser.Point.subtract(
                    this.waypoints[this.waypoints.length - 2],
                    this.waypoints[this.waypoints.length - 1]
                ).getMagnitude());

        }
    }

    /**
     * Places a tile containing a straight path segment at (x, y) in the grid.
     * @param tilemap the tilemap to place the tile in.
     * @param x the x coordinate of the tile in grid space.
     * @param y the y coordinate of the tile in grid space.
     * @param angle the angle in radians the the path is currently on.
     */
    private putStraight(tilemap: Phaser.Tilemap, x: number, y: number, angle: number,
        linePart: boolean, start: boolean, end: boolean): void {
        let index: number;
        let rotation: number;

        // Find the appropriate index and rotation angle
        if (GeometryUtils.almostEquals(angle % piOver2, 0)) {
            // If we're going non-diagonal
            // index = 0;
            if (start || end) {
                index = 4;
            }
            else {
                linePart ? index = 0 : index = 2;
            }
            rotation = angle;
        } else if (GeometryUtils.almostEquals(angle % piOver2, piOver4)) {
            // If we're going diagonal
            if (start || end) {
                index = 5;
            }
            else {
                linePart ? index = 1 : index = 3;
            }
            rotation = angle - piOver4;
        }

        this.putTile(tilemap, x, y, index, rotation);
    }

    /**
     * Places a tile containing a 45 degree corner path segment at (x, y) in the grid.
     * @param tilemap the tilemap to place the tile in.
     * @param x the x coordinate of the tile in grid space.
     * @param y the y coordinate of the tile in grid space.
     * @param angle the angle in radians the the path was on before the corner.
     * @param deltaAngle the angle of the corner in radians.
     */
    private putCorner(tilemap: Phaser.Tilemap, x: number, y: number, angle: number, deltaAngle: number): void {
        let rotation: number;
        let flipped: boolean;
        let index: number;

        // Find the appropriate rotation and whether or not the flip the image
        if (GeometryUtils.almostEquals(angle % piOver2, 0)) {
            // If we're turning from non-diagonal to diagonal
            if (deltaAngle > 0) {
                // Left turn
                rotation = angle;
                flipped = false;
            } else {
                // Right turn
                rotation = angle + pi;
                flipped = true;
            }
            index = 1;
        } else if (GeometryUtils.almostEquals(angle % piOver2, piOver4)) {
            // If we're turning from diagonal to non-diagonal
            if (deltaAngle > 0) {
                // Left turn
                rotation = angle + piOver4;
                flipped = true;
            } else {
                // Right turn
                rotation = angle + pi - piOver4;
                flipped = false;
            }
            index = 0;
        }

        this.putTile(tilemap, x, y, index, rotation, flipped);
    }

    /**
     * Places a specified tile at position (x, y) in the gird
     * @param tilemap the Phaser.Tilemap to place the tile in.
     * @param x the x coordinate of the tile in grid space.
     * @param y the y coordinate of the tile in grid space.
     * @param index the index of the tile in the tilesheet.
     * @param rotation the rotation of the tile in radians.
     * @param flipped whether or not to mirror the tile (default: false).
     */
    private putTile(tilemap: Phaser.Tilemap, x: number, y: number, index: number, rotation: number, flipped: boolean = false): void {
        if (this.level.getGrid().isWithinBounds(x, y)) {
            // NOTE: tile is typed as any because the TypeScript declaration of Phaser.Tile seems
            // to be missing the 'rotation' and 'flipped' properties, although they do actually work.
            const tile: any = tilemap.putTile(index, x, y);
            // Phaser's rotation is clock-wise, so we invert the rotation
            tile.rotation = -rotation;
            tile.flipped = flipped;
        } else if (DEBUG) {
            console.error(`Index grid out of bounds at (${x}, ${y})`);
        }
    }

    /**
     * @returns an array of waypoints in screen space.
     */
    public getWaypoints(): Phaser.Point[] { return this.waypoints; }

    /**
     * @returns an array of distances for all waypoints.
     * Distances are accumulated for each waypoint.
     * These distances could also be calculated from the waypoints array,
     * however this array allows access to them in O(1) time.
     */
    public getWaypointDistances(): number[] { return this.waypointDistances; }

    public getCells(): Cell[] { return this.cells; }

    public getOnReachedEnd(): (spark: Spark) => void { return this.onReachedEnd; }
    public setOnReachedEnd(onReachedEnd: (spark: Spark) => void): void { this.onReachedEnd = onReachedEnd; }

    public getConditionScanner(): ConditionScanner { return this.conditionScanner; }
    public setConditionScanner(conditionTower: ConditionScanner): void { this.conditionScanner = conditionTower; }
}
