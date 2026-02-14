package net.trygve55.eratosthenes.mapprojections;

import net.dries007.tfc.world.region.Region;
import net.dries007.tfc.world.region.Units;
import net.minecraft.util.Mth;

public class CrasterParabolic implements MapProjection {
  private final int halfMeridian;

  public CrasterParabolic(int halfMeridian) {
    this.halfMeridian = halfMeridian;
  }

  @Override
  public int getHalfMeridian() {
    return halfMeridian;
  }

  public float continentFactor(Region.Point point) {
    // Eratosthenes continent area is within one pole-pole Craster Parabolic map area.
    // Preventing continents outside this area. Interpolate for smooth borders

    final float equatorDistance = getDistanceFromEquator(point);
    final float latitude = getLatitude(equatorDistance);
    final int currentHalfCircumference = getHalfCircumferenceAtLatitude(latitude);

    return Math.min(
        latitude < 90 ? 1.f : 0.f,
        Math.min(
            currentHalfCircumference == 0
                ? 1f
                : Mth.clampedMap(
                    Math.abs(Units.gridToBlock(point.x)),
                    currentHalfCircumference * 0.85f,
                    1.1f * currentHalfCircumference,
                    1,
                    0),
            getHalfMeridian() == 0
                ? 1f
                : Mth.clampedMap(
                    Math.abs(Units.gridToBlock(point.z) - getEquatorOffset()),
                    getHalfMeridian() * 0.93f,
                    1.05f * getHalfMeridian(),
                    1,
                    0)));
  }

  public float getEquatorOffset() {
    return 0.5f * getHalfMeridian();
  }

  public int getHalfCircumferenceAtLatitude(float latitude) {
    return (int) (getHalfMeridian() * 2 * Math.cos(Math.toRadians(latitude)));
  }

  public float getLatitude(float equatorDistance) {
    return 90 * (equatorDistance / getHalfMeridian());
  }

  public float getLongitude(float x) {
    return x / getHalfMeridian() * 90;
  }

  @Override
  public boolean havePolarArea() {
    return true;
  }
}
