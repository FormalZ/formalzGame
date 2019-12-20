/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University
 * within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
export default class GeometryUtils {
    /**
     * Calculate the squared distance from a point to a line segment.
     * @param point The point from which to calculate the squared distance.
     * @param lineSegment The line segment to which to calculate the squared distance.
     * @returns The squared distance between the given point and path.
     */
    public static squaredDistancePointLineSegment(point: Phaser.Point, lineSegment: Phaser.Line): number {
        const relativeEnd: Phaser.Point = Phaser.Point.subtract(lineSegment.end, lineSegment.start);
        const relativePoint: Phaser.Point = Phaser.Point.subtract(point, lineSegment.start);

        const transformation: Phaser.Point = this.complexProduct(relativePoint, this.complexConjugate(relativeEnd));

        const squaredLength: number = this.squaredDistance(lineSegment.start, lineSegment.end);
        if (transformation.x <= 0) {
            return this.squaredDistance(point, lineSegment.start);
        } else if (transformation.x >= squaredLength) {
            return this.squaredDistance(point, lineSegment.end);
        } else {
            return transformation.y * transformation.y / squaredLength;
        }
    }

    /**
     * Calculate the squared distance between two points.
     * Use this over regular distance whenever possible to spare the use of a square root.
     * @param point1 The first point.
     * @param point2 The second point.
     * @returns The squared distance between the two given points.
     */
    public static squaredDistance(point1: Phaser.Point, point2: Phaser.Point): number {
        return this.squaredLength(Phaser.Point.subtract(point2, point1));
    }

    /**
     * Calculate the squared length of a vector.
     * Use this over regular length whenever possible to spare the use of a square root.
     * @param vector The vector of which to calculate the squared length.
     * @returns The squared length of the given vector.
     */
    private static squaredLength(vector: Phaser.Point): number {
        return vector.x * vector.x + vector.y * vector.y;
    }

    /**
     * Calculate the complex product of two complex numbers given as points: (x,y) = x + yi.
     * @param z1 The first number.
     * @param z2 The second number.
     * @returns The product of the given numbers.
     */
    private static complexProduct(z1: Phaser.Point, z2: Phaser.Point): Phaser.Point {
        return new Phaser.Point(z1.x * z2.x - z1.y * z2.y, z1.x * z2.y + z1.y * z2.x);
    }

    /**
     * Calculate the complex conjugate of a complex number given as a point: (x,y) = x + yi.
     * @param z The complex number.
     * @returns The complex conjugate of the given number.
     */
    private static complexConjugate(z: Phaser.Point): Phaser.Point {
        return new Phaser.Point(z.x, -z.y);
    }

    /**
     * Converts an angle from degrees to radians.
     * @param degrees the angle in degrees.
     * @returns the angle in radians.
     */
    public static degreesToRadians(degrees: number): number {
        return degrees * Math.PI / 180;
    }

    /**
     * Converts an angle from radians to degrees.
     * @param radians the angle in radians.
     * @returns the angle in degrees.
     */
    public static RadiansToDegrees(radians: number): number {
        return radians * 180 / Math.PI;
    }

    /**
     * Clamps value between min and max.
     * @param value The value to be clamped.
     * @param min Lower bound.
     * @param max Upper bound.
     * @returns Value, clamped between min and max.
     */
    public static clamp(value: number, min: number, max: number): number {
        if (value <= min) {
            return min;
        }

        if (value >= max) {
            return max;
        }

        return value;
    }

    /**
     * Determines if two floating point values are equal enough based on an epsilon value.
     * @param a the first floating point value.
     * @param b the second floating point value.
     * @param epsilon the maximum difference that a and b are allowed to have to still be considered equal.
     * @returns whether or not a and b are considered equal enough.
     */
    public static almostEquals(a: number, b: number, epsilon: number = 0.001): boolean {
        return Math.abs(a - b) <= epsilon;
    }
}
