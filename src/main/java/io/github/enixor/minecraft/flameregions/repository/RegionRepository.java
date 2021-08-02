package io.github.enixor.minecraft.flameregions.repository;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public record RegionRepository(Server server, HikariDataSource dataSource) {

    public boolean addExploredRegionToPlayer(UUID playerId, ProtectedRegion region) {
        Player player = this.server.getPlayer(playerId);
        if (player == null) {
            return false;
        }

        if (this.hasPlayerExplored(playerId, region)) {
            return false;
        }

        try (Connection connection = this.dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("INSERT INTO explored_regions (region_name, explorer_uuid, explorer_name) VALUES (?, ?, ?);")) {
            statement.setString(1, region.getId());
            statement.setString(2, playerId.toString());
            statement.setString(3, player.getName());

            statement.executeUpdate();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        return true;
    }

    public boolean hasPlayerExplored(UUID playerId, ProtectedRegion region) {
        try (Connection connection = this.dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT id FROM explored_regions WHERE explorer_uuid = ? AND region_name = ?;")) {
            statement.setString(1, playerId.toString());
            statement.setString(2, region.getId());

            ResultSet resultSet = statement.executeQuery();

            return resultSet.next();
        } catch (SQLException exception) {
            exception.printStackTrace();
            return false;
        }
    }

}
