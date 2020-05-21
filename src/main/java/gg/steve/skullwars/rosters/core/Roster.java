package gg.steve.skullwars.rosters.core;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.Role;

import java.util.*;

public class Roster {
    private Faction faciton;
    private int maxMembers, invitesRemaining, addsRemaining;
    private FactionRosterFile data;
    private Map<UUID, Role> players;

    public Roster(Faction faction) {
        this.faciton = faction;
        this.data = new FactionRosterFile(faction.getId());
        this.maxMembers = this.data.get().getInt("max-members");
        this.invitesRemaining = this.data.get().getInt("remaining-invites");
        this.addsRemaining = this.data.get().getInt("adds-remaining");
        this.players = new HashMap<>();
        for (String entry : this.data.get().getStringList("players")) {
            String[] parts = entry.split(":");
            players.put(UUID.fromString(parts[0]), Role.fromString(parts[1]));
        }
    }

    public void saveToFile() {
        this.data.get().set("max-members", this.maxMembers);
        this.data.get().set("remaining-invites", this.invitesRemaining);
        this.data.get().set("adds-remaining", this.addsRemaining);
        List<String> playerList = new ArrayList<>();
        for (UUID playerId : this.players.keySet()) {
            playerList.add(playerId + ":" + this.players.get(playerId).name());
        }
        this.data.get().set("players", playerList);
        this.data.save();
    }

    public void addPlayer(UUID playerId, Role role) {
        this.players.put(playerId, role);
    }

    public void removePlayer(UUID playerId) {
        this.players.remove(playerId);
    }

    public void updateRole(UUID playerId, Role newRole) {
        this.players.put(playerId, newRole);
    }

    public Role getRole(UUID playerId) {
        return this.players.get(playerId);
    }

    public void delete() {
        this.data.delete();
    }

    public boolean isMaxMembers() {
        return this.faciton.getSize() >= this.maxMembers;
    }

    public void setRosterAddsRemaining() {
        this.data.get().set("adds-remaining", this.maxMembers - this.players.size());
        this.data.save();
    }

    public int getSize() {
        return this.players.size();
    }

    public void decrementInvitesRemaining() {
        this.invitesRemaining--;
    }

    public void decrementAddsRemaining() {
        this.addsRemaining--;
    }

    public void decrementMaxMembers() {
        this.maxMembers--;
    }

    public boolean hasAddsRemaining() {
        return this.addsRemaining > 0;
    }

    public boolean isOnRoster(UUID playerId) {
        return this.players.containsKey(playerId);
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

    public int getAddsRemaining() {
        return addsRemaining;
    }
}
