package se233.contra;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se233.contra.model.entity.*;
import se233.contra.model.entity.Character;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for basic collision detection
 * Tests: entity intersection logic
 */
public class CollisionTest {

    private Character character;
    private Bullet bullet;
    private Boss boss;

    @BeforeEach
    public void setUp() {
        character = new Character(100, 400);
        bullet = new Bullet(150, 420, 1, 0);
        boss = new DefenseWallBoss(600, 300);
    }

    @Test
    public void testBulletIntersectsCharacter() {
        // Place bullet at character's position
        Bullet closeBullet = new Bullet(
                character.getHitboxX() + 10,
                character.getHitboxY() + 10,
                1, 0
        );

        assertTrue(closeBullet.intersects(character),
                "Bullet should intersect character when positions overlap");
    }

    @Test
    public void testBulletDoesNotIntersectCharacter() {
        // Bullet far away from character
        Bullet farBullet = new Bullet(700, 100, 1, 0);

        assertFalse(farBullet.intersects(character),
                "Bullet should not intersect character when far away");
    }

    @Test
    public void testBulletIntersectsBoss() {
        // Place bullet at boss position
        Bullet bossBullet = new Bullet(
                boss.getX() + 10,
                boss.getY() + 10,
                1, 0
        );

        assertTrue(bossBullet.intersects(boss),
                "Bullet should intersect boss when positions overlap");
    }

    @Test
    public void testCharacterIntersectsBoss() {
        // Move character to boss position
        character.setX(boss.getX());
        character.setY(boss.getY());

        assertTrue(character.intersects(boss),
                "Character should intersect boss when positions overlap");
    }

    @Test
    public void testCharacterDoesNotIntersectBoss() {
        // Default positions - character at 100, boss at 600
        assertFalse(character.intersects(boss),
                "Character should not intersect boss when far apart");
    }

    @Test
    public void testMinionIntersectsBullet() {
        Minion minion = new Minion(200, 300, 1);
        Bullet minionBullet = new Bullet(
                minion.getX() + 5,
                minion.getY() + 5,
                1, 0
        );

        assertTrue(minionBullet.intersects(minion),
                "Bullet should intersect minion");
    }

    @Test
    public void testEdgeCollisionTop() {
        // Bullet at top edge of character hitbox
        Bullet edgeBullet = new Bullet(
                character.getHitboxX() + 5,
                character.getHitboxY() - 5,
                1, 0
        );

        boolean collides = edgeBullet.intersects(character);
        assertTrue(collides, "Should detect collision at top edge");
    }

    @Test
    public void testEdgeCollisionBottom() {
        // Bullet at bottom edge of character hitbox
        Bullet edgeBullet = new Bullet(
                character.getHitboxX() + 5,
                character.getHitboxY() + character.getHitboxHeight() - 5,
                1, 0
        );

        boolean collides = edgeBullet.intersects(character);
        assertTrue(collides, "Should detect collision at bottom edge");
    }

    @Test
    public void testNoCollisionJustOutsideRange() {
        // Bullet just outside character hitbox
        Bullet outsideBullet = new Bullet(
                character.getHitboxX() + character.getHitboxWidth() + 1,
                character.getHitboxY(),
                1, 0
        );

        assertFalse(outsideBullet.intersects(character),
                "Should not collide when just outside hitbox");
    }

    @Test
    public void testBulletPassesThroughAfterHit() {
        Bullet testBullet = new Bullet(
                character.getHitboxX(),
                character.getHitboxY(),
                1, 0
        );

        // Simulate hit
        assertTrue(testBullet.intersects(character));
        testBullet.setActive(false);

        assertFalse(testBullet.isActive(),
                "Bullet should be deactivated after hit");
    }

    @Test
    public void testMultipleBulletsIndependentCollision() {
        Bullet bullet1 = new Bullet(character.getHitboxX(), character.getHitboxY(), 1, 0);
        Bullet bullet2 = new Bullet(700, 100, 1, 0);

        assertTrue(bullet1.intersects(character),
                "Bullet 1 should collide with character");
        assertFalse(bullet2.intersects(character),
                "Bullet 2 should not collide with character");
    }

    @Test
    public void testEnemyBulletIntersectsCharacter() {
        EnemyBullet enemyBullet = new EnemyBullet(
                character.getHitboxX() + 10,
                character.getHitboxY() + 10,
                -1, 0
        );

        assertTrue(enemyBullet.intersects(character),
                "Enemy bullet should intersect character");
    }

    @Test
    public void testBossHitboxCollision() {
        // Create boss with known dimensions
        DefenseWallBoss testBoss = new DefenseWallBoss(500, 400);

        // Bullet at center of boss
        Bullet centerBullet = new Bullet(
                testBoss.getX() + testBoss.getWidth() / 2,
                testBoss.getY() + testBoss.getHeight() / 2,
                1, 0
        );

        assertTrue(centerBullet.intersects(testBoss),
                "Bullet should hit boss at center of hitbox");
    }
}