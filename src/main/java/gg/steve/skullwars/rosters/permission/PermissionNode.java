package gg.steve.skullwars.rosters.permission;

import gg.steve.elemental.gangs.core.Gang;
import gg.steve.elemental.gangs.managers.Files;
import gg.steve.elemental.gangs.role.Role;
import org.bukkit.command.CommandSender;

public enum PermissionNode {
    // gang permission nodes
    CREATE("player.create"),
    DISBAND("player.disband"),
    INVITE("player.invite"),
    ACCEPT("player.accept"),
    KICK("player.kick"),
    LEAVE("player.leave"),
    PROMOTE("player.promote"),
    DEMOTE("player.demote"),
    WHO("player.who"),
    CHAT_GANG("player.chat-gang"),
    CHAT_PUBLIC("player.chat-public"),
    TOP("player.top"),
    DEPOSIT("player.deposit"),
    TAG("player.tag"),
    PERMS("player.perms"),
    VIEW("player.view"),
    REJECT("player.reject"),
    // other perms
    RELOAD("command.reload"),
    HELP("command.help");

    private String path;

    PermissionNode(String path) {
        this.path = path;
    }

    public String get() {
        return Files.PERMISSIONS.get().getString(this.path);
    }

    public boolean hasPermission(CommandSender sender) {
        return sender.hasPermission(get());
    }

    public boolean isEditable() {
        return !Files.PERMS_PAGE_GUI.get().getStringList("uneditable-nodes").contains(toString());
    }

    public boolean isEnabled(Gang gang, Role role) {
        return gang.roleHasPermission(role, this);
    }

    public void onClick(Gang gang, Role role) {
        if (!isEditable()) return;
        if (gang.roleHasPermission(role, this)) {
            gang.removePermission(role, this);
        } else {
            gang.addPermission(role, this);
        }
        gang.refreshPermissionGui(role);
    }
}