package net.trygve55.eratosthenes;

import net.minecraftforge.common.ForgeConfigSpec;

public class Config {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.BooleanValue CRASTER_PARABOLIC_MAP_PROJECTION = BUILDER
            .comment("Whether to limit world generation to fit the Craster Parabolic map projection. Takes precedence over finiteContinents.")
            .define("craterParabolicMapProjection", true);

    public static final ForgeConfigSpec.BooleanValue CROSSING_180_MERIDIAN_TELEPORT = BUILDER
            .comment("Whether to teleport players crossing the 180° to the other side of the world.")
            .define("crossing180MeridianTeleport", true);

    public static final ForgeConfigSpec.BooleanValue KEEP_ISLANDS_AND_HOTSPOT_VOLCANOES_INSIDE = BUILDER
            .comment("Whether to prevent island and hotspot volcanoes outside the \"world\".")
            .define("keepIslandsAndHotspotVolcanoesInside", true);

    public static final ForgeConfigSpec.BooleanValue FINITE_CONTINENTS = BUILDER
            .comment("Prevents the world from spawning repeating temperature and rainfall bands. Almost the same as the 1.21.1 TFC world gen option. Ignored if craterParabolicMapProjection is enabled.")
            .define("finiteContinents", true);

    static final ForgeConfigSpec SPEC = BUILDER.build();


}
