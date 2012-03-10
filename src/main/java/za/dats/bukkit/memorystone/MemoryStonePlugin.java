package za.dats.bukkit.memorystone;

import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
// import za.dats.bukkit.memorystone.economy.EconomyManager;
import za.dats.bukkit.memorystone.ui.SpoutLocationPopupManager;
import za.dats.bukkit.memorystone.util.StructureManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;
import java.util.logging.Level;

public class MemoryStonePlugin extends JavaPlugin {

    private PluginDescriptionFile pdf;
    private PluginManager pm;
    private StructureManager structureManager = new StructureManager(this, "[MemoryStone] ");
    private MemoryStoneManager memoryStoneManager = new MemoryStoneManager(this);
    private CompassManager compassManager = new CompassManager(this);
    private SpoutLocationPopupManager spoutLocationPopupManager;
    // private EconomyManager economyManager = new EconomyManager();
    private static MemoryStonePlugin instance;
    private FileConfiguration[] myConfigs;
    private File[] myConfigFiles;

    /*
    public void onDisable() {
    }
    * 
    */

    public void info(String log) {
        getServer().getLogger().info("[MemoryStone] " + log);
    }

    public void warn(String log) {
        getServer().getLogger().warning("[MemoryStone] " + log);
    }

    @Override
    public void onEnable() {
        instance = this;
        Config.init(this);

        pm = getServer().getPluginManager();
        pdf = getDescription();

        info(pdf.getName() + " version " + pdf.getVersion() + " is enabled!");

        // economyManager.loadEconomy();

        structureManager.addStructureListener(memoryStoneManager);
        structureManager.registerEvents();

        // memoryStoneManager.registerEvents();
        pm.registerEvents(memoryStoneManager, this);
        compassManager.registerEvents();

        if (isSpoutEnabled()) {
            spoutLocationPopupManager = new SpoutLocationPopupManager(this);
            spoutLocationPopupManager.registerEvents();
        }
    }

    public boolean isSpoutEnabled() {
        if (pm.isPluginEnabled("Spout")) {
            return true;
        }
        return false;
    }

    public StructureManager getStructureManager() {
        return structureManager;
    }

    public MemoryStoneManager getMemoryStoneManager() {
        return memoryStoneManager;
    }

    public CompassManager getCompassManager() {
        return compassManager;
    }

    public SpoutLocationPopupManager getSpoutLocationPopupManager() {
        return spoutLocationPopupManager;
    }

    public static MemoryStonePlugin getInstance() {
        return instance;
    }

    /*
     * public EconomyManager getEconomyManager() { return economyManager; }
     *
     */
    /*
     * setup custom configuration handlers
     */
    public void reloadMyConfigs() {
        myConfigs = new FileConfiguration[4];
        myConfigFiles = new File[4];

        myConfigFiles[0] = new File(getDataFolder(), "configuration.yml");
        myConfigFiles[1] = new File(getDataFolder(), "locations.yml");
        myConfigFiles[2] = new File(getDataFolder(), "structures.yml");
        myConfigFiles[3] = new File(getDataFolder(), "structuretypes.yml");

        for (int i = 0; i < 4; i++) {
            myConfigs[i] = YamlConfiguration.loadConfiguration(myConfigFiles[i]);
        }

        /*
         * // Look for defaults in the jar InputStream defConfigStream =
         * getResource("customConfig.yml"); if (defConfigStream != null) {
         * YamlConfiguration defConfig =
         * YamlConfiguration.loadConfiguration(defConfigStream);
         * customConfig.setDefaults(defConfig); }
         *
         */
    }

    public FileConfiguration getMyConfig(String name) {
        if (myConfigs == null) {
            reloadMyConfigs();
        }

        if ("configuration".equals(name)) {
            return myConfigs[0];
        } else if ("locations".equals(name)) {
            return myConfigs[1];
        } else if ("structures".equals(name)) {
            return myConfigs[2];
        } else if ("structuretypes".equals(name)) {
            return myConfigs[3];
        }

        return myConfigs[0];
    }

    public void saveCustomConfig() {
        if (myConfigs == null || myConfigFiles == null) {
            return;
        }


        for (int i = 0; i < 4; i++) {
            try {
                myConfigs[i].save(myConfigFiles[i]);
            } catch (IOException ex) {
                Logger.getLogger(JavaPlugin.class.getName()).log(Level.SEVERE, "Could not save config to " + myConfigFiles[i], ex);
            }
        }
    }
}
