package se233.contra.util;

import javafx.scene.image.Image;
import java.util.List;

public class AnimationManager {
    private List<Image> frames;
    private int currentFrame;
    private int frameDelay;
    private int frameCounter;

    public AnimationManager(List<Image> frames, int frameDelay) {
        this.frames = frames;
        this.frameDelay = frameDelay;
        this.currentFrame = 0;
        this.frameCounter = 0;
    }

    public void update() {
        frameCounter++;
        if (frameCounter >= frameDelay) {
            currentFrame = (currentFrame + 1) % frames.size();
            frameCounter = 0;
        }
    }
    public void reset() {
        currentFrame = 0;
        frameCounter = 0;
    }

    public Image getCurrentFrame() {
        return frames.get(currentFrame);
    }
}