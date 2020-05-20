package gg.steve.skullwars.rosters.utils;

import gg.steve.skullwars.rosters.Rosters;

public class LogUtil {

    public static void info(String message) {
        Rosters.get().getLogger().info(message);
    }

    public static void warning(String message) {
        Rosters.get().getLogger().warning(message);
    }
}
