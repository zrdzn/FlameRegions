package io.github.zrdzn.minecraft.flameregions.region;

import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ExploredRegionRepository {

    private final HikariDataSource dataSource;

    public ExploredRegionRepository(HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public boolean save(UUID playerId, String regionId) {
        try (Connection connection = this.dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("INSERT INTO explored_regions (region_name, explorer_uuid) VALUES (?, ?);")) {
            statement.setString(1, regionId);
            statement.setString(2, playerId.toString());

            statement.executeUpdate();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        return true;
    }

    public void delete(UUID playerId, String regionId) {
        try (Connection connection = this.dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM explored_regions WHERE region_name = ? AND explorer_uuid = ? LIMIT 1;")) {
            statement.setString(1, regionId);
            statement.setString(2, playerId.toString());

            statement.executeUpdate();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public List<String> list(UUID playerId) {
        List<String> regionNames = new ArrayList<>();
        try (Connection connection = this.dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT region_name FROM explored_regions WHERE explorer_uuid = ?;")) {
            statement.setString(1, playerId.toString());

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                regionNames.add(resultSet.getString("region_name"));
            }

            return regionNames;
        } catch (SQLException exception) {
            exception.printStackTrace();
            return regionNames;
        }
    }

}
