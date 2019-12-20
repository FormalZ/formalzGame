/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University
 * within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
/**
 * Hitbox represents the area covered by a GameObject in the Grid that it occupies.
 * No other GameObject can be placed on any Cell that a GameObject's Hitbox contains.
 */
export default class Hitbox {
    private offsetX: number;
    private offsetY: number;
    private width: number;
    private height: number;

    /**
     * Instantiates a new Hitbox.
     * @param offsetX Horizontal offset to the GameObject that this Hitbox belongs to in grid space.
     * @param offsetY Vertical offset to the GameObject that this Hitbox belongs to in grid space.
     * @param width Width of Hitbox in grid space.
     * @param height Height of Hitbox in grid space.
     */
    constructor(offsetX: number, offsetY: number, width: number, height: number) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.width = width;
        this.height = height;
    }

    /**
     * Foreach to loop over the hitbox cell positions.
     * @param xHitbox x of the Hitbox middle-right-bottom position in Grid Space.
     * @param yHitbox y of the Hitbox middle-right-bottom position in Grid Space.
     * @param callBack Function where to loop over.
     */
    public foreach(xHitbox: number, yHitbox: number, callBack: (x: number, y: number) => void): void {
        for (let x: number = xHitbox + this.offsetX; x < xHitbox + this.width + this.offsetX; x++) {
            for (let y: number = yHitbox + this.offsetY; y < yHitbox + this.height + this.offsetY; y++) {
                callBack(x, y);
            }
        }
    }

    public getOffsetX(): number { return this.offsetX; }
    public getOffsetY(): number { return this.offsetY; }

    public getWidth(): number { return this.width; }
    public getHeight(): number { return this.height; }
}