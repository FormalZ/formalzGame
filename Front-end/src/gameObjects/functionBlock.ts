/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University
 * within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
import * as Assets from '../assets';
import Level from '../states/level';
import GameObject from './gameObject';
import Hitbox from './grid/hitbox';

const spriteName: string = Assets.Images.SpritesChipBlock.getName();

/**
 * FunctionBlock is a Sprite showing a CPU that can be placed in the Grid.
 */
export default class FunctionBlock extends GameObject {
    private hitbox: Hitbox;

    /**
    * The constructor for the Function Block GameObject, once the constructor is called, the Function Block will
    * automatically be drawn and added to the world
    * @param game The Phaser.Game object
    * @param x the x-position of the Function Block
    * @param y the y-position of the Function Block
    * @param width the width of the Function Block object
    * @param height the height of the Function Block object
    */
    constructor(level: Level, x: number, y: number, width: number, height: number) {
        super(level, x, y, spriteName);

        this.hitbox = new Hitbox(0, 0, 4, 4);

        this.width = width;
        this.height = height;

        this.smoothed = true;

        this.game.add.existing(this);
    }

    public getHitbox(): Hitbox { return this.hitbox; }
}
