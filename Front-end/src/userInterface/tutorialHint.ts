/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University
 * within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */

/**
 * A tutorial hint is used in the tutorial to highlight various aspects of the game.
 */
export default class TutorialHint {
    private text: string;
    private rectangles: PIXI.Rectangle[];
    private action: () => void;

    /**
     * Constructor for a Tutorial Hint.
     * @param text The text that provides an explanation.
     * @param rectangles An array of PIXI.Rectangles that are used to highlight aspects of the game.
     * This was made a PIXI.Rectangle so you can use getBounds() on a sprite (which returns a PIXI.Rectangle).
     * @param action A method that will be executed when the hint is shown.
     */
    public constructor(text: string, rectangles: PIXI.Rectangle[] = [], action: () => void = () => { }) {
        this.text = text;
        this.rectangles = rectangles;
        this.action = action;
    }

    public getText(): string { return this.text; }
    public getRectangles(): PIXI.Rectangle[] { return this.rectangles; }
    public getAction(): () => void { return this.action; }
}