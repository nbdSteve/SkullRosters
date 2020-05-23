package gg.steve.skullwars.rosters.message;

import com.massivecraft.factions.Faction;
import gg.steve.skullwars.rosters.managers.Files;
import gg.steve.skullwars.rosters.utils.ColorUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public enum MessageType {
    PLAYER_REMOVE("player-remove", "{offline}", "{player}"),
    NOT_ON_ROSTER_JOINER("not-on-roster-joiner", "{faction}"),
    NOT_ON_ROSTER_FACTION("not-on-roster-faction", "{player}"),
    ROSTER_ADD("roster-add", "{player}"),
    ROSTER_REMOVE("roster-remove", "{player}"),
    NOT_ON_ROSTER_REMOVE("not-on-roster-remove", "{player}"),
    ROSTER_FULL("roster-full", "{player}", "{size}"),
    ROSTER_REMOVE_PLAYER("roster-remove-player", "{faction}", "{player}"),
    FACTION_MAX_ONLINE("faction-max-online", "{faction}"),
    FACTION_MAX_ONLINE_FACTION("faction-max-online-faction", "{player}"),
    ROSTER_KICK("roster-kick", "{player}", "{size}"),
    ROSTER_ADD_LOSE("roster-add-lose", "{amount}"),
    INVITE_DECREMENT("invites-decremented", "{player}", "{amount}"),
    OUT_OF_INVITES("out-of-invites", "{player}"),
    ROSTER_LEAVE("roster-leave", "{player}"),
    // admin cmd
    ADD_ADMIN("add-admin", "{faction}"),
    MEMBER_ADMIN("member-admin", "{faction}"),
    INVITE_ADMIN("invite-admin", "{faction}"),
    ADD_FACTION("add-faction", "{amount}"),
    MEMBER_FACTION("member-faction", "{amount}"),
    INVITE_FACTION("invite-faction", "{amount}"),
    // misc
    RELOAD("reload"),
    HELP("help");

    private String path;
    private List<String> placeholders;

    MessageType(String path, String... placeholders) {
        this.path = path;
        this.placeholders = Arrays.asList(placeholders);
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void message(Player receiver, String... replacements) {
        List<String> data = Arrays.asList(replacements);
        for (String line : Files.MESSAGES.get().getStringList(this.path)) {
            for (int i = 0; i < this.placeholders.size(); i++) {
                line = line.replace(this.placeholders.get(i), data.get(i));
            }
            receiver.sendRawMessage(ColorUtil.colorize(line));
        }
    }

    public void message(CommandSender receiver, String... replacements) {
        List<String> data = Arrays.asList(replacements);
        for (String line : Files.MESSAGES.get().getStringList(this.path)) {
            for (int i = 0; i < this.placeholders.size(); i++) {
                line = line.replace(this.placeholders.get(i), data.get(i));
            }
//            receiver.sendRawMessage(ColorUtil.colorize(line));
        }
    }

    public void factionMessage(Faction faction, String... replacements) {
        List<String> data = Arrays.asList(replacements);
        for (Player receiver : faction.getOnlinePlayers()) {
            for (String line : Files.MESSAGES.get().getStringList(this.path)) {
                for (int i = 0; i < this.placeholders.size(); i++) {
                    line = line.replace(this.placeholders.get(i), data.get(i));
                }
                receiver.sendRawMessage(ColorUtil.colorize(line));
            }
        }
    }

    public static void doMessage(Player receiver, List<String> lines) {
        for (String line : lines) {
            receiver.sendRawMessage(ColorUtil.colorize(line));
        }
    }
}