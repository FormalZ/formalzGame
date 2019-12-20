/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University
 * within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
import * as Assets from '../../assets';
import Level from '../../states/level';
import GameObject from '../../gameObjects/gameObject';
import Grid from '../../gameObjects/grid/grid';
import ShootingMode from '../../gameObjects/towers/shootingMode';
import Tower from '../../gameObjects/towers/tower';
import SoundManager from '../../soundManager';
import GameTask from '../gameTask';
import SingleShotTower from '../../gameObjects/towers/singleshotTower';
import CircleTower from '../../gameObjects/towers/circleTower';
import MultiShotTower from '../../gameObjects/towers/multishotTower';
import PierceTower from '../../gameObjects/towers/pierceTower';
import SniperTower from '../../gameObjects/towers/sniperTower';

const errorSound: string = Assets.Audio.AudioError.getName();

/**
 * TowerGameTask is a GameTask that manager all the Towers in the game.
 * It updates placed Towers.
 * It places Towers that are being dragged.
 * It takes care of Tower hotkeys.
 */
export default class TowerGameTask extends GameTask {
    private hotkeys: object;

    private highestId: number;

    /**
     * Initialize the tower game task.
     * This is done by resetting the selected tower graphic and initializing the hot key handlers.
     * @param level The current level to initialize the game task in
     */
    public initialize(level: Level): void {
        super.initialize(level);

        this.level.setSelectedTowerGraphics(this.level.game.add.graphics(0, 0));
        this.level.setSelectedTower(null);

        this.hotkeys = this.level.cache.getJSON('hotkeys')['playing'];

        this.level.captureKey(Phaser.KeyCode[this.hotkeys['removeObject1']]);           // R
        this.level.captureKey(Phaser.KeyCode[this.hotkeys['removeObject2']]);           // DELETE
        this.level.captureKey(Phaser.KeyCode[this.hotkeys['effectUpgrade']]);           // E
        this.level.captureKey(Phaser.KeyCode[this.hotkeys['boostUpgrade']]);            // B
        this.level.captureKey(Phaser.KeyCode[this.hotkeys['shootingModeLast']]);        // L
        this.level.captureKey(Phaser.KeyCode[this.hotkeys['shootingModeFirst']]);       // F
        this.level.captureKey(Phaser.KeyCode[this.hotkeys['shootingModeStrongest']]);   // S
        this.level.captureKey(Phaser.KeyCode[this.hotkeys['shootingModeWeakest']]);     // W
        this.level.captureKey(Phaser.KeyCode[this.hotkeys['shootingModeClosest']]);     // C

        this.highestId = 0;

        this.placeTowersFromStorage('singleshottowers');
        this.placeTowersFromStorage('circletowers');
        this.placeTowersFromStorage('multishottowers');
        this.placeTowersFromStorage('piercetowers');
        this.placeTowersFromStorage('snipertowers');
    }

    /**
     * Update shooting towers, have them shoot and destroy old shots.
     */
    public update(): void {
        // Shoot a tower on a ShootingTower update
        this.level.getTowers().forEach(tower => tower.updateObject());

        const placingTower: Tower = this.level.getPlacingTower();
        if (placingTower) {
            const grid: Grid = this.level.getGrid();

            const screenSpace: Phaser.Point = this.level.game.input.activePointer.position;
            const gridSpace: Phaser.Point = grid.screenSpaceToGridSpace(screenSpace.x, screenSpace.y);
            const position: Phaser.Point = grid.gridSpaceToScreenSpace(gridSpace.x, gridSpace.y);

            const money: number = this.level.getMoney();
            const valid: boolean = money >= placingTower.getTowerCost() &&
                grid.isValidHitbox(gridSpace.x, gridSpace.y, placingTower.getHitbox());

            placingTower.position = position;

            // Add placing tower tint, red if the placing is not within range, green if the tower's range is affected,
            // simulate the rangemultiplier
            if (!valid) {
                placingTower.setRangeMultiplier(1);
                placingTower.updateObject();
                placingTower.tint = 0xff0000;
            } else if ((grid.isWithinBounds(gridSpace.x, gridSpace.y)) && (grid.isValidHitbox(gridSpace.x, gridSpace.y,
                placingTower.getHitbox())) && (this.isAffected(grid, gridSpace, placingTower))) {
                placingTower.tint = 0x0B510F;
                const support: object = this.level.game.cache.getJSON('effectSettings');
                placingTower.setRangeMultiplier(support['Support']['rangeMultiplier']);
                placingTower.updateObject();
            } else {
                placingTower.setRangeMultiplier(1);
                placingTower.updateObject();
                placingTower.tint = 0xffffff;
            }

            this.level.setSelectedTower(placingTower);
        }
    }

    /**
     * Checks whether the given tower is affected by the range effect.
     * @param grid The grid.
     * @param gridSpace Position of the given tower in grid space.
     * @param placingTower The given tower.
     * @returns true if the given tower is affected by the range effect.
     */
    private isAffected(grid: Grid, gridSpace: Phaser.Point, placingTower: Tower): boolean {
        let isAffected: boolean = false;

        placingTower.getHitbox().foreach(gridSpace.x, gridSpace.y, (i: number, j: number) => {
            if (grid.getCell(i, j).hasRangeEffect()) {
                isAffected = true;
            }
        });
        return isAffected;
    }

