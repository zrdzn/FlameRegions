package io.github.enixor.minecraft.flameregions.handler;

import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.session.MoveType;
import com.sk89q.worldguard.session.Session;
import com.sk89q.worldguard.session.handler.FlagValueChangeHandler;
import com.sk89q.worldguard.session.handler.Handler;
import io.github.enixor.minecraft.flameregions.FlameRegionsPlugin;
import io.github.enixor.minecraft.flameregions.configuration.PluginConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Set;

public class RegionEnterHandler extends FlagValueChangeHandler<String> {

    public static final Factory FACTORY = new Factory();

    protected RegionEnterHandler(Session session) {
        super(session, FlameRegionsPlugin.ENTER_FLAG);
    }

    @Override
    public boolean onCrossBoundary(LocalPlayer player, Location from, Location to, ApplicableRegionSet toSet,
                                   Set<ProtectedRegion> entered, Set<ProtectedRegion> exited, MoveType moveType) {
        FlameRegionsPlugin plugin = FlameRegionsPlugin.getInstance();
        PluginConfiguration pluginConfiguration = plugin.getPluginConfiguration();

        for (ProtectedRegion protectedRegion : toSet.getRegions()) {
            String protectedRegionId = protectedRegion.getId();
            if (!protectedRegionId.startsWith(pluginConfiguration.getRegionsPrefix())) {
                continue;
            }

            Player bukkitPlayer = Bukkit.getPlayer(player.getUniqueId());
            if (bukkitPlayer == null) {
                continue;
            }

            String regionName = protectedRegion.getFlag(FlameRegionsPlugin.ENTER_FLAG);
            if (regionName == null) {
                regionName = protectedRegionId;
            } else {
                regionName = ChatColor.translateAlternateColorCodes('&', regionName);
            }

            if (plugin.getRegionRepository().addExploredRegionToPlayer(bukkitPlayer.getUniqueId(), protectedRegion)) {
                bukkitPlayer.playSound(bukkitPlayer.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);

                bukkitPlayer.sendTitle(plugin.translateToString(bukkitPlayer.getLocale(), "title.header", regionName),
                        plugin.translateToString(bukkitPlayer.getLocale(), "title.footer", regionName),
                        10, 70, 20);
            }

        }

        return true;
    }

    @Override
    protected void onInitialValue(LocalPlayer player, ApplicableRegionSet set, String value) {
    }

    @Override
    protected boolean onSetValue(LocalPlayer player, Location from, Location to, ApplicableRegionSet toSet, String currentValue, String lastValue, MoveType moveType) {
        return false;
    }

    @Override
    protected boolean onAbsentValue(LocalPlayer player, Location from, Location to, ApplicableRegionSet toSet, String lastValue, MoveType moveType) {
        return false;
    }

    public static class Factory extends Handler.Factory<RegionEnterHandler> {
        public RegionEnterHandler create(final Session session) {
            return new RegionEnterHandler(session);
        }
    }

}
