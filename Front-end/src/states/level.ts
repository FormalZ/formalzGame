/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University
 * within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
import * as Assets from '../assets';
import Connection from '../connection';
import BlockBuildScreen from '../gameObjects/blockBuilding/blockBuildScreen';
import Block from '../gameObjects/blockBuilding/blocks/block';
import BlockType from '../gameObjects/blockBuilding/blockType';
import Connector from '../gameObjects/blockBuilding/connector';
import ConditionScanner from '../gameObjects/conditionScanner';
import Grid from '../gameObjects/grid/grid';
import HintScreen from '../gameObjects/hintScreen';
import Path from '../gameObjects/path/path';
import Spark from '../gameObjects/sparks/spark';
import Tower from '../gameObjects/towers/tower';
import MainGameTask from '../gameTasks/mainGameTask';
import SoundManager from '../soundManager';
import MenuButton from '../userInterface/menuButton';
import Tooltip from '../userInterface/tooltip';
import Dictionary from '../utils/dictionary';
import PathData from './../gameObjects/path/pathData';
import GameState from './gameState';
import LevelState from './levelState';
import ErrorState from './errorState';
import MenuSlideButton from '../userInterface/menuSlideButton';

const music: string = Assets.Audio.AudioMusic.getName();
const waveEndSound: string = Assets.Audio.AudioWaveEnd.getName();
const moneyLoseSound: string = Assets.Audio.AudioMoneyLose.getName();
const moneyGainSound: string = Assets.Audio.AudioMoneyGain.getName();

export default class Level extends GameState {
    private levelState: LevelState;

    private fullHealth: number;
    private startMoney: number;
    private maxPauseTime: number;
    private maxBetweenWavesTime: number;
    private currBetweenWavesTime: number;
    private challengeClearGold: number;
    private challengeClearHealth: number;

    // The score function index with which scores will be calculated.
    private scoreFunction: number;

    private grid: Grid;

    private prePath: Path;
    private postPath: Path;

    private towers: Set<Tower>;
    private placingTower: Tower;

    private preScanner: ConditionScanner;
    private postScanner: ConditionScanner;
    private currentScanner: ConditionScanner;

    private selectedTower: Tower;
    private selectedTowerGraphics: Phaser.Graphics;

    private sparks: Set<Spark>;
    private sparksPerWave: number;
    private sparkHealth: number;
    private sparkSpeed: number;
    private sparkSpawnTime: number;
    private specialSparkPercentages: number[];

    private gameRenderGroup: Phaser.Group;
    private blockBuildScreenRenderGroup: Phaser.Group;
    private blockBuildingRenderGroup: Phaser.Group;
    private UIScreenRenderGroup: Phaser.Group;

    private isWaveEnded: boolean;
    private isWaveStarted: boolean;

    private pause: boolean;
    private remainingPauseTime: number;
    private pauseDuration: number;

    private score: number;
    private deltaScore: number;
    private questionScore: number;

    // The score available for a certain challenge; is reset every challenge.
    private availableScore: number;

    // The amount of waves required by the player for this challenge; is reset to 0 every challenge.
    private wavesRequired: number;

    private description: string;

    private refundTowerModifier: number;
    private refundBlockModifier: number;

    private health: number;
    private money: number;

    // A boolean that indicates whether the current challenge is the first challenge.
    private firstChallenge: boolean;

    private moneySpentOnTowers: number;
    private moneySpentOnPreCondition: number;
    private moneySpentOnPostCondition: number;

    private timeSpentBeforeWave: number;
    private timeSpentPaused: number;
    private waveEndTime: number;

    private preFeedback: string;
    private postFeedback: string;
    private hintScreen: HintScreen;

    private pathData: PathData;

    private tooltip: Tooltip;

    protected isTutorial: boolean;

    private dragging: boolean;
    private preBlockBuildScreen: BlockBuildScreen;
    private postBlockBuildScreen: BlockBuildScreen;
    private currentConnector: Connector;

    // Variable to indicate that before the last closing of the blockbuilding there was a change in the condition
    private conditionChanged: boolean;

