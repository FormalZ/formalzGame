/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University
 * within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
export default abstract class GameState extends Phaser.State {
    private isDown: boolean = false;

    public update(): void {
        if (this.input.activePointer.isDown && !this.isDown) {
            this.onMouseDown();

            this.isDown = true;
        }

        if (this.input.activePointer.isUp && this.isDown) {
            this.onMouseUp();

            this.isDown = false;
        }
    }

    protected onMouseDown(): void { }
    protected onMouseUp(): void { }
}