package net.trygve55.eratosthenes.mapprojections;

import net.dries007.tfc.world.region.Region;
import net.dries007.tfc.world.region.Units;
import net.minecraft.world.phys.Vec3;

public interface MapProjection {
  int getHalfMeridian();

  float continentFactor(Region.Point point);

  float getEquatorOffset();

  int getHalfCircumferenceAtLatitude(float latitude);

  default float getDistanceFromEquator(Region.Point point) {
    return Math.abs((Units.gridToBlock(point.z) - getEquatorOffset()));
  }

  default float getDistanceFromEquator(Vec3 position) {
    return (float) Math.abs(position.z - getEquatorOffset());
  }

  float getLatitude(float equatorDistance);

  boolean havePolarArea();
}
