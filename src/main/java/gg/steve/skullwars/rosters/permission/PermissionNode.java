package gg.steve.skullwars.rosters.permission;

import gg.steve.skullwars.rosters.managers.Files;
import org.bukkit.command.CommandSender;

public enum PermissionNode {
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
}