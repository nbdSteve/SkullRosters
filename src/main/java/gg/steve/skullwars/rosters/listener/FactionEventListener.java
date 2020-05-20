package gg.steve.skullwars.rosters.listener;

import com.massivecraft.factions.event.FPlayerJoinEvent;
import com.massivecraft.factions.event.FactionCreateEvent;
import com.massivecraft.factions.event.FactionDisbandEvent;
import gg.steve.skullwars.rosters.core.FactionRosterManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class FactionEventListener implements Listener {

    @EventHandler
    public void create(FactionCreateEvent event) {
        FactionRosterManager.addRoster(event.getFPlayer().getFaction());
    }

    @EventHandler
    public void disband(FactionDisbandEvent event) {
        FactionRosterManager.disband(event.getFaction());
    }

    @EventHandler
    public void invite(FPlayerJoinEvent event) {

    }
}
