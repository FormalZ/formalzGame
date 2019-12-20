/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University
 * within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
/**
 * Defines a direction to turn in the Path that is a multiple of 45 degrees.
 * Turns of 180 degrees are not allowed, EXCEPT for when you start.
 */
enum Direction {
    LEFT135 = 135,
    LEFT90 = 90,
    LEFT45 = 45,
    FORWARD = 0,
    RIGHT45 = -45,
    RIGHT90 = -90,
    RIGHT135 = -135,
    LEFT180 = 180,
    RIGHT180 = -180
}

export default Direction;