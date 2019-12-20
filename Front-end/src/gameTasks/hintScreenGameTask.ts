/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University
 * within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
import Level from '../states/level';
import LevelState from '../states/levelState';
import ErrorState from '../states/errorState';
import GameTask from './gameTask';
import HintScreen from '../gameObjects/hintScreen';

export default class HintScreenGameTask extends GameTask {
    /**
     * Initializes the hint screen game task.
     * @param level The current level to initialize the hint screen game task in
     */
    public initialize(level: Level): void {
        super.initialize(level);

        level.setHintScreen(new HintScreen(level));
    }

    /**
     * The general update function to run every game loop. This calls the update function for the hint screen
     */
    public update(): void {
        this.level.getHintScreen().updateObject();
    }

    /**
     * Callback method for a mouse down event. Calls the mouse down event for the hint screen
     * @param pointer the position where the mouse down event was triggered
     */
    public onMouseDown(pointer: Phaser.Pointer): boolean {
        return this.level.getHintScreen().onMouseDown(pointer);
    }

    /**
     * Callback method for a mouse up event. Calls the mouse up event for the hint screen
     * @param pointer the position where the mouse up event was triggered
     */
    public onMouseUp(pointer: Phaser.Pointer): boolean {
        return this.level.getHintScreen().onMouseUp(pointer);
    }

    /**
     * Change the input recognition based on the current level state
     * @param levelState the current level state
     */
    public changeState(levelState: LevelState): void {
        if (levelState === LevelState.BLOCKBUILDING) {
            this.level.getHintScreen().closeScreen();
        }
    }
}