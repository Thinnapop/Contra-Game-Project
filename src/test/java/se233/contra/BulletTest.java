package se233.contra;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se233.contra.model.entity.Bullet;
import se233.contra.model.entity.EnemyBullet;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Bullet mechanics
 * Tests: bullet creation, movement, deactivation
 */
public class BulletTest {

    private Bullet bullet;

    @BeforeEach
    public void setUp() {
        // Create bullet at position (100, 100) moving right
        bullet = new Bullet(100, 100, 1, 0);
    }

    @Test
    public void testBulletCreation() {
        assertNotNull(bullet, "Bullet should be created");
        assertTrue(bullet.isActive(), "Bullet should be active when created");
    }

    @Test
    public void testBulletInitialPosition() {
        assertEquals(100, bullet.getX(), 0.1,
                "Bullet should start at X=100");
        assertEquals(100, bullet.getY(), 0.1,
                "Bullet should start at Y=100");
    }

    @Test
    public void testBulletMovesRight() {
        double initialX = bullet.getX();
        bullet.update();

        assertTrue(bullet.getX() > initialX,
                "Bullet should move right (X increases)");
    }

    @Test
    public void testBulletMovesLeft() {
        Bullet leftBullet = new Bullet(200, 100, -1, 0);
        double initialX = leftBullet.getX();
        leftBullet.update();

        assertTrue(leftBullet.getX() < initialX,
                "Bullet should move left (X decreases)");
    }

    @Test
    public void testBulletMovesUp() {
        Bullet upBullet = new Bullet(100, 200, 0, -1);
        double initialY = upBullet.getY();
        upBullet.update();

        assertTrue(upBullet.getY() < initialY,
                "Bullet should move up (Y decreases)");
    }

    @Test
    public void testBulletMovesDown() {
        Bullet downBullet = new Bullet(100, 100, 0, 1);
        double initialY = downBullet.getY();
        downBullet.update();

        assertTrue(downBullet.getY() > initialY,
                "Bullet should move down (Y increases)");
    }

    @Test
    public void testBulletDiagonalMovement() {
        Bullet diagBullet = new Bullet(100, 100, 1, 1);
        double initialX = diagBullet.getX();
        double initialY = diagBullet.getY();

        diagBullet.update();

        assertTrue(diagBullet.getX() > initialX && diagBullet.getY() > initialY,
                "Bullet should move diagonally");
    }

    @Test
    public void testBulletDeactivatesOutOfBoundsRight() {
        Bullet rightBullet = new Bullet(790, 100, 1, 0);

        // Update until bullet goes off screen
        for (int i = 0; i < 5; i++) {
            rightBullet.update();
        }

        assertFalse(rightBullet.isActive(),
                "Bullet should deactivate when moving off screen (right)");
    }

    @Test
    public void testBulletDeactivatesOutOfBoundsLeft() {
        Bullet leftBullet = new Bullet(10, 100, -1, 0);

        // Update until bullet goes off screen
        for (int i = 0; i < 5; i++) {
            leftBullet.update();
        }

        assertFalse(leftBullet.isActive(),
                "Bullet should deactivate when moving off screen (left)");
    }

    @Test
    public void testBulletDeactivatesOutOfBoundsTop() {
        Bullet upBullet = new Bullet(100, 10, 0, -1);

        for (int i = 0; i < 5; i++) {
            upBullet.update();
        }

        assertFalse(upBullet.isActive(),
                "Bullet should deactivate when moving off screen (top)");
    }

    @Test
    public void testBulletDeactivatesOutOfBoundsBottom() {
        Bullet downBullet = new Bullet(100, 590, 0, 1);

        for (int i = 0; i < 5; i++) {
            downBullet.update();
        }

        assertFalse(downBullet.isActive(),
                "Bullet should deactivate when moving off screen (bottom)");
    }

    @Test
    public void testBulletDamage() {
        assertEquals(20, bullet.getDamage(),
                "Player bullet should deal 20 damage");
    }

    @Test
    public void testBulletSetActive() {
        bullet.setActive(false);
        assertFalse(bullet.isActive(),
                "Bullet should be deactivated");

        bullet.setActive(true);
        assertTrue(bullet.isActive(),
                "Bullet should be reactivated");
    }

    @Test
    public void testEnemyBulletCreation() {
        EnemyBullet enemyBullet = new EnemyBullet(200, 200, -1, 0);

        assertNotNull(enemyBullet, "Enemy bullet should be created");
        assertTrue(enemyBullet.isActive(),
                "Enemy bullet should be active when created");
    }

    @Test
    public void testEnemyBulletDamage() {
        EnemyBullet enemyBullet = new EnemyBullet(200, 200, -1, 0);

        assertEquals(999, enemyBullet.getDamage(),
                "Enemy bullet should deal 999 damage (one-hit kill)");
    }

    @Test
    public void testEnemyBulletMovement() {
        EnemyBullet enemyBullet = new EnemyBullet(300, 200, -1, 0);
        double initialX = enemyBullet.getX();

        enemyBullet.update();

        assertTrue(enemyBullet.getX() < initialX,
                "Enemy bullet should move toward player (left)");
    }

    @Test
    public void testBulletRemainsActiveInBounds() {
        Bullet centerBullet = new Bullet(400, 300, 1, 0);

        // Update a few times (should still be in bounds)
        for (int i = 0; i < 10; i++) {
            centerBullet.update();
        }

        assertTrue(centerBullet.isActive(),
                "Bullet should remain active while in bounds");
    }
}