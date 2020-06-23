package gg.steve.skullwars.rosters.listener;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.struct.Role;
import gg.steve.skullwars.rosters.SkullRosters;
import gg.steve.skullwars.rosters.core.FactionRosterManager;
import gg.steve.skullwars.rosters.core.Roster;
import gg.steve.skullwars.rosters.managers.Files;
import gg.steve.skullwars.rosters.message.MessageType;
import gg.steve.skullwars.rosters.utils.ColorUtil;
import gg.steve.skullwars.rosters.utils.LogUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JoinListener implements Listener {
    private static List<UUID> kicked = new ArrayList<>();

    @EventHandler
    public void join(PlayerJoinEvent event) {
        if (!SkullRosters.isRosters()) return;
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(event.getPlayer());
        if (fPlayer.hasFaction()) return;
        Roster roster;
        if ((roster = FactionRosterManager.getRosterForPlayer(fPlayer)) == null) return;
        int add = 0;
        if (!roster.getFaciton().getFPlayerLeader().isOnline()) add++;
        if (roster.getFaciton().getSize() + add < Files.CONFIG.get().getInt("faction-size")) return;
        if (roster.getFaciton().getOnlinePlayers().size() + add >= Files.CONFIG.get().getInt("faction-size")) {
            if (Files.CONFIG.get().getBoolean("roster-full-kick")) {
                event.setJoinMessage("");
                kicked.add(event.getPlayer().getUniqueId());
                event.getPlayer().kickPlayer(ColorUtil.colorize(Files.CONFIG.get().getString("kick-message").replace("{faction}", roster.getFaciton().getTag())));
            } else {
                MessageType.FACTION_FULL_JOIN.message(event.getPlayer());
            }
            return;
        }
        FPlayer off = null;
        for (FPlayer offline : roster.getFaciton().getFPlayers()) {
            if (offline.isOffline() && !offline.getRole().equals(Role.LEADER)) {
                off = offline;
                roster.getFaciton().removeFPlayer(offline);
                offline.resetFactionData();
                break;
            }
        }
        if (off == null) {
            if (Files.CONFIG.get().getBoolean("roster-full-kick")) {
                event.setJoinMessage("");
                kicked.add(event.getPlayer().getUniqueId());
                event.getPlayer().kickPlayer(ColorUtil.colorize(Files.CONFIG.get().getString("kick-message").replace("{faction}", roster.getFaciton().getTag())));
            } else {
                MessageType.FACTION_FULL_JOIN.message(event.getPlayer());
            }
            return;
        }
        roster.getFaciton().addFPlayer(fPlayer);
        fPlayer.setFaction(roster.getFaciton(), false);
        fPlayer.setRole(roster.getRole(fPlayer.getPlayer().getUniqueId()));
        MessageType.PLAYER_REMOVE.factionMessage(roster.getFaciton(), off.getName(), fPlayer.getName());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void kick(PlayerQuitEvent event) {
        if (kicked.contains(event.getPlayer().getUniqueId())) {
            event.setQuitMessage("");
            kicked.remove(event.getPlayer().getUniqueId());
        }
    }
}
