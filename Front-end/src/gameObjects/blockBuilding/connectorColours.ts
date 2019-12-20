import BlockType from './blockType';

enum ConnectorColours {
    REAL = 0xff0000,
    BOOLEAN = 0x00ff00,
    ARRAY = 0x0000ff,
    ARRAY_POLYMORPHIC = 0x0000fe,

    POLYMORPHIC = 0x707070,
    NULL = 0x111111,

    ANY = 0xffffff
}

export default ConnectorColours;
export function BlockTypeToColour(b: BlockType): ConnectorColours {
    switch (b) {
        case BlockType.REAL: return ConnectorColours.REAL;
        case BlockType.BOOL: return ConnectorColours.BOOLEAN;
        case BlockType.NULL: return ConnectorColours.NULL;

        case BlockType.BOOL_ARRAY:
        case BlockType.BOOL_ARRAY_ARRAY:
        case BlockType.REAL_ARRAY:
        case BlockType.REAL_ARRAY_ARRAY:
            return ConnectorColours.ARRAY;

        case BlockType.ANY: return ConnectorColours.ANY;
    }
}

export function ArrayIndexToColour(b: BlockType): ConnectorColours {
    switch (b) {
        case BlockType.REAL_ARRAY: return ConnectorColours.REAL;
        case BlockType.BOOL_ARRAY: return ConnectorColours.BOOLEAN;

        case BlockType.BOOL_ARRAY_ARRAY:
        case BlockType.REAL_ARRAY_ARRAY:
            return ConnectorColours.ARRAY;

        // Mistake cases or any, return base colour
        case BlockType.ANY:
        case BlockType.REAL:
        case BlockType.BOOL:
        case BlockType.NULL:
            return ConnectorColours.ANY;
    }
}