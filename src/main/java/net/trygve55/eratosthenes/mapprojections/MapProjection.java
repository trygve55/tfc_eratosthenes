package net.trygve55.eratosthenes.mapprojections;

import net.dries007.tfc.world.region.Region;
import net.dries007.tfc.world.region.Units;
import net.minecraft.world.phys.Vec3;

import static java.lang.Math.*;

public interface MapProjection {
  int getHalfMeridian();

  float continentFactor(Region.Point point);

  float getEquatorOffset();

  int getHalfCircumferenceAtLatitude(float latitude);

  default float getDistanceFromEquator(Region.Point point) {
    return Units.gridToBlock(point.z) - getEquatorOffset();
  }

  default float getDistanceFromEquator(Vec3 position) {
    return (float) abs(position.z - getEquatorOffset());
  }

  float getLatitude(float equatorDistance);

  default float getLatitude(Vec3 position) {
    return getLatitude(getDistanceFromEquator(position));
  }

  float getLongitude(Vec3 position);

  boolean havePolarArea();
}
