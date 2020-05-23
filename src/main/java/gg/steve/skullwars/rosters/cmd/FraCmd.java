package gg.steve.skullwars.rosters.cmd;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import gg.steve.skullwars.rosters.Rosters;
import gg.steve.skullwars.rosters.core.FactionRosterManager;
import gg.steve.skullwars.rosters.core.Roster;
import gg.steve.skullwars.rosters.message.CommandDebug;
import gg.steve.skullwars.rosters.message.MessageType;
import gg.steve.skullwars.rosters.permission.PermissionNode;
import gg.steve.skullwars.rosters.utils.LogUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FraCmd implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player)) {
            LogUtil.warning("Only players can use admin roster commands");
            return true;
        }
        Player player = (Player) sender;
        if (args.length == 0 || args[0].equalsIgnoreCase("h") || args[0].equalsIgnoreCase("help")) {
            if (!PermissionNode.HELP.hasPermission(player)) {
                CommandDebug.PERMISSION.message(player, PermissionNode.HELP.get());
                return true;
            }
            MessageType.HELP.message(player);
            return true;
        } else if (args[0].equalsIgnoreCase("r") || args[0].equalsIgnoreCase("reload")) {
            if (!PermissionNode.RELOAD.hasPermission(player)) {
                CommandDebug.PERMISSION.message(player, PermissionNode.RELOAD.get());
                return true;
            }
            Bukkit.getPluginManager().disablePlugin(Rosters.get());
            Bukkit.getPluginManager().enablePlugin(Rosters.get());
            MessageType.RELOAD.message(player);
        }
        if (args.length != 2) {
            CommandDebug.INCORRECT_ARGS.message(player);
            return true;
        }
        Faction faction;
        try {
            faction = Factions.getInstance().getByTag(args[0]);
        } catch (Exception e) {
            CommandDebug.FACTION_NOT_EXIST.message(player);
            return true;
        }
        Roster roster = FactionRosterManager.getRoster(faction);
        switch (args[1].toLowerCase()) {
            case "add":
                if (!PermissionNode.ADD.hasPermission(player)) {
                    CommandDebug.PERMISSION.message(player, PermissionNode.ADD.get());
                    return true;
                }
                roster.incrementAddsRemaining();
                MessageType.ADD_ADMIN.message(player, faction.getTag());
                MessageType.ADD_FACTION.factionMessage(faction, String.valueOf(roster.getAddsRemaining()));
                return true;
            case "member":
                if (!PermissionNode.MEMBER.hasPermission(player)) {
                    CommandDebug.PERMISSION.message(player, PermissionNode.MEMBER.get());
                    return true;
                }
                roster.incrementMaxMembers();
                MessageType.MEMBER_ADMIN.message(player, faction.getTag());
                MessageType.MEMBER_FACTION.factionMessage(faction, String.valueOf(roster.getMaxMembers()));
                return true;
            case "invite":
                if (!PermissionNode.INVITE.hasPermission(player)) {
                    CommandDebug.PERMISSION.message(player, PermissionNode.INVITE.get());
                    return true;
                }
                roster.incrementInvitesRemaining();
                MessageType.INVITE_ADMIN.message(player, faction.getTag());
                MessageType.INVITE_FACTION.factionMessage(faction, String.valueOf(roster.getInvitesRemaining()));
                return true;
        }
        CommandDebug.INVALID_COMMAND.message(player);
        return true;
    }
}
