package se233.contra;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se233.contra.model.Score;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Score system
 * Tests: addScore, getCurrentScore, reset
 */
public class ScoreTest {

    private Score score;

    @BeforeEach
    public void setUp() {
        score = new Score();
    }

    @Test
    public void testScoreInitialValue() {
        assertEquals(0, score.getCurrentScore(),
                "Score should start at 0");
    }

    @Test
    public void testAddScore() {
        score.addScore(10);
        assertEquals(10, score.getCurrentScore(),
                "Score should be 10 after adding 10 points");
    }

    @Test
    public void testAddMultipleScores() {
        score.addScore(5);
        score.addScore(10);
        score.addScore(15);

        assertEquals(30, score.getCurrentScore(),
                "Score should be cumulative (5 + 10 + 15 = 30)");
    }

    @Test
    public void testAddLargeScore() {
        score.addScore(1000);
        assertEquals(1000, score.getCurrentScore(),
                "Score should handle large values");
    }

    @Test
    public void testAddZeroScore() {
        score.addScore(10);
        score.addScore(0);

        assertEquals(10, score.getCurrentScore(),
                "Adding 0 points should not change score");
    }

    @Test
    public void testResetScore() {
        score.addScore(100);
        score.reset();

        assertEquals(0, score.getCurrentScore(),
                "Score should reset to 0");
    }

    @Test
    public void testScoreAfterReset() {
        score.addScore(50);
        score.reset();
        score.addScore(25);

        assertEquals(25, score.getCurrentScore(),
                "Score should work correctly after reset");
    }

    @Test
    public void testMinionScoring() {
        // Regular minion: 1 point
        score.addScore(1);
        assertEquals(1, score.getCurrentScore());

        // Second-tier minion: 2 points
        score.addScore(2);
        assertEquals(3, score.getCurrentScore());
    }

    @Test
    public void testBossScoring() {
        // Defense Wall Boss: 2 points
        score.addScore(2);
        assertEquals(2, score.getCurrentScore());

        // Java Boss: 3 points
        score.addScore(3);
        assertEquals(5, score.getCurrentScore());

        // Custom Boss: 5 points
        score.addScore(5);
        assertEquals(10, score.getCurrentScore());
    }

    @Test
    public void testCompleteGameScoring() {
        // Simulate a complete game
        score.addScore(1);  // Minion
        score.addScore(2);  // Minion
        score.addScore(2);  // Boss 1
        score.addScore(1);  // Minion
        score.addScore(3);  // Boss 2
        score.addScore(5);  // Boss 3

        assertEquals(14, score.getCurrentScore(),
                "Complete game score should be calculated correctly");
    }
}