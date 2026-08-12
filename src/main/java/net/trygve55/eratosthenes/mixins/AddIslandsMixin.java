package net.trygve55.eratosthenes.mixins;

import static net.trygve55.eratosthenes.config.ServerConfig.KEEP_ISLANDS_AND_HOTSPOT_VOLCANOES_INSIDE;

import net.dries007.tfc.world.region.AddIslands;
import net.dries007.tfc.world.region.Region;
import net.dries007.tfc.world.region.RegionGenerator;
import net.minecraft.util.RandomSource;
import net.trygve55.eratosthenes.MapProjectionHolder;
import net.trygve55.eratosthenes.config.ServerConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AddIslands.class)
public class AddIslandsMixin {

  @Inject(method = "apply", at = @At("HEAD"), cancellable = true, remap = false)
  public void apply(RegionGenerator.Context context, CallbackInfo ci) {
    if (!ServerConfig.getOrDefault(KEEP_ISLANDS_AND_HOTSPOT_VOLCANOES_INSIDE).get()) {
      return;
    }

    final Region region = context.region;
    final RandomSource random = context.random;

    for (int attempt = 0, placed = 0; attempt < 130 && placed < 15; attempt++) {
      int x = region.minX() + random.nextInt(region.sizeX());
      int z = region.minZ() + random.nextInt(region.sizeZ());

      Region.Point point = region.maybeAt(x, z);
      if (point != null
          && !point.land()
          && !point.shore()
          && point.distanceToEdge > 2
          && MapProjectionHolder.get().continentFactor(x, z) > 0.45f) {
        // Place a small island chain
        for (int island = 0; island < 12; island++) {
          point.setLand();
          point.setIsland();

          x += random.nextInt(4) - random.nextInt(4);
          z += random.nextInt(4) - random.nextInt(4);

          point = region.maybeAt(x, z);
          if (point == null
              || (point.land() && !point.island())
              || point.distanceToEdge <= 2
              || MapProjectionHolder.get().continentFactor(x, z) <= 0.45f) {
            break;
          }
        }
        placed += 1;
      }
    }

    ci.cancel();
  }
}
