/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University
 * within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
import BlockType from './blockType';

/**
 * 'Static' class that contains helper methods for type checking.
 * This class can not be instantiated.
 */
export default class TypeCheck {
    // Making the constructor of this class private prevents instantiation of this class.
    private constructor() { }

    /**
     * Checks if a given BlockType is a real number or an ANY type
     * @param blockType the blockType to check.
     */
    public static isReal(blockType: BlockType): boolean {
        return blockType === BlockType.REAL
            || blockType === BlockType.ANY;
    }

    /**
     * Checks if a given BlockType is a boolean or an ANY type
     * @param blockType the blockType to check.
     */
    public static isBoolean(blockType: BlockType): boolean {
        return blockType === BlockType.BOOL
            || blockType === BlockType.ANY;
    }

    /**
     * Checks if a given BlockType is an array or an ANY type
     * @param blockType the blockType to check.
     */
    public static isArray(blockType: BlockType): boolean {
        return TypeCheck.isArrayStrict(blockType)
            || blockType === BlockType.ANY;
    }

    /**
     * Checks if a given BlockType is an array
     * @param blockType the blockType to check.
     */
    private static isArrayStrict(blockType: BlockType): boolean {
        return blockType === BlockType.REAL_ARRAY
            || blockType === BlockType.BOOL_ARRAY
            || blockType === BlockType.BOOL_ARRAY_ARRAY
            || blockType === BlockType.REAL_ARRAY_ARRAY;
    }

    /**
     * Checks if two given BlockTypes have equal type.
     * A NULL type is considered equal to an array type.
     * An ANY type is considered equal to any other type.
     * @param a the left hand side of the equation.
     * @param b the right hand side of the equation.
     */
    public static equals(a: BlockType, b: BlockType): boolean {
        // The null type is considered equal to an array type
        if (TypeCheck.isArrayStrict(a) && b === BlockType.NULL ||
            TypeCheck.isArrayStrict(b) && a === BlockType.NULL) {
            return true;
        }

        return a === b
            || a === BlockType.ANY
            || b === BlockType.ANY;
    }
}