package io.github.zrdzn.minecraft.flameregions;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.LocationFlag;
import com.sk89q.worldguard.protection.flags.StringFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.session.SessionManager;
import com.zaxxer.hikari.HikariDataSource;
import io.github.zrdzn.minecraft.flameregions.command.LocationsCommand;
import io.github.zrdzn.minecraft.flameregions.configuration.PluginConfiguration;
import io.github.zrdzn.minecraft.flameregions.configuration.PluginConfigurationParser;
import io.github.zrdzn.minecraft.flameregions.configuration.TravelConfiguration;
import io.github.zrdzn.minecraft.flameregions.configuration.TravelConfigurationParser;
import io.github.zrdzn.minecraft.flameregions.datasource.DataSourceParser;
import io.github.zrdzn.minecraft.flameregions.handler.RegionEnterHandler;
import io.github.zrdzn.minecraft.flameregions.menu.LocationMenu;
import io.github.zrdzn.minecraft.flameregions.repository.RegionRepository;
import io.github.zrdzn.minecraft.flameregions.travel.TravelSystem;
import io.github.zrdzn.minecraft.flameregions.travel.TravelTrait;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.trait.TraitInfo;
import net.ess3.api.IEssentials;
import net.kyori.adventure.text.Component;
import org.bukkit.Server;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Logger;

public class FlameRegionsPlugin extends JavaPlugin {

    public static final StringFlag ENTER_FLAG = new StringFlag("fr-enter");
    public static final LocationFlag TRAVEL_LOCATION_FLAG = new LocationFlag("fr-travel-location");

    private static FlameRegionsPlugin instance = null;

    private final Server server = this.getServer();
    private final PluginManager pluginManager = this.server.getPluginManager();
    private final Logger logger = this.getLogger();
    private final TravelSystem travelSystem = new TravelSystem(this);
    private final LocationMenu menu = new LocationMenu(this);
    private final Map<String, ResourceBundle> bundleMap = new HashMap<>();

    private HikariDataSource dataSource;
    private RegionRepository regionRepository;
    private IEssentials essentialsApi;
    private TravelConfiguration travelConfiguration;
    private PluginConfiguration pluginConfiguration;

    public static FlameRegionsPlugin getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        this.saveDefaultConfig();
        this.loadBundles();

        Configuration configuration = this.getConfig();

        ConfigurationSection databaseSection = configuration.getConfigurationSection("database");
        if (databaseSection == null) {
            this.logger.severe("Section database does not exist.");
            this.pluginManager.disablePlugin(this);
            return;
        }

        DataSourceParser dataSourceParser = new DataSourceParser();
        this.dataSource = dataSourceParser.parse(databaseSection).getHikariDataSource();

        if (this.dataSource == null) {
            this.logger.severe("Something went wrong while connecting to database. Check your database configuration and restart your server after correcting it.");
            this.pluginManager.disablePlugin(this);
            return;
        }

        String query = "CREATE TABLE IF NOT EXISTS explored_regions (" +
                "id INT NOT NULL PRIMARY KEY AUTO_INCREMENT," +
                "region_name VARCHAR(128) NOT NULL UNIQUE KEY," +
                "explorer_uuid VARCHAR(36)," +
                "explorer_name VARCHAR(18));";
        try (Connection connection = this.dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement(query)) {
            statement.executeUpdate();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        this.regionRepository = new RegionRepository(this.server, this.dataSource);

        this.essentialsApi = (IEssentials) this.pluginManager.getPlugin("Essentials");

        TravelConfigurationParser travelConfigurationParser = new TravelConfigurationParser();
        ConfigurationSection travelSection = configuration.getConfigurationSection("travel");
        if (travelSection == null) {
            this.logger.severe("Section travel does not exist.");
            this.pluginManager.disablePlugin(this);
            return;
        }
        this.travelConfiguration = travelConfigurationParser.parse(travelSection);

        PluginConfigurationParser pluginConfigurationParser = new PluginConfigurationParser();
        this.pluginConfiguration = pluginConfigurationParser.parse(configuration);

        this.getCommand("locations").setExecutor(new LocationsCommand(this));

        SessionManager sessionManager = WorldGuard.getInstance().getPlatform().getSessionManager();
        sessionManager.registerHandler(RegionEnterHandler.FACTORY, null);

        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(TravelTrait.class).withName("qr-trait-travel"));
    }

    @Override
    public void onLoad() {
        try {
            FlagRegistry flagRegistry = WorldGuard.getInstance().getFlagRegistry();

            flagRegistry.register(ENTER_FLAG);
            flagRegistry.register(TRAVEL_LOCATION_FLAG);
        } catch (FlagConflictException exception) {
            this.logger.severe("Failed to register custom flags, check if this custom flag already exists and change it.");
            exception.printStackTrace();
            this.pluginManager.disablePlugin(this);
        }
    }

    public HikariDataSource getDataSource() {
        return this.dataSource;
    }

    public IEssentials getEssentialsApi() {
        return this.essentialsApi;
    }

    public RegionRepository getRegionRepository() {
        return this.regionRepository;
    }

    public TravelSystem getTravelSystem() {
        return this.travelSystem;
    }

    public LocationMenu getMenu() {
        return this.menu;
    }

    public TravelConfiguration getTravelConfiguration() {
        return this.travelConfiguration;
    }

    public PluginConfiguration getPluginConfiguration() {
        return this.pluginConfiguration;
    }

    public String translateToString(String locale, String key, Object... replacements) {
        return String.format(this.getResourceBundle(locale).getString(key), replacements);
    }

    public Component translateToComponent(String locale, String key, Object... replacements) {
        return Component.text(this.translateToString(locale, key, replacements));
    }

    public List<Component> translateToComponentList(String locale, String key, Object... replacements) {
        ResourceBundle bundle = this.getResourceBundle(locale);

        List<Component> componentList = new ArrayList<>();
        Arrays.stream(this.getResourceBundle(locale).getStringArray(key))
                .forEach(message -> componentList.add(Component.text(String.format(bundle.getString(key), replacements))));

        return componentList;
    }

    // TODO Store Locale as object and not string.
    private void loadBundles() {
        String baseName = "locale/locale";
        this.bundleMap.put("en_us", ResourceBundle.getBundle(baseName, Locale.forLanguageTag("en-US")));
        this.bundleMap.put("pl_pl", ResourceBundle.getBundle(baseName, Locale.forLanguageTag("pl-PL")));
        this.bundleMap.put("de_de", ResourceBundle.getBundle(baseName, Locale.forLanguageTag("de-DE")));
    }

    private ResourceBundle getResourceBundle(String locale) {
        ResourceBundle bundle = this.bundleMap.get(locale);
        if (bundle == null) {
            bundle = ResourceBundle.getBundle("keys/keys", Locale.ENGLISH);
        }
        return bundle;
    }

}
