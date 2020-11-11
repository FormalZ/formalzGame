package formalz.utils;

import formalz.connection.Client;
import formalz.data.WaveData;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.eucm.tracker.AlternativeTracker;
import es.eucm.tracker.CompletableTracker;
import es.eucm.tracker.TrackerAsset;
import es.eucm.tracker.TrackerAssetSettings;
import es.eucm.tracker.TrackerSettings;
import eu.rageproject.asset.manager.Severity;
import formalz.utils.analytics.JavaBridge;

public class Tracker {
    private static final Logger LOGGER = LoggerFactory.getLogger(Tracker.class);

    private Client client;

    private long startTime;

    private boolean disabled;
    
    private TrackerAsset tracker;
    
    private GameState gameState;
    
    private TrackerAssetSettings settings;
    
    public Tracker(Client client) {
        this.client = client;
        this.startTime = System.currentTimeMillis() / 1000;
        this.disabled = true;
    }

    public void sendGameStart(int money, int lives) {
        if(disabled)
            return;
        setGameState(money, 0, lives);
        try {
            this.sendGameStart();
            this.tracker.flush();
        } catch (Exception e) {
            LOGGER.error("Can not send game start", e);
        }
    }

    private void setGameState(int money, int towers, int lives) {
          this.gameState.money = money;
          this.gameState.towers = towers;
          this.gameState.lives = lives;
    }

    private void sendGameStart() {
      this.appendGameState();
      this.tracker.getCompletable().initialized("level1", CompletableTracker.Completable.Level);
    }

    private void appendGameState() {
      this.tracker.setVar("money", this.gameState.money);
      this.tracker.setVar("towers", this.gameState.towers);
      this.tracker.setVar("lives", this.gameState.lives);
    }

    public void sendGameProgress(float progress) {
        if(disabled)
            return;
        setGameState();
        try {
            this.appendGameState();
            this.tracker.getCompletable().progressed("level1", CompletableTracker.Completable.Level, progress);
            this.tracker.flush();
        } catch (Exception e) {
            LOGGER.error("Can not send game progress", e);
        }
    }

    public void sendGameEnd() {
        if(disabled)
            return;
        setGameState();
        try {
              this.appendGameState();
              this.tracker.getCompletable().completed("level1", CompletableTracker.Completable.Level, true, 1.0F);
            this.tracker.flush();
        } catch (Exception e) {
            LOGGER.error("Can not send game end", e);
        }
    }

    public void sendWaveStart() {
        if(disabled)
            return;
        setGameState();
        try {
              this.appendGameState();
              this.tracker.getCompletable().initialized("wave", CompletableTracker.Completable.Stage);
            this.tracker.flush();
        } catch (Exception e) {
            LOGGER.error("Can not send wave start", e);
        }
    }

    public void sendWaveEnd(int distance) {
        if(disabled)
            return;
        setGameState();
        try {
              this.appendGameState();
              this.tracker.getCompletable().completed("wave", CompletableTracker.Completable.Stage, distance == 1.0F);
            this.tracker.flush();
        } catch (Exception e) {
            LOGGER.error("Can not send wave end", e);
        }
    }

    public void sendBuiltCondition(String type, String condition, float distance, int writingTime) {
        if(disabled)
            return;
        setGameState();
        this.tracker.setVar("writing_time", writingTime);
        try {
              this.tracker.setSuccess(distance == 0.0F);
              this.tracker.setScore(distance);
              int complexity = (int)(3.0D - Math.ceil((double)(distance * 3.0F)));
              this.tracker.getAlternative().selected(type, condition, AlternativeTracker.Alternative.Question);
            this.tracker.flush();
        } catch (Exception e) {
            LOGGER.error("Can not send condition written", e);
        }
    }

    public void sendLiveLost(float progress) {
        if(disabled)
            return;
        setGameState();
        try {
              --this.gameState.lives;
              this.appendGameState();
              this.tracker.getCompletable().progressed("wave", CompletableTracker.Completable.Level, progress);
            this.tracker.flush();
        } catch (Exception e) {
            LOGGER.error("Can not send live lost", e);

        }
    }

    public void sendEnemyKilled(float progress, int moneyGained) {
        if(disabled)
            return;
        setGameState();
        try {
            GameState var10000 = this.gameState;
            var10000.money += moneyGained;
            this.appendGameState();
            this.tracker.getCompletable().progressed("wave", CompletableTracker.Completable.Stage, progress);
            this.tracker.flush();
        } catch (Exception e) {
            LOGGER.error("Can not send enemy killed", e);
        }
    }

    private void setGameState(){
        try {
            WaveData waveData = client.getState().getCurrentStatistic().getLastWave();
            this.gameState.money = waveData.getMoney();
            this.gameState.towers = waveData.getTowerCount();
            this.gameState.lives = waveData.getHealth();
            this.tracker.setVar("time", (int) ((System.currentTimeMillis() / 1000) - startTime));
        }
        catch(Throwable e){
            LOGGER.error("Can not send game state", e);
        }
    }

    public void disable(){
        disabled = true;
    }

    public void enable(){
        disabled = false;
    }

    public void run(String analyticsServerHost, int analyticsServerPort, boolean analyticsServerSecureConnection, String problemTracking, String userTracking) {
        this.tracker = new TrackerAsset();
        this.tracker.setBridge(new JavaBridge() {
            public void log(Severity severity, String msg) {
                super.log(severity, msg);
                switch(severity) {
                    case Critical:
                    case Error:
                        LOGGER.error(msg);
                    break;

                    case Warning:
                        LOGGER.warn(msg);
                    break;

                    case Information:
                        LOGGER.info(msg);
                    break;
                
                    case Verbose:
                    default:
                        LOGGER.debug(msg);
                    break;                
                }
            }
        });
        this.settings = this.createSettings(analyticsServerHost, analyticsServerPort, analyticsServerSecureConnection, problemTracking, userTracking);
        this.tracker.setSettings(this.settings);
        this.tracker.start();
        this.gameState = new GameState();
        this.disabled = false;
    }

    protected TrackerAssetSettings createSettings(String analyticsServerHost, int analyticsServerPort, boolean analyticsServerSecureConnection, String trackingCode, String userToken) {
        TrackerAssetSettings settings = new TrackerAssetSettings();
        settings.setHost(analyticsServerHost);
        settings.setPort(analyticsServerPort);
        settings.setSecure(analyticsServerSecureConnection);
        settings.setTraceFormat(TrackerSettings.TraceFormats.XAPI);
        settings.setBasePath("/api/");
        settings.setTrackingCode(trackingCode);
        settings.setUserToken(userToken);
        return settings;
    }

    public void stop() {
        if(disabled)
            return;
        this.tracker.flush();
    }

    private static class GameState {
        public int money = 100;
        public int towers = 0;
        public int lives = 20;
    }
}
