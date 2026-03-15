package net.trygve55.eratosthenes.mapprojections;

import net.dries007.tfc.world.region.Region;
import net.dries007.tfc.world.region.Units;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

import static java.lang.Math.*;

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

    return min(
        abs(latitude) < 90 ? 1.f : 0.f,
        min(
            currentHalfCircumference == 0
                ? 1f
                : Mth.clampedMap(
                    abs(Units.gridToBlock(point.x)),
                    currentHalfCircumference * 0.85f,
                    1.1f * currentHalfCircumference,
                    1,
                    0),
            getHalfMeridian() == 0
                ? 1f
                : Mth.clampedMap(
                    abs(Units.gridToBlock(point.z) - getEquatorOffset()),
                    getHalfMeridian() * 0.93f,
                    1.05f * getHalfMeridian(),
                    1,
                    0)));
  }

  public float getEquatorOffset() {
    return 0.5f * getHalfMeridian();
  }

  public int getHalfCircumferenceAtLatitude(float latitude) {
    return (int) (getHalfMeridian() * 2 * cos(toRadians(abs(latitude))));
  }

  public float getLatitude(float equatorDistance) {
    return 90 * (equatorDistance / getHalfMeridian());
  }

  @Override
  public float getLongitude(Vec3 position) {
    return (float)
        (position.x
            / getHalfCircumferenceAtLatitude(getLatitude(getDistanceFromEquator(position)))
            * 180);
  }

  @Override
  public boolean havePolarArea() {
    return true;
  }
}
