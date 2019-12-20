/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University
 * within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
import Effect from '../effects/effect';
import Level from '../states/level';
import Dictionary from '../utils/dictionary';

/**
 * GameObject is an abstract class that provides a common interface for all objects that are to be placed in the game world.
 */
export default abstract class GameObject extends Phaser.Sprite {
    protected level: Level;
    protected newAllEffects: Dictionary<Effect<GameObject>>;

    /**
     * Instantiates a new GameObject
     * @param level The Level that this GameObject exists in.
     * @param x The x coordinate of this GameObject in screen space.
     * @param y The y coordinate of this GameObject in screen space.
     * @param image The image that this GameObject should display.
     */
    constructor(level: Level, x: number, y: number, image: string) {
        super(level.game, x, y, image);

        this.level = level;
        this.newAllEffects = new Dictionary<Effect<GameObject>>();
    }

    /**
    * Method to handle updates.
    * For all the gameObjects that can have an effect, call super.updateObject()!
    * This method should be overridden instead of Phaser's built-in 'update' method.
    * This allows GameTasks to decide when a GameObject should receive its updateObject() call,
    * instead of Phaser deciding when a Sprite should receive its update call.
    */
    public updateObject(): void {
        this.newAllEffects.getValues().forEach(effect => {
            effect.setTimeUntilApply(effect.getTimeUntilApply() - this.game.time.physicsElapsedMS);

            // If the effect should be applied.
            if (effect.getTimeUntilApply() <= 0) {
                effect.setTimeUntilApply(effect.getApplyInterval());
                effect.setApplyCount(effect.getApplyCount() - 1);
                effect.applyEffect();

                // If the effect is now done.
                if (effect.getApplyCount() <= 0) {
                    if (DEBUG && effect.getApplyCount() < 0) {
                        console.log('BUG! Apply count became negative');
                    }

                    effect.endEffect();
                    this.newAllEffects.remove(effect.getName());
                }
            }
        });
     }

    /**
    * Method to handle mouse down events, sent from the Level.
    * @param pointer The Phaser.Pointer object
    * @returns whether or not the event was handled by this GameObject
    */
    public onMouseDown(pointer: Phaser.Pointer): boolean {
        return false;
    }

    /**
    * Method to handle mouse up events, sent from the Level.
    * @param pointer The Phaser.Pointer object
    * @returns whether or not the event was handled by this GameObject
    */
    public onMouseUp(pointer: Phaser.Pointer): boolean {
        return false;
    }

    /**
     * Checks if this GameObject contains the mouse.
     * @param pointer the Phaser.Pointer object.
     * @returns whether or not this GameObject's bounds contain the mouse pointer.
     */
    public containsMouse(pointer: Phaser.Pointer): boolean {
        return this.getBounds().contains(pointer.x, pointer.y);
    }

    /**
     * Apply the effect to the gameObject.
     * @param effect The effect that gets applied.
     */
    public applyEffect(effect: Effect<GameObject>): void {
        if (this.newAllEffects.containsKey(effect.getName())) {
            effect.reapply(this.newAllEffects.get(effect.getName()));
        } else {
            this.newAllEffects.add(effect.getName(), effect);
            effect.initializeEffect();
        }
    }

    /**
     * If the objects gets destroyed, apply all the death effects.
     */
    public destroyObject(): void {
        this.destroy();

        this.newAllEffects.getValues().forEach(effect => effect.onObjectDeath());
     }
}