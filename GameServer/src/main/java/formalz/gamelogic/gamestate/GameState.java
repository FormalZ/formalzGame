/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
package formalz.gamelogic.gamestate;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import formalz.data.GameSession;
import formalz.data.LocalStatistic;
import formalz.data.Problem;
import formalz.data.Queries;
import formalz.data.WaveData;
import formalz.gamelogic.gamestate.AdaptiveDifficulty.FeatureRequirement;
import formalz.data.Problem.Feature;
import formalz.haskellapi.Response;

/**
 * An object to store the game state.
 * 
 * @author Ludiscite
 * @version 1.0
 */
public class GameState {

    private static final Logger LOGGER = LoggerFactory.getLogger(GameState.class);

    private int questionAmount = 3;

    private int teacherProblemId = 1;

    private List<Integer> oldIds;

    private Problem currentProb;

    private Problem teacherProblem;

    private List<LocalStatistic> localStatistics;
    private LocalStatistic currentLocalStatistic;

    private int score = 0;

    private int problemNumber = 0;

    private int phase;

    private int playedGamesId = -1;

    private GameSession gameSession;
    private AdaptiveDifficulty adaptiveDifficulty;
    private ProblemState problemState;
    private HintSystem hintSystem;
    private CheatDetector cheatDetector;

    /**
     * Constructor of a GameState object.
     * 
     * @param LOGGER Logger to log to.
     */
    public GameState() {
        phase = 0;
        oldIds = new ArrayList<Integer>();
        localStatistics = new ArrayList<LocalStatistic>();
        problemState = new ProblemState(this);
        hintSystem = new HintSystem();
        cheatDetector = new CheatDetector();
    }

    /**
     * Check whether the game is still going on.
     * 
     * @return A boolean value representing whether the game is still going on.
     */
    public boolean isOngoing() {
        return phase == 1;
    }

    /**
     * Check if the game has finished.
     * 
     * @return A boolean value representing whether the game has finished.
     */
    public boolean isFinished() {
        return phase == 2;
    }

    /**
     * Get the next problem to solve
     * 
     * @return The problem that needs to be solved next. This is either a random
     *         problem or the teacher problem.
     */
    public Problem getProblem() {
        if (teacherProblem == null) {
            // Initialise the problems in the track and the teacher problem:
            Queries queries = Queries.getInstance();
            teacherProblem = queries.getProblemById(teacherProblemId);
            questionAmount = queries.getProblemCount(teacherProblemId);
            adaptiveDifficulty = new AdaptiveDifficulty(teacherProblem, questionAmount);
        }

        // Fetch new problem
        if (currentProb == null) {
            boolean isRepo;
            int id;

            if (adaptiveDifficulty.isFinalProblem()) {
                isRepo = false;
                LOGGER.debug("Reached final problem");
                currentProb = teacherProblem;
                id = teacherProblemId;
            } else {
                isRepo = true;

                Map<Feature, FeatureRequirement> features = adaptiveDifficulty.getFeatureRequirements();
                int minimumDifficulty = adaptiveDifficulty.getMinimumDifficulty();
                int maximumDifficulty = adaptiveDifficulty.getMaximumDifficulty();

                currentProb = ProblemSelector.getRandomProblem(minimumDifficulty, maximumDifficulty, features, oldIds,
                        teacherProblem.getLives(), teacherProblem.getMoney(), teacherProblem.getDeadline());

                adaptiveDifficulty.resetFeatureUsage();
                oldIds.add(currentProb.getId());
                id = currentProb.getId();
                LOGGER.debug("Currently doing repo problem with id '{}'", id);
                adaptiveDifficulty.processProblemStatistics(Queries.getInstance().getProblemStatistics(id));
            }
            hintSystem.processNewProblem(currentProb);
            currentLocalStatistic = new LocalStatistic(id, isRepo, new Timestamp(System.currentTimeMillis()));
            localStatistics.add(currentLocalStatistic);
            adaptiveDifficulty.setCurrentProblem(currentProb);
            cheatDetector.processNewProblem(currentProb, adaptiveDifficulty.getAvailableScore());
        }

        return currentProb;
    }

    /**
     * Start the game.
     * 
     * @return A boolean value representing whether the game is successfully
     *         started.
     */
    public boolean start() {
        if (phase != 0) {
            return false;
        }

        teacherProblemId = gameSession.getProblemId();

        phase = 1;
        score = 0;
        oldIds = new ArrayList<Integer>();
        return true;
    }

    /**
     * End the game.
     * 
     * @return A boolean value representing whether the game has successfully ended.
     */
    public boolean end() {
        if (phase != 2) {
            return false;
        }
        phase = 0;
        return true;
    }

    /**
     * Process the event of a precondition being answered wrongly.
     */
    public void processPreWrongAnswer() {
        currentLocalStatistic.incrementPreMistakeCount();
        adaptiveDifficulty.setMistakeCount(currentLocalStatistic.getPreMistakeCount(),
                currentLocalStatistic.getPostMistakeCount());
    }

