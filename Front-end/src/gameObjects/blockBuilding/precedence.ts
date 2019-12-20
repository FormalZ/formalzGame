/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University
 * within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
/**
 * Defines Java's operator precedence.
 * A lower number means lower precedence.
 * Operators that aren't currently used in the BlockBuilding are also included for extensibility.
 * Precedence based on: https://introcs.cs.princeton.edu/java/11precedence/
 */
enum Precedence {
    ASSIGNMENT = 0,
    TERNARY = 1,
    LOGICAL_OR = 2,
    LOGICAL_AND = 3,
    BITWISE_OR = 4,
    BITWISE_AND = 5,
    EQUALITY = 6,
    RELATIONAL = 7,
    SHIFT = 8,
    ADDITIVE = 9,
    MULTIPLICATIVE = 10,
    CAST = 11,
    UNARY_PRE = 12,
    UNARY_POST = 13,
    ARRAY_INDEX = 14,
    NONE = 15
}

export default Precedence;