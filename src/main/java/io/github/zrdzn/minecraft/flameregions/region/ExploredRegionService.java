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
