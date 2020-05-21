package gg.steve.skullwars.rosters.listener;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.sun.xml.internal.ws.addressing.v200408.MemberSubmissionWsaClientTube;
import gg.steve.skullwars.rosters.core.FactionRosterManager;
import gg.steve.skullwars.rosters.core.Roster;
import gg.steve.skullwars.rosters.message.CommandDebug;
import gg.steve.skullwars.rosters.message.MessageType;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandListener implements Listener {

    @EventHandler
    public void command(PlayerCommandPreprocessEvent event) {
        // /f roster add player
        if (!event.getMessage().startsWith("/f roster")) return;
        event.setCancelled(true);
        String[] args = event.getMessage().split(" ");
        if (args.length != 4) {
            CommandDebug.INCORRECT_ARGS.message(event.getPlayer());
            return;
        }
        Roster roster = FactionRosterManager.getRoster(FPlayers.getInstance().getByPlayer(event.getPlayer()).getFaction());
        OfflinePlayer player = Bukkit.getOfflinePlayer(args[3]);
        if (!player.isOnline() && !player.hasPlayedBefore()) {
            CommandDebug.NOT_PLAYED_BEFORE.message(event.getPlayer());
            return;
        }
        FPlayer fPlayer = FPlayers.getInstance().getByOfflinePlayer(player);
        if (fPlayer.hasFaction() && !fPlayer.getFaction().equals(roster.getFaciton())) {
            CommandDebug.ALREADY_IN_FACTION.message(event.getPlayer(), player.getName(), fPlayer.getTag());
            return;
        }
        if (FactionRosterManager.getRosterForPlayer(fPlayer) != null && !FactionRosterManager.getRosterForPlayer(fPlayer).getFaciton().equals(roster.getFaciton())) {
            CommandDebug.ALREADY_ON_ROSTER.message(event.getPlayer(), player.getName());
            return;
        }
        if (!FPlayers.getInstance().getByPlayer(event.getPlayer()).equals(roster.getFaciton().getFPlayerLeader())) {
            CommandDebug.ONLY_LEADER_CAN_EDIT.message(event.getPlayer());
            return;
        }
        if (!roster.hasAddsRemaining()) {
            CommandDebug.NO_ADDS_REMAINING.message(event.getPlayer(), fPlayer.getName());
            return;
        }
        switch (args[2].toLowerCase()) {
            case "add":
                if (roster.isOnRoster(player.getUniqueId())) {
                    CommandDebug.ALREADY_ON_ROSTER.message(event.getPlayer(), player.getName());
                    return;
                }
                if (roster.getSize() >= roster.getMaxMembers()) {
                    MessageType.ROSTER_FULL.message(event.getPlayer(), player.getName());
                    return;
                }
                if (!Bukkit.isGracePeriod()) {
                    roster.decrementAddsRemaining();
                    MessageType.ROSTER_ADD_LOSE.factionMessage(roster.getFaciton(), String.valueOf(roster.getAddsRemaining()));
                }
                roster.addPlayer(player.getUniqueId(), fPlayer.getRole());
                MessageType.ROSTER_ADD.factionMessage(roster.getFaciton(), player.getName());
                return;
            case "remove":
                if (!roster.isOnRoster(player.getUniqueId())) {
                    MessageType.NOT_ON_ROSTER_REMOVE.message(event.getPlayer(), player.getName());
                    return;
                }
                if (roster.getFaciton().getFPlayers().contains(fPlayer)) {
                    roster.getFaciton().removeFPlayer(fPlayer);
                    fPlayer.resetFactionData();
                }
                roster.removePlayer(player.getUniqueId());
                if (fPlayer.isOnline()) {
                    MessageType.ROSTER_REMOVE_PLAYER.message(fPlayer.getPlayer(), roster.getFaciton().getTag(), event.getPlayer().getName());
                }
                MessageType.ROSTER_REMOVE.factionMessage(roster.getFaciton(), player.getName());
                return;
            default:
                CommandDebug.INCORRECT_ARGS.message(event.getPlayer());
        }
    }

    @EventHandler
    public void grace(PlayerCommandPreprocessEvent event) {
        if (!event.getMessage().equalsIgnoreCase("/grace off")) return;
        for (Roster roster : FactionRosterManager.getRosters().values()) {
            roster.setRosterAddsRemaining();
        }
    }
}
