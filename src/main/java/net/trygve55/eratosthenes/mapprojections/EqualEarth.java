package net.trygve55.eratosthenes.mapprojections;

public abstract class EqualEarth implements MapProjection {
  private static final float A1 = 1.340264f;
  private static final float A2 = -0.081106f;
  private static final float A3 = 0.000893f;
  private static final float A4 = 0.003796f;
  private static final float M = (float) (Math.sqrt(3) / 2);
  private static final int MAX_ITERATIONS = 12;
  private static final float EPSILON_2 = 1e-12f;
  private static final float MAGIC_NUMBER =
      1.317362725f; // invertY(MAGIC_NUMBER) = PI/2 = Math.toRadians(90)
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
    float l = (float) Math.asin(M * Math.sin(Math.toRadians(latitude)));
    float l2 = l * l;
    float l6 = l2 * l2 * l2;

    return Math.cos(l) / (M * (A1 + 3 * A2 * l2 + l6 * (7 * A3 + 9 * A4 * l2)));
  }

  @Override
  public float getLatitude(float equatorDistance) {
    return (float)
        Math.toDegrees(
            invertEqualEarthLatitude(equatorDistance / getHalfMeridian() * MAGIC_NUMBER));
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
      if (Math.abs(delta) < EPSILON_2) {
        break;
      }
    }

    return (float) Math.asin(Math.sin(l) / M);
  }

  @Override
  public boolean havePolarArea() {
    return false;
  }
}
