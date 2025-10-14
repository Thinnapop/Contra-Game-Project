package se233.contra.model.entity;

public class EnemyBullet extends Bullet {
    public EnemyBullet(double x, double y, double directionX, double directionY) {
        super(x, y, directionX, directionY);
        this.damage = 999; // One-hit kill
    }
}