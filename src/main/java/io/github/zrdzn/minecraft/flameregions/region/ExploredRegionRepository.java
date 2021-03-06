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

import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ExploredRegionRepository {

    private final Logger logger;
    private final HikariDataSource dataSource;

    public ExploredRegionRepository(Logger logger, HikariDataSource dataSource) {
        this.logger = logger;
        this.dataSource = dataSource;
    }

    public boolean save(UUID playerId, String regionId) {
        try (Connection connection = this.dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("INSERT INTO explored_regions (region_name, explorer_uuid) VALUES (?, ?);")) {
            statement.setString(1, regionId);
            statement.setString(2, playerId.toString());

            return (statement.executeUpdate() > 0);
        } catch (SQLException exception) {
            this.logger.error("Something went wrong while inserting explored region.", exception);
            return true;
        }
    }

    public void delete(UUID playerId, String regionId) {
        try (Connection connection = this.dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM explored_regions WHERE region_name = ? AND explorer_uuid = ? LIMIT 1;")) {
            statement.setString(1, regionId);
            statement.setString(2, playerId.toString());

            statement.executeUpdate();
        } catch (SQLException exception) {
            this.logger.error("Something went wrong while deleting explored region.", exception);
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
            this.logger.error("Something went wrong while selecting explored regions.", exception);
            return regionNames;
        }
    }

}
