/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University
 * within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
import Effect from '../../effects/effect';
import Level from '../../states/level';
import GameObject from '../gameObject';
import Cell from '../grid/cell';
import Grid from '../grid/grid';
import Spark from '../sparks/spark';

export default abstract class Bullet extends GameObject {
    protected distance: number = 0;
    protected maxDistance: number;
    protected originX: number;
    protected originY: number;
    protected anglePhi: number;
    protected cells: Cell[];
    protected power: number;
    protected bulletSpeed: number;
    protected effects: Effect<Spark>[];

    /**
     * Creates a new instance of Bullet
     * @param level The Level that the Bullet exists in.
     * @param x The x coordinate in screen space of the Bullet.
     * @param y The y coordinate in screen space of the Bullet.
     * @param range The maximum range that the Bullet can reach. After the Bullet has travelled this distance it will self-destruct.
     * @param angle The angle at which the Bullet travels.
     * @param bulletSpeed The speed at which the Bullet travels.
     * @param power The amount of damage the Bullet will do to any Spark it hits.
     * @param width The width of the Bullet's sprite.
     * @param height The height of the Bullet's sprite.
     * @param cells An array of all the Cells within the Bullet's range that are Path cells.
     * @param spriteName The name of the Bullet's sprite.
     * @param effects The effects that the Bullet will apply to any Spark it hits.
     */
    constructor(level: Level, x: number, y: number, range: number, angle: number, bulletSpeed: number, power: number,
        width: number, height: number, cells: Cell[], spriteName: string, effects: Effect<Spark>[]) {
        super(level, x, y, spriteName);

        this.maxDistance = range;
        this.originX = x;
        this.originY = y;
        this.anglePhi = angle;
        this.bulletSpeed = bulletSpeed;
        this.power = power;
        this.cells = cells;
        this.width = width;
        this.height = height;
        this.effects = effects;

        this.level.getGameRenderGroup().add(this);
    }

    /**
     * Move bullet and check collision
     * @returns whether this Bullet has reached the tower's maximum range or the bounds of the screen.
     */
    public updateBullet(): boolean {
        this.distance += this.bulletSpeed;
        this.x = this.originX + this.distance * Math.cos(this.anglePhi);
        this.y = this.originY + this.distance * Math.sin(this.anglePhi);

        // Handle possible collision.
        this.collideSparks();

        // Check if the bullet has reached the range of the tower; if so, remove it.
        return this.distance >= this.maxDistance || this.checkOutOfBounds();
    }

    protected abstract collideSparks(): void;

    /**
     * Check whether this bullet is out of bounds
     */
    protected checkOutOfBounds(): boolean {
        const grid: Grid = this.level.getGrid();
        const cellSize: number = grid.getCellSize();

        // We multiply by cellSize because grid.width and grid.height are in terms of amount of cells
        return this.x < 0 || this.y < 0 || grid.getWidth() * cellSize < this.x || grid.getHeight() * cellSize < this.y;
    }
}