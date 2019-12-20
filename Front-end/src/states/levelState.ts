/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University
 * within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
/**
 * Describes a state that a Level can be in.
 * A GameTask can change its behaviour based on the Level's current state.
 */
enum LevelState {
    PLAYING,
    BLOCKBUILDING
}

export default LevelState;