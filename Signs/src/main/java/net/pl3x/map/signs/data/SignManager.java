package net.pl3x.map.signs.data;

import net.pl3x.map.api.Key;
import net.pl3x.map.signs.Logger;
import net.pl3x.map.signs.SignsPlugin;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.logging.Level;

public class SignManager {
    private final SignsPlugin plugin;
    private final File dataDir;

    public SignManager(SignsPlugin plugin) {
        this.plugin = plugin;
        this.dataDir = new File(plugin.getDataFolder(), "data");

        if (!this.dataDir.exists()) {
            //noinspection ResultOfMethodCallIgnored
            this.dataDir.mkdirs();
        }
    }

    public void load() {
        plugin.getPl3xmapHook().getProviders().forEach((uuid, provider) -> {
            YamlConfiguration config = new YamlConfiguration();
            try {
                File file = new File(dataDir, uuid + ".yml");
                if (file.exists()) {
                    config.load(file);
                }
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }
            config.getKeys(false).forEach(entry -> {
                System.out.println(entry);
                try {
                    String[] split = entry.split(",");
                    int x = Integer.parseInt(split[0]);
                    int y = Integer.parseInt(split[1]);
                    int z = Integer.parseInt(split[2]);
                    Position pos = Position.of(x, y, z);
                    Key key = Key.of(Objects.requireNonNull(config.getString(entry + ".key")));
                    String[] lines = config.getStringList(entry + ".lines").toArray(new String[0]);
                    provider.add(pos, key, lines);
                } catch (Exception e) {
                    Logger.log().log(Level.SEVERE, "Could not load " + entry + " from " + uuid, e);
                }
            });
        });
    }

    public void save() {
        plugin.getPl3xmapHook().getProviders().forEach((uuid, provider) -> {
            YamlConfiguration config = new YamlConfiguration();
            provider.getData().forEach((pos, data) -> {
                String entry = pos.getX() + "," + pos.getY() + "," + pos.getZ();
                config.set(entry + ".key", data.getKey().getKey());
                config.set(entry + ".lines", data.getLines());
            });
            try {
                config.save(new File(new File(plugin.getDataFolder(), "data"), uuid + ".yml"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void removeSign(BlockState state) {
        SignLayerProvider provider = plugin.getPl3xmapHook().getProvider(state.getWorld());
        if (provider == null) {
            return;
        }
        Location loc = state.getLocation();
        provider.remove(Position.of(loc));
    }

    public void putSign(BlockState state, String[] lines) {
        SignLayerProvider provider = plugin.getPl3xmapHook().getProvider(state.getWorld());
        if (provider == null) {
            return;
        }
        provider.add(Position.of(state.getLocation()), Icons.getIcon(state.getType()), lines);
    }

    public boolean isTracked(BlockState state) {
        SignLayerProvider provider = plugin.getPl3xmapHook().getProvider(state.getWorld());
        if (provider == null) {
            return false;
        }
        return provider.getData(Position.of(state.getLocation())) != null;
    }

    public void checkChunk(Chunk chunk) {
        int minX = chunk.getX();
        int minZ = chunk.getZ();
        int maxX = minX + 16;
        int maxZ = minZ + 16;
        SignLayerProvider provider = plugin.getPl3xmapHook().getProvider(chunk.getWorld());
        if (provider != null) {
            provider.getPositions().forEach(pos -> {
                if (pos.getX() >= minX && pos.getZ() >= minZ &&
                        pos.getX() <= maxX && pos.getZ() <= maxZ &&
                        !pos.isSign(chunk.getWorld())) {
                    provider.remove(pos);
                }
            });
        }
    }
}
