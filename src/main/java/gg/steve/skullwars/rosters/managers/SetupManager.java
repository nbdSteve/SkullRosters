package gg.steve.skullwars.rosters.managers;

import gg.steve.skullwars.rosters.Rosters;
import gg.steve.skullwars.rosters.cmd.FraCmd;
import gg.steve.skullwars.rosters.gui.GuiClickListener;
import gg.steve.skullwars.rosters.listener.CommandListener;
import gg.steve.skullwars.rosters.listener.FactionEventListener;
import gg.steve.skullwars.rosters.listener.JoinListener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

/**
 * Class that handles setting up the plugin on start
 */
public class SetupManager {

    private SetupManager() throws IllegalAccessException {
        throw new IllegalAccessException("Manager class cannot be instantiated.");
    }

    /**
     * Loads the files into the file manager
     *
     * @param fileManager FileManager, the plugins file manager
     */
    public static void setupFiles(FileManager fileManager) {
        // general files
        for (Files file : Files.values()) {
            file.load(fileManager);
        }
    }

    public static void registerCommands(Rosters instance) {
        instance.getCommand("fra").setExecutor(new FraCmd());
    }

    /**
     * Register all of the events for the plugin
     *
     * @param instance Plugin, the main plugin instance
     */
    public static void registerEvents(Plugin instance) {
        PluginManager pm = instance.getServer().getPluginManager();
        pm.registerEvents(new CommandListener(), instance);
        pm.registerEvents(new FactionEventListener(), instance);
        pm.registerEvents(new JoinListener(), instance);
        pm.registerEvents(new GuiClickListener(), instance);
    }
}
