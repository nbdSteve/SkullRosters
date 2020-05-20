package gg.steve.skullwars.rosters.core;

import com.massivecraft.factions.Faction;

import java.util.List;
import java.util.UUID;

public class Roster {
    private Faction faciton;
    private int maxMembers, invitesRemaining;
    private FactionRosterFile data;
    private List<String> players;

    public Roster(Faction faction) {
        this.faciton = faction;
        this.data = new FactionRosterFile(faction.getId());
        this.maxMembers = this.data.get().getInt("max-members");
        this.invitesRemaining = this.data.get().getInt("remaining-invites");
        this.players = this.data.get().getStringList("players");
    }

    public void saveToFile() {
        this.data.get().set("max-members", this.maxMembers);
        this.data.get().set("remaining-invites", this.invitesRemaining);
        this.data.get().set("players", this.players);
        this.data.save();
    }

    public void addPlayer(UUID playerId) {
        this.players.add(String.valueOf(playerId));
    }

    public void removePlayer(UUID playerId) {
        this.players.remove(String.valueOf(playerId));
    }

    public void delete() {
        this.data.delete();
    }

    public boolean isMaxMembers() {
        return this.faciton.getSize() >= this.maxMembers;
    }

    public boolean isCreatedAfterGrace() {
        return this.data.get().getBoolean("created-after-grace");
    }

    public void setHitMaxMembers() {
        this.data.get().set("has-reached-max-members", true);
        this.data.save();
    }

    public int getSize() {
        return this.players.size();
    }

    public void decrementInvitesRemaining() {
        this.invitesRemaining--;
    }

    public boolean isOnRoster(UUID playerId) {
        return this.players.contains(String.valueOf(playerId));
    }

    public void decrementMaxMembers() {
        this.maxMembers--;
    }

    public Faction getFaciton() {
        return faciton;
    }

    public int getMaxMembers() {
        return maxMembers;
    }

    public int getInvitesRemaining() {
        return invitesRemaining;
    }

    public FactionRosterFile getData() {
        return data;
    }
}
