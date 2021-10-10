package io.github.zrdzn.minecraft.flameregions.location;

import io.github.zrdzn.minecraft.flameregions.FlameRegionsPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

public class LocationCommand implements CommandExecutor {

    private final Logger logger;
    private final LocationMenu menu;
    private final MessageService service;

    public LocationCommand(Logger logger, LocationMenu menu, MessageService service) {
        this.logger = logger;
        this.menu = menu;
        this.service = service;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            return true;
        }

        if (this.plugin.getMenu().show(player.getUniqueId(), false)) {
            return true;
        }

        this.plugin.getLogger().warning("Could not show location-menu to " + player.getName());
        player.sendMessage(this.plugin.translateToComponent(player.getLocale(), "menu.open_error"));

        return true;
    }

}
