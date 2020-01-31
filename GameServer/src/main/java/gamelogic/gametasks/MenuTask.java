/*
 * This program has been developed by students from the bachelor Computer Science at Utrecht University within the Software and Game project course (time-period)
 * (c)Copyright Utrecht University (Department of Information and Computing Sciences)
 */
package gamelogic.gametasks;

import connection.Client;
import connection.Connection;
import data.GameSession;
import data.Problem;
import data.Queries;
import data.Settings;
import gamelogic.gamestate.GameState;
import gamelogic.gamestate.AdaptiveDifficulty;
import logger.AbstractLogger;
import utils.Tracker;

/**
 * The GameTask for handling the menu.
 * @author Ludiscite
 * @version 1.0
 */
public class MenuTask extends GameTask
{
    /**
     * Constructor for a MenuTask object.
     * @param client The client that is playing the game.
     */
    public MenuTask(Client client)
    {
        super(client);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean tryCommand(String command, String arguments)
    {
        switch (command)
        {
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
    private void startGame(String hash)
    {
        AbstractLogger logger = client.getLogger();
        Connection connection = client.getConnection();
        Tracker tracker = client.getTracker();

        logger.log("Sending path");
        Queries queries = Queries.getInstance();
        GameSession session = client.getState().getGameSession();
        int problemId = session.getProblemId();

        logger.log("Performing hash check");
        Integer hashInt = Integer.parseInt(hash);
        connection.sendHashCheck(queries.getHashCheck(hashInt, problemId, session.getUserId()));
        connection.sendPath(queries.getPath(problemId));
        String problemTracking = queries.getProblemTrackingCode(problemId);
        String userTracking = queries.getUserTrackingCode(session.getUserId());
        Problem problem = queries.getProblemById(problemId);
        if (userTracking == null || problemTracking == null) {
            logger.log("Tracking codes not set");
            tracker.disable();
        }
        else {
        	  if (Settings.isAnalyticsEnabled()) {
		        	  String analyticsServerHost = Settings.getAnalyticsServerHost();
		        	  int analyticsServerPort = Settings.getAnalyticsServerPort();
		        	  boolean analyticsServerSecureConnection = Settings.isAnalyticsServerSecureConnection();
		        	  
		            tracker.run(analyticsServerHost, analyticsServerPort, analyticsServerSecureConnection, problemTracking, userTracking);
		            try {
		                tracker.sendGameStart(problem.getMoney(), problem.getLives());
		            } catch (NullPointerException e) {
		                logger.log("Tracker not functioning");
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
     * @param challengeCounter if the game is reloaded from local storage data,
     * challengeCounter will indicate on what subProblem the game was
     */
    private void startProblem(String challengeCounter)
    {
        AbstractLogger logger = client.getLogger();
        Connection connection = client.getConnection();
        GameState state = client.getState();
        int challengeCounterInt = Integer.parseInt(challengeCounter);
        if (state.start())
        {
            logger.log("Game started");

            connection.startNewQuestion(state, logger, challengeCounterInt);
            client.getTracker().sendWaveStart();
        }
        else
        {
            logger.log("Invalid start time");
        }
    }
}
