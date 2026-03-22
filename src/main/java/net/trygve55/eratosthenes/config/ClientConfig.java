package net.trygve55.eratosthenes.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ClientConfig {
  private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

  public static final ForgeConfigSpec.BooleanValue SHOW_COORDINATES =
      BUILDER.comment("Show current coordinates").define("showCoordinates", false);

  public static final ForgeConfigSpec SPEC = BUILDER.build();
}
