package net.trygve55.eratosthenes.mapprojections;

import net.minecraft.world.phys.Vec3;

public class TfcRealWorldFullWorld extends EqualEarth implements TfcRealWorld {
  private final int halfMeridian;

  public TfcRealWorldFullWorld(int halfMeridian) {
    this.halfMeridian = halfMeridian;
  }

  @Override
  public float getWestEdgeLongitude() {
    return -170;
  }

  @Override
  public float getEastEdgeLongitude() {
    return 190;
  }

  @Override
  public float getWidthToHeightRatio() {
    return TfcRealWorld.super.getWidthToHeightRatio();
  }

  @Override
  public float getLongitude(Vec3 position) {
    return super.getLongitude(position) + 10f * ((float) getHalfCircumferenceAtLatitude(getLatitude((float) position.z)) / getHalfCircumferenceAtLatitude(0));
  }

  @Override
  public int getHalfMeridian() {
    return halfMeridian;
  }
}
