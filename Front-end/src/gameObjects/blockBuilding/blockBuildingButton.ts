/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University
 * within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
import * as Assets from '../../assets';
import Level from '../../states/level';
import MenuButton from '../../userInterface/menuButton';
import Block from './blocks/block';
import BlockCreator from './blockCreator';

const blockSprite: string = Assets.Images.SpritesChipBlock.getName();

/**
 * A BlockBuildingButton is a button that allows the button to place a Block.
 * This can both be done by dragging the mouse or just clicking the button.
 */
export default class BlockBuildingButton extends MenuButton {

    private level: Level;
    private icon: Block = null;

    /**
     * Instantiates a new BlockBuildingButton.
     * @param level The Level that this Button belongs to.
     * @param creator The BlockCreator that this Button should use to instantiate its Block.
     */
    public constructor(level: Level, creator: BlockCreator) {
        super(level.game,
            0, 0,
            blockSprite,
            '',
            new Phaser.Point(0.15, 0.15),
            () => {
                level.setDragging(false);

                const previousPlacingBlock: Block = level.getPlacingBlock();
                if (previousPlacingBlock) {
                    previousPlacingBlock.destroy();
                }

                const block: Block = creator(this.x, this.y);
                block.mask = level.getBlockBuildingMask();
                block.alpha = 0.65;

                level.setPlacingBlock(block);
                level.game.add.existing(block);

                level.getBlockBuildingRenderGroup().add(block);
            },
            () => level.setDragging(false)
        );

        this.level = level;

        // Add an instance of the block as the icon of this button.
        // This block will not receive any update or mouse handling calls, so it will not be functional.
        this.icon = creator(0, 0);
        this.icon.scale.set(1, 1);
        this.addChild(this.icon);

        // Display the cost of the block above the block icon
        const cost: Phaser.Text = new Phaser.Text(level.game, 0, -this.icon.height / 2, this.icon.getCost().toString(), {
            fontSize: 100,
            fill: 'yellow'
        });
        cost.anchor.set(0.5, 1);
        this.addChild(cost);
    }

    public updateObject(): void {
        super.updateObject();

        const pointer: Phaser.Point = this.level.input.activePointer.position;

        if (this.visible && this.getBounds().contains(pointer.x, pointer.y)) {
            const text: string = this.icon.getTooltipString();
            this.level.getTooltip().show(text, this);
        }
    }
}