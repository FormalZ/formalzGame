/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University
 * within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
import Level from '../states/level';
import * as Assets from '../assets';
import Connection from '../connection';
import SoundManager from '../soundManager';
import ErrorState from '../states/errorState';
import { StringUtils } from '../utils/utils';
import GameTask from './gameTask';

const conditionUpdatedSound: string = Assets.Audio.AudioConditionUpdated.getName();

/**
 * QuestionGameTask is a GameTask that takes are of handling commands from the server
 * that are mostly related to the problem that the player has to solve.
 */
export default class QuestionGameTask extends GameTask {

    private startingMoney: number;
    private startingHealth: number;
    private deadline: number;
    public initialize(level: Level): void {
        super.initialize(level);

        if (this.startingMoney !== undefined) {
          this.level.setStartingMoney(this.startingMoney);
        }
        if (this.startingHealth !== undefined) {
          this.level.setStartingHealth(this.startingHealth);
        }
        if (this.deadline !== undefined) {
          this.level.setDeadline(this.deadline);
        }
    }

    /**
     * Handle all the commands regarding the question and waves.
     * @inheritDoc
     */
    public tryCommand(command: string, args: string): boolean {
        switch (command) {
            case 'spawnWave':
                this.handleSpawnWave(args);
                return true;
            case 'resultPre':
                this.handleResultPre(args);
                return true;
            case 'resultPost':
                this.handleResultPost(args);
                return true;
            case 'description':
                this.handleDescription(args);
                return true;
            case 'difficulty':
                this.handleDifficulty(args);
                return true;
            case 'error':
                this.handleError(args);
                return true;
            case 'finish':
                this.handleFinish(args);
                return true;
            case 'preFeedback':
                this.handlePreFeedback(args);
                return true;
            case 'postFeedback':
                this.handlePostFeedback(args);
                return true;
            case 'hint':
                this.handleHint(args);
                return true;
            case 'availableScore':
                this.handleScore(args);
                return true;
            case 'gamedata':
                this.handleGameData(args);
                return true;
            default:
                return false;
        }
    }

    /**
     * If the command 'spawnWave' is called:
     * Save the sparks per wave.
     * Save the percentages of all the sparks for the pre- and postCondition towers.
     * @param args the data received from the server.
     */
    private handleSpawnWave(args: string): void {
        const data: string[] = args.split(';');
        const sparkCount: number = parseInt(data[0]);
        const preWeights: number[] = this.splitPercentages(data[1]);
        const postWeights: number[] = this.splitPercentages(data[2]);
        const sparkHealth: number = parseInt(data[3]);
        const sparkSpeed: number = parseInt(data[4]);
        const sparkSpawnTime: number = parseInt(data[5]);
        const specialSparkPercentages: number[] = this.splitPercentages(data[6]);

        if (DEBUG) {
            console.log('preWeights: ' + preWeights);
        }

        this.level.setSparksPerWave(sparkCount);
        this.level.getPreScanner().setWaveWeights(preWeights);
        this.level.getPostScanner().setWaveWeights(postWeights);
        this.level.setSparkHealth(sparkHealth);
        this.level.setSparkSpeed(sparkSpeed);
        this.level.setSparkSpawnTime(sparkSpawnTime);
        this.level.setSpecialSparkPercentages(specialSparkPercentages);
    }

    /**
     * If the command 'resultPre' is called:
     * Save what kind of sparks to mark for the precondition tower.
     * Save the percentages of those sparks.
     * @param args the data received from the server.
     */
    private handleResultPre(args: string): void {
        this.level.getPreScanner().setWaveWeights(this.splitPercentages(args));

        SoundManager.playSoundEffect(conditionUpdatedSound);
    }

    /**
     * If the command 'resultPost' is called:
     * Save what kind of sparks to mark for the postCondition tower.
     * Save the percentages of those sparks.
     * @param args the data received from the server.
     */
    private handleResultPost(args: string): void {
        this.level.getPostScanner().setWaveWeights(this.splitPercentages(args));

        SoundManager.playSoundEffect(conditionUpdatedSound);
    }

    /**
     * If the command 'description' is called:
     * Save the description of the question.
     * @param args the data received from the server.
     */
    private handleDescription(args: string): void {
        if (DEBUG) {
            console.log('The description of the question will be ' + args);
        }
        this.level.setDescription(args);
        // A new description is acquired, so a new challenge starts; give the player some rewards.
        this.level.finishedChallenge();
    }

