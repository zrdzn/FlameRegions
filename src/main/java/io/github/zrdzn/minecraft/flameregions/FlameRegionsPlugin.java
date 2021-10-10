package io.github.zrdzn.minecraft.flameregions;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.LocationFlag;
import com.sk89q.worldguard.protection.flags.StringFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.session.SessionManager;
import com.zaxxer.hikari.HikariDataSource;
import io.github.zrdzn.minecraft.flameregions.configuration.PluginConfiguration;
import io.github.zrdzn.minecraft.flameregions.configuration.PluginConfigurationParser;
import io.github.zrdzn.minecraft.flameregions.datasource.DataSourceParser;
import io.github.zrdzn.minecraft.flameregions.location.LocationCommand;
import io.github.zrdzn.minecraft.flameregions.location.LocationMenu;
import io.github.zrdzn.minecraft.flameregions.message.MessageService;
import io.github.zrdzn.minecraft.flameregions.region.RegionEnterHandler;
import io.github.zrdzn.minecraft.flameregions.region.RegionRepository;
import io.github.zrdzn.minecraft.flameregions.travel.TravelService;
import io.github.zrdzn.minecraft.flameregions.travel.TravelTrait;
import io.github.zrdzn.minecraft.flameregions.travel.configuration.TravelConfiguration;
import io.github.zrdzn.minecraft.flameregions.travel.configuration.TravelConfigurationParser;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.trait.TraitInfo;
import net.ess3.api.IEssentials;
import org.bukkit.Server;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public class FlameRegionsPlugin extends JavaPlugin {

    public static final StringFlag ENTER_FLAG = new StringFlag("fr-enter");
    public static final LocationFlag TRAVEL_LOCATION_FLAG = new LocationFlag("fr-travel-location");

    private final Map<Locale, ResourceBundle> bundleMap = new HashMap<>();
    private final Logger logger = this.getSLF4JLogger();
    private final Server server = this.getServer();
    private final PluginManager pluginManager = this.server.getPluginManager();

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        this.loadBundles();

        Configuration configuration = this.getConfig();

        ConfigurationSection databaseSection = configuration.getConfigurationSection("database");
        if (databaseSection == null) {
            this.logger.error("Section database does not exist.");
            this.pluginManager.disablePlugin(this);
            return;
        }

        HikariDataSource dataSource = new DataSourceParser().parse(databaseSection);

        if (dataSource == null) {
            this.logger.error("Something went wrong while connecting to database. Check your database configuration and restart your server after correcting it.");
            this.pluginManager.disablePlugin(this);
            return;
        }

        String query = "CREATE TABLE IF NOT EXISTS explored_regions (" +
                "id INT NOT NULL PRIMARY KEY AUTO_INCREMENT," +
                "region_name VARCHAR(128) NOT NULL UNIQUE KEY," +
                "explorer_uuid VARCHAR(36)," +
                "explorer_name VARCHAR(18));";
        try (Connection connection = dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement(query)) {
            statement.executeUpdate();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        RegionRepository regionRepository = new RegionRepository(this.server, dataSource);

        MessageService messageService = new MessageService(this.logger, this.server, this.bundleMap);

        ConfigurationSection travelSection = configuration.getConfigurationSection("travel");
        if (travelSection == null) {
            this.logger.error("Section travel does not exist.");
            this.pluginManager.disablePlugin(this);
            return;
        }

        TravelConfiguration travelConfiguration = new TravelConfigurationParser().parse(travelSection);

        PluginConfiguration pluginConfiguration = new PluginConfigurationParser().parse(configuration);

        IEssentials essentialsApi = (IEssentials) this.pluginManager.getPlugin("Essentials");

        TravelService travelService = new TravelService(this.logger, travelConfiguration, essentialsApi);

        LocationMenu locationMenu = new LocationMenu(this.server, messageService, pluginConfiguration, regionRepository, travelService);

        this.getCommand("locations").setExecutor(new LocationCommand(this.logger, locationMenu, messageService));

        SessionManager sessionManager = WorldGuard.getInstance().getPlatform().getSessionManager();
        sessionManager.registerHandler(new RegionEnterHandler.Factory(pluginConfiguration, regionRepository, messageService), null);

        TravelTrait travelTrait = new TravelTrait(this.logger, messageService, locationMenu);
        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(travelTrait.getClass()).withName("fr-trait-travel"));
    }

    @Override
    public void onLoad() {
        try {
            FlagRegistry flagRegistry = WorldGuard.getInstance().getFlagRegistry();

            flagRegistry.register(ENTER_FLAG);
            flagRegistry.register(TRAVEL_LOCATION_FLAG);
        } catch (FlagConflictException exception) {
            this.logger.error("Failed to register custom flags, check if this custom flag already exists and change it.");
            exception.printStackTrace();
            this.pluginManager.disablePlugin(this);
        }
    }

    private void loadBundles() {
        String baseName = "locale/locale";
        this.bundleMap.put(Locale.forLanguageTag("en-US"), ResourceBundle.getBundle(baseName, Locale.forLanguageTag("en-US")));
        this.bundleMap.put(Locale.forLanguageTag("pl-PL"), ResourceBundle.getBundle(baseName, Locale.forLanguageTag("pl-PL")));
    }

}