    private placingBlock: Block;

    private movingBlock: Block;
    private movingMouseOffset: Phaser.Point;

    private blockBuildingMask: Phaser.Graphics;

    private selectedBlockOrConnector: Block | Connector;

    private validTypes: Dictionary<BlockType>;

    private slideUpPre: MenuSlideButton;
    private slideUpPost: MenuSlideButton;
    private slideUpDesc: MenuSlideButton;

    private deadline: number;

    // the current id number to allocate to towers and blocks
    private towerid: number;
    private blockid: number;

    // counter of how many sub challenges already have been completed
    private challengeCounter: number;

    // the session id (userid + problemid) of this game
    private sessionId: string;

    /**
     * init is the very first function called when your State starts up.
     * It's called before preload, create or anything else.
     * If you need to route the game away to another State you could do so here,
     * or if you need to prepare a set of variables or objects before the preloading starts.
     * @param args the optional arguments. Currently this only includes path data for when there is no connection
     */
    public init(args?: any): void {
        if (DEBUG) {
            SoundManager.setSoundEffectsVolume(0);
            SoundManager.setMusicVolume(0);
        }

        this.isWaveStarted = false;
        this.isWaveEnded = false;

        const levelSettings: object = this.game.cache.getJSON('levelSettings');

        this.fullHealth = levelSettings['fullHealth'];
        this.startMoney = levelSettings['startMoney'];
        this.maxPauseTime = levelSettings['maxPauseTime'];
        this.maxBetweenWavesTime = levelSettings['maxBetweenWavesTime'];
        this.challengeClearGold = levelSettings['challengeClearGold'];
        this.challengeClearHealth = levelSettings['challengeClearHealth'];
        this.scoreFunction = levelSettings['scoreFunction'];

        this.health = this.fullHealth;
        this.money = this.startMoney;
        this.score = 0;

        this.towers = new Set<Tower>();
        this.sparks = new Set<Spark>();

        this.wavesRequired = 0;

        this.firstChallenge = true;

        this.specialSparkPercentages = [];

        this.moneySpentOnTowers = 0;
        this.moneySpentOnPreCondition = 0;
        this.moneySpentOnPostCondition = 0;

        this.deltaScore = 0;
        this.questionScore = 0;

        this.pause = false;
        this.pauseDuration = 0;
        this.remainingPauseTime = this.maxPauseTime;

        this.timeSpentBeforeWave = 0;
        this.timeSpentPaused = 0;
        this.waveEndTime = this.time.time;

        this.preFeedback = undefined;
        this.postFeedback = undefined;
        this.description = '';

        this.tooltip = new Tooltip(this);

        this.isTutorial = false;

        this.dragging = false;
        this.currentConnector = null;
        this.conditionChanged = false;

        // Base these off of difficulty.
        this.refundTowerModifier = levelSettings['refundTowerModifier'];
        this.refundBlockModifier = levelSettings['refundBlockModifier'];

        this.movingBlock = null;
        this.movingMouseOffset = new Phaser.Point(0, 0);

        this.selectedBlockOrConnector = null;

        this.validTypes = new Dictionary<BlockType>();

        this.levelState = LevelState.PLAYING;

        this.placingTower = null;

        this.towerid = 0;
        this.blockid = 0;

        this.challengeCounter = 0;

        if (DEBUG) {
          this.sessionId = 'debug';
        }
        else {
          this.sessionId = Connection.connection.getSessionId();
        }

        // if this game is not in debug, and the send hash did not correspond with the hash in the back end
        // remove all local storage of this level
        if (!DEBUG && !Connection.connection.getHashCheck()) {
          this.removeAllLocalStorage();
        }


        // The UNIX timestamp of the deadline. By default, there is no deadline.
        // The deadline is received from the GameServer via the command 'gameData'
        this.deadline = null;

        if (args && args.length > 0) {
            MainGameTask.mainTask.tryCommand('path', args[0]);
        }


        Connection.connection.setLevel(this);
        MainGameTask.mainTask.initialize(this);
        // set the money to what was stored in local storage
        const localStorageMoneyString: string = localStorage.getItem(this.getSessionId() + 'money');
        if (localStorageMoneyString !== null) {
          this.setMoney(Number(localStorageMoneyString));
        }

        // set the challenge counter to what was stored in local storage
        const localStorageChallengeString: string = localStorage.getItem(this.getSessionId() + 'challengecounter');
        if (localStorageChallengeString !== null) {
          this.challengeCounter = Number(localStorageChallengeString);
        }

        if (DEBUG && !Connection.connection.hasSucceeded()) {
            MainGameTask.mainTask.tryCommand('variableTypes', '[int a, int[] b, boolean[] c, int i]');
            MainGameTask.mainTask.tryCommand('validPreTokens',
                '[[Quantifier forall], [Variable a], [Variable b], [Variable c], [Variable i], ' +
                '[ArrayIndex []], [RelationalComparer >=], [Number 1], [LogicOperator &&], [LogicOperator ||], ' +
                '[With With], [Implication imp], ' +
                '[EqualityComparer ==], [Arithmetic *], [Arithmetic /], [Arithmetic +], [Arithmetic -], [QuantifierR forallr], ' +
                '[Length Length], [Boolean true], [Not !]]');

            MainGameTask.mainTask.tryCommand('validPostTokens', '[[Variable a], [RelationalComparer >], [Number 0]]');

            MainGameTask.mainTask.tryCommand('hint', 'This is a default hint message. You are in Debug mode and have no connection.');
            MainGameTask.mainTask.tryCommand('hint', 'This is a second default hint message.');

            MainGameTask.mainTask.tryCommand('spawnWave', '10;[0.5, 0.5, 0.0, 0.0];[0.5, 0.5, 0.0, 0.0];20;80;500;[0.01, 0.01]');

            this.specialSparkPercentages = [0.01, 0.01];

            SoundManager.setSoundEffectsVolume(0);
            SoundManager.setMusicVolume(0);
        }

        Connection.connection.sendStartProblem(this.challengeCounter);

        SoundManager.stopAll();
        SoundManager.playMusic(music);


        this.currentScanner = this.preScanner;
        this.preBlockBuildScreen.closeScreen();
        if (this.preScanner.getCode() === 'true') {
          Connection.connection.sendCondition('Pre', 'true');
        }
        this.currentScanner = this.postScanner;
        this.postBlockBuildScreen.closeScreen();
        if (this.postScanner.getCode() === 'true') {
          Connection.connection.sendCondition('Post', 'true');
        }
    }

