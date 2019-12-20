/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University
 * within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
import Level from '../states/level';
import * as Assets from '../assets';
import Grid from '../gameObjects/grid/grid';
import GameTask from './gameTask';

const path: string = Assets.Images.SpritesheetsPath.getName();
const gameScreenSprite: string = Assets.Images.SpritesBackground31green.getName();
const background: string = Assets.Images.SpritesBackground.getName();

const cellSize: number = 16;

/**
 * GridGameTask is a GameTask that initialized the Grid.
 * This includes the grid based Phaser.Tilemap that the Path is rendered to.
 */
export default class GridGameTask extends GameTask {
    public initialize(level: Level): void {
        super.initialize(level);

        // Add the actual background
        this.level.add.sprite(0, this.level.game.height * 0.095, background);

        // The offsets indicate how many gridcells the small game screen is moved in each direction.
        // If they were all 0, then the grid would be the exact size of the entire background.
        const leftOffset: number = 8;
        const rightOffset: number = 8;
        const topOffset: number = 5;
        const botOffset: number = 4;

        // These are the sizes of the grid if there were absolutely no modifications.
        const originalSizeX: number = 71;
        const originalSizeY: number = 38;

        // The maximum size of gridcells in the Y direction.
        // Since there are UI elements on the right side, it's the actual number is smaller.
        const maxSizeY: number = 47;

        // Grid size is hardcoded. This is because the path generation algorithm assumes this grid size.
        const originX: number = cellSize * leftOffset;
        const originY: number = Math.ceil(this.level.game.height * 0.095) + cellSize * topOffset;
        const grid: Grid = new Grid(
            cellSize,
            (originalSizeX - leftOffset - rightOffset),
            (originalSizeY - topOffset - botOffset),
            new Phaser.Point(originX, originY)
        );
        this.level.setGrid(grid);

        // Add background of the small game screen
        // Since the anchor is set in the middle of the sprite (for mirroring), add half the size of the grid to compensate.
        // The y coordinate has an extra small offset to prevent towers being slightly above the grid.
        const gameScreen: Phaser.Sprite = this.level.game.add.sprite(
            originX + ((originalSizeX - leftOffset - rightOffset) * cellSize / 2),
            originY - 6 + ((originalSizeY - topOffset - botOffset) * cellSize) / 2,
            gameScreenSprite
        );
        gameScreen.anchor.setTo(0.5, 0.5);

        // The size of the sprite of the gamescreen. Since this is bigger than what we need, it needs to be scaled down.
        const gameScreenSpriteSizeX: number = 1122;
        const gameScreenSpriteSizeY: number = 606;

        // First of all, scale the sprite up to an multiple of the gridcell.
        // After that, scale it down to the amount of gridcells that we now have using the offsets from before.
        const scaleX: number = ((originalSizeX * cellSize) / gameScreenSpriteSizeX)
         * ((originalSizeX - leftOffset  - rightOffset) / originalSizeX);
        const scaleY: number = ((maxSizeY * cellSize) / gameScreenSpriteSizeY)
        * ((originalSizeY - topOffset - botOffset)  / maxSizeY);
        gameScreen.scale = new Phaser.Point(scaleX, scaleY);

        // Mirror the sprite in X and Y direction, to make it less obvious that it's a recolour of the background.
        gameScreen.scale.y *= -1;
        gameScreen.scale.x *= -1;

        // Initialize tilemap
        const tilemap: Phaser.Tilemap = this.level.game.add.tilemap(null, cellSize, cellSize);
        tilemap.addTilesetImage(path, path, cellSize, cellSize);

        // Add the layer to which the paths will be drawn
        const layer: Phaser.TilemapLayer = tilemap.createBlankLayer(
            'Path',
            grid.getWidth(),
            grid.getHeight(),
            grid.getCellSize(),
            grid.getCellSize()
        );
        layer.fixedToCamera = false;
        layer.position = grid.getOrigin();

        // Attach the tilemap to the grid
        grid.setTilemap(tilemap);
    }
}