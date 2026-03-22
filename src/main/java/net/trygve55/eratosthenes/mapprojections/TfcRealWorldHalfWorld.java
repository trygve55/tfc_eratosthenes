package net.trygve55.eratosthenes.mapprojections;

import net.minecraft.world.phys.Vec3;

public abstract class TfcRealWorldHalfWorld extends EqualEarth implements TfcRealWorld {
  private final EqualEarth equalEarth = new EqualEarth() {
    @Override
    protected float getWidthToHeightRatio() { throw new UnsupportedOperationException(); }

    @Override
    public int getHalfMeridian() {
      return TfcRealWorldHalfWorld.this.getHalfMeridian();
    }

    @Override
    public float continentFactor(int x, int z) { throw new UnsupportedOperationException(); }

    @Override
    public float getEquatorOffset() { throw new UnsupportedOperationException(); }
  };

  @Override
  public float getLatitude(float equatorDistance) {
    return equalEarth.getLatitude(equatorDistance);
  }

  @Override
  public float getLongitude(Vec3 position) {
    return (float)
        ((getWestEdgeLongitude() + getEastEdgeLongitude()) / 2
            + position.x
                / getHalfCircumferenceAtLatitude(getLatitude(getDistanceFromEquator(position)))
                * 90);
  }

  @Override
  public float getWidthToHeightRatio() {
    return TfcRealWorld.super.getWidthToHeightRatio();
  }
}
