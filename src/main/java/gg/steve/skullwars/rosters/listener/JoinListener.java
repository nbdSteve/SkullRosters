package gg.steve.skullwars.rosters.listener;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.event.FPlayerJoinEvent;
import com.massivecraft.factions.struct.Role;
import gg.steve.skullwars.rosters.core.FactionRosterManager;
import gg.steve.skullwars.rosters.core.Roster;
import gg.steve.skullwars.rosters.managers.Files;
import gg.steve.skullwars.rosters.message.MessageType;
import gg.steve.skullwars.rosters.utils.LogUtil;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;

public class JoinListener implements Listener {

    @EventHandler
    public void join(PlayerJoinEvent event) {
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
            event.getPlayer().kickPlayer("Your faction already has the maximum amount of members online.");
            return;
        }
        roster.getFaciton().addFPlayer(fPlayer);
        fPlayer.setFaction(roster.getFaciton(), false);
        fPlayer.setRole(roster.getRole(fPlayer.getPlayer().getUniqueId()));
        MessageType.PLAYER_REMOVE.factionMessage(roster.getFaciton(), off.getName(), fPlayer.getName());
    }

    @EventHandler
    public void kick(PlayerKickEvent event) {
        event.setLeaveMessage("");
    }
}