    /**
     * Checks whether a tower can be built on this location, and does so when allowed.
     * @param pointer Position the mouse was clicked on.
     */
    public onMouseUp(pointer: Phaser.Pointer): boolean {
        const grid: Grid = this.level.getGrid();
        const placingTower: Tower = this.level.getPlacingTower();

        if (placingTower) {
            const money: number = this.level.getMoney();
            const gridSpace: Phaser.Point = grid.screenSpaceToGridSpace(placingTower.position.x, placingTower.position.y);

            if (money >= placingTower.getTowerCost() && grid.isValidHitbox(gridSpace.x, gridSpace.y, placingTower.getHitbox())) {
                this.createTower(placingTower, gridSpace, this.level.getMoney(), true);

                this.level.setPlacingTower(null);
            } else {
                SoundManager.playSoundEffect(errorSound);
            }

            return true;
        }

        // If the cell the player clicked was not a valid place to build a Tower, select the tower that occupies that cell, if there is one
        const position: Phaser.Point = grid.screenSpaceToGridSpace(pointer.position.x, pointer.position.y);
        if (position.x < grid.getWidth() && position.x >= 0 && position.y < grid.getHeight() && position.y >= 0) {
            const object: GameObject = grid.getCell(position.x, position.y).getObject();

            if (object instanceof Tower) {
                this.level.setSelectedTower(object);

                return true;
            }
        }

        this.level.setSelectedTower(null);

        return false;
    }

    /**
     * Handle the key event.
     * @param key The key code of the pressed key
     */
    public onKeyPressed(key: number): boolean {
        switch (key) {
            case Phaser.KeyCode[this.hotkeys['effectUpgrade']]:         // E
                this.upgradeTower(true);
                return true;
            case Phaser.KeyCode[this.hotkeys['boostUpgrade']]:          // B
                this.upgradeTower(false);
                return true;
            case Phaser.KeyCode[this.hotkeys['shootingModeLast']]:      // L
                if (this.level.getSelectedTower()) {
                    this.level.getSelectedTower().setShootingMode(ShootingMode.Last);
                }
                return true;
            case Phaser.KeyCode[this.hotkeys['shootingModeFirst']]:     // F
                if (this.level.getSelectedTower()) {
                    this.level.getSelectedTower().setShootingMode(ShootingMode.First);
                }
                return true;
            case Phaser.KeyCode[this.hotkeys['shootingModeStrongest']]: // S
                if (this.level.getSelectedTower()) {
                    this.level.getSelectedTower().setShootingMode(ShootingMode.Strongest);
                }
                return true;
            case Phaser.KeyCode[this.hotkeys['shootingModeWeakest']]:   // W
                if (this.level.getSelectedTower()) {
                    this.level.getSelectedTower().setShootingMode(ShootingMode.Weakest);
                }
                return true;
            case Phaser.KeyCode[this.hotkeys['shootingModeClosest']]:   // C
                if (this.level.getSelectedTower()) {
                    this.level.getSelectedTower().setShootingMode(ShootingMode.Closest);
                }
                return true;
            default:
                return false;
        }
    }