    /**
     * General update function. This is called for every loop in the game loop.
     */
    public update(): void {
        this.game.debug.text(this.time.fps.toString(), 0, 0);

        if (this.health === 0) {
            this.game.state.start('gameOver');
        }

        super.update();

        this.tooltip.updateObject();

        if (this.pause) {
            this.pauseDuration += this.game.time.elapsed;
            this.remainingPauseTime -= this.game.time.elapsed;

            if (this.remainingPauseTime <= 0) {
                this.remainingPauseTime = 0;

                this.togglePause();
            }
        } else if (this.remainingPauseTime < this.maxPauseTime) {
            this.remainingPauseTime += 10;
            this.remainingPauseTime = Math.min(this.remainingPauseTime, this.maxPauseTime);
        }

        if (this.isWaveEnded) {
            this.currBetweenWavesTime = this.time.time - this.waveEndTime;
            if (this.currBetweenWavesTime >= this.maxBetweenWavesTime) {
                this.startWave();
            }
        }

        MainGameTask.mainTask.update();
    }

    /**
     * Callback method for a mouse down event
     */
    protected onMouseDown(): void {
        MainGameTask.mainTask.onMouseDown(this.game.input.activePointer);
    }

    /**
    * Callback method for a mouse up event
    */
    protected onMouseUp(): void {
        MainGameTask.mainTask.onMouseUp(this.game.input.activePointer);
    }

