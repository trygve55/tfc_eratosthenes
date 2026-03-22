package net.trygve55.eratosthenes.mapprojections;

import static java.lang.Math.abs;
import static java.lang.Math.min;

import net.trygve55.eratosthenes.Units;

interface TfcRealWorld extends MapProjection {

  default float continentFactor(int x, int z) {
    final float equatorDistance = getDistanceFromEquator(z);
    final float latitude = getLatitude(equatorDistance);
    final int currentHalfCircumference = getHalfCircumferenceAtLatitude(latitude);

    return min(
        abs(latitude) < 90 ? 1.f : 0.f,
        abs(Units.gridToBlock(x)) < currentHalfCircumference ? 1f : 0f);
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
