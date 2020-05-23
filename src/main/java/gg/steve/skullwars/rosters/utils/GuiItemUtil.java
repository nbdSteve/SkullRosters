package gg.steve.skullwars.rosters.utils;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import gg.steve.skullwars.rosters.core.Roster;
import me.arcaniax.hdb.api.HeadDatabaseAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GuiItemUtil {

    public static ItemStack createItem(ConfigurationSection section, String entry) {
        ItemBuilderUtil builder;
        if (section.getString(entry + ".material").startsWith("hdb")) {
            String[] parts = section.getString(entry + ".material").split("-");
            try {
                builder = new ItemBuilderUtil(new HeadDatabaseAPI().getItemHead(parts[1]));
            } catch (NullPointerException e) {
                builder = new ItemBuilderUtil(new ItemStack(Material.valueOf("SKULL_ITEM")));
            }
        } else {
            builder = new ItemBuilderUtil(section.getString(entry + ".material"), section.getString(entry + ".data"));
        }
        builder.addName(section.getString(entry + ".name"));
        builder.addLore(section.getStringList(entry + ".lore"));
        builder.addEnchantments(section.getStringList(entry + ".enchantments"));
        builder.addItemFlags(section.getStringList(entry + ".item-flags"));
        return builder.getItem();
    }

    public static ItemStack createRosterItem(ConfigurationSection section, String entry, Roster roster) {
        List<UUID> players = new ArrayList<>(roster.getPlayers().keySet());
        OfflinePlayer player;
        try {
            player = Bukkit.getOfflinePlayer(players.get(section.getInt(entry + ".player")));
        } catch (Exception e) {
            return new ItemStack(Material.BARRIER);
        }
        ItemBuilderUtil builder = new ItemBuilderUtil(section.getString("roster-item.material"), section.getString("roster-item.data"));
        if (builder.getMaterial().toString().toLowerCase().contains("skull_item")) {
            SkullMeta meta = (SkullMeta) builder.getItemMeta();
            meta.setOwner(player.getName());
            builder.setItemMeta(meta);
        }
        builder.addName(section.getString("roster-item.name").replace("{player}", player.getName()));
        builder.setLorePlaceholders("{role}", "{adds-remaining}", "{roster-size}");
        builder.addLore(section.getStringList("roster-item.lore"),
                roster.getPlayers().get(player.getUniqueId()).name(),
                String.valueOf(roster.getAddsRemaining()),
                String.valueOf(roster.getMaxMembers()));
        builder.addEnchantments(section.getStringList("roster-item.enchantments"));
        builder.addItemFlags(section.getStringList("roster-item.item-flags"));
        players.clear();
        return builder.getItem();
    }
}
