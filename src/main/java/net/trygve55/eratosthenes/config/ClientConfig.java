package net.trygve55.eratosthenes.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class ClientConfig {
  private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

  public static final ModConfigSpec.BooleanValue SHOW_COORDINATES =
      BUILDER.comment("Show current coordinates").define("showCoordinates", false);

  public static final ModConfigSpec SPEC = BUILDER.build();
}
