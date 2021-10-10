package io.github.zrdzn.minecraft.flameregions.travel.configuration;

public class TravelConfiguration {

    private final boolean priceMultiplierEnabled;
    private final double priceMultiplier;

    public TravelConfiguration(boolean priceMultiplierEnabled, double priceMultiplier) {
        this.priceMultiplierEnabled = priceMultiplierEnabled;
        this.priceMultiplier = priceMultiplier;
    }

    public boolean isPriceMultiplierEnabled() {
        return this.priceMultiplierEnabled;
    }

    public double getPriceMultiplier() {
        return this.priceMultiplier;
    }

}
