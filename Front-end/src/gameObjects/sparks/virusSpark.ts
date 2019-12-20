import VirusEffect from '../../effects/virusEffect';
import Level from '../../states/level';
import Grid from '../grid/grid';
import Path from '../path/path';
import Spark from './spark';

const range: number = 80;

export default class VirusSpark extends Spark {

    private lastAffectTime: number;

    /**
     * Constructor of a virus spark
     * @param level The level to initialze the virus spark in
     * @param grid The grid to place the virus spark in
     * @param health The total health of the virus spark
     * @param speed The speed of the virus spark
     */
    constructor(level: Level, grid: Grid, health: number, speed: number) {
        super(level, grid, health, speed);

        this.lastAffectTime = null;
    }

    public placeOnPath(path: Path, correct: boolean, satisfiesCondition: boolean): void {
        super.placeOnPath(path, false, satisfiesCondition);

        this.tint = Phaser.Color.BLACK;
    }

    public mark(): void {
        super.mark();

        this.tint = Phaser.Color.BLACK;
    }

    public updateObject(): void {
        super.updateObject();

        // Update lastAffectTime while the game is paused to keep sparks from affecting towers at the wrong time after pausing
        if (this.level.getPause()) {
            this.lastAffectTime += this.level.game.time.elapsed;

            return;
        }

        if (this.alive && (this.lastAffectTime === null ||
            this.game.time.time - this.lastAffectTime > this.effectSettings['Virus']['cooldown'])) {
            this.level.getTowers().forEach(tower => {
                const distance: number = Math.sqrt(
                    (tower.centerX - this.x) * (tower.centerX - this.x) +
                    (tower.centerY - this.y) * (tower.centerY - this.y)
                );

                if (distance <= range) {
                    this.lastAffectTime = this.game.time.time;
                    tower.applyEffect(new VirusEffect(tower, this.effectSettings['Virus']['duration']));
                }
            });
        }
    }

}