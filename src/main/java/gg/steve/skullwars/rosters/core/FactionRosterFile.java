package gg.steve.skullwars.rosters.core;

import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.FactionsPlugin;
import gg.steve.skullwars.rosters.Rosters;
import gg.steve.skullwars.rosters.managers.Files;
import gg.steve.skullwars.rosters.utils.LogUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class FactionRosterFile {
    //Store the file name string
    private String fileName;
    //Store the player file
    private File file;
    //Store the yaml config
    private YamlConfiguration config;

    public FactionRosterFile(String factionId) {
        //Set instance variable
        this.fileName = factionId;
        //Get the player file
        file = new File(Rosters.get().getDataFolder(), "roster-data" + File.separator + fileName + ".yml");
        //Load the configuration for the file
        config = YamlConfiguration.loadConfiguration(file);
        //If the file doesn't exist then set the defaults
        if (!file.exists()) {
            setupFactionFileDefaults(config, factionId);
        }
        save();
    }

    private void setupFactionFileDefaults(YamlConfiguration config, String factionId) {
        //Set defaults for the information about the players tiers and currency
        config.set("max-members", Files.CONFIG.get().getInt("roster-size"));
        if (Bukkit.getServer().isGracePeriod()) {
            config.set("remaining-invites", Files.CONFIG.get().getInt("invites-after-grace"));
        } else {
            config.set("remaining-invites", Files.CONFIG.get().getInt("faction-size") + Files.CONFIG.get().getInt("invites-after-grace"));
        }
        config.set("adds-remaining", Files.CONFIG.get().getInt("roster-size"));
        config.set("players", new ArrayList<String>());
        //Send a nice message
        LogUtil.info("Successfully created a new faction roster file for faction with id: " + fileName + ", defaults have been set.");
    }

    public void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            LogUtil.warning("Critical error saving the file: " + fileName + ", please contact nbdSteve#0583 on discord.");
        }
        reload();
    }

    public void reload() {
        try {
            config.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            LogUtil.warning("Critical error loading the file: " + fileName + ", please contact nbdSteve#0583 on discord.");
        }
    }

    public void delete() {
        file.delete();
    }

    public YamlConfiguration get() {
        return config;
    }
}
