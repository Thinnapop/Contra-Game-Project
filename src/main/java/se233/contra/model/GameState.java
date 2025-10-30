package se233.contra.model;

import se233.contra.util.GameLogger;

public class GameState {
    public enum State {
        MENU,
        PLAYING,
        PAUSED,
        GAME_OVER,
        VICTORY
    }

    public enum Phase {
        MINION_WAVE,
        BOSS_FIGHT
    }

    private State currentState;
    private int currentBossLevel;
    private Phase currentPhase;

    public GameState() {
        currentState = State.PLAYING;
        currentBossLevel = 1;
        currentPhase = Phase.MINION_WAVE;  // Start with minion wave
    }

    public void setState(State state) {
        GameLogger.info("Game state changed to: " + state);
        this.currentState = state;
    }

    public State getCurrentState() {
        return currentState;
    }

    public void nextBoss() {
        currentBossLevel++;
        currentPhase = Phase.MINION_WAVE;  // Reset to minion wave for next boss
    }

    public int getCurrentBossLevel() {
        return currentBossLevel;
    }

    public Phase getCurrentPhase() {
        return currentPhase;
    }

    public void setPhase(Phase phase) {
        GameLogger.info("Phase changed to: " + phase + " for Boss Level " + currentBossLevel);
        this.currentPhase = phase;
    }
}