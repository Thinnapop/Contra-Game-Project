package se233.contra.util;

import javafx.scene.image.Image;
import se233.contra.exception.SpriteLoadException;
import java.util.HashMap;
import java.util.Map;

public class SpriteLoader {
    private static Map<String, Image> spriteCache = new HashMap<>();

    public static Image loadSprite(String path) throws SpriteLoadException {
        try {
            if (spriteCache.containsKey(path)) {
                return spriteCache.get(path);
            }

            Image image = new Image(
                    SpriteLoader.class.getResourceAsStream(path)
            );
            spriteCache.put(path, image);
            GameLogger.info("Loaded sprite: " + path);
            return image;

        } catch (Exception e) {
            throw new SpriteLoadException("Failed to load sprite: " + path, e);
        }
    }
}