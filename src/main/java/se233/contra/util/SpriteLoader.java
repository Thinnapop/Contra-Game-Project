package se233.contra.util;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import se233.contra.exception.SpriteLoadException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpriteLoader {
    private static Map<String, Image> spriteCache = new HashMap<>();
    private static Map<String, List<Image>> animationCache = new HashMap<>();

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

    public static Image extractFrame(String spriteSheetPath, int col, int row,
                                     int frameWidth, int frameHeight)
            throws SpriteLoadException {
        try {
            Image spriteSheet = loadSprite(spriteSheetPath);
            PixelReader reader = spriteSheet.getPixelReader();

            int x = col * frameWidth;
            int y = row * frameHeight;

            return new WritableImage(reader, x, y, frameWidth, frameHeight);

        } catch (Exception e) {
            throw new SpriteLoadException(
                    "Failed to extract frame from: " + spriteSheetPath, e
            );
        }
    }


    public static List<Image> extractFramesFromRow(String spriteSheetPath, int row, int startCol, int frameCount, int frameWidth, int frameHeight)
            throws SpriteLoadException {

        String cacheKey = spriteSheetPath + "_row" + row + "_" + startCol + "_" + frameCount;

        if (animationCache.containsKey(cacheKey)) {
            return animationCache.get(cacheKey);
        }

        List<Image> frames = new ArrayList<>();
        try {
            Image spriteSheet = loadSprite(spriteSheetPath);
            PixelReader reader = spriteSheet.getPixelReader();

            for (int i = 0; i < frameCount; i++) {
                int x = (startCol + i) * frameWidth;
                int y = row * frameHeight;
                frames.add(new WritableImage(reader, x, y, frameWidth, frameHeight));
            }

            animationCache.put(cacheKey, frames);
            GameLogger.info("Extracted " + frameCount + " frames from row " + row);
            return frames;

        } catch (Exception e) {
            throw new SpriteLoadException(
                    "Failed to extract frames from: " + spriteSheetPath, e
            );
        }
    }
}