    /**
    * Callback method for a key press event
    */
    protected onKeyPressed(key: number): void {
        MainGameTask.mainTask.onKeyPressed(key);
    }

    /**
     * Pause or play the game
     */
    public togglePause(): void {
        this.pause = !this.pause;

        this.timeSpentPaused += this.pauseDuration;
        this.pauseDuration = 0;

        MainGameTask.mainTask.changeState();
    }

    /**
     * Start a wave of sparks.
     */
    public startWave(): void {
        this.isWaveStarted = true;
        this.isWaveEnded = false;
        this.timeSpentBeforeWave = this.time.time - this.waveEndTime;
    }

    /**
     * Ends the wave of sparks, and resets the pre conditions
     */
    public endWave(): void {
        this.isWaveStarted = false;
        this.isWaveEnded = true;
        this.wavesRequired++;

        this.moneySpentOnTowers = 0;
        this.moneySpentOnPreCondition = 0;
        this.moneySpentOnPostCondition = 0;

        this.timeSpentPaused = 0;
        this.timeSpentBeforeWave = 0;
        this.waveEndTime = this.time.time;

        this.preFeedback = undefined;
        this.postFeedback = undefined;

        this.getPreScanner().resetWaveData();
        this.getPostScanner().resetWaveData();

        SoundManager.playSoundEffect(waveEndSound);
    }

    /**
     * Grants the player health, money and score when they have finished a challenge.
     */
    public finishedChallenge(): void {
        // The player should not acquire bonuses at the first challenge.
        if (!this.firstChallenge) {
            this.setMoney(this.getMoney() + this.challengeClearGold);
            this.setHealth(this.getHealth() + this.challengeClearHealth);
            this.calculateScore();
            this.challengeCounter += 1;
            localStorage.setItem(this.getSessionId() + 'challengecounter', String(this.challengeCounter));
            localStorage.removeItem(this.getSessionId() + 'connections');
            this.sendHash();
        } else {
            this.firstChallenge = false;
        }
        // A new challenge begins; reset the waves required to 0.
        this.setWavesRequired(0);
    }

    /**
     * Calculates the new score of the player, based on a certain function.
     * The function chosen can be selected in levelSettings.json.
     */
    public calculateScore(): void {
        // Check if waves required is 0, to prevent dividing by 0.
        if (this.getWavesRequired() === 0) {
            ErrorState.throw(this.game, 'Waves required is equal to 0');
        }
        switch (this.scoreFunction) {
            // Gradually decrease the score based on the amount of waves required.
            case 1:
                this.setScore(this.getScore() + Math.floor(this.getAvailableScore() / this.getWavesRequired()));
                break;
            // The same as function 1, with a lower limit.
            case 2:
                this.setScore(this.getScore() +
                    Math.floor(Math.max(this.getAvailableScore() / 4,
                        this.getAvailableScore() / this.getWavesRequired())));
                break;
            // Linearly decrease the score based on the amount of waves required, with a lower limit.
            case 3:
                this.setScore(this.getScore() +
                    Math.floor(Math.max(this.getAvailableScore() / 4,
                        this.getAvailableScore() - (this.getAvailableScore() / 4) * (this.getWavesRequired() - 1))));
                break;
            default:
                console.log('Score function not selected! Defaulting to score function 1.');
                this.setScore(this.getScore() + Math.floor(this.getAvailableScore() / this.getWavesRequired()));
                break;
        }
    }

    // remove all items in local storage from this game
    public removeAllLocalStorage(): void {
      let keys: string[] = Object.keys(localStorage);
      let i: number = keys.length;

      while ( i-- ) {
        if (keys[i].match(this.getSessionId()) !== null)
          localStorage.removeItem(keys[i]);
      }
    }

    public makeHash(): number {

      // Get all the locally stored information in one concattenated string
      let totalString: string = '';
      let keys: string[] = Object.keys(localStorage);
      let i: number = keys.length;

      while ( i-- ) {
        if (keys[i].match(this.getSessionId()) !== null)
        totalString = totalString + localStorage.getItem(keys[i]);
      }

      // convert this string into a hash value
      let hash: number = 0;
      for (i = 0; i < totalString.length; i++) {
        let chr: number   = totalString.charCodeAt(i);
        hash  = ((hash << 5) - hash) + chr;
        hash |= 0; // Convert to 32bit integer
      }
      return hash;
    }

