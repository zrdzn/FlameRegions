package io.github.enixor.minecraft.flameregions.configuration;

public record TravelConfiguration(boolean priceMultiplierEnabled, double priceMultiplier) {

    public boolean isPriceMultiplierEnabled() {
        return this.priceMultiplierEnabled;
    }

    public double getPriceMultiplier() {
        return this.priceMultiplier;
    }

}
