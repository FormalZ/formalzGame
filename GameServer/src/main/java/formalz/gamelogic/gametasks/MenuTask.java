/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
package formalz.gamelogic.gametasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import formalz.connection.Client;
import formalz.connection.Connection;
import formalz.data.GameSession;
import formalz.data.Problem;
import formalz.data.Queries;
import formalz.data.Settings;
import formalz.gamelogic.gamestate.GameState;
import formalz.utils.Tracker;

/**
 * The GameTask for handling the menu.
 * 
 * @author Ludiscite
 * @version 1.0
 */
public class MenuTask extends GameTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(MenuTask.class);

    /**
     * Constructor for a MenuTask object.
     * 
     * @param client The client that is playing the game.
     */
    public MenuTask(Client client) {
        super(client);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean tryCommand(String command, String arguments) {
        switch (command) {
            case "startGame":
                startGame(arguments);
                return true;
            case "startProblem":
                startProblem(arguments);
                return true;
            default:
                return false;
        }
    }

    /**
     * Starts the game.
     */
    private void startGame(String hash) {
        Connection connection = client.getConnection();
        Tracker tracker = client.getTracker();

        LOGGER.debug("Sending path");
        Queries queries = Queries.getInstance();
        GameSession session = client.getState().getGameSession();
        int problemId = session.getProblemId();

        LOGGER.debug("Performing hash check");
        Integer hashInt = Integer.parseInt(hash);
        connection.sendHashCheck(queries.getHashCheck(hashInt, problemId, session.getUserId()));
        connection.sendPath(queries.getPath(problemId));
        String problemTracking = queries.getProblemTrackingCode(problemId);
        String userTracking = queries.getUserTrackingCode(session.getUserId());
        Problem problem = queries.getProblemById(problemId);
        if (userTracking == null || problemTracking == null) {
            LOGGER.debug("Tracking codes not set");
            tracker.disable();
        } else {
            if (Settings.isAnalyticsEnabled()) {
                String analyticsServerHost = Settings.getAnalyticsServerHost();
                int analyticsServerPort = Settings.getAnalyticsServerPort();
                boolean analyticsServerSecureConnection = Settings.isAnalyticsServerSecureConnection();

                tracker.run(analyticsServerHost, analyticsServerPort, analyticsServerSecureConnection, problemTracking,
                        userTracking);
                tracker.enable();
                try {
                    tracker.sendGameStart(problem.getMoney(), problem.getLives());
                } catch (NullPointerException e) {
                    LOGGER.error("Tracker not functioning", e);
                    tracker.disable();
                }
            } else {
                tracker.disable();
            }
        }
        session.setDeadline(problem.getDeadline());
        connection.sendProblemData(problem.getLives(), problem.getMoney(), problem.getDeadline());
        tracker.sendWaveStart();
    }

    /**
     * Starts the problem.
     * 
     * @param challengeCounter if the game is reloaded from local storage data,
     *                         challengeCounter will indicate on what subProblem the
     *                         game was
     */
    private void startProblem(String challengeCounter) {
        Connection connection = client.getConnection();
        GameState state = client.getState();
        int challengeCounterInt = Integer.parseInt(challengeCounter);
        if (state.start()) {
            LOGGER.debug("Game started");

            connection.startNewQuestion(state, challengeCounterInt);
            client.getTracker().sendWaveStart();
        } else {
            LOGGER.debug("Invalid start time");
        }
    }
}
