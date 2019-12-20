import { expect } from 'chai';
import { } from 'mocha';
import * as Game from '../../src/app';
import PathData from '../../src/gameObjects/path/pathData';
import Level from '../../src/states/level';
import PathTurn from '../../src/gameObjects/path/pathTurn';
import Direction from '../../src/gameObjects/path/direction';
import * as Point from 'phaser-ce';
import Grid from '../../src/gameObjects/grid/grid';

const cellSize: number = 16;

describe('PathData', () => {
    const game: Phaser.Game = Game.startApp();
    const level: Level = new Level();
    const grid: Grid = new Grid(cellSize, Math.ceil(game.width / cellSize), Math.ceil(game.height / cellSize), new Phaser.Point(0, 0));
    const preStartPoint: Phaser.Point = new Phaser.Point(2, 7);
    const prePath: PathTurn[] = [
        new PathTurn(Direction.FORWARD, 5),
        new PathTurn(Direction.RIGHT90, 5),
        new PathTurn(Direction.FORWARD, 5),
        new PathTurn(Direction.LEFT45, 5),
        new PathTurn(Direction.LEFT90, 5),
        new PathTurn(Direction.RIGHT45, 5)
    ];
    const postStartPoint: Phaser.Point = new Phaser.Point(27, 17);
    const postPath: PathTurn[] = [
        new PathTurn(Direction.FORWARD, 5),
        new PathTurn(Direction.RIGHT45, 5),
        new PathTurn(Direction.LEFT135, 5),
        new PathTurn(Direction.FORWARD, 5),
        new PathTurn(Direction.RIGHT45, 5),
        new PathTurn(Direction.LEFT45, 5),
        new PathTurn(Direction.RIGHT135, 5),
        new PathTurn(Direction.RIGHT45, 5)
    ];

    const startAngle: number = 45;
    const pathData: PathData = new PathData(startAngle, preStartPoint, prePath, postStartPoint, postPath);
    describe('on construction', () => {
        it('should be defined', () => {
            expect(pathData).to.exist;
        });
    });

    describe('makePath', () => {
        let parsedPath: PathData = PathData.makePath(
            '(2.7);[[5,0].[5,-90].[5,0].[5,45].[5,90].[5,-45]];(27.17);' +
            '[[5,0].[5,-45].[5,135].[5,0].[5,-45].[5,45].[5,-135].[5,-45]]');
        it('makes correct PathData', () => {
            expect(parsedPath).to.be.an.instanceof(PathData);
        });

        it('has a correct pre start point', () => {
            expect(parsedPath).to.have.property('preStartPoint').to.eql(pathData.getPreStartPoint());
            let xPos: number = parsedPath.getPreStartPoint().x;
            let yPos: number = parsedPath.getPreStartPoint().y;

            expect(xPos).to.be.a('number').and.to.be.greaterThan(0).and.to.be.lessThan(grid.getWidth());
            expect(yPos).to.be.a('number').and.to.be.greaterThan(0).and.to.be.lessThan(grid.getHeight());
        });

        it('has correct pre path turns', () => {
            expect(parsedPath).to.have.property('prePathTurns').that.is.an.instanceof(Array).to.eql(pathData.getPrePathTurns());
        });

        it('has a correct post start point', () => {
            expect(parsedPath).to.have.property('postStartPoint').to.eql(pathData.getPostStartPoint());
            let xPos: number = parsedPath.getPostStartPoint().x;
            let yPos: number = parsedPath.getPostStartPoint().y;

            expect(xPos).to.be.a('number').and.to.be.greaterThan(0).and.to.be.lessThan(grid.getWidth());
            expect(yPos).to.be.a('number').and.to.be.greaterThan(0).and.to.be.lessThan(grid.getHeight());
        });

        it('has correct post path turns', () => {
            expect(parsedPath).to.have.property('postPathTurns').that.is.an.instanceof(Array).to.eql(pathData.getPostPathTurns());
        });
    });
});
