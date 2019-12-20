/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University
 * within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
import Menu from './menu';

/**
 * MenuTextAlert is a notification box that can be used to provide a player with information about an events that occurred.
 */
export default class MenuTextAlert extends Phaser.Sprite {
    private labelText: Phaser.Text;

    /**
     * Constructs a sprite that shows an image with text next to it
     * @param game The game object
     * @param x The x-coordinate of the sprite
     * @param y The y-coordinate of the sprite
     * @param image The name of the image asset
     * @param text The text you want to display next to the sprite
     */
    public constructor(game: Phaser.Game, x: number, y: number, image: string, text: string, tween: boolean,
        tweenY: number = 0, scale: number = 0.25, fontSize: number = 64) {
        super(game, x, y, image);

        this.anchor.setTo(0, 0);
        this.scale.setTo(scale, scale);

        this.labelText = new Phaser.Text(game, this.width * (1.25 / scale), this.height * (0.55 / scale), text, {
            font: fontSize + 'px Arial',
            fill: 'white'
        });
        this.labelText.anchor.setTo(0, 0.5);
        this.addChild(this.labelText);

        if (tween) {
            const tweenCounter: number = Menu.getTweenCounter();
            const yDiff: number = this.y <= tweenY ? - 30 * tweenCounter : 30 * tweenCounter;

            this.y = game.world.height;
            game.add.tween(this).to({ y: (tweenY + yDiff) }, 2000, Phaser.Easing.Bounce.Out, true, 1000 * tweenCounter, 0, false);
            game.add.tween(this).to({ alpha: 0 }, 5000, Phaser.Easing.Linear.None, true, 3000 + 1000 * tweenCounter).onComplete.add(
                () => {
                    this.destroy();
                    Menu.incrementDoneTweenCounter();
                }
            );

            Menu.incrementTweenCounter();
        }
    }

    public setText(newText: string): void {
        this.labelText.setText(newText, false);
    }
}