    /**
     * Process the event of a postcondition being answered wrongly.
     */
    public void processPostWrongAnswer() {
        currentLocalStatistic.incrementPostMistakeCount();
        adaptiveDifficulty.setMistakeCount(currentLocalStatistic.getPreMistakeCount(),
                currentLocalStatistic.getPostMistakeCount());
    }

    /**
     * Process the event of a question being answered correctly.
     */
    public void processCorrectAnswer() {
        if (!adaptiveDifficulty.isFinalProblem()) {
            Queries.getInstance().insertRepoStatistics(currentProb.getId(), this.playedGamesId, problemNumber,
                    currentLocalStatistic.getWaveAmount(), currentLocalStatistic.getPreMistakeCount(),
                    currentLocalStatistic.getPostMistakeCount());
        }
        problemNumber++;

        currentProb = null;
        currentLocalStatistic.setEndTime(new Timestamp(System.currentTimeMillis()));

        if (adaptiveDifficulty.isFinalProblem()) {
            processGameFinished();
            return;
        }

        adaptiveDifficulty.newProblem();

        clearProblemProgress();
    }

    /**
     * Process the event of the game being finished.
     */
    private void processGameFinished() {
        phase = 2;

        int totalTime = localStatistics.stream().map(LocalStatistic::getTimeSpent).mapToInt(i -> i.intValue()).sum()
                / 1000;
        int problemTime = (int) (currentLocalStatistic.getTimeSpent() / 1000L);

        Queries.getInstance().insertStatistics(teacherProblemId, this.playedGamesId, totalTime, problemTime,
                questionAmount - (problemNumber - 1), currentLocalStatistic.getPreMistakeCount(),
                currentLocalStatistic.getPostMistakeCount());
    }

    /**
     * Clear progress of the current problem.
     */
    public void clearProblemProgress() {
        problemState.clearProgress();
    }

    /**
     * Process the data of wave.
     * 
     * @param waveData Data of a wave
     */
    public void addWaveData(WaveData waveData) {
        cheatDetector.processWaveData(waveData, adaptiveDifficulty.getAvailableScore());
        adaptiveDifficulty.processWaveData(waveData);
        currentLocalStatistic.addWaveData(waveData);
    }

    /**
     * Process a response for the preconditions.
     * 
     * @param preResponse Precondition response
     */
    public void processPreResponse(Response preResponse) {
        adaptiveDifficulty.processPreResponse(preResponse);
    }

    /**
     * Process a response for the postconditions.
     * 
     * @param postResponse Postcondition response
     */
    public void processPostResponse(Response postResponse) {
        adaptiveDifficulty.processPostResponse(postResponse);
    }

    /**
     * Set the gameSession.
     * 
     * @param gameSession Session information
     */
    public void updateGameSession(GameSession gameSession) {
        this.gameSession = gameSession;
        playedGamesId = Queries.getInstance().createPlayedGamesId(gameSession.getUserId(), gameSession.getProblemId());
    }

    /**
     * Update the features used in the student solution.
     * 
     * @param featureUsage Feature usages to update.
     */
    public void updateFeatureUsage(int[] featureUsage) {
        this.adaptiveDifficulty.updateFeatureUsage(featureUsage);
    }

    /**
     * Update the score.
     * 
     * @param dif Value to add to the score.
     */
    public void updateScore(int dif) {
        score += dif;
    }

    /**
     * Set the adaptive difficulty for testing purposes.
     * 
     * @param ad Adaptive difficulty.
     */
    public void setAdaptiveDifficulty(AdaptiveDifficulty ad) {
        this.adaptiveDifficulty = ad;
    }

    /**
     * Returns the adaptive difficulty of the game.
     * 
     * @return The adaptive difficulty of the game.
     */
    public AdaptiveDifficulty getAdaptiveDifficulty() {
        return this.adaptiveDifficulty;
    }

    /**
     * Returns the state of the problem.
     * 
     * @return State of the problem.
     */
    public ProblemState getProblemState() {
        return this.problemState;
    }

    /**
     * Get the current score.
     * 
     * @return The current score.
     */
    public int getScore() {
        return score;
    }

    /**
     * Set the score.
     * 
     * @param score Score
     */
    public void setScore(int score) {
        this.score = score;
    }

    /**
     * Get the current question number.
     * 
     * @return The current question number.
     */
    public int getProblemNumber() {
        return problemNumber;
    }

    /**
     * Get the final problem id from the teacher
     * 
     * @return The id of the teacher problem
     */
    public int getTeacherProblemId() {
        return teacherProblemId;
    }

    /**
     * Get the gameSession.
     * 
     * @return Session information,
     */
    public GameSession getGameSession() {
        return this.gameSession;
    }

    /**
     * Returns current local statistic
     * 
     * @return currentLocalStatistic,
     */
    public LocalStatistic getCurrentStatistic() {
        return this.currentLocalStatistic;
    }

    /**
     * Returns the hint system.
     * 
     * @return Hint system.
     */
    public HintSystem getHintSystem() {
        return this.hintSystem;
    }

    /**
     * Return the cheat detector.
     * 
     * @return Cheat detector.
     */
    public CheatDetector getCheatDetector() {
        return this.cheatDetector;
    }

    public boolean checkDeadline() {
        return gameSession.checkDeadline();
    }
}
