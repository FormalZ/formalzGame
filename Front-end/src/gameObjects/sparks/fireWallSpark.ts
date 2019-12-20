/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University
 * within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
import * as Assets from '../../assets';
import ProtectedEffect from '../../effects/protectedEffect';
import Level from '../../states/level';
import Grid from '../grid/grid';
import Path from '../path/path';
import Spark from './spark';
import VirusSpark from './virusSpark';

const range: number = 65;
const incorrectUnmarkedSpriteName: string = Assets.Images.SpritesBadSpark.getName();

export default class FireWallSpark extends Spark {

    /**
     * The constructor of a firewall spark
     * @param level The level to initialize the firewall spark
     * @param grid The grid to place the firewall spark in
     * @param health The total health of the firewall spark
     * @param speed The speed of the firewall spark
     */
    constructor(level: Level, grid: Grid, health: number, speed: number) {
        super(level, grid, health, speed);
    }

    public placeOnPath(path: Path, correct: boolean, satisfiesCondition: boolean): void {
        super.placeOnPath(path, false, satisfiesCondition);

        this.loadTexture(incorrectUnmarkedSpriteName);
        this.tint = Phaser.Color.AQUA;
    }

    public mark(): void {
        super.mark();

        this.tint = Phaser.Color.AQUA;
    }

    public updateObject(): void {
        super.updateObject();

        if (this.alive) {
            this.level.getSparks().forEach(spark => {
                const distance: number = Math.sqrt(
                    (spark.centerX - this.x) * (spark.centerX - this.x) +
                    (spark.centerY - this.y) * (spark.centerY - this.y)
                );

                if (distance <= range && !(spark instanceof FireWallSpark) && !(spark instanceof VirusSpark)) {
                    spark.applyEffect(new ProtectedEffect(spark));
                }
            });
        }
    }
}