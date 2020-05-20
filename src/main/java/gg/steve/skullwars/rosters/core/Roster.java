package gg.steve.skullwars.rosters.core;

import com.massivecraft.factions.Faction;

public class Roster {
    private Faction faciton;
    private int maxMembers, invitesRemaining;
    private FactionRosterFile data;

    public Roster(Faction faction) {
        this.faciton = faction;
        this.data = new FactionRosterFile(faction.getId());
        this.maxMembers = this.data.get().getInt("max-members");
        this.invitesRemaining = this.data.get().getInt("invites-remaining");
    }

    public void saveToFile() {
        this.data.get().set("max-members", this.maxMembers);
        this.data.get().set("invites-remaining", this.invitesRemaining);
        this.data.save();
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

    public void decrementInvitesRemaining() {
        this.invitesRemaining--;
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