    /**
     * If the command 'difficulty' is called:
     * Set the score of this question.
     * @param args the data received from the server.
     */
    private handleDifficulty(args: string): void {
        if (DEBUG) {
            console.log('The difficulty is: ' + args);
        }

        this.level.setQuestionScore(Number.parseInt(args));
    }

    /**
     * If the command 'error' is called:
     * Probably no response from the Haskell backend.
     * @param args the data received from the server.
     */
    private handleError(args: string): void {
        ErrorState.throw(this.level.game, args, false);
    }

    /**
     * If the command 'finish' is called:
     * Handles the finish of the game, so that you can win.
     * @param args the data received from the server.
     */
    private handleFinish(args: string): void {
        // Add the score of the final challenge to the score acquired thus far.
        this.level.calculateScore();

        // Send final score to game server
        Connection.connection.sendFinalScore();

        // remove everything from local storage
        this.level.removeAllLocalStorage();

        // Send the score as well, so it can be printed at the final screen.
        this.level.state.start('winGame', true, false, [this.level.getScore()]);
    }

    /**
     * If the command 'preFeedback' is called:
     * Store the feedback for the precondition.
     * @param args the data received from the server.
     */
    private handlePreFeedback(args: string): void {
        // If we don't get any arguments, then the precondition was correct
        if (args === '') {
            this.level.setPreFeedback('');

            return;
        }

        this.level.setPreFeedback(this.makeFeedbackNice(args));
    }

    /**
     * If the command 'postFeedback' is called:
     * Store the feedback for the postCondition.
     * @param args the data received from the server.
     */
    private handlePostFeedback(args: string): void {
        // If we don't get any arguments, then the postCondition was correct.
        if (args === '') {
            this.level.setPostFeedback('');

            return;
        }

        this.level.setPostFeedback(this.makeFeedbackNice(args));
    }

    /**
     * If the command 'hint' is called:
     * Store the newly received hint.
     * @param args the data received from the server.
     */
    private handleHint(args: string): void {
        this.level.getHintScreen().addHint(args);
    }

    /**
     * If the command 'availableScore' is called:
     * Store the score available to acquire for this problem.
     */
    private handleScore(args: string): void {
        this.level.setAvailableScore(Number.parseInt(args));
    }

    /**
     * If the command 'gameData' is called:
     * Store the starting health, money and the deadline of this problem.
     * @param args the data received from the server
     */
    private handleGameData(args: string): void {
        const data: string[] = args.split(' ');
        const money: number = parseInt(data[0]);
        const health: number = parseInt(data[1]);
        const deadline: number = parseInt(data[2]);

        if (money !== 0)
            this.startingMoney = money;
        if (health !== 0)
            this.startingHealth = health;
        if (deadline !== 0)
            this.deadline = deadline;
    }

    /**
     * Split an array style string into an actual number array
     * @param arr the string version of an array
     * @returns the percentages/weights, as a float array
     */
    private splitPercentages(arr: string): number[] {
        return arr.substring(1, arr.length - 1).split(', ').map(Number);
    }

    /**
     * Split an array style string into an actual boolean array
     * @param tokens the possibilities of the sparks, as a string
     * @returns the possibilities of the sparks, as a boolean array
     */
    private splitBools(tokens: string): boolean[] {
        return tokens.substring(1, tokens.length - 1).split(', ').map((x) => x === 'true');
    }

    /**
     * Change the feedback into something human readable to be printed at the screen at another place.
     * @param feedback the feedback array as a string
     */
    private makeFeedbackNice(feedback: string): string {
        const tokens: string[] = feedback.split(';');
        const output: string[] = [];
        const niceFeedbackPrefix: string = this.level.game.cache.getJSON('uiText')['questionGameTask']['niceFeedbackPrefix'];
        const niceFeedbackSuffix: string = this.level.game.cache.getJSON('uiText')['questionGameTask']['niceFeedbackSuffix'];

        if (tokens.length === 1) {
          const str: string = tokens[0];
          const [variable, value] = str.substring(1, str.length - 1).split('=');
          output.push(niceFeedbackPrefix + ' ' + StringUtils.interpolateString(niceFeedbackSuffix, variable, value) + '?');
        }

        else {
          const outputPerToken: string[] = [];
          tokens.forEach(str => {
              const [variable, value] = str.substring(1, str.length - 1).split('=');
              outputPerToken.push(StringUtils.interpolateString(niceFeedbackSuffix, variable, value));
          });
          output.push(niceFeedbackPrefix + ': ' + outputPerToken.join('; ') + '?');
        }

        return output.join('\n');
    }

}
