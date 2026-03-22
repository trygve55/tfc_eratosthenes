package net.trygve55.eratosthenes.mapprojections;

import static java.lang.Math.*;

import net.minecraft.world.phys.Vec3;
import net.trygve55.eratosthenes.Units;

public interface MapProjection {
  int getHalfMeridian();

  float continentFactor(int x, int z);

  float getEquatorOffset();

  int getHalfCircumferenceAtLatitude(float latitude);

  default float getDistanceFromEquator(int z) {
    return Units.gridToBlock(z) - getEquatorOffset();
  }

  default float getDistanceFromEquator(Vec3 position) {
    return (float) abs(position.z - getEquatorOffset());
  }

  float getLatitude(float equatorDistance);

  float getLongitude(Vec3 position);

  boolean havePolarArea();
}
