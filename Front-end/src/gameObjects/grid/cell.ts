/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University
 * within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
import GameObject from '../gameObject';
import Spark from '../sparks/spark';

/**
 * Simple data-structure that represents a single cell within the grid.
 * It provides data for collision as well as spacial information about the sparks.
 */
export default class Cell {
    private x: number;
    private y: number;

    // If this cell is affected by a tower increase range, this value will be more than zero
    private rangeEffectAmount: number;

    private object: GameObject;

    private sparks: Set<Spark>;

    /**
     * Constructor for a Cell.
     * @param x x-coordinate of this cell, in grid space.
     * @param y y-coordinate of this cell, in grid space.
     */
    constructor(x: number, y: number) {
        this.x = x;
        this.y = y;

        this.rangeEffectAmount = 0;

        this.object = null;

        this.sparks = new Set<Spark>();
    }

    public getX(): number { return this.x; }
    public getY(): number { return this.y; }

    public getObject(): GameObject { return this.object; }
    public setObject(object: GameObject): void { this.object = object; }

    /**
     * @returns true if this Cell is effected by a range increase effect from one or more Towers.
     */
    public hasRangeEffect(): boolean { return this.rangeEffectAmount > 0; }
    /**
     * Increase the amount of Towers that increase the range of any Tower placed on this Cell.
     * This effect does not stack.
     */
    public addRangeEffect(): void { this.rangeEffectAmount++; }
    /**
     * Decrease the amount of Towers that increase the range of any Tower placed on this Cell.
     * This effect does not stack.
     */
    public removeRangeEffect(): void { this.rangeEffectAmount--; }

    public getSparks(): Set<Spark> { return this.sparks; }
}