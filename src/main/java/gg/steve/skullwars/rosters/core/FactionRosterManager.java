package gg.steve.skullwars.rosters.core;

import com.massivecraft.factions.Faction;

import java.util.HashMap;
import java.util.Map;

public class FactionRosterManager {
    private static Map<Faction, Roster> rosters;

    public static void initialise() {
        rosters = new HashMap<>();
    }

    public static void saveRosters() {
        for (Roster roster : rosters.values()) {
            roster.saveToFile();
        }
    }

    public static Roster getRoster(Faction faction) {
        if (!rosters.containsKey(faction)) addRoster(faction);
        return rosters.get(faction);
    }

    public static void addRoster(Faction faction) {
        rosters.put(faction, new Roster(faction));
    }

    public static void disband(Faction faction) {
        rosters.get(faction).delete();
        rosters.remove(faction);
    }
}
