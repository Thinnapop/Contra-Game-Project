package se233.contra;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se233.contra.model.entity.Boss;
import se233.contra.model.entity.Character;
import se233.contra.model.entity.Bullet;
import se233.contra.model.entity.DefenseWallBoss;

import static org.junit.jupiter.api.Assertions.*;

public class CharacterTest {

    private Character character;

    @BeforeEach
    public void setUp() {
        character = new Character(100, 400);
    }

    @Test
    public void testMoveLeft() {
        double initialX = character.getX();
        character.moveLeft();
        character.update();

        assertTrue(character.getX() < initialX,
                "Character should move left");
    }

    @Test
    public void testMoveRight() {
        double initialX = character.getX();
        character.moveRight();
        character.update();

        assertTrue(character.getX() > initialX,
                "Character should move right");
    }

    @Test
    public void testJump() {
        double initialY = character.getY();
        character.jump();
        character.update();

        assertTrue(character.getY() < initialY,
                "Character should jump up");
    }

    @Test
    public void testProne() {
        character.prone();
        assertTrue(character.isProne(), "Character should be in prone state");
    }

    @Test
    public void testStopMoving() {
        character.moveRight();
        character.update();
        double xAfterMove = character.getX();

        character.stopMoving();
        character.update();

        assertEquals(xAfterMove, character.getX(), 0.1,
                "Character should stop moving");
    }

    @Test
    public void testShoot() {
        Bullet bullet = character.shoot();

        assertNotNull(bullet, "Shoot should return a Bullet");
        assertTrue(bullet.isActive(), "Bullet should be active");
    }

    @Test
    public void testShootCooldown() {
        Bullet bullet1 = character.shoot();
        Bullet bullet2 = character.shoot(); // Immediate second shot

        assertNotNull(bullet1, "First bullet should be created");
        assertNull(bullet2, "Second bullet should be null due to cooldown");
    }

    @Test
    public void testRespawn() {
        character.setX(300);
        character.setY(200);

        character.respawn();

        assertEquals(100, character.getX(), "Character should respawn at X=100");
        assertEquals(300, character.getY(), "Character should respawn at Y=400");
    }

    @Test
    public void testCharacterInitialLives() {
        assertEquals(3, character.getLives(), "Character should start with 3 lives");
    }

    @Test
    public void testLoseLife() {
        int initialLives = character.getLives();
        character.loseLife();
        assertEquals(initialLives - 1, character.getLives(), "Character should lose 1 life");
    }
    @Test
    public void testPlayerBulletHitsBoss() {
        Boss boss = new DefenseWallBoss(0, 0);
        int initialBossHealth = boss.getHealth();
        double bossX = boss.getX();
        double bossY = boss.getY();
        double bulletX = bossX + boss.getWidth() / 2;
        double bulletY = bossY + boss.getHeight() / 2;
        Bullet bullet = new Bullet(bulletX, bulletY, 1, 0);
        assertTrue(bullet.intersects(boss), "Bullet should intersect with boss");
        boss.takeDamage(bullet.getDamage());
        assertTrue(boss.getHealth() < initialBossHealth, "Boss health should decrease after bullet hit");
        assertEquals(initialBossHealth - 20, boss.getHealth(), "Boss should have lost 20 HP");
    }
}