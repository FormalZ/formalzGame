/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
package formalz.connection;

import java.util.Arrays;
import java.util.List;

import org.java_websocket.WebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import formalz.data.Problem;
import formalz.gamelogic.gamestate.AdaptiveDifficulty;
import formalz.gamelogic.gamestate.GameState;

/**
 * Wraps the connection tot the front-end to simplify sending messages.
 * 
 * @author Ludiscite
 * @version 1.0
 */
public class Connection {
    private static final Logger LOGGER = LoggerFactory.getLogger(Connection.class);

    private WebSocket socket;

    /**
     * The constructor for the Connection object.
     * 
     * @param socket The WebSocket of the Client that sends the messages.
     */
    public Connection(WebSocket socket) {
        this.socket = socket;
    }

    /**
     * Stop the connection.
     */
    public void stop() {
        this.sendStop();
        socket.close();
    }

    /**
     * Gets the socket with which to send messages to the client.
     * 
     * @return The socket with which to send messages to the client.
     */
    public WebSocket getSocket() {
        return socket;
    }

    /**
     * Send a message to the client that the game has started.
     */
    public void sendStart() {
        socket.send("start");
    }

    /**
     * Send a message to the client that the game has finished. So the player goes
     * from the game screen to the end screen.
     */
    public void sendFinish() {
        socket.send("finish");
    }

    /**
     * Send a message to the client that the connection will be stopped.
     */
    public void sendStop() {
        socket.send("stop");
    }

    /**
     * Send a message to the client that the game session has ended. So the player
     * goes from the end screen to the menu screen.
     */
    public void sendEnd() {
        socket.send("end");
    }

    /**
     * Send a message to the client that the authentication token was correct.
     */
    public void sendStartUpCorrect() {
        socket.send("startup done");
    }

    /**
     * Send a message to the client that the authentication token was incorrect.
     */
    public void sendStartUpWrong() {
        socket.send("startup wrong");
    }

    /**
     * Send a message to the client that the authentication token was correct.
     */
    public void sendStartUpTimeOut() {
        socket.send("startup timeout");
    }

    /**
     * Send an warning message to the client.
     * 
     * @param string Warning message.
     */
    public void sendWarning(String string) {
        socket.send("warning " + string);
    }

    /**
     * Send an error message to the client.
     * 
     * @param string Error message.
     */
    public void sendError(String string) {
        socket.send("error " + string);
    }

    /**
     * Send the description of a problem to the client.
     * 
     * @param problem Problem to send the description of.
     */
    public void sendDescription(Problem problem) {
        String desc = problem.getDescription();
        socket.send("description " + desc);
    }

    /**
     * Send the difficulty of the problem.
     * 
     * @param difficulty The difficulty to send.
     */
    public void sendDifficulty(int difficulty) {
        socket.send("difficulty " + Integer.toString(difficulty));
    }

    /**
     * Send a message to the client with all the valid precondition tokens that the
     * teacher solution uses.
     * 
     * @param validPreTokens The String[] with all the lexed tokens from the teacher
     *                       solution.
     */
    public void sendValidPreTokens(String[] validPreTokens) {
        socket.send("validPreTokens " + Arrays.toString(validPreTokens));
    }

    /**
     * Send a message to the client with all the valid postcondition tokens that the
     * teacher solution uses.
     * 
     * @param validPostTokens The String[] with all the lexed tokens from the
     *                        teacher solution.
     */
    public void sendValidPostTokens(String[] validPostTokens) {
        socket.send("validPostTokens " + Arrays.toString(validPostTokens));
    }

    /**
     * Send a message to the client about what to send in the wave.
     * 
     * @param amount               Amount of the sparks that spawn.
     * @param health               Health of the sparks that spawn.
     * @param speed                Speed of the sparks that spawn.
     * @param spawnTime            Time between spawning enemies.
     * @param preWeights           Float array with weights of spark types for the
     *                             pre path.
     * @param postWeights          Float array with weights of spark types for the
     *                             post path.
     * @param sparkTypePercentages Float array of percentages of special spark types
     *                             to spawn.
     */
    public void sendSpawnWave(int amount, int health, int speed, int spawnTime, float[] preWeights, float[] postWeights, float[] sparkTypePercentages) {
        socket.send("spawnWave " + amount + ";" + Arrays.toString(preWeights) + ";" + Arrays.toString(postWeights) + ";"
                + health + ";" + speed + ";" + spawnTime + ";" + Arrays.toString(sparkTypePercentages));
    }

