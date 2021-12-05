package io.github.zrdzn.minecraft.flameregions.region;

import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class ExploredRegionServiceImpl implements ExploredRegionService {

    private final ExploredRegionRepository repository;
    private final RegionContainer regionContainer;

    public ExploredRegionServiceImpl(ExploredRegionRepository repository, RegionContainer regionContainer) {
        this.repository = repository;
        this.regionContainer = regionContainer;
    }

    @Override
    public CompletableFuture<Boolean> addRegion(UUID playerId, String regionId) {
        return CompletableFuture.supplyAsync(() -> this.repository.save(playerId, regionId));
    }

    @Override
    public CompletableFuture<Optional<ProtectedRegion>> getRegion(UUID playerId, String regionId, World world) {
        return this.getRegions(playerId, world).thenApplyAsync(regions -> regions.stream()
            .filter(region -> region.getId().equals(regionId))
            .findAny());
    }

    @Override
    public CompletableFuture<List<ProtectedRegion>> getRegions(UUID playerId, World world) {
        return CompletableFuture.supplyAsync(() -> {
            RegionManager regionManager = this.regionContainer.get(world);
            if (regionManager == null) {
                throw new IllegalArgumentException(String.format("Region manager for world %s does not exist.", world.getName()));
            }

            return this.repository.list(playerId).stream()
                .filter(regionManager::hasRegion)
                .map(regionManager::getRegion)
                .collect(Collectors.toList());
        });
    }

    @Override
    public CompletableFuture<Void> removeRegion(UUID playerId, String regionId) {
        return CompletableFuture.runAsync(() -> this.repository.delete(playerId, regionId));
    }

}
