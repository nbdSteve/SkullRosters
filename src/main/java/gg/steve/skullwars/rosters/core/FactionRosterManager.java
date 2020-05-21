package gg.steve.skullwars.rosters.core;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;

import java.util.HashMap;
import java.util.Map;

public class FactionRosterManager {
    private static Map<Faction, Roster> rosters;

    public static void initialise() {
        rosters = new HashMap<>();
        for (Faction faction : Factions.getInstance().getAllFactions()) {
            if (faction.isWarZone() || faction.isWilderness() || faction.isSafeZone()) continue;
            addRoster(faction);
        }
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

    public static Roster getRosterForPlayer(FPlayer fPlayer) {
        for (Roster roster : rosters.values()) {
            if (roster.isOnRoster(fPlayer.getPlayer().getUniqueId())) return roster;
        }
        return null;
    }

    public static void addRoster(Faction faction) {
        rosters.put(faction, new Roster(faction));
    }

    public static void disband(Faction faction) {
        rosters.get(faction).delete();
        rosters.remove(faction);
    }

    public static Map<Faction, Roster> getRosters() {
        return rosters;
    }
}
