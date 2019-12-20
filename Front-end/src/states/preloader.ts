import * as Assets from '../assets';
import * as AssetUtils from '../utils/assetUtils';
import SoundManager from '../soundManager';

export default class Preloader extends Phaser.State {
    private preloadBarSprite: Phaser.Sprite = null;
    private preloadFrameSprite: Phaser.Sprite = null;

    /**
     * Setup your loading screen and preload sprite (if you want a loading progress indicator) here
     */
    public preload(): void {

        this.preloadBarSprite = this.game.add.sprite(this.game.world.centerX, this.game.world.centerY,
            Assets.Atlases.AtlasesPreloadSpritesArray.getName(),
            Assets.Atlases.AtlasesPreloadSpritesArray.Frames.PreloadBar);
        this.preloadBarSprite.anchor.setTo(0, 0.5);
        this.preloadBarSprite.x -= this.preloadBarSprite.width * 0.5;

        this.preloadFrameSprite = this.game.add.sprite(this.game.world.centerX, this.game.world.centerY,
            Assets.Atlases.AtlasesPreloadSpritesArray.getName(),
            Assets.Atlases.AtlasesPreloadSpritesArray.Frames.PreloadFrame);
        this.preloadFrameSprite.anchor.setTo(0.5);

        this.game.load.setPreloadSprite(this.preloadBarSprite);

        this.game.time.advancedTiming = true;

        this.game.load.json('settings', 'assets/settings.json');
        this.game.load.json('towerSettings', 'assets/towerSettings.json');
        this.game.load.json('effectSettings', 'assets/effectSettings.json');
        this.game.load.json('blockbuildingSettings', 'assets/blockbuildingSettings.json');
        this.game.load.json('levelSettings', 'assets/levelSettings.json');
        this.game.load.json('hotkeys', 'assets/hotkeys.json');

        const language: string = 'en';

        this.game.load.json('towersText', 'assets/localization/' + language + '/towersText.json');
        this.game.load.json('buttonsText', 'assets/localization/' + language + '/buttonsText.json');
        this.game.load.json('tutorialText', 'assets/localization/' + language + '/tutorialText.json');
        this.game.load.json('uiText', 'assets/localization/' + language + '/uiText.json');
        this.game.load.json('warningsText', 'assets/localization/' + language + '/warningsText.json');

        this.game.stage.disableVisibilityChange = true;

        AssetUtils.Loader.loadAllAssets(this.game, this.waitForSoundDecoding, this);

        SoundManager.initialize(this.game);
    }

    private waitForSoundDecoding(): void {
        AssetUtils.Loader.waitForSoundDecoding(this.startGame, this);
    }

    private startGame(): void {
        this.game.camera.onFadeComplete.addOnce(this.loadTitle, this);
        this.game.camera.fade(0x000000, 1000);
    }

    private loadTitle(): void {
        this.game.state.start('menu');
    }
}
