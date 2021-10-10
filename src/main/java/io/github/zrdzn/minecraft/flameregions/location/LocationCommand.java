package io.github.zrdzn.minecraft.flameregions.location;

import io.github.zrdzn.minecraft.flameregions.message.MessageService;
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

        if (this.menu.show(player.getUniqueId(), false)) {
            return true;
        }

        this.logger.warn("Could not show location-menu to {}.", player.getName());

        this.service.sendMessage(player.getUniqueId(), "menu.open_error");

        return true;
    }

}
