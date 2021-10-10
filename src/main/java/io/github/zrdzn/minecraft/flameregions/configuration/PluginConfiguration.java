package io.github.zrdzn.minecraft.flameregions.configuration;

// TODO Do something with this object, move configuration somewhere else. It's redundant.
public class PluginConfiguration {

    private final String regionsPrefix;

    public PluginConfiguration(String regionsPrefix) {
        this.regionsPrefix = regionsPrefix;
    }

    public String getRegionsPrefix() {
        return this.regionsPrefix;
    }

}
