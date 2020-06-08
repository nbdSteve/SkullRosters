package gg.steve.skullwars.rosters.utils;

import gg.steve.skullwars.rosters.SkullRosters;

public class LogUtil {

    public static void info(String message) {
        SkullRosters.get().getLogger().info(message);
    }

    public static void warning(String message) {
        SkullRosters.get().getLogger().warning(message);
    }
}
