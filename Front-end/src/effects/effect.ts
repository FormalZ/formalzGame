/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University
 * within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
import GameObject from './../gameObjects/gameObject';

export default abstract class Effect<T extends GameObject> {
    protected object: T;
    private name: string;
    protected applyInterval: number;
    protected applyCount: number;
    protected timeUntilApply: number;

    /**
     * A generic effect that can be applied to another game object.
     * @param obj The effected object.
     * @param name The name of the effect.
     * @param applyInterval The amount of milliseconds between each apply.
     * @param applyCount How often the effect applies.
     */
    constructor(obj: T, name: string, applyInterval: number, applyCount: number) {
        this.object = obj;
        this.name = name;
        this.applyInterval = applyInterval;
        this.applyCount = applyCount;
        this.timeUntilApply = applyInterval;
    }

    /**
     * The part of the effect that gets applied when the effect is just applied.
     */
    public initializeEffect(): void { }

    /**
     * The part of the effect that gets applied when the effect ends.
     */
    public endEffect(): void { }

    /**
     * The part of the effect that gets applied when the respective game object dies.
     */
    public onObjectDeath(): void { }

    /**
     * The part of the effect that gets applied every time interval.
     */
    public applyEffect(): void { }

    /**
     * What happens when the effect already exists and gets reapplied.
     * @param activeEffect The new effect.
     */
    public reapply(activeEffect: Effect<T>): void { }

    public getApplyCount(): number { return this.applyCount; }
    public setApplyCount(applyCount: number): void { this.applyCount = applyCount; }

    public getName(): string { return this.name; }

    public setObject(object: T): void { this.object = object; }

    public getTimeUntilApply(): number { return this.timeUntilApply; }
    public setTimeUntilApply(timeUntilApply: number): void { this.timeUntilApply = timeUntilApply; }

    public getApplyInterval(): number { return this.applyInterval; }
}