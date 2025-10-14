package se233.contra.model.entity;

public class Bullet extends Entity {
    private double speed;
    private int damage;

    public Bullet(double x, double y, double directionX, double directionY) {
        this.x = x;
        this.y = y;
        this.speed = 10;
        this.damage = 1;
        this.velocityX = directionX * speed;
        this.velocityY = directionY * speed;
    }

    @Override
    public void update() {
        x += velocityX;
        y += velocityY;

        // Deactivate if out of bounds
        if (x < 0 || x > 800 || y < 0 || y > 600) {
            active = false;
        }
    }
}