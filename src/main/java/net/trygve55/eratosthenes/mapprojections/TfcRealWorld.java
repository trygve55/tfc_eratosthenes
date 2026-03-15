package net.trygve55.eratosthenes.mapprojections;

import net.dries007.tfc.world.region.Region;
import net.dries007.tfc.world.region.Units;

import static java.lang.Math.abs;
import static java.lang.Math.min;

interface TfcRealWorld extends MapProjection {

  default float continentFactor(Region.Point point) {
    final float equatorDistance = getDistanceFromEquator(point);
    final float latitude = getLatitude(equatorDistance);
    final int currentHalfCircumference = getHalfCircumferenceAtLatitude(latitude);

    return min(
        abs(latitude) < 90 ? 1.f : 0.f,
        abs(Units.gridToBlock(point.x)) < currentHalfCircumference ? 1f : 0f);
  }

  default float getEquatorOffset() {
    return 0;
  }

  float getWestEdgeLongitude();

  float getEastEdgeLongitude();

  default float getWidthToHeightRatio() {
    return (getEastEdgeLongitude() - getWestEdgeLongitude()) / 180f;
  }

  @Override
  default boolean havePolarArea() {
    return false;
  }
}
