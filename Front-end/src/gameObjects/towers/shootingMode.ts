/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University
 * within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
/**
 * Describes the current shooting Mode of a tower.
 * First will shoot the Spark that is furthest along the Path.
 * Last will shoot the Spark that is the least far along the Path.
 * Strongest will shoot the Spark that has the most health left.
 * Weakest will shoot the Spark that has the least health left.
 * Closest will shoot the Spark that has the least distance to the Tower.
 */
enum ShootingMode {
    First,
    Last,
    Strongest,
    Weakest,
    Closest
}

export default ShootingMode;