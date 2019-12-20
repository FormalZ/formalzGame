/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University
 * within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
import Level from '../states/level';
import GameTask from './gameTask';

/**
 * RenderGroupTask is a GameTask that takes care of the various render groups that the game has.
 * Most notably this includes the game render group, which is depth-sorted to provide the illusion
 * that objects are in front of or behind each other.
 */
export default class RenderGroupTask extends GameTask {
    /**
     * Initialize the seperate render groups
     * @param level The level to intialize the render groups in
     */
    public initialize(level: Level): void {
        super.initialize(level);

        this.level.setGameRenderGroup(this.level.game.add.group());
        this.level.setBlockBuildScreenRenderGroup(this.level.game.add.group());
        this.level.setBlockBuildingRenderGroup(this.level.game.add.group());
        this.level.setUIScreenScreenGroup(this.level.game.add.group());
    }

    /**
     * Update the game render group by sorting each object in the group based on the bottom value of those objects
     */
    public update(): void {
        this.level.getGameRenderGroup().sort('bottom', Phaser.Group.SORT_ASCENDING);
    }
}