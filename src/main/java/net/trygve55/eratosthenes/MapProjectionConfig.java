package net.trygve55.eratosthenes;

import net.trygve55.eratosthenes.mapprojections.*;

public enum MapProjectionConfig {
  CRASTER_PARABOLIC,
  TFC_REAL_WORLD_FULL_WORLD,
  TFC_REAL_WORLD_NEW_WORLD,
  TFC_REAL_WORLD_OLD_WORLD;

  public MapProjection toMapProjection(int halfMedian) {
    return switch (this) {
      case CRASTER_PARABOLIC -> new CrasterParabolic(halfMedian);
      case TFC_REAL_WORLD_FULL_WORLD -> new TfcRealWorldFullWorld(halfMedian);
      case TFC_REAL_WORLD_NEW_WORLD -> new TfcRealWorldNewWorld(halfMedian);
      case TFC_REAL_WORLD_OLD_WORLD -> new TfcRealWorldOldWorld(halfMedian);
    };
  }
}
