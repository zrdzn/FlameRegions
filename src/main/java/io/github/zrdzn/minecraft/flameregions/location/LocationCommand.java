package io.github.zrdzn.minecraft.flameregions.location;

import io.github.zrdzn.minecraft.flameregions.FlameRegionsPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public record LocationCommand(FlameRegionsPlugin plugin) implements CommandExecutor {

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
