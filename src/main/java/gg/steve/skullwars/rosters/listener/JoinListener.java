package gg.steve.skullwars.rosters.listener;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.struct.Role;
import gg.steve.skullwars.rosters.Rosters;
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
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JoinListener implements Listener {
    private static List<UUID> kicked = new ArrayList<>();

    @EventHandler
    public void join(PlayerJoinEvent event) {
        if (!Rosters.isRosters()) return;
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(event.getPlayer());
        if (fPlayer.hasFaction()) return;
        Roster roster;
        if ((roster = FactionRosterManager.getRosterForPlayer(fPlayer)) == null) return;
        LogUtil.info(roster.getFaciton().getSize() + "");
        if (roster.getFaciton().getSize() < Files.CONFIG.get().getInt("faction-size")) return;
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
            event.setJoinMessage("");
            kicked.add(event.getPlayer().getUniqueId());
            event.getPlayer().kickPlayer(ColorUtil.colorize(Files.CONFIG.get().getString("kick-message").replace("{faction}", roster.getFaciton().getTag())));
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
