/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University
 * within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
import FunctionBlock from '../../gameObjects/functionBlock';
import Grid from '../../gameObjects/grid/grid';
import Level from '../../states/level';
import GameTask from '../gameTask';

/**
 * FunctionBlockGameTask is a GameTask that initializes the FunctionBlock sprite, based on the location of the Paths.
 */
export default class FunctionBlockGameTask extends GameTask {
    /**
     * Initialize the function CPU block between the pre and post paths.
     */
    public initialize(level: Level): void {
        super.initialize(level);

        const grid: Grid = this.level.getGrid();

        // Get the last waypoint of the pre condition path
        const waypoints: Phaser.Point[] = this.level.getPrePath().getWaypoints();
        const lastWaypoint: Phaser.Point = waypoints[waypoints.length - 1];

        const cellSize: number = grid.getCellSize();

        // Calculate the position of the function block based on the last waypoint
        const xPos: number = lastWaypoint.x + cellSize - 16;
        const yPos: number = lastWaypoint.y - 3 * cellSize;
        const gridPos: Phaser.Point = grid.screenSpaceToGridSpace(xPos, yPos);

        // Place the function block
        const functionBlock: FunctionBlock = new FunctionBlock(this.level, xPos, yPos, 64, 64);
        functionBlock.anchor.set(0.5, 0);

        grid.placeHitbox(gridPos.x, gridPos.y, functionBlock.getHitbox(), functionBlock);

        this.level.getGameRenderGroup().add(functionBlock);
    }
}
