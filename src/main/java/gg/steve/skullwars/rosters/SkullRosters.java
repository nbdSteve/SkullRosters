package gg.steve.skullwars.rosters;

import gg.steve.skullwars.rosters.core.FactionRosterManager;
import gg.steve.skullwars.rosters.managers.FileManager;
import gg.steve.skullwars.rosters.managers.Files;
import gg.steve.skullwars.rosters.managers.SetupManager;
import gg.steve.skullwars.rosters.utils.LogUtil;
import org.bukkit.plugin.java.JavaPlugin;

public final class SkullRosters extends JavaPlugin {
    private static SkullRosters instance;
    private static boolean roster;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        SetupManager.setupFiles(new FileManager(instance));
        SetupManager.registerCommands(instance);
        SetupManager.registerEvents(instance);
        FactionRosterManager.initialise();
        roster = !Files.CONFIG.get().getBoolean("using-force-invites");
        LogUtil.info("SkullRosters has successfully loaded, please contact nbdSteve#0583 on discord if you have any issues.");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        FactionRosterManager.saveRosters();
    }

    public static SkullRosters get() {
        return instance;
    }

    public static boolean isRosters() {
        return roster;
    }
}
