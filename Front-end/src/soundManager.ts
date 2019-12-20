/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University
 * within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
// TODO: maybe put these in a JSON as well?
const defaultSoundEffectsVolume: number = 0.6;
const defaultMusicVolume: number = 1;

/**
 * Static class to deal with managing sound.
 */
export default class SoundManager {
    private static game: Phaser.Game;

    private static soundEffectsVolume: number = defaultSoundEffectsVolume;
    private static soundEffects: Set<string> = new Set<string>();

    private static musicVolume: number = defaultMusicVolume;
    private static music: Phaser.Sound;

    // Private constructor to prevent anyone from instantiating this class.
    private constructor() { }

    /**
     * Initializes the SoundManager.
     * This must be called once before the class becomes usable.
     * @param game the game in which the SoundManager runs.
     */
    public static initialize(game: Phaser.Game): void {
        SoundManager.game = game;
    }

    /**
     * Plays a given sound effect.
     * @param name The name of the sound effect to be played.
     */
    public static playSoundEffect(name: string): void {
        if (!SoundManager.soundEffects.has(name)) {
            const sound: Phaser.Sound = SoundManager.game.sound.play(name, SoundManager.soundEffectsVolume);
            SoundManager.soundEffects.add(name);

            sound.onStop.add(() => SoundManager.soundEffects.delete(name));
        }
    }

    /**
     * Plays given music on loop.
     * @param name The name of the music to play.
     */
    public static playMusic(name: string): void {
        SoundManager.music = SoundManager.game.sound.play(name, SoundManager.musicVolume, true);
    }

    /**
     * Stops all playing sound effects as well as the music.
     */
    public static stopAll(): void {
        SoundManager.game.sound.stopAll();
    }

    /**
     * Switches the sound effects volume between the default volume and 0
     */
    public static switchSoundEffectsVolume(): void {
        SoundManager.setSoundEffectsVolume(SoundManager.soundEffectsVolume === 0 ? defaultSoundEffectsVolume : 0);
    }

    /**
     * Switches the music volume between the default volume and 0
     */
    public static switchMusicVolume(): void {
        SoundManager.setMusicVolume(SoundManager.musicVolume === 0 ? defaultMusicVolume : 0);
    }

    public static getSoundEffectsVolume(): number { return SoundManager.soundEffectsVolume; }
    public static setSoundEffectsVolume(volume: number): void {
        SoundManager.soundEffectsVolume = volume;
    }

    public static getMusicVolume(): number { return SoundManager.musicVolume; }
    public static setMusicVolume(volume: number): void {
        SoundManager.musicVolume = volume;

        if (SoundManager.music) {
            SoundManager.music.volume = volume;
        }
    }

    public static getDefaultSoundEffectsVolume(): number { return defaultSoundEffectsVolume; }
    public static getDefaultMusicVolume(): number { return defaultMusicVolume; }
}
