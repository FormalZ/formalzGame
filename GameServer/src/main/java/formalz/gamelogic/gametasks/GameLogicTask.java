/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
package formalz.gamelogic.gametasks;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import formalz.connection.Client;
import formalz.connection.Connection;
import formalz.data.Problem;
import formalz.data.Queries;
import formalz.data.WaveData;
import formalz.gamelogic.gamestate.AdaptiveDifficulty;
import formalz.gamelogic.gamestate.GameState;
import formalz.gamelogic.gamestate.ProblemState;
import formalz.haskellapi.Response;
import formalz.haskellapi.jsonrunnables.JsonAccumulatorRunnable;
import formalz.utils.LexUtils;
import formalz.utils.Tracker;

/**
 * The GameTask for handling all the game logic.
 * @author Ludiscite
 * @version 1.0
 */
public class GameLogicTask extends GameTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(GameLogicTask.class);

    /**
     * Constructor for a GameLogicTask object.
     *
     * @param client The client that is playing the game.
     */
    public GameLogicTask(Client client) {
        super(client);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean tryCommand(String command, String arguments) {
        if (client.getState().checkDeadline()) {
            client.getConnection().sendError("The deadline for this problem has passed");
        }
        switch (command) {

            case "submitPre":
                submitPre(arguments);
                return true;
            case "submitPost":
                submitPost(arguments);
                return true;

            case "timeSpentPre":
                ProblemState problemState = client.getState().getProblemState();
                client.getTracker().sendBuiltCondition("pre", problemState.getLastPreCondition(), problemState.getPreCorrect() ? 0 : 1, Integer.parseInt(arguments) / 1000);
                return true;
            case "timeSpentPost":
                problemState = client.getState().getProblemState();
                client.getTracker().sendBuiltCondition("post", problemState.getLastPostCondition(), problemState.getPostCorrect() ? 0 : 1, Integer.parseInt(arguments) / 1000);
                return true;

            case "setPre":
                setPre(arguments);
                return true;
            case "setPost":
                setPost(arguments);
                return true;

            case "checkPre":
                checkPre();
                return true;
            case "checkPost":
                checkPost();
                return true;

            case "waveDone":
                waveDone(arguments);
                return true;

            case "finalScore":
                finalScore(arguments);
                return true;

            case "hash":
                hashUpdate(arguments);
                return true;
            default:
                return false;

        }
    }

    /**
     * Set the precondition.
     *
     * @param arguments Precondition.
     */
    private void setPre(String arguments) {
        GameState state = client.getState();
        ProblemState problemState = state.getProblemState();
        String oldPre = state.getProblemState().getLastPreCondition();
        if (oldPre.equals(arguments)) {
            problemState.setPreChanged(false);
        } else {
            problemState.setPreChanged(true);
        }
        problemState.setLastPreCondition(arguments);
    }

    /**
     * Set the postcondtion.
     *
     * @param arguments Postcondition.
     */
    private void setPost(String arguments) {
        GameState state = client.getState();
        ProblemState problemState = state.getProblemState();
        String oldPost = problemState.getLastPostCondition();
        if (oldPost.equals(arguments)) {
            problemState.setPostChanged(false);
        } else {
            problemState.setPostChanged(true);
        }
        problemState.setLastPostCondition(arguments);
    }

    /**
     * Set the precondition and check if it is correct.
     *
     * @param arguments Precondition.
     */
    private void submitPre(String arguments) {
        setPre(arguments);
        checkPre();

        Connection connection = client.getConnection();
        GameState state = client.getState();

        state.updateFeatureUsage(LexUtils.extractFeatureUsage(arguments));

        connection.sendPreResult(state.getProblemState().getPreFeedback(), state.getProblemState().generatePrePercentages());
    }

    /**
     * Set the postcondition and check if it is correct.
     *
     * @param arguments Postcondition.
     */
    private void submitPost(String arguments) {
        setPost(arguments);
        checkPost();

        Connection connection = client.getConnection();
        GameState state = client.getState();

        state.updateFeatureUsage(LexUtils.extractFeatureUsage(arguments));

        connection.sendPostResult(state.getProblemState().getPostFeedback(), state.getProblemState().generatePostPercentages());
    }

    /**
     * Check if the preconditions are correct.
     */
    private void checkPre() {
        check(() ->
        {
            return client.getState().getProblem().comparePre(client.getState().getProblemState().getLastPreCondition());
        }, (Problem problem, Response response) ->
        {
            processPreResponse(problem, response);
        }, (Response response) ->
        {
            client.getState().getProblemState().setLastPreResponse(response);
            client.getState().processPreResponse(response);
        }, () ->
        {
            return client.getState().getProblemState().getPreChanged();
        }, (GameState state) ->
        {
            state.processPreWrongAnswer();
        });
    }

    /**
     * Check if the postconditions are correct.
     */
    private void checkPost() {
        check(() ->
        {
            return client.getState().getProblem().comparePost(client.getState().getProblemState().getLastPostCondition());
        }, (Problem problem, Response response) ->
        {
            processPostResponse(problem, response);
        }, (Response response) ->
        {
            client.getState().getProblemState().setLastPostResponse(response);
            client.getState().processPostResponse(response);
        }, () ->
        {
            return client.getState().getProblemState().getPostChanged();
        }, (GameState state) ->
        {
            state.processPostWrongAnswer();
        });
    }

    private void check(Supplier<Response> getResponse, BiConsumer<Problem, Response> processResponse,
                       Consumer<Response> setAndProcessResponse, Supplier<Boolean> getChanged, Consumer<GameState> processWrongAnswer) {
        GameState state = client.getState();
        Problem problem = state.getProblem();

        Response response = getResponse.get();
        setAndProcessResponse.accept(response);

        String model = JsonAccumulatorRunnable.getModelString(response);
        if (!(model != null && model.equals(""))) {
            LOGGER.debug("counterexample " + model);
        }

        if (!response.isEquivalent() && getChanged.get()) {
            processWrongAnswer.accept(state);
        }

        int responseCode = response.getResponseCode();

        if (responseCode == 200) {
            processResponse.accept(problem, response);
        } else {
            incorrectResponseCode(responseCode);
        }
    }

    /**
     * Process the event of an incorrect response code.
     *
     * @param responseCode ResponseCode.
     */
    private void incorrectResponseCode(int responseCode) {
        // The error sending is commented, as sending an error disconnects the front end.

        Connection connection = client.getConnection();
        switch (responseCode) {
            case 400:
                connection.sendWarning("The solution checker has failed");
                LOGGER.error("The solution checker has failed");
                break;
            case 500:
                connection.sendWarning("There is probably something wrong with your syntax");
                LOGGER.error("There is probably something wrong with your syntax");
                break;
            default:
                connection.sendWarning("Something went wrong while checking your solution");
                LOGGER.warn("Something went wrong while checking your solution");
                break;
        }
    }

    /**
     * Process a response for the precondition.
     *
     * @param problem  Problem related to the response.
     * @param response Response for the precondition.
     */
    private void processPreResponse(Problem problem, Response response) {
        GameState state = client.getState();
        ProblemState problemState = state.getProblemState();

        boolean[] feedback = response.getPreFeedback();

        LOGGER.debug("answer is {}", response.isEquivalent());

        problemState.setPreFeedback(feedback);
        problemState.setPreCorrect(response.isEquivalent());

    }

    /**
     * Process a response for the postcondition.
     *
     * @param problem  Problem related to the response.
     * @param response Response for the postcondition.
     */
    private void processPostResponse(Problem problem, Response response) {
        GameState state = client.getState();
        ProblemState problemState = state.getProblemState();

        boolean[] feedback = response.getPostFeedback();

        LOGGER.debug("answer is {}", response.isEquivalent());

        problemState.setPostFeedback(feedback);
        problemState.setPostCorrect(response.isEquivalent());

    }

    /**
     * Process the event of the user finishing a wave.
     *
     * @param arguments String containing the wave data.
     */
    private void waveDone(String arguments) {
        Connection connection = client.getConnection();
        GameState state = client.getState();
        ProblemState problemState = state.getProblemState();
        Tracker tracker = client.getTracker();
        AdaptiveDifficulty adaptiveDifficulty = state.getAdaptiveDifficulty();

        WaveData waveData = new WaveData(arguments);
        state.addWaveData(waveData);

        state.setScore(waveData.getScore());


        if (problemState.problemCorrect()) {
            tracker.sendWaveEnd(0);
            LOGGER.debug("Question {} is answered correctly", state.getProblemNumber());
            state.processCorrectAnswer();
            LOGGER.debug("Time taken {}", state.getCurrentStatistic().getTimeSpent());
            if (state.isFinished()) {
                tracker.sendGameEnd();
                LOGGER.debug("Game finished");
                connection.sendFinish();
            } else {
                connection.startNewQuestion(state, 0);
                tracker.sendWaveStart();
            }
        } else {
            tracker.sendWaveEnd(1);
            tracker.sendWaveStart();
            Response response = problemState.getLastPreResponse();
            if (response != null) {
                connection.sendPreFeedback(JsonAccumulatorRunnable.getModelString(response));
            } else {
                connection.sendPreFeedback("");
            }

            response = problemState.getLastPostResponse();
            if (response != null) {
                connection.sendPostFeedback(JsonAccumulatorRunnable.getModelString(response));
            } else {
                connection.sendPostFeedback("");
            }
            connection.sendSpawnWave(adaptiveDifficulty.getWaveSparkAmount(), adaptiveDifficulty.getWaveSparkHealth(),
                    adaptiveDifficulty.getWaveSparkSpeed(), adaptiveDifficulty.getWaveSparkSpawnTime(),
                    problemState.generatePrePercentages(), problemState.generatePostPercentages(),
                    adaptiveDifficulty.getSpecialSparkSpawnPercentage());

        }
    }

    /**
     * Process the event of the user sending the final score.
     *
     * @param arguments String containing the final score.
     */
    private void finalScore(String arguments) {
        int score = Integer.parseInt(arguments);
        GameState state = client.getState();

        if (!state.isFinished()) {
            return;
        }

        state.setScore(score);
        Queries.getInstance().insertScore(state.getGameSession().getUserId(), state.getTeacherProblemId(), state.getScore(),
                !state.getCheatDetector().hasCheated());
    }

    /**
     * Process the event of the client sending a new hash value
     * @param arguments the new hash value
     */
    private void hashUpdate(String arguments) {
        int hash = Integer.parseInt(arguments);
        GameState state = client.getState();
        Queries.getInstance().updateHash(hash, state.getGameSession().getUserId(), state.getTeacherProblemId());
    }
}
