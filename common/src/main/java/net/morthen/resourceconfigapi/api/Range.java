package net.morthen.resourceconfigapi.api;

import net.morthen.resourceconfigapi.api.annotation.RangedEntry;

/**
 * Inclusive value range of a numeric option.
 * @see RangedEntry
 */
public record Range(double min, double max) {

    public Range {
        if (min > max) throw new IllegalArgumentException("min " + min + " is greater than max " + max);
    }

    public double clamp(double value) {
        return Math.clamp(value, this.min, this.max);
    }

    public boolean contains(double value) {
        return value >= this.min && value <= this.max;
    }
}