    // send the new version of the hash to the game server
    public sendHash(): void {
      const hash: number = this.makeHash();
      Connection.connection.sendHash(hash);
    }

    /**
     * Adds a key-capture for the specified Phaser.KeyCode
     * @param key the key to start capturing. Phaser.KeyCode.KEY should be used for this.
     */
    public captureKey(key: number): void {
        this.game.input.keyboard.addKey(key).onDown.add(() => this.onKeyPressed(key), this);
    }

    public getLevelState(): LevelState { return this.levelState; }
    public setLevelState(levelState: LevelState): void {
        this.levelState = levelState;
        MainGameTask.mainTask.changeState();
    }

    public getGrid(): Grid { return this.grid; }
    public setGrid(grid: Grid): void { this.grid = grid; }

    public getPrePath(): Path { return this.prePath; }
    public setPrePath(prePath: Path): void { this.prePath = prePath; }

    public getPostPath(): Path { return this.postPath; }
    public setPostPath(postPath: Path): void { this.postPath = postPath; }

    public getTowers(): Set<Tower> { return this.towers; }

    public getPreScanner(): ConditionScanner { return this.preScanner; }
    public setPreScanner(preTower: ConditionScanner): void { this.preScanner = preTower; }

    public getPostScanner(): ConditionScanner { return this.postScanner; }
    public setPostScanner(postTower: ConditionScanner): void { this.postScanner = postTower; }

    public getCurrentScanner(): ConditionScanner { return this.currentScanner; }
    public setCurrentScanner(scanner: ConditionScanner): void { this.currentScanner = scanner; }

    public getPlacingTower(): Tower { return this.placingTower; }
    public setPlacingTower(placingTower: Tower): void { this.placingTower = placingTower; }

    public getSelectedTower(): Tower { return this.selectedTower; }
    /**
     * Set the selected tower variable, as well as drawing a circle around the tower to visualize its range
     * @param selectedTower The tower to select
     */
    public setSelectedTower(selectedTower: Tower): void {
        this.selectedTower = selectedTower;
        this.selectedTowerGraphics.clear();
        if (selectedTower instanceof Tower) {
            const screenSpace: Phaser.Point = selectedTower.position;
            const gridSpace: Phaser.Point = this.grid.screenSpaceToGridSpace(screenSpace.x, screenSpace.y);

            if (this.grid.isWithinBounds(gridSpace.x, gridSpace.y)) {
                // Draw a circle to visualize the tower's range
                this.selectedTowerGraphics.lineStyle(1, 0xffffff, 0.5);
                this.selectedTowerGraphics.drawCircle(selectedTower.x, selectedTower.y, selectedTower.getRange() * 2);
            }
        }
    }

    public getSelectedTowerGraphics(): Phaser.Graphics { return this.selectedTowerGraphics; }
    public setSelectedTowerGraphics(selectedTowerGraphics: Phaser.Graphics): void { this.selectedTowerGraphics = selectedTowerGraphics; }

    public getSparks(): Set<Spark> { return this.sparks; }

    public getBlockBuildingRenderGroup(): Phaser.Group { return this.blockBuildingRenderGroup; }
    public setBlockBuildingRenderGroup(blockBuildingRenderGroup: Phaser.Group): void {
        this.blockBuildingRenderGroup = blockBuildingRenderGroup;
    }

    public getBlockBuildScreenRenderGroup(): Phaser.Group { return this.blockBuildScreenRenderGroup; }
    public setBlockBuildScreenRenderGroup(blockBuildScreenRenderGroup: Phaser.Group): void {
        this.blockBuildScreenRenderGroup = blockBuildScreenRenderGroup;
    }

