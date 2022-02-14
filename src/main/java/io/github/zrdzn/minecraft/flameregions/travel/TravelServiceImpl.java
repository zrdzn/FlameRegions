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
package io.github.zrdzn.minecraft.flameregions.travel;

import com.earth2me.essentials.Trade;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import io.github.zrdzn.minecraft.flameregions.FlameRegionsPlugin;
import io.github.zrdzn.minecraft.flameregions.travel.configuration.TravelConfiguration;
import net.ess3.api.IEssentials;
import net.ess3.api.IUser;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.slf4j.Logger;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class TravelServiceImpl implements TravelService {

    private final Logger logger;
    private final TravelConfiguration travelConfiguration;
    private final Object teleportProvider;

    public TravelServiceImpl(Logger logger, TravelConfiguration travelConfiguration, Object teleportProvider) {
        this.logger = logger;
        this.travelConfiguration = travelConfiguration;
        this.teleportProvider = teleportProvider;
    }

    public Optional<Location> getTravelLocation(ProtectedRegion protectedRegion) {
        com.sk89q.worldedit.util.Location location = protectedRegion.getFlag(FlameRegionsPlugin.TRAVEL_LOCATION_FLAG);
        return location == null ? Optional.empty() : Optional.of(BukkitAdapter.adapt(location));
    }

    // TODO Add no-charge option for players with flameregions.charge.bypass permission.
    public double calculatePrice(double distance) {
        double multiplier = this.travelConfiguration.isPriceMultiplierEnabled() ? this.travelConfiguration.getPriceMultiplier() : 1.0D;
        return distance * Math.pow(multiplier, 2) * 3 / 10;
    }

    public double calculateDistanceToRegion(Location current, ProtectedRegion protectedRegion) {
        Optional<Location> destination = this.getTravelLocation(protectedRegion);
        if (destination.isEmpty()) {
            this.logger.warn("Destination location is null for {}.", protectedRegion.getId());
            return 0.0D;
        }

        return current.distance(destination.get());
    }

    public void travelPlayer(UUID playerId, ProtectedRegion protectedRegion, double price) {
        Optional<Location> location = this.getTravelLocation(protectedRegion);
        if (location.isEmpty()) {
            this.logger.error("Travel vector is null for {}.", protectedRegion.getId());
            return;
        }

        Player player = Bukkit.getPlayer(playerId);
        if (player == null) {
            this.logger.error("Player is either offline or does not exist {}.", playerId);
            return;
        }

        if (this.teleportProvider instanceof IEssentials essentials) {
            IUser user = essentials.getUser(playerId);
            BigDecimal tradePrice = new BigDecimal(price, MathContext.DECIMAL64);
            Trade trade = new Trade(tradePrice, essentials);
            CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();

            user.getAsyncTeleport().teleportPlayer(user, location.get(), trade, PlayerTeleportEvent.TeleportCause.PLUGIN, completableFuture);
            return;
        } else if (this.teleportProvider instanceof Economy vault) {
            vault.withdrawPlayer(player, price);
        }

        player.teleport(location.get(), PlayerTeleportEvent.TeleportCause.PLUGIN);
    }

}
