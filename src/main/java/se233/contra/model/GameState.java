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

    private State currentState;
    private int currentBossLevel;

    public GameState() {
        currentState = State.PLAYING;
        currentBossLevel = 1;
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
    }

    public int getCurrentBossLevel() {
        return currentBossLevel;
    }
}