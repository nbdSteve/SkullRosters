package gg.steve.skullwars.rosters.listener;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.event.FPlayerJoinEvent;
import com.massivecraft.factions.event.FPlayerLeaveEvent;
import com.massivecraft.factions.event.FPlayerRoleChangeEvent;
import com.massivecraft.factions.event.FactionDisbandEvent;
import com.massivecraft.factions.struct.Role;
import gg.steve.skullwars.rosters.core.FactionRosterManager;
import gg.steve.skullwars.rosters.core.Roster;
import gg.steve.skullwars.rosters.managers.Files;
import gg.steve.skullwars.rosters.message.MessageType;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class FactionEventListener implements Listener {

    @EventHandler
    public void disband(FactionDisbandEvent event) {
        FactionRosterManager.disband(event.getFaction());
    }

    @EventHandler
    public void invite(FPlayerJoinEvent event) {
        if (event.getReason().equals(FPlayerJoinEvent.PlayerJoinReason.CREATE)) {
            FactionRosterManager.addRoster(event.getFaction());
            Roster roster = FactionRosterManager.getRoster(event.getFaction());
            roster.addPlayer(event.getfPlayer().getPlayer().getUniqueId(), event.getfPlayer().getRole());
            return;
        }
        Roster roster = FactionRosterManager.getRoster(event.getFaction());
        if (!roster.isOnRoster(event.getfPlayer().getPlayer().getUniqueId())) {
            MessageType.NOT_ON_ROSTER_JOINER.message(event.getfPlayer().getPlayer(), event.getFaction().getTag());
            MessageType.NOT_ON_ROSTER_FACTION.factionMessage(event.getFaction(), event.getfPlayer().getName());
            event.setCancelled(true);
            return;
        }
        if (roster.getFaciton().getOnlinePlayers().size() >= Files.CONFIG.get().getInt("faction-size")) {
            MessageType.FACTION_MAX_ONLINE.message(event.getfPlayer().getPlayer(), event.getFaction().getTag());
            MessageType.FACTION_MAX_ONLINE_FACTION.factionMessage(event.getFaction(), event.getfPlayer().getName());
            event.setCancelled(true);
            return;
        }
        if (roster.getFaciton().getSize() >= Files.CONFIG.get().getInt("faction-size")) {
            FPlayer off = null;
            for (FPlayer offline : roster.getFaciton().getFPlayers()) {
                if (offline.isOffline() && !offline.getRole().equals(Role.LEADER)) {
                    off = offline;
                    roster.getFaciton().removeFPlayer(offline);
                    offline.resetFactionData();
                    break;
                }
            }
            roster.getFaciton().addFPlayer(event.getfPlayer());
            event.getfPlayer().setFaction(roster.getFaciton(), false);
            event.getfPlayer().setRole(roster.getRole(event.getfPlayer().getPlayer().getUniqueId()));
            MessageType.PLAYER_REMOVE.factionMessage(roster.getFaciton(), off.getName(), event.getfPlayer().getName());
        }
    }

    @EventHandler
    public void leave(FPlayerLeaveEvent event) {
        Roster roster = FactionRosterManager.getRoster(event.getFaction());
        roster.removePlayer(event.getfPlayer().getPlayer().getUniqueId());
        MessageType.ROSTER_REMOVE.factionMessage(roster.getFaciton(), event.getfPlayer().getName());
        if (event.getReason().equals(FPlayerLeaveEvent.PlayerLeaveReason.KICKED)) {
            if (!Bukkit.getServer().isGracePeriod()) {
                roster.decrementMaxMembers();
                MessageType.ROSTER_KICK.factionMessage(event.getFaction(), event.getfPlayer().getName(), String.valueOf(roster.getMaxMembers()));
            }
        }
    }

    @EventHandler
    public void role(FPlayerRoleChangeEvent event) {
        Roster roster = FactionRosterManager.getRoster(event.getFaction());
        roster.updateRole(event.getfPlayer().getPlayer().getUniqueId(), event.getTo());
    }
}
