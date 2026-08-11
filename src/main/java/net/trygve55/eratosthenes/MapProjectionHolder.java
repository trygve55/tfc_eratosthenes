package net.trygve55.eratosthenes;

import com.mojang.logging.LogUtils;
import net.trygve55.eratosthenes.mapprojections.MapProjection;
import org.slf4j.Logger;

public class MapProjectionHolder {
  private static MapProjection currentMapProjection;

  private static final Logger LOGGER = LogUtils.getLogger();

  private MapProjectionHolder() {}

  public static void set(MapProjection mapProjection) {
    MapProjectionHolder.currentMapProjection = mapProjection;

    LOGGER.info(
        "Half meridian set to {}, equatorial circumference is {}, equator offset is {}",
        MapProjectionHolder.get().getHalfMeridian(),
        MapProjectionHolder.get().getHalfCircumferenceAtLatitude(0) * 2,
        MapProjectionHolder.get().getEquatorOffset());
  }

  public static MapProjection get() {
    if (currentMapProjection == null) {
      throw new IllegalStateException("No MapProjection set for TFC Eratosthenes.");
    }

    return currentMapProjection;
  }
}
