/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.trygve55.eratosthenes;

import net.dries007.tfc.world.region.Region;
import net.dries007.tfc.world.region.Units;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class EratosthenesHelper {
    public static float continentFactor(Region.Point point, int halfMeridian) {
        // Eratosthenes continent area is within one pole-pole Craster Parabolic map area.
        // Preventing continents outside this area. Interpolate for smooth borders

        final float equatorOffset = getEquatorOffset(halfMeridian);
        final float equatorDistance = getDistanceFromEquator(point, equatorOffset);
        final float latitude = getLatitude(equatorDistance, halfMeridian);
        final int currentHalfCircumference = getHalfCircumferenceAtLatitude(latitude, halfMeridian);

        return Math.min(
            currentHalfCircumference == 0 ? 1f : Mth.clampedMap(
                Math.abs(Units.gridToBlock(point.x)),
                currentHalfCircumference * 0.85f,
                1.1f * currentHalfCircumference,
                1,
                0),
            halfMeridian == 0 ? 1f : Mth.clampedMap(
                Math.abs(Units.gridToBlock(point.z) - equatorOffset),
                halfMeridian * 0.87f,
                1.08f * halfMeridian,
                1,
                0)
        );
    }

    private static float getEquatorOffset(int halfMeridian) {
        return 0.5f * halfMeridian;
    }

    static int getHalfCircumferenceAtLatitude(float latitude, int halfMeridian) {
        return (int) (halfMeridian * 2 * Math.cos(Math.toRadians(latitude)));
    }

    static float getDistanceFromEquator(Region.Point point, float equatorOffset) {
        return Math.abs((Units.gridToBlock(point.z) - equatorOffset));
    }

    static float getDistanceFromEquator(Vec3 position, float equatorOffset) {
        return (float) Math.abs(position.z - equatorOffset);
    }

    static float getLatitude(float equatorDistance, int halfMeridian) {
        return 90 * (equatorDistance / halfMeridian);
    }
}
