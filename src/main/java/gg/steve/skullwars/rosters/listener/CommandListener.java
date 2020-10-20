package gg.steve.skullwars.rosters.listener;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.struct.Role;
import gg.steve.skullwars.rosters.SkullRosters;
import gg.steve.skullwars.rosters.core.FactionRosterManager;
import gg.steve.skullwars.rosters.core.Roster;
import gg.steve.skullwars.rosters.managers.Files;
import gg.steve.skullwars.rosters.message.CommandDebug;
import gg.steve.skullwars.rosters.message.MessageType;
import gg.steve.skullwars.rosters.utils.LogUtil;
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
        if (!SkullRosters.isRosters()) {
            CommandDebug.ROSTERS_NOT_ENABLED.message(event.getPlayer());
            return;
        }
        String[] args = event.getMessage().split(" ");
        FPlayer sender = FPlayers.getInstance().getByPlayer(event.getPlayer());
        if (sender.getFaction().isWilderness()) {
            if (event.getMessage().equalsIgnoreCase("/f roster")) {
                if (FactionRosterManager.getRosterForPlayer(sender) == null) {
                    CommandDebug.NO_ROSTER.message(event.getPlayer());
                    return;
                } else {
                    FactionRosterManager.getRosterForPlayer(sender).openGui(event.getPlayer());
                    return;
                }
            } else if (args[2].equalsIgnoreCase("leave")) {
                if (FactionRosterManager.getRosterForPlayer(sender) == null) {
                    CommandDebug.ROSTER_LEAVE_NOT_ON_ROSTER.message(event.getPlayer());
                    return;
                } else {
                    Roster roster = FactionRosterManager.getRosterForPlayer(sender);
                    roster.removePlayer(sender.getPlayer().getUniqueId());
                    MessageType.ROSTER_LEAVE.message(event.getPlayer(), event.getPlayer().getName());
                    MessageType.ROSTER_LEAVE.factionMessage(roster.getFaciton(), sender.getName());
                    return;
                }
            }
        } else if (args.length == 3 && args[2].equalsIgnoreCase("leave")) {
            Roster roster = FactionRosterManager.getRosterForPlayer(sender);
            roster.removePlayer(sender.getPlayer().getUniqueId());
            CommandDebug.ROSTER_LEAVE_IN_FACTION.message(event.getPlayer());
            return;
        }
        Roster roster = FactionRosterManager.getRoster(FPlayers.getInstance().getByPlayer(event.getPlayer()).getFaction());
        if (event.getMessage().equalsIgnoreCase("/f roster")) {
            roster.openGui(event.getPlayer());
            return;
        }
        if (args.length != 4) {
            CommandDebug.INCORRECT_ARGS.message(event.getPlayer());
            return;
        }
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
        if (FactionRosterManager.getRosterForPlayer(player) != null && !FactionRosterManager.getRosterForPlayer(player).getFaciton().equals(roster.getFaciton())) {
            CommandDebug.ALREADY_ON_ROSTER.message(event.getPlayer(), player.getName());
            return;
        }
        if (!FPlayers.getInstance().getByPlayer(event.getPlayer()).equals(roster.getFaciton().getFPlayerLeader())) {
            CommandDebug.ONLY_LEADER_CAN_EDIT.message(event.getPlayer());
            return;
        }
        switch (args[2].toLowerCase()) {
            case "add":
                if (!roster.hasAddsRemaining()) {
                    CommandDebug.NO_ADDS_REMAINING.message(event.getPlayer(), fPlayer.getName());
                    return;
                }
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
    public void showRoster(PlayerCommandPreprocessEvent event) {
        // /f showroster faciton/player
        if (!event.getMessage().contains("/f showroster")) return;
        event.setCancelled(true);
        if (!SkullRosters.isRosters()) {
            CommandDebug.ROSTERS_NOT_ENABLED.message(event.getPlayer());
            return;
        }
        if (event.getMessage().equalsIgnoreCase("/f showroster")) {
            Roster roster = FactionRosterManager.getRoster(FPlayers.getInstance().getByPlayer(event.getPlayer()).getFaction());
            roster.openGui(event.getPlayer());
            return;
        }
        String[] args = event.getMessage().split(" ");
        if (args.length != 3) {
            CommandDebug.INCORRECT_ARGS.message(event.getPlayer());
            return;
        }
        if (args[2].equalsIgnoreCase("wilderness") || args[2].equalsIgnoreCase("warzone") || args[2].equalsIgnoreCase("safezone")) {
            CommandDebug.NO_ROSTER.message(event.getPlayer());
            return;
        }
        Faction faction = null;
        OfflinePlayer player;
        try {
            faction = Factions.getInstance().getByTag(args[2]);
        } catch (Exception e) {

        }
        if (faction == null) {
            player = Bukkit.getOfflinePlayer(args[2]);
            if (!player.hasPlayedBefore()) {
                CommandDebug.ROSTER_DOES_NOT_EXIST.message(event.getPlayer());
                return;
            }
        } else {
            Roster roster = FactionRosterManager.getRoster(faction);
            roster.openGui(event.getPlayer());
            return;
        }
        Roster roster = FactionRosterManager.getRosterForPlayer(player);
        if (roster == null) {
            CommandDebug.NO_ROSTER.message(event.getPlayer());
            return;
        }
        roster.openGui(event.getPlayer());
    }

    @EventHandler
    public void grace(PlayerCommandPreprocessEvent event) {
        if (!event.getMessage().equalsIgnoreCase("/grace off")) return;
        if (!SkullRosters.isRosters()) return;
        for (Roster roster : FactionRosterManager.getRosters().values()) {
            roster.setRosterAddsRemaining();
        }
    }

    @EventHandler
    public void join(PlayerCommandPreprocessEvent event) {
        if (!event.getMessage().contains("/f join ")) return;
        if (!SkullRosters.isRosters()) return;
        if (Files.CONFIG.get().getBoolean("roster-full-kick")) return;
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(event.getPlayer());
        if (fPlayer.hasFaction()) return;
        Roster roster;
        if ((roster = FactionRosterManager.getRosterForPlayer(fPlayer)) == null) return;
        if (roster.getFaciton().getSize() < Files.CONFIG.get().getInt("faction-size")) {
            event.setCancelled(true);
            roster.getFaciton().addFPlayer(fPlayer);
            fPlayer.setRole(roster.getRole(fPlayer.getPlayer().getUniqueId()));
            fPlayer.setFaction(roster.getFaciton(), false);
            MessageType.PLAYER_JOIN.factionMessage(fPlayer.getFaction(), fPlayer.getRolePrefix(), fPlayer.getName());
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
            event.setCancelled(true);
            MessageType.FACTION_FULL.message(event.getPlayer());
            return;
        }
        event.setCancelled(true);
        roster.getFaciton().addFPlayer(fPlayer);
        fPlayer.setFaction(roster.getFaciton(), false);
        fPlayer.setRole(roster.getRole(fPlayer.getPlayer().getUniqueId()));
        MessageType.PLAYER_JOIN.factionMessage(fPlayer.getFaction(), fPlayer.getRolePrefix(), fPlayer.getName());
        MessageType.PLAYER_REMOVE.factionMessage(roster.getFaciton(), off.getName(), fPlayer.getName());
    }
}
