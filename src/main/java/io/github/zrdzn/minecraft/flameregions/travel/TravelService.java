package io.github.zrdzn.minecraft.flameregions.travel;

import com.earth2me.essentials.Trade;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import io.github.zrdzn.minecraft.flameregions.FlameRegionsPlugin;
import io.github.zrdzn.minecraft.flameregions.travel.configuration.TravelConfiguration;
import net.ess3.api.IEssentials;
import net.ess3.api.IUser;
import org.bukkit.Location;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.slf4j.Logger;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class TravelService {

    private final Logger logger;
    private final TravelConfiguration travelConfiguration;
    private final IEssentials essentialsApi;

    public TravelService(Logger logger, TravelConfiguration travelConfiguration, IEssentials essentialsApi) {
        this.logger = logger;
        this.travelConfiguration = travelConfiguration;
        this.essentialsApi = essentialsApi;

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

    // TODO Use Vault API and custom teleports if Essentials is disabled in config or not found on the server.
    public void travelPlayer(UUID playerId, ProtectedRegion protectedRegion, double price) {
        Optional<Location> location = this.getTravelLocation(protectedRegion);
        if (location.isEmpty()) {
            this.logger.error("Travel vector is null.");
            return;
        }

        IUser user = this.essentialsApi.getUser(playerId);
        BigDecimal tradePrice = new BigDecimal(price, MathContext.DECIMAL64);
        Trade trade = new Trade(tradePrice, this.essentialsApi);
        CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();

        user.getAsyncTeleport().teleportPlayer(user, location.get(), trade, PlayerTeleportEvent.TeleportCause.PLUGIN, completableFuture);
    }

}
