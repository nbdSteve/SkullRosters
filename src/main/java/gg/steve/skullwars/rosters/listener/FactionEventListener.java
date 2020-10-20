package gg.steve.skullwars.rosters.listener;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.event.FPlayerJoinEvent;
import com.massivecraft.factions.event.FPlayerLeaveEvent;
import com.massivecraft.factions.event.FPlayerRoleChangeEvent;
import com.massivecraft.factions.event.FactionDisbandEvent;
import com.massivecraft.factions.struct.Role;
import gg.steve.skullwars.rosters.SkullRosters;
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
        // rosters code
        if (event.getfPlayer().isAdminBypassing()) return;
        if (event.getReason().equals(FPlayerJoinEvent.PlayerJoinReason.CREATE)) {
            FactionRosterManager.addRoster(event.getFaction());
            Roster roster = FactionRosterManager.getRoster(event.getFaction());
            roster.addPlayer(event.getfPlayer().getPlayer().getUniqueId(), event.getfPlayer().getRole());
            return;
        }
        Roster roster = FactionRosterManager.getRoster(event.getFaction());
        if (SkullRosters.isRosters() && !roster.isOnRoster(event.getfPlayer().getPlayer().getUniqueId())) {
            MessageType.NOT_ON_ROSTER_JOINER.message(event.getfPlayer().getPlayer(), event.getFaction().getTag());
            MessageType.NOT_ON_ROSTER_FACTION.factionMessage(event.getFaction(), event.getfPlayer().getName());
            event.setCancelled(true);
            return;
        }
        int add = 0;
        if (!roster.getFaciton().getFPlayerLeader().isOnline()) add++;
        if (SkullRosters.isRosters() && roster.getFaciton().getOnlinePlayers().size() + add >= Files.CONFIG.get().getInt("faction-size")) {
            MessageType.FACTION_MAX_ONLINE.message(event.getfPlayer().getPlayer(), event.getFaction().getTag());
            MessageType.FACTION_MAX_ONLINE_FACTION.factionMessage(event.getFaction(), event.getfPlayer().getName());
            event.setCancelled(true);
            return;
        }
        if (SkullRosters.isRosters() && (roster.getFaciton().getSize() + add >= Files.CONFIG.get().getInt("faction-size"))) {
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
        } else {
            roster.getFaciton().addFPlayer(event.getfPlayer());
            event.getfPlayer().setFaction(roster.getFaciton(), false);
            event.getfPlayer().setRole(roster.getRole(event.getfPlayer().getPlayer().getUniqueId()));
            MessageType.PLAYER_JOIN.factionMessage(event.getfPlayer().getFaction(), event.getfPlayer().getRolePrefix(), event.getfPlayer().getName());
        }
        // force invites code
        if (!SkullRosters.isRosters() && !Bukkit.getServer().isGracePeriod() && roster.getInvitesRemaining() > 0) {
            roster.decrementInvitesRemaining();
            MessageType.INVITE_DECREMENT.factionMessage(roster.getFaciton(), event.getfPlayer().getName(), String.valueOf(roster.getInvitesRemaining()));
        } else if (!SkullRosters.isRosters() && !Bukkit.getServer().isGracePeriod() && roster.getInvitesRemaining() <= 0) {
            event.setCancelled(true);
            MessageType.OUT_OF_INVITES.factionMessage(roster.getFaciton(), event.getfPlayer().getName());
        }
    }

    @EventHandler
    public void leave(FPlayerLeaveEvent event) {
        if (!SkullRosters.isRosters() || !Files.CONFIG.get().getBoolean("leave-kick-affect-roster-size"))
            return;
        if (event.getReason().equals(FPlayerLeaveEvent.PlayerLeaveReason.DISBAND)) return;
        if (event.getfPlayer().isAdminBypassing()) return;
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
        if (!SkullRosters.isRosters()) return;
        if (event.getfPlayer().isAdminBypassing()) return;
        Roster roster = FactionRosterManager.getRoster(event.getFaction());
        roster.updateRole(Bukkit.getOfflinePlayer(event.getfPlayer().getName()).getUniqueId(), event.getTo());
    }
}
