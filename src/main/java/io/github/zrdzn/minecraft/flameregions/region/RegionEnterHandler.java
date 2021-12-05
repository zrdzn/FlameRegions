package io.github.zrdzn.minecraft.flameregions.region;

import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.session.MoveType;
import com.sk89q.worldguard.session.Session;
import com.sk89q.worldguard.session.handler.FlagValueChangeHandler;
import com.sk89q.worldguard.session.handler.Handler;
import io.github.zrdzn.minecraft.flameregions.FlameRegionsPlugin;
import io.github.zrdzn.minecraft.flameregions.configuration.PluginConfiguration;
import io.github.zrdzn.minecraft.flameregions.message.MessageService;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Locale;
import java.util.Set;

public class RegionEnterHandler extends FlagValueChangeHandler<String> {

    private PluginConfiguration configuration;
    private ExploredRegionService regionService;
    private MessageService messageService;

    protected RegionEnterHandler(Session session) {
        super(session, FlameRegionsPlugin.ENTER_FLAG);
    }

    @Override
    public boolean onCrossBoundary(LocalPlayer player, Location from, Location to, ApplicableRegionSet toSet,
                                   Set<ProtectedRegion> entered, Set<ProtectedRegion> exited, MoveType moveType) {
        for (ProtectedRegion protectedRegion : toSet.getRegions()) {
            String protectedRegionId = protectedRegion.getId();
            if (!protectedRegionId.startsWith(this.configuration.getRegionsPrefix())) {
                continue;
            }

            Player bukkitPlayer = Bukkit.getPlayer(player.getUniqueId());
            if (bukkitPlayer == null) {
                continue;
            }

            StringBuilder regionName = new StringBuilder();
            regionName.append(protectedRegion.getFlag(FlameRegionsPlugin.ENTER_FLAG));

            if (regionName.isEmpty()) {
                regionName.append(protectedRegionId);
            } else {
                regionName.append(ChatColor.translateAlternateColorCodes('&', String.valueOf(regionName)));
            }

            this.regionService.addRegion(bukkitPlayer.getUniqueId(), protectedRegionId).thenAcceptAsync(added -> {
                if (added) {
                    bukkitPlayer.playSound(bukkitPlayer.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);

                    Locale locale = bukkitPlayer.locale();

                    bukkitPlayer.showTitle(Title.title(this.messageService.getComponent(locale, "title.header", regionName),
                        this.messageService.getComponent(locale, "title.footer", regionName)));
                }
            });
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

        private final PluginConfiguration configuration;
        private final ExploredRegionService regionService;
        private final MessageService messageService;

        public Factory(PluginConfiguration configuration, ExploredRegionService regionService, MessageService messageService) {
            this.configuration = configuration;
            this.regionService = regionService;
            this.messageService = messageService;
        }

        public RegionEnterHandler create(Session session) {
            RegionEnterHandler regionEnterHandler = new RegionEnterHandler(session);

            regionEnterHandler.configuration = this.configuration;
            regionEnterHandler.regionService = this.regionService;
            regionEnterHandler.messageService = this.messageService;

            return regionEnterHandler;
        }

    }

}
