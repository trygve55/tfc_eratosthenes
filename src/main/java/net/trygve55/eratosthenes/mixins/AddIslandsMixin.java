package net.trygve55.eratosthenes.mixins;

import net.dries007.tfc.world.region.AddIslands;
import net.dries007.tfc.world.region.Region;
import net.dries007.tfc.world.region.RegionGenerator;
import net.minecraft.util.RandomSource;
import net.trygve55.eratosthenes.Config;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AddIslands.class)
public class AddIslandsMixin {

    @Inject(method = "apply", at = @At("HEAD"), cancellable = true)
    public void apply(RegionGenerator.Context context, CallbackInfo ci) {
        if (Config.KEEP_ISLANDS_AND_HOTSPOT_VOLCANOES_INSIDE.isFalse()) {
            return;
        }

        final Region region = context.region;
        final RandomSource random = context.random;

        for (int attempt = 0, placed = 0; attempt < 130 && placed < 15; attempt++) {
            Region.Point point = region.random(random);
            if (point == null) {
                continue;
            }

            if (!point.land() && !point.shore() && point.distanceToEdge > 2 && context.generator().continentFactor(point) > 0.45f) {
                // Place a small island chain
                for (int island = 0; island < 12; island++) {
                    if (context.generator().continentFactor(point) <= 0.45f) {
                        continue;
                    }
                    point.setLand();
                    point.setIsland();
                    point = region.at(
                            point.x + random.nextInt(4) - random.nextInt(4),
                            point.z + random.nextInt(4) - random.nextInt(4)
                    );
                    if (point == null || (point.land() && !point.island()) || point.distanceToEdge <= 2) {
                        break;
                    }
                }
                placed += 1;
            }
        }

        ci.cancel();
    }
}
