/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University
 * within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
import Tower from '../gameObjects/towers/tower';
import Level from '../states/level';
import Effect from './effect';

export default class SupportEffect extends Effect<Tower> {
    private rangeMultiplier: number;
    private level: Level;

    /**
     * Increase the range of all the towers.
     * @param tower The tower that increases the range.
     * @param duration For how long the effect lasts in updates.
     * @param multiplier The multiplier of the range.
     */
    constructor(level: Level, tower: Tower, duration: number, multiplier: number) {
        super(tower, 'support', 25, duration / 25);

        this.level = level;
        this.rangeMultiplier = multiplier;
    }

    /**
     * @inheritDoc Increases the range of this tower, and all towers in its range.
     */
    public initializeEffect(): void {
        this.increaseRange();
    }

    /**
     * For every tower in the range of the support tower, change its tint and increase its range
     */
    private increaseRange(): void {
        this.object.setRangeMultiplier(this.rangeMultiplier);

        this.object.updateCellsInRange(this.level.getPrePath().getCells(), this.level.getPostPath().getCells());
        this.object.addTowersInRange();
        this.object.getTowersInRange().forEach(tower => {
            if (tower.getRangeMultiplier() !== this.rangeMultiplier) {
                tower.tint = 0x0B510F;
                tower.setRangeMultiplier(this.rangeMultiplier);

                tower.addTowersInRange();
                tower.updateCellsInRange(this.level.getPrePath().getCells(), this.level.getPostPath().getCells());
            }
        });
    }

    /**
     * @inheritDoc Reset the range of all the towers.
     */
    public onObjectDeath(): void {
        this.object.tint = 0xFFFFFF;
        this.object.getTowersInRange().forEach(tower => {
            tower.tint = 0xFFFFFF;
            tower.setRangeMultiplier(1);
            tower.addTowersInRange();
            tower.updateCellsInRange(this.level.getPrePath().getCells(), this.level.getPostPath().getCells());
        });
        this.object.setRangeMultiplier(1);
        this.object.addTowersInRange();
        this.object.updateCellsInRange(this.level.getPrePath().getCells(), this.level.getPostPath().getCells());
    }

    /**
     * Add the applyCount of the reapplied effect to this effect.
     * @param effect The reapplied effect.
     */
    public reapply(effect: Effect<Tower>): void {
        effect.setApplyCount(this.getApplyCount() + effect.getApplyCount());
        this.increaseRange();
    }
}