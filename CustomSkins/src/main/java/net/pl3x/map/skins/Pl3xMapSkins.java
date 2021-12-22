package net.pl3x.map.skins;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import net.pl3x.map.api.Pl3xMapProvider;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Base64;

public final class Pl3xMapSkins extends JavaPlugin {
    private static Pl3xMapSkins instance;
    private static File skinsDir;

    public Pl3xMapSkins() {
        instance = this;
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();

        if (!getServer().getPluginManager().isPluginEnabled("Pl3xMap")) {
            getLogger().severe("Pl3xMap not found!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        skinsDir = new File(Pl3xMapProvider.get().webDir().toFile(), "skins");
        if (!skinsDir.exists() && !skinsDir.mkdirs()) {
            getLogger().severe("Could not create skins directory!");
            getLogger().severe("Check your file permissions and try again");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        getServer().getPluginManager().registerEvents(new Listener() {
            @EventHandler(priority = EventPriority.MONITOR)
            public void onPlayerJoin(PlayerJoinEvent event) {
                new FetchSkinURL(event.getPlayer()).runTaskLater(instance, 5);
            }
        }, this);

        int interval = getConfig().getInt("update-interval", 60);
        new UpdateTask().runTaskTimer(instance, interval, interval);
    }

    private static class UpdateTask extends BukkitRunnable {
        @Override
        public void run() {
            Bukkit.getOnlinePlayers().forEach(player ->
                    new FetchSkinURL(player).runTask(instance));
        }
    }

    private static class FetchSkinURL extends BukkitRunnable {
        private final Player player;

        private FetchSkinURL(Player player) {
            this.player = player;
        }

        @Override
        public void run() {
            if (!player.isOnline()) {
                return;
            }
            String url = getTexture(player);
            if (url == null || url.isEmpty()) {
                return;
            }
            String name = player.getName();
            new SaveSkin(name, url).runTaskAsynchronously(instance);
        }
    }

    private static class SaveSkin extends BukkitRunnable {
        private final String name;
        private final String url;

        private SaveSkin(String name, String url) {
            this.name = name;
            this.url = url;
        }

        @Override
        public void run() {
            saveTexture(name, url);
        }
    }

    private static String getTexture(Player player) {
        PlayerProfile profile = player.getPlayerProfile();
        for (ProfileProperty property : profile.getProperties()) {
            if (property.getName().equals("textures")) {
                try {
                    String data = property.getValue();
                    byte[] base64 = Base64.getDecoder().decode(data);
                    String json = new String(base64);
                    JSONObject obj = (JSONObject) new JSONParser().parse(json);
                    JSONObject obj2 = (JSONObject) obj.get("textures");
                    JSONObject obj3 = (JSONObject) obj2.get("SKIN");
                    return (String) obj3.get("url");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private static void saveTexture(String name, String url) {
        try {
            BufferedImage img = ImageIO.read(new URL(url)).getSubimage(8, 8, 8, 8);
            File file = new File(skinsDir, name + ".png");
            ImageIO.write(img, "png", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
