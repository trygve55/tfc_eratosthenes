package net.trygve55.eratosthenes.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class CommonConfig {
  private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

  public static final ModConfigSpec.BooleanValue
      DEFAULT_LIMIT_WORLD_GENERATION_OUTSIDE_MAP_PROJECTION =
          BUILDER
              .comment(
                  "Whether to limit world generation to fit the current map projection. Similar to \"Finite Continents\".")
              .define("limitWorldGenerationOutsideMapProjection", true);

  public static final ModConfigSpec.EnumValue<MapProjectionConfig> DEFAULT_MAP_PROJECTION =
      BUILDER
          .comment("What map/world projection to use on new worlds.")
          .defineEnum(
              "mapProjection",
              MapProjectionConfig.CRASTER_PARABOLIC,
              MapProjectionConfig.allowedValues());

  public static final ModConfigSpec.BooleanValue DEFAULT_KEEP_ISLANDS_AND_HOTSPOT_VOLCANOES_INSIDE =
      BUILDER
          .comment("Whether to prevent island and hotspot volcanoes outside the \"world\".")
          .define("keepIslandsAndHotspotVolcanoesInside", true);

  public static final ModConfigSpec SPEC = BUILDER.build();
}
