package utils;

import connection.Client;
import data.WaveData;
import es.eucm.tracker.formalz.FormalZDemo;

public class Tracker extends FormalZDemo {

    private Client client;
    private long startTime;
    private boolean disabled = false;

    public Tracker(Client client) {
        super();
        this.client = client;
        startTime = System.currentTimeMillis() / 1000;
    }

    public void sendGameStart(int money, int lives) {
        super.setGameState(money, 0, lives);
        try {
            super.sendGameStart();
            super.tracker.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendGameProgress(float progress) {
        if(disabled)
            return;
        setGameState();
        try {
            super.sendGameProgress(progress);
            super.tracker.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendGameEnd() {
        if(disabled)
            return;
        setGameState();
        try {
            super.sendGameEnd();
            super.tracker.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendWaveStart() {
        if(disabled)
            return;
        setGameState();
        try {
            super.sendWaveStart();
            super.tracker.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendWaveEnd(int distance) {
        if(disabled)
            return;
        setGameState();
        try {
            super.sendWaveEnd(distance);
            super.tracker.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendBuiltCondition(String type, String condition, float distance, int writingTime) {
        if(disabled)
            return;
        setGameState();
        super.setWritingTime(writingTime);
        try {
            super.sendBuiltCondition(type, condition, distance);
            super.tracker.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendLiveLost(float progress) {
        if(disabled)
            return;
        setGameState();
        try {
            super.sendLiveLost(progress);
            super.tracker.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendEnemyKilled(float progress, int moneyGained) {
        if(disabled)
            return;
        setGameState();
        try {
            super.sendEnemyKilled(progress, moneyGained);
            super.tracker.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setGameState(){
        try {
            WaveData waveData = client.getState().getCurrentStatistic().getLastWave();
            super.setGameState(waveData.getMoney(), waveData.getTowerCount(), waveData.getHealth());
            super.setTime((int) ((System.currentTimeMillis() / 1000) - startTime));
        }
        catch(Throwable e){
            e.printStackTrace();
        }
    }

    public void disable(){
        disabled = true;
    }
}
