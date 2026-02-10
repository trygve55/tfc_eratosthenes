package net.trygve55.eratosthenes;

import java.util.Optional;

public class WorldScaleHolder {
    private final static int SCALE_NOT_SET = Integer.MIN_VALUE;
    private static int temperatureScale = SCALE_NOT_SET;
    private static int rainfallScale = SCALE_NOT_SET;

    private WorldScaleHolder() {}

    public static void setTemperatureScale(float temperatureScale) {
        WorldScaleHolder.temperatureScale = (int) temperatureScale;
    }

    public static void setRainfallScale(float rainfallScale) {
        WorldScaleHolder.rainfallScale = (int) rainfallScale;
    }

    public static Optional<Integer> getTemperatureScale() {
        if (temperatureScale == SCALE_NOT_SET) {
            return Optional.empty();
        }

        return Optional.of(temperatureScale);
    }

    public static Optional<Integer> getRainfallScale() {
        if (rainfallScale == SCALE_NOT_SET) {
            return Optional.empty();
        }

        return Optional.of(temperatureScale);
    }
}
