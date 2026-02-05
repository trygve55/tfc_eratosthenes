package net.trygve55.eratosthenes;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue CRASTER_PARABOLIC_MAP_PROJECTION = BUILDER
            .comment("Whether to limit world generation to fit the Craster Parabolic map projection.")
            .define("craterParabolicMapProjection", true);

    public static final ModConfigSpec.BooleanValue CROSSING_180_MERIDIAN_TELEPORT = BUILDER
            .comment("Whether to teleport players crossing the 180° to the other side of the world.")
            .define("crossing180MeridianTeleport", true);

    public static final ModConfigSpec.BooleanValue KEEP_ISLANDS_AND_HOTSPOT_VOLCANOES_INSIDE = BUILDER
            .comment("Whether to prevent island and hotspot volcanoes outside the \"world\".")
            .define("keepIslandsAndHotspotVolcanoesInside", true);

    static final ModConfigSpec SPEC = BUILDER.build();
}
