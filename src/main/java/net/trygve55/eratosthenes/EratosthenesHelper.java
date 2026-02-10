package net.trygve55.eratosthenes;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class EratosthenesHelper {
    public static float continentFactor(int x, int z) {
        if (Config.CRASTER_PARABOLIC_MAP_PROJECTION.get()) {
            return EratosthenesHelper.continentFactorCrasterParabolic(x, z, WorldScaleHolder.getTemperatureScale().orElseThrow());
        }

        if (Config.FINITE_CONTINENTS.get()) {
            return EratosthenesHelper.continentFactorFiniteContinents(
                    x,
                    z,
                    WorldScaleHolder.getTemperatureScale().orElseThrow(),
                    WorldScaleHolder.getRainfallScale().orElseThrow());
        }

        return 1f;
    }

    public static float continentFactorFiniteContinents(int x, int z, int temperatureScale, int rainfallScale) {
        // Finite continent area is within one pole-pole area. Interpolate from 1 -> 0 to 1.2x scale for smooth borders
        final int scaleX = rainfallScale;
        final int scaleZ = temperatureScale;
        return Math.min(
                scaleX == 0 ? 1f : Mth.clampedMap(Math.abs(Units.gridToBlock(x)), scaleX, 1.2f * scaleX, 1, 0),
                scaleZ == 0 ? 1f : Mth.clampedMap(Math.abs(Units.gridToBlock(z) - 0.5f * scaleZ), scaleZ, 1.2f * scaleZ, 1, 0)
        );
    }

    public static float continentFactorCrasterParabolic(int x, int z, int halfMeridian) {
        // Eratosthenes continent area is within one pole-pole Craster Parabolic map area.
        // Preventing continents outside this area. Interpolate for smooth borders

        final float equatorOffset = getEquatorOffset(halfMeridian);
        final float equatorDistance = getDistanceFromEquator(x, z, equatorOffset);
        final float latitude = getLatitude(equatorDistance, halfMeridian);
        final int currentHalfCircumference = getHalfCircumferenceAtLatitude(latitude, halfMeridian);

        return Math.min(latitude < 90 ? 1.f : 0.f
                , Math.min(
                        currentHalfCircumference == 0 ? 1f : Mth.clampedMap(
                                Math.abs(Units.gridToBlock(x)),
                                currentHalfCircumference * 0.85f,
                                1.1f * currentHalfCircumference,
                                1,
                                0),
                        halfMeridian == 0 ? 1f : Mth.clampedMap(
                                Math.abs(Units.gridToBlock(z) - equatorOffset),
                                halfMeridian * 0.93f,
                                1.05f * halfMeridian,
                                1,
                                0)
                ));
    }

    private static float getEquatorOffset(int halfMeridian) {
        return 0.5f * halfMeridian;
    }

    static int getHalfCircumferenceAtLatitude(float latitude, int halfMeridian) {
        return (int) (halfMeridian * 2 * Math.cos(Math.toRadians(latitude)));
    }

    static float getDistanceFromEquator(int x, int z, float equatorOffset) {
        return Math.abs((Units.gridToBlock(z) - equatorOffset));
    }

    static float getDistanceFromEquator(Vec3 position, float equatorOffset) {
        return (float) Math.abs(position.z - equatorOffset);
    }

    static float getLatitude(float equatorDistance, int halfMeridian) {
        return 90 * (equatorDistance / halfMeridian);
    }
}
