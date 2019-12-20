/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University
 * within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
import Level from '../states/level';
import LevelState from '../states/levelState';

/**
 * GameTask is an abstract class that is meant to be used to manage various GameObjects.
 * GameTasks can pass down updates and input events to their GameObjects.
 */
export default abstract class GameTask {
    protected level: Level;

    private updateEnabled: boolean;
    private inputEnabled: boolean;
    // This is the state in which the task should be operational
    protected enabledState: LevelState;

    /**
    * The constructor for a GameTask.
    * @param level The level that this GameTask is running in.
    */
    constructor(enabledState: LevelState = LevelState.PLAYING) {
        this.updateEnabled = true;
        this.inputEnabled = true;
        this.enabledState = enabledState;
    }

    /**
    * Method to handle initialization.
    */
    public initialize(level: Level): void {
        this.level = level;
    }

    /**
    * Method to handle updates.
    */
    public update(): void { }

    /**
    * Method to handle mouse down events, sent from the Level.
    * @param pointer The Phaser.Pointer object
    * @returns whether or not the event was handled by this GameTask
    */
    public onMouseDown(pointer: Phaser.Pointer): boolean { return false; }

    /**
    * Method to handle mouse up events, sent from the Level.
    * @param pointer The Phaser.Pointer object
    * @returns whether or not the event was handled by this GameTask
    */
    public onMouseUp(pointer: Phaser.Pointer): boolean { return false; }

    /**
    * Method to handle key press events, sent from the Level.
    * @param key The keycode of the key that was pressed
    * @returns whether or not the event was handled by this GameTask
    */
    public onKeyPressed(key: number): boolean { return false; }

    /**
    * Used for receiving messages
    * @param command The keyword of the received command.
    * @param args The arguments of the received command.
    * @returns A boolean representing whether the command got processed.
    */
    public tryCommand(command: string, args: string): boolean { return false; }

    /**
     * Method that gets called when the Level's state changes
     * @param levelState the new LevelState
     */
    public changeState(levelState: LevelState): void {
        this.setInputEnabled(levelState === this.enabledState);
        this.setUpdateEnabled(!this.level.getPause());
    }

    public getUpdateEnabled(): boolean { return this.updateEnabled; }
    public setUpdateEnabled(updateEnabled: boolean): void { this.updateEnabled = updateEnabled; }

    public getInputEnabled(): boolean { return this.inputEnabled; }
    public setInputEnabled(inputEnabled: boolean): void { this.inputEnabled = inputEnabled; }
}