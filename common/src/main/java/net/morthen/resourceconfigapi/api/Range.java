package net.morthen.resourceconfigapi.api;

public record Range(double min, double max) {

    public Range {
        if (min > max) throw new IllegalArgumentException("min " + min + " is greater than max " + max);
    }

    public double clamp(double value) {
        return Math.clamp(value, this.min(), this.max());
    }
}