    public getUIScreenRenderGroup(): Phaser.Group { return this.UIScreenRenderGroup; }
    public setUIScreenScreenGroup(UIScreenRenderGroup: Phaser.Group): void { this.UIScreenRenderGroup = UIScreenRenderGroup; }

    public getGameRenderGroup(): Phaser.Group { return this.gameRenderGroup; }
    public setGameRenderGroup(gameRenderGroup: Phaser.Group): void { this.gameRenderGroup = gameRenderGroup; }

    public getDescription(): string { return this.description; }
    public setDescription(description: string): void { this.description = description; this.hintScreen.resetHints(); }

    public getRefundTowerModifier(): number { return this.refundTowerModifier; }
    public getRefundBlockModifier(): number { return this.refundBlockModifier; }

    public getScore(): number { return this.score; }
    public setScore(score: number): void { this.score = score; }

    public getAvailableScore(): number { return this.availableScore; }
    public setAvailableScore(availableScore: number): void { this.availableScore = availableScore; }

    public getWavesRequired(): number { return this.wavesRequired; }
    public setWavesRequired(wavesRequired: number): void { this.wavesRequired = wavesRequired; }

    public getHealth(): number { return this.health; }
    /**
     * Set the health, as long as it is not lower than 0
     * @param health The new health value
     */
    public setHealth(health: number): void { this.health = Math.min(this.fullHealth, Math.max(0, health)); }

    public setStartingHealth(health: number): void {this.fullHealth = health; this.health = health; }

    public getFullHealth(): number { return this.fullHealth; }

    public getMoney(): number { return this.money; }
    /**
     * Set the money, as long as it is not lower than 0
     * @param money The new money value
     */
    public setMoney(money: number): void {
        money = Math.max(0, money);

        if (money < this.money) {
            SoundManager.playSoundEffect(moneyLoseSound);
        } else if (money > this.money) {
            SoundManager.playSoundEffect(moneyGainSound);
        }

        this.money = money;

        localStorage.setItem(this.getSessionId() + 'money', String(this.money));
        this.sendHash();
    }

    public setStartingMoney(money: number): void {
        this.startMoney = money;
        this.money = money;
    }

    public getRemainingPauseTime(): number { return this.remainingPauseTime; }

    public getMaxPauseTime(): number { return this.maxPauseTime; }

    public getMoneySpentOnTowers(): number { return this.moneySpentOnTowers; }
    public setMoneySpentOnTowers(moneySpentOnTowers: number): void { this.moneySpentOnTowers = moneySpentOnTowers; }

    public getMoneySpentOnPreCondition(): number { return this.moneySpentOnPreCondition; }

    public getMoneySpentOnPostCondition(): number { return this.moneySpentOnPostCondition; }

    public getSparksPerWave(): number { return this.sparksPerWave; }
    public setSparksPerWave(sparksPerWave: number): void { this.sparksPerWave = sparksPerWave; }

    public getIsWaveStarted(): boolean { return this.isWaveStarted; }
    public getIsWaveEnded(): boolean { return this.isWaveEnded; }

    public getTimeSpentBeforeWave(): number { return this.timeSpentBeforeWave; }

    public getTimeSpentPaused(): number { return this.timeSpentPaused; }

    public getDeltaScore(): number { return this.deltaScore; }
    public setDeltaScore(deltaScore: number): void { this.deltaScore = deltaScore; }

    public getQuestionScore(): number { return this.questionScore; }
    public setQuestionScore(questionScore: number): void { this.questionScore = questionScore; }

    public getPreBlockBuildScreen(): BlockBuildScreen { return this.preBlockBuildScreen; }
    public setPreBlockBuildScreen(preBlockBuildScreen: BlockBuildScreen): void { this.preBlockBuildScreen = preBlockBuildScreen; }

    public getPostBlockBuildScreen(): BlockBuildScreen { return this.postBlockBuildScreen; }
    public setPostBlockBuildScreen(postBlockBuildScreen: BlockBuildScreen): void { this.postBlockBuildScreen = postBlockBuildScreen; }

