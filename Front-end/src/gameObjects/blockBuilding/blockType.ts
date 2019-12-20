/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University
 * within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
/**
 * BlockType represents the Java EDSL type of a Block.
 * These all correspond directly to their Java counterparts with a few exceptions:
 * ANY represents an unknown type and is considered equal to all other types.
 * NULL is considered an Array type, as Arrays are the only Objects in the Java EDSL. Arrays are thus the only nullable type.
 */
enum BlockType {
    ANY = 'any',

    REAL = 'real',
    BOOL = 'boolean',

    NULL = 'null',

    REAL_ARRAY = 'real[]',
    BOOL_ARRAY = 'boolean[]',
    REAL_ARRAY_ARRAY = 'real[][]',
    BOOL_ARRAY_ARRAY = 'boolean[][]'
}

export default BlockType;