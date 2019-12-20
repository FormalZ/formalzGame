/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University
 * within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
import Level from '../states/level';
import LevelState from '../states/levelState';
import BlockBuildingGameTask from './blockBuilding/blockBuildingGameTask';
import ConditionScannerGameTask from './conditionScannerGameTask';
import ConnectionGameTask from './connectionGameTask';
import GameTask from './gameTask';
import GridGameTask from './gridGameTask';
import HintScreenGameTask from './hintScreenGameTask';
import FunctionBlockGameTask from './path/functionBlockGameTask';
import PathGameTask from './path/pathGameTask';
import QuestionGameTask from './questionGameTask';
import RenderGroupTask from './renderGroupTask';
import SparkGameTask from './sparks/sparkGameTask';
import TowerGameTask from './towers/towerGameTask';
import UIGameTask from './uiGameTask';

/**
 * MainGameTask is the GameTask that manages all other GameTasks.
 * Events such as mouse input, key input, server commands and state changes are passed from Level to all other GameTasks via this GameTask.
 */
export default class MainGameTask extends GameTask {
    public static mainTask: MainGameTask = new MainGameTask();

    private gameTasks: GameTask[];

    /**
     * Construct the main game task by construct every sub game task.
     */
    public constructor() {
        super();
        this.gameTasks = [
            new GridGameTask(),
            new RenderGroupTask(),
            new UIGameTask(),
            new PathGameTask(),
            new FunctionBlockGameTask(),
            new TowerGameTask(),
            new ConditionScannerGameTask(),
            new ConnectionGameTask(),
            new QuestionGameTask(),
            new SparkGameTask(),
            new BlockBuildingGameTask(LevelState.BLOCKBUILDING),
            new HintScreenGameTask()
        ];
    }

    /**
     * Initialize the maintask and every sub game task
     * @param level The current level to initialize the game tasks with
     */
    public initialize(level: Level): void {
        super.initialize(level);

        for (let task of this.gameTasks) {
            task.initialize(level);
        }

        this.changeState();
    }

    /**
     * The general update function to run every game loop. This calls the update function of every sub game task
     */
    public update(): void {
        for (let task of this.gameTasks) {
            if (task.getUpdateEnabled()) {
                task.update();
            }
        }
    }

    /**
     * Callback method for a mouse down event. Calls the mouse down event for every sub game task
     * @param pointer the position where the mouse down event was triggered
     */
    public onMouseDown(pointer: Phaser.Pointer): boolean {
        for (let task of this.gameTasks) {
            if (task.getInputEnabled() && task.onMouseDown(pointer)) {
                return true;
            }
        }

        return false;
    }

    /**
    * Callback method for a mouse up event. Calls the mouse up event for every sub game task
    * @param point the position where the mouse up event was triggered
    */
    public onMouseUp(pointer: Phaser.Pointer): boolean {
        for (let task of this.gameTasks) {
            if (task.getInputEnabled() && task.onMouseUp(pointer)) {
                return true;
            }
        }

        return false;
    }

    /**
    * Callback method for a key press event. Calls the key even for every sub game task
    * @param key the key code for the key that was pressed
    */
    public onKeyPressed(key: number): boolean {
        for (let task of this.gameTasks) {
            if (task.getInputEnabled() && task.onKeyPressed(key)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Handle a command from the server. This triggers the tryCommand function from every sub game task
     * @param keyWord the key word describing what command is triggered
     * @param args the possible arguments for a command
     */
    public tryCommand(keyWord: string, args: string): boolean {
        for (let task of this.gameTasks) {
            if (task.tryCommand(keyWord, args)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Change the state for every sub game task
     */
    public changeState(): void {
        for (let task of this.gameTasks) {
            task.changeState(this.level.getLevelState());
        }
    }
}