    /**
     * Send a message to the client with the result for the pre conditions.
     * 
     * @param preFeedback    Boolean array with pre result.
     * @param prePercentages Float array with the pre spark percentages.
     */
    public void sendPreResult(boolean[] preFeedback, float[] prePercentages) {
        socket.send("resultPre " + Arrays.toString(prePercentages));
    }

    /**
     * Send a message to the client with the result for the post conditions.
     * 
     * @param postFeedback    Boolean array with post result.
     * @param postPercentages Float array with the post spark percentages.
     */
    public void sendPostResult(boolean[] postFeedback, float[] postPercentages) {
        socket.send("resultPost " + Arrays.toString(postPercentages));
    }

    /**
     * Send feedback for the precondition.
     * 
     * @param feedback A string containing feedback of the preconditions.
     */
    public void sendPreFeedback(String feedback) {
        socket.send("preFeedback " + feedback);
    }

    /**
     * Send feedback for the postcondition.
     * 
     * @param feedback A string containing feedback of the postconditions.
     */
    public void sendPostFeedback(String feedback) {
        socket.send("postFeedback " + feedback);
    }

    /**
     * Send path to client.
     * 
     * @param path String of the path.
     */
    public void sendPath(String path) {
        socket.send("path " + path);
    }

    /**
     * Send problem data to client.
     * 
     * @param lives    Amount of lives for problem.
     * @param money    Amount of money for problem.
     * @param deadline Deadline for problem.
     */
    public void sendProblemData(int lives, int money, int deadline) {
        socket.send("gamedata " + money + " " + lives + " " + deadline);
    }

    /**
     * Send the boolean value of if the hash compared to the hash in the back end to
     * the client
     * 
     * @param hashCheck the boolean value indicating if it compared or not
     */
    public void sendHashCheck(boolean hashCheck) {
        socket.send("hashCheck " + hashCheck);
    }

    /**
     * Send the session id to the client
     * 
     * @param problemId the problem id
     * @param userId    the user id
     */
    public void sendSessionId(int problemId, int userId) {
        socket.send("sessionId " + problemId + " " + userId);
    }

    /**
     * Send the types of variable tokens to the client.
     * 
     * @param tokenTypes Types of variable tokens.
     */
    public void sendVariableTypes(String tokenTypes) {
        socket.send("variableTypes " + tokenTypes);
    }

    /**
     * Send a hint to the client.
     * 
     * @param hint Hint to send to the client.
     */
    public void sendHint(String hint) {
        socket.send("hint " + hint);
    }

    /**
     * Send the amount of score available for the current problem.
     * 
     * @param availableScore Score available for the current problem.
     */
    public void sendAvailableScore(int availableScore) {
        socket.send("availableScore " + availableScore);
    }

    /**
     * Send the progression of the game.
     * 
     * @param progression Progression of the game.
     */
    public void sendProgression(float progression) {
        socket.send("progression " + progression);
    }

    /**
     * Send messages to start a new question on the client.
     * 
     * @param state GameState of the game.
     */
    public void startNewQuestion(GameState state, int challengeCounter) {
        Problem problem = state.getProblem();
        AdaptiveDifficulty adaptiveDifficulty = state.getAdaptiveDifficulty();

        if (challengeCounter > 0) {
            while (challengeCounter > 0) {
                state.processCorrectAnswer();
                challengeCounter--;
            }

            problem = state.getProblem();
        }

        sendDescription(problem);
        sendDifficulty(problem.getDifficulty());
        sendVariableTypes(problem.getVariableTypes());
        sendValidPreTokens(problem.getPreTokens());
        sendValidPostTokens(problem.getPostTokens());
        sendSpawnWave(adaptiveDifficulty.getWaveSparkAmount(), adaptiveDifficulty.getWaveSparkHealth(),
                adaptiveDifficulty.getWaveSparkSpeed(), adaptiveDifficulty.getWaveSparkSpawnTime(),
                state.getProblemState().generatePrePercentages(), state.getProblemState().generatePostPercentages(),
                adaptiveDifficulty.getSpecialSparkSpawnPercentage());

        sendAvailableScore(adaptiveDifficulty.getAvailableScore());
        sendProgression(adaptiveDifficulty.getProgression());

        List<String> hints = state.getHintSystem().getHints();
        for (String hint : hints) {
            sendHint(hint);
        }
        LOGGER.debug("Variable types: {}", problem.getVariableTypes());
    }
}
