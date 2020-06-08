package gg.steve.skullwars.rosters.gui;

import gg.steve.skullwars.rosters.core.Roster;
import gg.steve.skullwars.rosters.managers.Files;
import gg.steve.skullwars.rosters.utils.ColorUtil;
import gg.steve.skullwars.rosters.utils.GuiItemUtil;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class RosterGui extends AbstractGui {
    private ConfigurationSection section;
    private Roster roster;

    /**
     * Constructor the create a new Gui
     *
     * @param section
     */
    public RosterGui(ConfigurationSection section, Roster roster) {
        super(section, section.getString("type"), section.getInt("size"), roster.getFaciton().getTag());
        this.section = section;
        this.roster = roster;
        for (int i = 0; i < section.getInt("size"); i++) {
            setItemInSlot(i, getFillerGlass((byte) section.getInt("filler-glass.data")), player -> {});
        }
        refresh();
    }

    public void refresh() {
        for (String entry : section.getKeys(false)) {
            try {
                Integer.parseInt(entry);
            } catch (Exception e) {
                continue;
            }
            switch (section.getString(entry + ".player").toLowerCase()) {
                case "close":
                    setItemInSlot(section.getInt(entry + ".slot"), GuiItemUtil.createItem(section, entry), HumanEntity::closeInventory);
                    break;
                default:
                    setItemInSlot(section.getInt(entry + ".slot"), GuiItemUtil.createRosterItem(section, entry, roster), player -> {});
            }
        }
    }

    public ItemStack getFillerGlass(byte data) {
        ItemStack item = new ItemStack(Material.valueOf("STAINED_GLASS_PANE"), 1, data);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ColorUtil.colorize(Files.CONFIG.get().getString("filler-glass-name")));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
        return item;
    }
}