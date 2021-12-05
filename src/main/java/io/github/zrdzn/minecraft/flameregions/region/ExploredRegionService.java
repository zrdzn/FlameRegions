package io.github.zrdzn.minecraft.flameregions.region;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldedit.world.World;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface ExploredRegionService {

    /**
     * Adds explored region to specified player.
     *
     * @param playerId the id of the player
     * @param regionId the id of the region
     *
     * @return future of whether the region was added or not
     */
    CompletableFuture<Boolean> addRegion(UUID playerId, String regionId);

    /**
     * Finds specified region.
     *
     * @param playerId the id of the player
     * @param regionId the id of the region
     * @param world the world where the region is located
     *
     * @return future of region if found
     */
    CompletableFuture<Optional<ProtectedRegion>> getRegion(UUID playerId, String regionId, World world);

    /**
     * Gets all regions for specified player in specified world.
     *
     * @param playerId the id of the player
     * @param world the world where the regions are located
     *
     * @return future of regions list in the specified world
     */
    CompletableFuture<List<ProtectedRegion>> getRegions(UUID playerId, World world);

    /**
     * Removes specified region.
     *
     * @param playerId the id of the player
     * @param regionId the id of the region
     *
     * @return future of the removed region
     */
    CompletableFuture<Void> removeRegion(UUID playerId, String regionId);

}
