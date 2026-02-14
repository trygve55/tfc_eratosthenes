package net.trygve55.eratosthenes;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
  private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

  public static final ModConfigSpec.BooleanValue LIMIT_WORLD_GENERATION_OUTSIDE_MAP_PROJECTION =
      BUILDER
          .comment("Whether to limit world generation to fit the current map projection.")
          .define("limitWorldGenerationOutsideMapProjection", true);

  public static final ModConfigSpec.EnumValue<MapProjectionConfig> MAP_PROJECTION =
      BUILDER
          .comment("What map/world projection to use.")
          .defineEnum(
              "mapProjection", MapProjectionConfig.CRASTER_PARABOLIC, MapProjectionConfig.values());

  public static final ModConfigSpec.BooleanValue CROSSING_180_MERIDIAN_TELEPORT =
      BUILDER
          .comment(
              "Crossing 180 meridian teleport: Whether to teleport players crossing the 180° to the other side of the world.")
          .define("crossing180MeridianTeleport", true);

  public static final ModConfigSpec.BooleanValue KEEP_ISLANDS_AND_HOTSPOT_VOLCANOES_INSIDE =
      BUILDER
          .comment("Whether to prevent island and hotspot volcanoes outside the \"world\".")
          .define("keepIslandsAndHotspotVolcanoesInside", true);

  public static final ModConfigSpec.IntValue POLAR_AREA_SIZE =
      BUILDER
          .comment(
              "Polar area size: The radius of the special polar area. Only available for CRASTER_PARABOLIC.")
          .defineInRange("polarAreaSize", 100, 20, 1000);

  public static final ModConfigSpec.IntValue MERIDIAN_CROSSING_GRACE_DISTANCE =
      BUILDER
          .comment(
              "Meridian crossing grace distance: How far you need to pass the 180° meridian to get teleported.")
          .defineInRange("meridianCrossingGraceDistance", 40, 1, 500);

  static final ModConfigSpec SPEC = BUILDER.build();
}
