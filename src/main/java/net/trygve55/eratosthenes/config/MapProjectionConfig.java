package net.trygve55.eratosthenes.config;

import java.util.ArrayList;
import java.util.List;
import net.trygve55.eratosthenes.compat.TFCRealWorld;
import net.trygve55.eratosthenes.mapprojections.*;

public enum MapProjectionConfig {
  CRASTER_PARABOLIC,
  TFC_REAL_WORLD_FULL_WORLD,
  TFC_REAL_WORLD_NEW_WORLD,
  TFC_REAL_WORLD_OLD_WORLD;

  public MapProjection toMapProjection(int halfMeridian) {
    return switch (this) {
      case CRASTER_PARABOLIC -> new CrasterParabolic(halfMeridian);
      case TFC_REAL_WORLD_FULL_WORLD -> new TfcRealWorldFullWorld(halfMeridian);
      case TFC_REAL_WORLD_NEW_WORLD -> new TfcRealWorldNewWorld(halfMeridian);
      case TFC_REAL_WORLD_OLD_WORLD -> new TfcRealWorldOldWorld(halfMeridian);
    };
  }

  public static MapProjectionConfig[] allowedValues() {
    List<MapProjectionConfig> allowedValues = new ArrayList<>();

    allowedValues.add(CRASTER_PARABOLIC);

    if (TFCRealWorld.isLoaded()) {
      allowedValues.add(TFC_REAL_WORLD_FULL_WORLD);
      allowedValues.add(TFC_REAL_WORLD_NEW_WORLD);
      allowedValues.add(TFC_REAL_WORLD_OLD_WORLD);
    }

    return allowedValues.toArray(new MapProjectionConfig[0]);
  }
}
