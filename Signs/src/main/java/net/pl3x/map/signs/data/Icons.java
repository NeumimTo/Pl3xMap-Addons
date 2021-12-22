package net.pl3x.map.signs.data;

import net.pl3x.map.api.Key;
import net.pl3x.map.api.Pl3xMapProvider;
import net.pl3x.map.signs.Logger;
import net.pl3x.map.signs.SignsPlugin;
import org.bukkit.Material;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class Icons {
    public static final Key OAK = register("sign_oak");
    public static final Key SPRUCE = register("sign_spruce");
    public static final Key BIRCH = register("sign_birch");
    public static final Key JUNGLE = register("sign_jungle");
    public static final Key ACACIA = register("sign_acacia");
    public static final Key DARK_OAK = register("sign_dark_oak");
    public static final Key CRIMSON = register("sign_crimson");
    public static final Key WARPED = register("sign_warped");

    private static Key register(String name) {
        SignsPlugin plugin = SignsPlugin.getInstance();
        String filename = "icons" + File.separator + name + ".png";
        File file = new File(plugin.getDataFolder(), filename);
        if (!file.exists()) {
            plugin.saveResource(filename, false);
        }
        Key key = Key.of(name);
        try {
            BufferedImage image = ImageIO.read(file);
            Pl3xMapProvider.get().iconRegistry().register(key, image);
        } catch (IOException e) {
            Logger.log().log(Level.WARNING, "Failed to register signs icon", e);
        }
        return key;
    }

    public static Key getIcon(Material type) {
        switch (type) {
            case SPRUCE_SIGN:
            case SPRUCE_WALL_SIGN:
                return SPRUCE;
            case BIRCH_SIGN:
            case BIRCH_WALL_SIGN:
                return BIRCH;
            case JUNGLE_SIGN:
            case JUNGLE_WALL_SIGN:
                return JUNGLE;
            case ACACIA_SIGN:
            case ACACIA_WALL_SIGN:
                return ACACIA;
            case DARK_OAK_SIGN:
            case DARK_OAK_WALL_SIGN:
                return DARK_OAK;
            case CRIMSON_SIGN:
            case CRIMSON_WALL_SIGN:
                return CRIMSON;
            case WARPED_SIGN:
            case WARPED_WALL_SIGN:
                return WARPED;
            case OAK_SIGN:
            case OAK_WALL_SIGN:
            default:
                return OAK;
        }
    }
}
