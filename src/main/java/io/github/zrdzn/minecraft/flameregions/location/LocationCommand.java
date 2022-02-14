/*
 * Copyright (c) 2022 zrdzn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
