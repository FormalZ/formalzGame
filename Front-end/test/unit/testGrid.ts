import { expect } from 'chai';
import * as Game from '../../src/app';
import Grid from '../../src/gameObjects/grid/grid';
import Hitbox from '../../src/gameObjects/grid/hitbox';

const cellSize: number = 16;

describe('Grid', () => {


    const game: Phaser.Game = Game.startApp();
    const grid: Grid = new Grid(cellSize, Math.ceil(game.width * 0.85 / cellSize), Math.ceil(game.height * 0.85 / cellSize),
        new Phaser.Point(0, Math.ceil(game.height * 0.095)));

    describe('on construction', () => {

        it('should be defined', () => {
            expect(grid).to.exist;
        });

        it('should have defined cells', () => {
            expect(grid.getCellSize()).to.equal(16);

            expect(grid.getCell(0, 0)).to.exist;
        });

        it('Should have the right width and height', () => {
            expect(grid.getWidth()).to.equal(Math.ceil(game.width * 0.85 / grid.getCellSize()));
            expect(grid.getHeight()).to.equal(Math.ceil(game.height * 0.85 / grid.getCellSize()));
        });
    });

    describe('in action', () => {

        it('should see 0-area hitbox as valid', () => {
            const hitbox: Hitbox = new Hitbox(0, 0, 0, 0);
            expect(grid.isValidHitbox(0, 0, hitbox)).to.equal(true);
        });
    });

});
