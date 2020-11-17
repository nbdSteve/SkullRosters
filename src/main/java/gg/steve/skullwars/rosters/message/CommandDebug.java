package gg.steve.skullwars.rosters.message;

import gg.steve.skullwars.rosters.managers.Files;
import gg.steve.skullwars.rosters.utils.ColorUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public enum CommandDebug {
    //
    NOT_PLAYED_BEFORE("not-played-before"),
    ALREADY_IN_FACTION("already-in-faction", "{player}", "{faction}"),
    ALREADY_ON_ROSTER("already-on-roster", "{player}"),
    ONLY_LEADER_CAN_EDIT("only-leader-can-edit"),
    NO_ADDS_REMAINING("no-adds-remaining", "{player}"),
    INCORRECT_ARGS("incorrect-args"),
    INVALID_COMMAND("invalid-command"),
    ROSTERS_NOT_ENABLED("rosters-not-enabled"),
    ROSTER_DOES_NOT_EXIST("roster-does-not-exist"),
    NO_ROSTER("no-roster"),
    FACTION_NOT_EXIST("faction-not-exist"),
    PERMISSION("permission", "{node}"),
    ROSTER_LEAVE_NOT_ON_ROSTER("roster-leave-not-on-roster"),
    ROSTER_LEAVE_IN_FACTION("roster-leave-in-faction"),
    // console debug
    ONLY_PLAYERS_ACCESSIBLE("only-player-accessible");

    private final String path;
    private List<String> placeholders;

    CommandDebug(String path, String... placeholders) {
        this.path = path;
        this.placeholders = Arrays.asList(placeholders);
    }

    public void message(Player receiver, String... replacements) {
        List<String> data = Arrays.asList(replacements);
        for (String line : Files.DEBUG.get().getStringList(this.path)) {
            for (int i = 0; i < this.placeholders.size(); i++) {
                line = line.replace(this.placeholders.get(i), data.get(i));
            }
            receiver.sendRawMessage(ColorUtil.colorize(line));
        }
    }

    public void message(CommandSender receiver, String... replacements) {
        List<String> data = Arrays.asList(replacements);
        for (String line : Files.DEBUG.get().getStringList(this.path)) {
            for (int i = 0; i < this.placeholders.size(); i++) {
                line = line.replace(this.placeholders.get(i), data.get(i));
            }
//            receiver.(ColorUtil.colorize(line));
        }
    }
}
