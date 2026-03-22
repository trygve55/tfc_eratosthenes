package net.trygve55.eratosthenes.mapprojections;

import net.minecraft.world.phys.Vec3;

import static java.lang.Math.*;
import static java.lang.Math.asin;
import static java.lang.Math.cos;
import static java.lang.Math.sqrt;
import static java.lang.Math.toDegrees;

public abstract class EqualEarth implements MapProjection {
  private static final float A1 = 1.340264f;
  private static final float A2 = -0.081106f;
  private static final float A3 = 0.000893f;
  private static final float A4 = 0.003796f;
  private static final float M = (float) (sqrt(3) / 2);
  private static final int MAX_ITERATIONS = 12;
  private static final float EPSILON_2 = 1e-12f;
  private static final float MAGIC_NUMBER =
      1.317362725f; // invertY(MAGIC_NUMBER) = PI/2 = toRadians(90)
  private static final float MAGIC_NUMBER_2 =
      1.1607025861777f; // getRelativeWidthAtLatitude(0) * MAGIC_NUMBER_2 = 1

  // Implementation from https://github.com/d3/d3-geo/blob/main/src/projection/equalEarth.js
  // See GeographicLib_License.txt

  protected abstract float getWidthToHeightRatio();

  public int getHalfCircumferenceAtLatitude(float latitude) {
    return (int)
        (getRelativeWidthAtLatitude(latitude)
            * getHalfMeridian()
            * MAGIC_NUMBER_2
            * getWidthToHeightRatio());
  }

  private double getRelativeWidthAtLatitude(float latitude) {
    float l = (float) asin(M * sin(toRadians(abs(latitude))));
    float l2 = l * l;
    float l6 = l2 * l2 * l2;

    return cos(l) / (M * (A1 + 3 * A2 * l2 + l6 * (7 * A3 + 9 * A4 * l2)));
  }

  @Override
  public float getLongitude(Vec3 position) {
    return (float)
        (position.x / getHalfCircumferenceAtLatitude(getLatitude((float) position.z)) * 180f);
  }

  @Override
  public float getLatitude(float equatorDistance) {
    return (float)
        toDegrees(invertEqualEarthLatitude(equatorDistance / getHalfMeridian() * MAGIC_NUMBER));
  }

  private float invertEqualEarthLatitude(float y) {
    float l = y;
    float l2 = l * l;
    float l6 = l2 * l2 * l2;

    for (int i = 0; i < MAX_ITERATIONS; ++i) {
      float fy = l * (A1 + A2 * l2 + l6 * (A3 + A4 * l2)) - y;
      float fpy = A1 + 3 * A2 * l2 + l6 * (7 * A3 + 9 * A4 * l2);
      float delta = fy / fpy;
      l -= delta;
      l2 = l * l;
      l6 = l2 * l2 * l2;
      if (abs(delta) < EPSILON_2) {
        break;
      }
    }

    return (float) asin(sin(l) / M);
  }

  @Override
  public boolean havePolarArea() {
    return false;
  }
}
