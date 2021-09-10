package io.github.zrdzn.minecraft.flameregions.configuration;

import org.bukkit.configuration.ConfigurationSection;

public class PluginConfigurationParser {

    public PluginConfiguration parse(ConfigurationSection section) {
        String regionsPrefix = section.getString("regions-prefix", "");

        return new PluginConfiguration(regionsPrefix);
    }

}
