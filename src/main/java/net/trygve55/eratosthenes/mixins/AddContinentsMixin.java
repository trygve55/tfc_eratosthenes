package net.trygve55.eratosthenes.mixins;

import net.dries007.tfc.world.noise.Cellular2D;
import net.dries007.tfc.world.region.AddContinents;
import net.dries007.tfc.world.region.Region;
import net.dries007.tfc.world.region.RegionGenerator;
import net.dries007.tfc.world.region.Units;
import net.trygve55.eratosthenes.MapProjectionHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AddContinents.class)
public class AddContinentsMixin {

  @Inject(method = "apply", at = @At("HEAD"), cancellable = true, remap = false)
  public void apply(RegionGenerator.Context context, CallbackInfo ci) {
    ci.cancel();

    for (int dx = -Units.REGION_RADIUS_IN_GRID; dx <= Units.REGION_RADIUS_IN_GRID; dx++) {
      for (int dz = -Units.REGION_RADIUS_IN_GRID; dz <= Units.REGION_RADIUS_IN_GRID; dz++) {
        final int gridX = context.region.minX() + Units.REGION_RADIUS_IN_GRID + dx;
        final int gridZ = context.region.minZ() + Units.REGION_RADIUS_IN_GRID + dz;
        final Cellular2D.Cell otherCell = context.generator().sampleCell(gridX, gridZ);

        if (otherCell.x() == context.regionCell.x() && otherCell.y() == context.regionCell.y()) {
          final Region.Point point = context.region.atInit(gridX, gridZ);
          final double continent =
              context.generator().continentNoise.noise(gridX, gridZ)
                  * MapProjectionHolder.get().continentFactor(gridX, gridZ);

          if (continent > 4.4) {
            point.setLand();
          }

          if (gridX < context.minX) {
            context.minX = gridX;
          }
          if (gridZ < context.minZ) {
            context.minZ = gridZ;
          }
          if (gridX > context.maxX) {
            context.maxX = gridX;
          }
          if (gridZ > context.maxZ) {
            context.maxZ = gridZ;
          }
        }
      }
    }
  }
}