    /**
     * Initializes and places a tower.
     * @param gridPos Position to place tower in grid space.
     * @param money Current amount of money, tower cost will be subtracted.
     * @param localS Indicator whether to store this tower to local storage.
     */
    public createTower(tower: Tower, gridPos: Phaser.Point, money: number, localS: boolean): void {
        if (localS) {
          // check which type of tower this is, and add that type of tower to local storage with its position
          // the 'X' indicates that the tower has no upgrades applied to it
          if (tower instanceof SingleShotTower) {
            const localStorageString: string = localStorage.getItem(this.level.getSessionId() + 'singleshottowers');
            if (localStorageString !== null) {
              localStorage.setItem(this.level.getSessionId() + 'singleshottowers', localStorageString + '|' + tower.getId() + ';' + gridPos.x + ';' + gridPos.y + ';X');
            }
            else {
              localStorage.setItem(this.level.getSessionId() + 'singleshottowers', tower.getId() + ';' + gridPos.x + ';' + gridPos.y + ';X');
            }
          }
          else if (tower instanceof CircleTower) {
            const localStorageString: string = localStorage.getItem(this.level.getSessionId() + 'circletowers');
            if (localStorageString !== null) {
              localStorage.setItem(this.level.getSessionId() + 'circletowers', localStorageString + '|' + tower.getId() + ';' + gridPos.x + ';' + gridPos.y + ';X');
            }
            else {
              localStorage.setItem(this.level.getSessionId() + 'circletowers', tower.getId() + ';' + gridPos.x + ';' + gridPos.y + ';X');
            }
          }
          else if (tower instanceof MultiShotTower) {
            const localStorageString: string = localStorage.getItem(this.level.getSessionId() + 'multishottowers');
            if (localStorageString !== null) {
              localStorage.setItem(this.level.getSessionId() + 'multishottowers', localStorageString + '|' + tower.getId() + ';' + gridPos.x + ';' + gridPos.y + ';X');
            }
            else {
              localStorage.setItem(this.level.getSessionId() + 'multishottowers', tower.getId() + ';' + gridPos.x + ';' + gridPos.y + ';X');
            }
          }
          else if (tower instanceof PierceTower) {
            const localStorageString: string = localStorage.getItem(this.level.getSessionId() + 'piercetowers');
            if (localStorageString !== null) {
              localStorage.setItem(this.level.getSessionId() + 'piercetowers', localStorageString + '|' + tower.getId() + ';' + gridPos.x + ';' + gridPos.y + ';X');
            }
            else {
              localStorage.setItem(this.level.getSessionId() + 'piercetowers', tower.getId() + ';' + gridPos.x + ';' + gridPos.y + ';X');
            }
          }
          else if (tower instanceof SniperTower) {
            const localStorageString: string = localStorage.getItem(this.level.getSessionId() + 'snipertowers');
            if (localStorageString !== null) {
              localStorage.setItem(this.level.getSessionId() + 'snipertowers', localStorageString + '|' + tower.getId() + ';' + gridPos.x + ';' + gridPos.y + ';X');
            }
            else {
              localStorage.setItem(this.level.getSessionId() + 'snipertowers', tower.getId() + ';' + gridPos.x + ';' + gridPos.y + ';X');
            }
          }
          this.level.sendHash();
        }
        const grid: Grid = this.level.getGrid();

        // Add the range from the different paths
        tower.addPathRange(this.level.getPrePath().getCells());
        tower.addPathRange(this.level.getPostPath().getCells());
        tower.addTowersInRange();

        // Update the grid
        grid.placeHitbox(gridPos.x, gridPos.y, tower.getHitbox(), tower);

        tower.updateObject();
        this.level.setSelectedTower(tower);
        this.level.getTowers().add(tower);

        this.level.setMoney(money - tower.getTowerCost());
        this.level.setMoneySpentOnTowers(this.level.getMoneySpentOnTowers() + tower.getTowerCost());

        tower.alpha = 1;
    }

    /**
     * Upgrades the tower the player has selected.
     * The upgrade cost and what a specific upgrade does is specified in each separate tower.
     * @param isEffect upgrade the tower with an effect (false), or with stats (true)
     */
    private upgradeTower(isEffect: boolean): void {
        const towerToUpgrade: Tower = this.level.getSelectedTower();
        if (towerToUpgrade) {
            // Perform what is necessary for the upgrade of this specific tower (what the upgrade costs and what it upgrades specifically).
            towerToUpgrade.upgradeTower(isEffect);
            // Reselect the tower, to revisualize a possible update to its range.
            towerToUpgrade.updateObject();

            this.level.setSelectedTower(towerToUpgrade);

            if (this.level.getTooltip().getObject() === towerToUpgrade) {
                this.level.getTooltip().upgradeUpdate(towerToUpgrade.getTooltipString(), towerToUpgrade);
            }
        }
    }

    private placeTowersFromStorage(type: string): void {
      const towersStorage: string = localStorage.getItem(this.level.getSessionId() + type);
      if (towersStorage !== null) {
        const positionsStrings: string[] = towersStorage.split('|');
        // loop over all towers from the given type of tower,
        // and place them based on the position loaded from local storage
        positionsStrings.forEach(towerStorage => {
          const positionStrings: string[] = towerStorage.split(';');
          const id: string = positionStrings[0];
          const position: Phaser.Point = new Phaser.Point(Number(positionStrings[1]), Number(positionStrings[2]));
          const positionScreen: Phaser.Point = this.level.getGrid().gridSpaceToScreenSpace(position.x, position.y);
          const upgrade: string = positionStrings[3];
          let placingTower: Tower = null;
          if (type === 'singleshottowers')
                placingTower = new SingleShotTower(this.level, positionScreen.x, positionScreen.y);
          else if (type === 'circletowers')
                placingTower = new CircleTower(this.level, positionScreen.x, positionScreen.y);
          else if (type === 'multishottowers')
                placingTower = new MultiShotTower(this.level, positionScreen.x, positionScreen.y);
          else if (type === 'piercetowers')
                placingTower = new PierceTower(this.level, positionScreen.x, positionScreen.y);
          else if (type === 'snipertowers')
                placingTower = new SniperTower(this.level, positionScreen.x, positionScreen.y);
          this.level.game.add.existing(placingTower);
          this.level.getGameRenderGroup().add(placingTower);
          this.createTower(placingTower, position, this.level.getMoney(), false);
          placingTower.setId(id);
          const idNum: number = Number(id.replace('T', ''));
          if (idNum > this.highestId) {
            this.highestId = idNum;
          }
          if (upgrade === 'N') {
            this.upgradeTower(false);
          }
          else if (upgrade === 'E') {
            this.upgradeTower(true);
          }
        });
        this.level.setTowerID( this.highestId + 1);
      }
      this.level.setSelectedTower(null);
    }
}
