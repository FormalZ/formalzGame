/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University
 * within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
import ErrorState from '../../states/errorState';
import GameObject from '../gameObject';
import Cell from './cell';
import Hitbox from './hitbox';

export default class Grid {
    private cellSize: number;

    private cells: Cell[];

    private tilemap: Phaser.Tilemap;

    private width: number; // in grid space
    private height: number; // in grid space

    private origin: Phaser.Point; // Coordinates of the top-left corner (in screen space)
    private offset: Phaser.Point; // Grid offset relative to the top-left corner (in grid space)

    /**
     * Construct a grid datastructure
     * @param cellSize The size of a single cell. Cells are always square, therefor cellSize is determined by the width of a cell.
     * @param width The width of the grid in amount of cells
     * @param height The height of the grid in amount of cells
     * @param origin The coordinates of the origin point of the grid.
     */
    constructor(cellSize: number, width: number, height: number, origin: Phaser.Point) {
        this.cellSize = cellSize;

        this.width = width;
        this.height = height;

        this.cells = new Array<Cell>(this.width * this.height);

        this.origin = origin;
        this.offset = new Phaser.Point(0, 0);

        for (let y: number = 0; y < this.height; y++) {
            for (let x: number = 0; x < this.width; x++) {
                this.cells[x + y * this.width] = new Cell(x, y);
            }
        }
    }

    /**
     * Check if a hitbox placed at (x, y) (in grid space) collides with any other object in the grid.
     * Also checks if the hitbox will be completely inside the grid to prevent out-of-bounds indexing.
     * @param x the x coordinate of the object being checked
     * @param y the y coordinate of the object being checked
     * @param hitbox the hitbox of the object being checked
     * @returns true if the hitbox is placed in a valid spot, false otherwise
     */
    public isValidHitbox(x: number, y: number, hitbox: Hitbox): boolean {
        const left: number = x + hitbox.getOffsetX();
        const right: number = left + hitbox.getWidth();

        const top: number = y + hitbox.getOffsetY();
        const bottom: number = top + hitbox.getHeight();

        // Check if the hitbox is completely inside the grid
        if (left < 0 || right > this.width || top < 0 || bottom > this.height) {
            return false;
        }

        // Check if the hitbox does not collide with any other object in the grid
        let valid: boolean = true;

        hitbox.foreach(x, y, (i: number, j: number) => {
            if (this.getCell(i, j).getObject() !== null) {
                valid = false;
            }
        });

        return valid;
    }

    /**
     * Places a hitbox in the grid.
     * This updates all cells within the bounds of the hitbox to become occupied.
     * @param x the x coordinate the hitbox should be placed at (in grid space)
     * @param y the y coordinate the hitbox should be placed at (in grid space)
     * @param hitbox the hitbox that is being placed
     * @param object What IGridObject does the hitbox belong to
     */
    public placeHitbox(x: number, y: number, hitbox: Hitbox, object: GameObject): void {
        const left: number = x + hitbox.getOffsetX();
        const right: number = left + hitbox.getWidth();

        const top: number = y + hitbox.getOffsetY();
        const bottom: number = top + hitbox.getHeight();

        for (let y: number = top; y < bottom; y++) {
            for (let x: number = left; x < right; x++) {
                this.getCell(x, y).setObject(object);
            }
        }
    }

    /**
     * Removes an object (and its hitbox) from the grid.
     * All cells previously occupied by the object become unoccupied.
     * @param xPos The horizontal position of the object in grid space.
     * @param yPos The vertical position of the object in grid space.
     * @param hitbox The hitbox of the object.
     */
    public removeHitbox(x: number, y: number, hitbox: Hitbox): void {
        const left: number = x + hitbox.getOffsetX();
        const right: number = left + hitbox.getWidth();

        const top: number = y + hitbox.getOffsetY();
        const bottom: number = top + hitbox.getHeight();

        for (y = top; y < bottom; y++) {
            for (x = left; x < right; x++) {
                this.getCell(x, y).setObject(null);
            }
        }
    }

    /**
     * Converts a coordinate in screen space (pixel measurement) to a coordinate in grid space
     * (i.e. the indices of the cell in the grid)
     * @param point the coordinates in screen space
     * @returns the input, converted to grid space.
     */
    public screenSpaceToGridSpace(x: number, y: number): Phaser.Point {
        return new Phaser.Point(
            Math.floor((x - this.origin.x) / this.cellSize) + this.offset.x,
            Math.floor((y - this.origin.y) / this.cellSize) + this.offset.y
        );

    }

    /**
     * Converts a coordinate in grid space to a coordinate in screen space (pixel measurement)
     * @param point the coordinates in grid space
     * @returns the input, converted to screen space.
     */
    public gridSpaceToScreenSpace(x: number, y: number): Phaser.Point {
        return new Phaser.Point(
            this.origin.x + this.cellSize * (x - this.offset.x),
            this.origin.y + this.cellSize * (y - this.offset.y)
        );
    }

    /**
     * Get the cell data from a coordinate.
     * If the coordinate is out of bounds, go to the error state
     * @param x The x coordinate
     * @param y The y coordinate
     */
    public getCell(x: number, y: number): Cell {
        if (!this.isWithinBounds(x, y)) {
            ErrorState.throw(this.tilemap.game, `Grid indexed out of bounds at (x: ${x}, y: ${y})`);
        }

        return this.cells[x + y * this.width];
    }

    /**
     * Check whether specified x,y are inside of the bounds of the grid.
     * @param x X coordinate of position to check.
     * @param y Y coordinate of position to check.
     */
    public isWithinBounds(x: number, y: number): boolean {
        return x >= 0 && x < this.width && y >= 0 && y < this.height;
    }

    public getTilemap(): Phaser.Tilemap { return this.tilemap; }
    public setTilemap(tilemap: Phaser.Tilemap): void { this.tilemap = tilemap; }

    public getCellSize(): number { return this.cellSize; }

    /**
     * @returns the width of the grid in grid space
     */
    public getWidth(): number { return this.width; }

    /**
     * @returns the height of the grid in grid space
     */
    public getHeight(): number { return this.height; }

    public getOrigin(): Phaser.Point { return this.origin; }

    public getOffset(): Phaser.Point { return this.offset; }
    public setOffset(offset: Phaser.Point): void { this.offset = offset; }
}