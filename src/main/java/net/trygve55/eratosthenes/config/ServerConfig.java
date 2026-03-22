package net.trygve55.eratosthenes.config;

import com.electronwill.nightconfig.core.EnumGetMethod;
import java.util.NoSuchElementException;
import net.minecraftforge.common.ForgeConfigSpec;
import net.trygve55.eratosthenes.compat.TFCRealWorld;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerConfig {
  private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

  public static final ForgeConfigSpec.BooleanValue LIMIT_WORLD_GENERATION_OUTSIDE_MAP_PROJECTION =
      BUILDER
          .comment(
              "Whether to limit world generation to fit the current map projection. Similar to \"Finite continents\".")
          .define(
              "limitWorldGenerationOutsideMapProjection",
              CommonConfig.DEFAULT_LIMIT_WORLD_GENERATION_OUTSIDE_MAP_PROJECTION);

  public static final ForgeConfigSpec.EnumValue<MapProjectionConfig> MAP_PROJECTION =
      BUILDER
          .comment("What map/world projection to use.")
          .worldRestart()
          .defineEnum(
              "mapProjection",
              () -> {
                if (TFCRealWorld.isLoaded()) {
                  return TFCRealWorld.getMapProjectionConfig();
                }
                return CommonConfig.DEFAULT_MAP_PROJECTION.get();
              },
              EnumGetMethod.NAME_IGNORECASE,
              (mapProjectionConfig) -> {
                if (mapProjectionConfig == null) {
                  return false;
                }

                if (mapProjectionConfig instanceof MapProjectionConfig) {
                  return true;
                }

                if (mapProjectionConfig instanceof String) {
                  return isValidMapProjectionConfig(mapProjectionConfig);
                }

                return false;
              },
              MapProjectionConfig.class);

  public static final ForgeConfigSpec.BooleanValue CROSSING_180_MERIDIAN_TELEPORT =
      BUILDER
          .comment(
              "Crossing 180 meridian teleport: Whether to teleport players crossing the 180° to the other side of the world.")
          .define("crossing180MeridianTeleport", true);

  public static final ForgeConfigSpec.BooleanValue KEEP_ISLANDS_AND_HOTSPOT_VOLCANOES_INSIDE =
      BUILDER
          .comment("Whether to prevent island and hotspot volcanoes outside the \"world\".")
          .define(
              "keepIslandsAndHotspotVolcanoesInside",
              CommonConfig.DEFAULT_KEEP_ISLANDS_AND_HOTSPOT_VOLCANOES_INSIDE);

  public static final ForgeConfigSpec.IntValue POLAR_AREA_SIZE =
      BUILDER
          .comment(
              "Polar area size: The radius of the special polar area. Only available for CRASTER_PARABOLIC.")
          .defineInRange("polarAreaSize", 100, 20, 1000);

  public static final ForgeConfigSpec.IntValue MERIDIAN_CROSSING_GRACE_DISTANCE =
      BUILDER
          .comment(
              "Meridian crossing grace distance: How far you need to pass the 180° meridian to get teleported.")
          .defineInRange("meridianCrossingGraceDistance", 40, 1, 500);

  public static final ForgeConfigSpec SPEC = BUILDER.build();
  private static final Logger log = LoggerFactory.getLogger(ServerConfig.class);

  public static <T extends ForgeConfigSpec.ConfigValue<E>, E> T getOrDefault(T configSpec) {
    if (SPEC.isLoaded()) {
      return configSpec;
    }

    if (equals(configSpec, LIMIT_WORLD_GENERATION_OUTSIDE_MAP_PROJECTION)) {
      return (T) CommonConfig.DEFAULT_LIMIT_WORLD_GENERATION_OUTSIDE_MAP_PROJECTION;
    } else if (equals(configSpec, MAP_PROJECTION)) {
      if (TFCRealWorld.isLoaded()) {
        CommonConfig.DEFAULT_MAP_PROJECTION.set(TFCRealWorld.getMapProjectionConfig());
        log.info("TFC Real World is loaded. Using map projection from TFC Real World.");
      }
      return (T) CommonConfig.DEFAULT_MAP_PROJECTION;
    } else if (equals(configSpec, KEEP_ISLANDS_AND_HOTSPOT_VOLCANOES_INSIDE)) {
      return (T) CommonConfig.DEFAULT_KEEP_ISLANDS_AND_HOTSPOT_VOLCANOES_INSIDE;
    }

    throw new NoSuchElementException(
        "Config does not have a default: "
            + configSpec.getPath().stream().findFirst().orElse("Unknown name"));
  }

  private static boolean equals(
      ForgeConfigSpec.ConfigValue<?> config, ForgeConfigSpec.ConfigValue<?> otherConfig) {
    return config.getPath().stream()
        .findFirst()
        .orElseThrow()
        .equals(otherConfig.getPath().stream().findFirst().orElseThrow());
  }

  private static boolean isValidMapProjectionConfig(Object mapProjectionConfig) {
    for (int i = 0; i < MapProjectionConfig.values().length; i++) {
      if (MapProjectionConfig.values()[i].name().equals(mapProjectionConfig)) {
        return true;
      }
    }
    return false;
  }
}