    public getBlockBuildingMask(): Phaser.Graphics { return this.blockBuildingMask; }
    public setBlockBuildingMask(blockBuildingMask: Phaser.Graphics): void { this.blockBuildingMask = blockBuildingMask; }

    public getSelectedBlockOrConnector(): Block | Connector { return this.selectedBlockOrConnector; }
    public setSelectedBlockOrConnector(blockOrConnector: Block | Connector): void {
        this.selectedBlockOrConnector = blockOrConnector;
    }

    public getPause(): boolean { return this.pause; }

    public getPreFeedback(): string { return this.preFeedback; }
    public setPreFeedback(preFeedback: string): void { this.preFeedback = preFeedback; }

    public getPostFeedback(): string { return this.postFeedback; }
    public setPostFeedback(postFeedback: string): void { this.postFeedback = postFeedback; }

    public getTooltip(): Tooltip { return this.tooltip; }

    public getIsTutorial(): boolean { return this.isTutorial; }

    public getDragging(): boolean { return this.dragging; }
    public setDragging(dragging: boolean): void { this.dragging = dragging; }

    public getCurrentConnector(): Connector { return this.currentConnector; }
    public setCurrentConnector(connector: Connector): void { this.currentConnector = connector; }

    public getPlacingBlock(): Block { return this.placingBlock; }
    public setPlacingBlock(placingBlock: Block): void { this.placingBlock = placingBlock; }

    public getConditionChanged(): boolean { return this.conditionChanged; }
    public setConditionChanged(changed: boolean): void { this.conditionChanged = changed; }

    public getMovingBlock(): Block { return this.movingBlock; }
    public setMovingBlock(movingBlock: Block): void { this.movingBlock = movingBlock; }

    public getMovingMouseOffset(): Phaser.Point { return this.movingMouseOffset; }
    public setMovingMouseOffset(movingMouseOffset: Phaser.Point): void { this.movingMouseOffset = movingMouseOffset; }

    public getValidTypes(): Dictionary<BlockType> { return this.validTypes; }

    public getSparkHealth(): number { return this.sparkHealth; }
    public setSparkHealth(sparkHealth: number): void { this.sparkHealth = sparkHealth; }

    public getSparkSpeed(): number { return this.sparkSpeed; }
    public setSparkSpeed(sparkSpeed: number): void { this.sparkSpeed = sparkSpeed; }

    public getSparkSpawnTime(): number { return this.sparkSpawnTime; }
    public setSparkSpawnTime(sparkSpawnTime: number): void { this.sparkSpawnTime = sparkSpawnTime; }

    public setPathData(pathData: PathData): void { this.pathData = pathData; }

    public setHintScreen(screen: HintScreen): void { this.hintScreen = screen; }
    public getHintScreen(): HintScreen { return this.hintScreen; }

    public getSpecialSparkPercentages(): number[] { return this.specialSparkPercentages; }
    public setSpecialSparkPercentages(specialSparkPercentages: number[]): void { this.specialSparkPercentages = specialSparkPercentages; }

    public setSlideUpPre(slideUp: MenuSlideButton): void { this.slideUpPre = slideUp; }
    public getSlideUpPre(): MenuSlideButton { return this.slideUpPre; }

    public setSlideUpPost(slideUp: MenuSlideButton): void { this.slideUpPost = slideUp; }
    public getSlideUpPost(): MenuSlideButton { return this.slideUpPost; }

    public setSlideUpDesc(slideUp: MenuSlideButton): void { this.slideUpDesc = slideUp; }
    public getSlideUpDesc(): MenuSlideButton { return this.slideUpDesc; }

    public getTimeBetweenWavesLeft(): number { return this.maxBetweenWavesTime - this.currBetweenWavesTime; }

    public setDeadline(deadline: number): void { this.deadline = deadline; }
    public getDeadline(): number { return this.deadline; }

    public setTowerID(towerid: number): void { this.towerid = towerid; }
    public getTowerId(): number { return this.towerid; }

    public setBlockID(blockid: number): void { this.blockid = blockid; }
    public getBlockId(): number { return this.blockid; }

    public getSessionId(): string { return this.sessionId; }
}
