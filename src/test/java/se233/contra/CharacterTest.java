package se233.contra;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se233.contra.model.entity.Character;
import se233.contra.model.entity.Bullet;
import se233.contra.model.Platform;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Character movements and actions
 * Tests: moveLeft, moveRight, jump, prone, shoot, respawn
 */
public class CharacterTest {

    private Character character;
    private List<Platform> platforms;

    @BeforeEach
    public void setUp() {
        character = new Character(100, 400);
        platforms = new ArrayList<>();
        platforms.add(new Platform(0, 540, 800, 60)); // Ground platform
    }

    // ===== MOVEMENT TESTS =====

    @Test
    public void testMoveLeft() {
        double initialX = character.getX();
        character.moveLeft();
        character.update();

        assertTrue(character.getX() < initialX,
                "Character should move left (X should decrease)");
    }

    @Test
    public void testMoveRight() {
        double initialX = character.getX();
        character.moveRight();
        character.update();

        assertTrue(character.getX() > initialX,
                "Character should move right (X should increase)");
    }

    @Test
    public void testStopMoving() {
        character.moveRight();
        character.update();
        double xAfterMove = character.getX();

        character.stopMoving();
        character.update();

        assertEquals(xAfterMove, character.getX(), 0.1,
                "Character should stop moving when stopMoving() is called");
    }

    @Test
    public void testJump() {
        double initialY = character.getY();
        character.jump();
        character.update();

        assertTrue(character.getY() < initialY,
                "Character should move up when jumping (Y should decrease)");
    }

    @Test
    public void testCannotDoubleJump() {
        character.jump();
        character.update();
        double yAfterFirstJump = character.getY();

        character.jump(); // Try to jump again while in air

        assertEquals(yAfterFirstJump, character.getY(), 0.1,
                "Character should not be able to double jump");
    }

    @Test
    public void testProne() {
        double initialHeight = character.getHeight();
        character.prone();

        assertTrue(character.isProne(),
                "Character should be in prone state after prone() is called");
        // Height changes when prone
        assertNotEquals(initialHeight, character.getHeight(),
                "Character height should change when prone");
    }

    @Test
    public void testStandUp() {
        character.prone();
        assertTrue(character.isProne(), "Character should be prone");

        character.standUp();
        assertFalse(character.isProne(),
                "Character should no longer be prone after standUp()");
    }

    @Test
    public void testCannotJumpWhileProne() {
        character.prone();
        double initialY = character.getY();

        character.jump();
        character.update();

        assertEquals(initialY, character.getY(), 0.1,
                "Character should not be able to jump while prone");
    }

    @Test
    public void testBoundaryLeft() {
        // Move far left beyond screen boundary
        for (int i = 0; i < 100; i++) {
            character.moveLeft();
            character.update();
        }

        assertTrue(character.getHitboxX() >= 0,
                "Character should not go beyond left boundary (X >= 0)");
    }

    @Test
    public void testBoundaryRight() {
        // Move far right beyond screen boundary
        for (int i = 0; i < 500; i++) {
            character.moveRight();
            character.update();
        }

        assertTrue(character.getHitboxX() + character.getHitboxWidth() <= 800,
                "Character should not go beyond right boundary (X <= 800)");
    }

    // ===== ACTION TESTS =====

    @Test
    public void testShoot() {
        Bullet bullet = character.shoot();

        assertNotNull(bullet, "Shoot should return a Bullet object");
        assertTrue(bullet.isActive(), "Bullet should be active when created");
    }

    @Test
    public void testShootCooldown() {
        Bullet bullet1 = character.shoot();
        Bullet bullet2 = character.shoot(); // Immediately try to shoot again

        assertNotNull(bullet1, "First bullet should be created");
        assertNull(bullet2, "Second bullet should be null due to cooldown");
    }

    @Test
    public void testShootAfterCooldown() {
        character.shoot();

        // Simulate cooldown period (update multiple times)
        for (int i = 0; i < 15; i++) {
            character.update();
        }

        Bullet bullet = character.shoot();
        assertNotNull(bullet, "Should be able to shoot again after cooldown");
    }

    @Test
    public void testRespawn() {
        // Move character away from spawn
        character.setX(500);
        character.setY(200);
        character.moveRight();
        character.prone();

        character.respawn();

        assertEquals(100, character.getX(), "Character should respawn at X=100");
        assertEquals(400, character.getY(), "Character should respawn at Y=400");
        assertFalse(character.isProne(), "Character should stand up after respawn");
    }

    @Test
    public void testCharacterInitialLives() {
        assertEquals(3, character.getLives(),
                "Character should start with 3 lives");
    }

    @Test
    public void testLoseLife() {
        int initialLives = character.getLives();
        character.loseLife();

        assertEquals(initialLives - 1, character.getLives(),
                "Character should lose 1 life");
    }

    @Test
    public void testCharacterIsActiveOnCreation() {
        assertTrue(character.isActive(),
                "Character should be active when created");
    }

    @Test
    public void testPlatformCollision() {
        // Place character above platform
        character.setY(450);
        character.jump(); // Make character fall

        // Simulate falling
        for (int i = 0; i < 50; i++) {
            character.update();
            character.checkPlatformCollision(platforms);
        }

        // Character should land on platform at Y=540
        assertTrue(character.getHitboxY() + character.getHitboxHeight() <= 550,
                "Character should land on platform");
    }

    @Test
    public void testGravityApplies() {
        character.setY(100); // Place in air
        double initialY = character.getY();

        // Update several times to let gravity take effect
        for (int i = 0; i < 10; i++) {
            character.update();
        }

        assertTrue(character.getY() > initialY,
                "Gravity should pull character down (Y increases)");
    }
}