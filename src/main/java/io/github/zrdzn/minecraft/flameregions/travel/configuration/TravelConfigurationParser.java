package io.github.zrdzn.minecraft.flameregions.travel.configuration;

import org.bukkit.configuration.ConfigurationSection;

public class TravelConfigurationParser {

    public TravelConfiguration parse(ConfigurationSection section) {
        boolean priceMultiplierEnabled = section.getBoolean("price-multiplier.enable");

        double priceMultiplier = section.getDouble("price-multiplier.value");

        return new TravelConfiguration(priceMultiplierEnabled, priceMultiplier);
    }

}
