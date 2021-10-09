package io.github.zrdzn.minecraft.flameregions.location;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import io.github.zrdzn.minecraft.flameregions.FlameRegionsPlugin;
import io.github.zrdzn.minecraft.flameregions.travel.TravelSystem;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;
import java.util.UUID;

public class LocationMenu {

    private final FlameRegionsPlugin plugin;

    private boolean isTravelItem;

    public LocationMenu(FlameRegionsPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Parameter logic is true when locations menu are opened through NPC
     * which means it's true when it's menu for travelling and false when
     * it's menu for checking places that you've had already discovered.
     */
    public boolean show(UUID playerId, boolean logic, Object... logicParameters) {
        Player player = this.plugin.getServer().getPlayer(playerId);
        if (player == null) {
            return false;
        }

        TravelSystem travelSystem = this.plugin.getTravelSystem();

        String locale = player.getLocale();

        PaginatedGui menu = Gui.paginated()
                .title(this.plugin.translateToComponent(locale, logic ? "menu.title.npc" : "menu.title.command"))
                .rows(4)
                .create();

        menu.setDefaultClickAction(event -> event.setCancelled(true));

        menu.setItem(4, 1, ItemBuilder.from(Material.PAPER)
                .name(this.plugin.translateToComponent(locale, "menu.pagination.left.display_name"))
                .lore(this.plugin.translateToComponentList(locale, "menu.pagination.left.lore"))
                .asGuiItem(event -> menu.previous()));

        menu.setItem(4, 9, ItemBuilder.from(Material.PAPER)
                .name(this.plugin.translateToComponent(locale, "menu.pagination.right.display_name"))
                .lore(this.plugin.translateToComponentList(locale, "menu.pagination.right.lore"))
                .asGuiItem(event -> menu.next()));

        menu.setItem(4, 5, ItemBuilder.from(Material.BARRIER)
                .name(this.plugin.translateToComponent(locale, "menu.close.display_name"))
                .lore(this.plugin.translateToComponentList(locale, "menu.close.lore"))
                .asGuiItem(event -> menu.close(player)));

        RegionManager regionManager = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(player.getWorld()));
        if (regionManager == null) {
            return false;
        }

        Location npcLocation = null;
        if (logic && logicParameters.length != 0 && (logicParameters[0] instanceof NPC)) {
            Entity npcEntity = ((NPC) logicParameters[0]).getEntity();
            if (npcEntity != null) {
                npcLocation = npcEntity.getLocation();
            }
        }

        Map<String, ProtectedRegion> regions = regionManager.getRegions();

        double distance;
        double price = 0.0D;

        for (Map.Entry<String, ProtectedRegion> entry : regions.entrySet()) {
            ProtectedRegion protectedRegion = entry.getValue();
            String protectedRegionId = protectedRegion.getId();

            ItemStack item;
            String displayName;

            String displayNameFlag = protectedRegion.getFlag(FlameRegionsPlugin.ENTER_FLAG);
            displayName = displayNameFlag == null ? protectedRegionId : ChatColor.translateAlternateColorCodes('&', displayNameFlag);

            if (!protectedRegionId.startsWith(this.plugin.getPluginConfiguration().getRegionsPrefix())) {
                continue;
            }

            if (this.plugin.getRegionRepository().hasPlayerExplored(player.getUniqueId(), protectedRegion)) {
                item = new ItemStack(Material.FILLED_MAP);

                if (logic && npcLocation != null) {
                    if (travelSystem.getTravelLocation(protectedRegion) == null) {
                        continue;
                    }

                    distance = travelSystem.calculateDistanceToRegion(npcLocation, protectedRegion);
                    price = Math.round(travelSystem.calculatePrice(distance));

                    item.lore(this.plugin.translateToComponentList(locale,
                            "menu.region.explored_lore.npc", Double.toString(price), Long.toString(Math.round(distance))));

                    this.isTravelItem = true;
                } else {
                    item.lore(this.plugin.translateToComponentList(locale, "menu.region.explored_lore.command"));
                }
            } else {
                item = new ItemStack(Material.MAP);

                if (logic && npcLocation != null) {
                    if (travelSystem.getTravelLocation(protectedRegion) == null) {
                        continue;
                    }

                    item.lore(this.plugin.translateToComponentList(locale, "menu.region.not_explored_lore.npc"));

                    this.isTravelItem = false;
                } else {
                    item.lore(this.plugin.translateToComponentList(locale, "menu.region.not_explored_lore.command"));
                }
            }

            ItemMeta itemMeta = item.getItemMeta();

            itemMeta.displayName(this.plugin.translateToComponent(locale, "menu.region.display_name", displayName));
            item.setItemMeta(itemMeta);

            double finalPrice = price;

            menu.getFiller().fillBetweenPoints(1, 1, 3, 9, new GuiItem(item, event -> {
                if (logic && this.isTravelItem) {
                    travelSystem.travelPlayer(playerId, protectedRegion, finalPrice);
                }
            }));
        }

        menu.open(player);

        return true;
    }

}
