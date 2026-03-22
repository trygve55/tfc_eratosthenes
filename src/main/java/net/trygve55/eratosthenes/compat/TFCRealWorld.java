package net.trygve55.eratosthenes.compat;

import net.neoforged.fml.ModList;
import net.trygve55.eratosthenes.config.MapProjectionConfig;
import net.yazloysasha.tfcrealworld.config.TFCRealWorldConfig;

public class TFCRealWorld {
  public static boolean isLoaded() {
    return ModList.get().isLoaded("tfc_real_world");
  }

  public static MapProjectionConfig getMapProjectionConfig() {
    return switch (TFCRealWorldConfig.MAP_PROFILE.get()) {
      case "DEFAULT:FULL_EQUAL_EARTH" -> MapProjectionConfig.TFC_REAL_WORLD_FULL_WORLD;
      case "DEFAULT:NEW_WORLD_EQUAL_EARTH" -> MapProjectionConfig.TFC_REAL_WORLD_NEW_WORLD;
      case "DEFAULT:OLD_WORLD_EQUAL_EARTH" -> MapProjectionConfig.TFC_REAL_WORLD_OLD_WORLD;
      default ->
          throw new IllegalStateException(
              "Unexpected value: " + TFCRealWorldConfig.MAP_PROFILE.get());
    };
  }

  public static int getHalfMeridian() {
    return TFCRealWorldConfig.VERTICAL_SCALE.get();
  }
}
