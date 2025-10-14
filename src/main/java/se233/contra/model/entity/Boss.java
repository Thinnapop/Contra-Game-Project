package se233.contra.model.entity;

public abstract class Boss extends Entity {
    protected int health;
    protected int maxHealth;
    protected int scoreValue;
    protected boolean isDefeated;

    public abstract void attack();
    public abstract void takeDamage(int damage);
    public abstract void move();

    public boolean isDefeated() {
        return health <= 0;
    }
}