package io.github.zrdzn.minecraft.flameregions.datasource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.configuration.ConfigurationSection;

public class DataSourceParser {

    public HikariDataSource parse(ConfigurationSection section) {
        String host = section.getString("host", "localhost");

        int port = section.getInt("port", 3306);

        String database = section.getString("database", "minecraft");

        String user = section.getString("user", "root");

        String password = section.getString("password", "");

        boolean ssl = section.getBoolean("enable-ssl");

        int poolSize = section.getInt("maximum-pool-size", 10);

        long connectionTimeout = section.getLong("connection-timeout");

        HikariConfig hikariConfig = new HikariConfig();

        hikariConfig.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database + "?enableSSL=" + ssl);
        hikariConfig.setUsername(user);
        hikariConfig.setPassword(password);
        hikariConfig.addDataSourceProperty("cachePrepStmts", true);
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", 250);
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
        hikariConfig.addDataSourceProperty("useServerPrepStmts", true);
        hikariConfig.addDataSourceProperty("cacheResultSetMetadata", true);
        hikariConfig.addDataSourceProperty("tcpKeepAlive", true);
        hikariConfig.setLeakDetectionThreshold(60000L);
        hikariConfig.setMaximumPoolSize(poolSize);
        hikariConfig.setConnectionTimeout(connectionTimeout);
        hikariConfig.setMinimumIdle(0);
        hikariConfig.setIdleTimeout(30000L);

        return new HikariDataSource(hikariConfig);
    }

}
