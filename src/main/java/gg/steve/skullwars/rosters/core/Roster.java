package gg.steve.skullwars.rosters.core;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.Role;

import java.util.*;

public class Roster {
    private Faction faciton;
    private int maxMembers, invitesRemaining;
    private FactionRosterFile data;
    private Map<UUID, Role> players;

    public Roster(Faction faction) {
        this.faciton = faction;
        this.data = new FactionRosterFile(faction.getId());
        this.maxMembers = this.data.get().getInt("max-members");
        this.invitesRemaining = this.data.get().getInt("remaining-invites");
        this.players = new HashMap<>();
        for (String entry : this.data.get().getStringList("players")) {
            String[] parts = entry.split(":");
            players.put(UUID.fromString(parts[0]), Role.fromString(parts[1]));
        }
    }

    public void saveToFile() {
        this.data.get().set("max-members", this.maxMembers);
        this.data.get().set("remaining-invites", this.invitesRemaining);
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
        return this.players.containsKey(playerId);
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
