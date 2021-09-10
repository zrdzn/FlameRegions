package io.github.zrdzn.minecraft.flameregions.configuration;

// TODO Do something with this object, move configuration somewhere else. It's redundant.
public record PluginConfiguration(String regionsPrefix) {

    public String getRegionsPrefix() {
        return this.regionsPrefix;
    }

}
