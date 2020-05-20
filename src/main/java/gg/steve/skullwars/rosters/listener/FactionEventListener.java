package gg.steve.skullwars.rosters.listener;

import com.massivecraft.factions.event.FPlayerJoinEvent;
import com.massivecraft.factions.event.FPlayerLeaveEvent;
import com.massivecraft.factions.event.FactionDisbandEvent;
import gg.steve.skullwars.rosters.core.FactionRosterManager;
import gg.steve.skullwars.rosters.core.Roster;
import gg.steve.skullwars.rosters.message.MessageType;
import gg.steve.skullwars.rosters.utils.LogUtil;
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
            FactionRosterManager.getRoster(event.getFaction()).addPlayer(event.getfPlayer().getPlayer().getUniqueId());
            return;
        }
//        if (Bukkit.getServer().isGracePeriod()) return;
        Roster roster = FactionRosterManager.getRoster(event.getFaction());
        if (!roster.isOnRoster(event.getfPlayer().getPlayer().getUniqueId())) {
            MessageType.NOT_ON_ROSTER_JOINER.message(event.getfPlayer().getPlayer(), event.getFaction().getTag());
            MessageType.NOT_ON_ROSTER_FACTION.factionMessage(event.getFaction(), event.getfPlayer().getName());
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void leave(FPlayerLeaveEvent event) {
        Roster roster = FactionRosterManager.getRoster(event.getFaction());
        roster.removePlayer(event.getfPlayer().getPlayer().getUniqueId());
        MessageType.ROSTER_REMOVE.factionMessage(roster.getFaciton(), event.getfPlayer().getName());
    }
}
