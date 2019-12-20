/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University
 * within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
import Level from '../states/level';
import ConditionScanner from '../gameObjects/conditionScanner';
import Cell from '../gameObjects/grid/cell';
import Grid from '../gameObjects/grid/grid';
import Path from '../gameObjects/path/path';
import GameTask from './gameTask';

/**
 * ConditionScannerGameTask is a GameTask that manages the pre and post ConditionScanners.
 */
export default class ConditionScannerGameTask extends GameTask {
    private grid: Grid;

    /**
     * Initialize conditionScanner by creating and placing them on the grid.
     */
    public initialize(level: Level): void {
        super.initialize(level);

        this.grid = this.level.getGrid();

        const prePath: Path = this.level.getPrePath();
        const postPath: Path = this.level.getPostPath();

        const preScanner: ConditionScanner = this.placeNewScanner(prePath, 'Pre');
        prePath.setConditionScanner(preScanner);
        this.level.setPreScanner(preScanner);

        const postScanner: ConditionScanner = this.placeNewScanner(postPath, 'Post');
        postPath.setConditionScanner(postScanner);
        this.level.setPostScanner(postScanner);

        this.level.setCurrentScanner(preScanner);
    }

    /**
     * Place a new scanner
     * @param path The path on which the scanner should be placed
     * @param type Whether the scanner is for the pre- or postcondition
     */
    private placeNewScanner(path: Path, type: string): ConditionScanner {
        const checkCell: Cell = path.getCells()[3];
        const x: number = checkCell.getX();
        const y: number = checkCell.getY();

        const screenPoint: Phaser.Point = this.grid.gridSpaceToScreenSpace(x, y);

        const scanner: ConditionScanner = new ConditionScanner(this.level, screenPoint.x, screenPoint.y, checkCell, type);
        this.grid.placeHitbox(x, y, ConditionScanner.getHitbox(), scanner);
        this.level.getGameRenderGroup().add(scanner);

        return scanner;
    }

    /**
     * The general update function to run every game loop. This calls the update function for both the scanners in the current level
     */
    public update(): void {
        this.level.getPreScanner().updateObject();
        this.level.getPostScanner().updateObject();
    }

    /**
     * Check if there was a click on a conditionScanner and show input UI if it was
     * @param pointer Click position
     */
    public onMouseUp(pointer: Phaser.Pointer): boolean {
        return this.level.getPreScanner().onMouseUp(pointer) || this.level.getPostScanner().onMouseUp(pointer);
    }
}