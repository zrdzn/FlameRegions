package io.github.zrdzn.minecraft.flameregions.travel;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Location;

import java.util.Optional;
import java.util.UUID;

public interface TravelService {

    /**
     * Gets value for location flag of the specified
     * WorldGuard's protected region.
     *
     * @param protectedRegion the protected region
     *
     * @return value of the location flag
     */
    Optional<Location> getTravelLocation(ProtectedRegion protectedRegion);

    /**
     * Calculates the price based on configuration's
     * price multiplier and distance of the target
     * region.
     *
     * @param distance the distance to the target region
     *
     * @return calculated price for distance
     */
    double calculatePrice(double distance);

    /**
     * Calculates distance from specified location to
     * target region location.
     *
     * @param current the current location
     * @param protectedRegion the protected region
     *
     * @return calculated distance of two locations
     */
    double calculateDistanceToRegion(Location current, ProtectedRegion protectedRegion);

    /**
     * Travels specified player to specified region
     * with specified price.
     *
     * @param playerId the id of the player
     * @param protectedRegion the protected region
     * @param price the price of the journey
     */
    void travelPlayer(UUID playerId, ProtectedRegion protectedRegion, double price);

}
