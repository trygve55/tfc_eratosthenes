package net.trygve55.eratosthenes.mixins;

import com.alekiponi.firmaciv.common.item.AbstractNavItem;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import net.trygve55.eratosthenes.MapProjectionHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractNavItem.class)
public class AbstractNavItemMixin {

  @Inject(
      method = "getNavLocation(Lnet/minecraft/world/phys/Vec3;)[D",
      at = @At("HEAD"),
      remap = false,
      cancellable = true)
  private static void getNavLocation(Vec3 position, CallbackInfoReturnable<double[]> cir) {
    float latitude = MapProjectionHolder.get().getLatitude(position);
    float longitude = MapProjectionHolder.get().getLongitude(position);
    double altitude = (position.get(Direction.Axis.Y)) - 64;

    latitude = clipLatitudeAtPoles(latitude);

    cir.setReturnValue(new double[] {latitude, longitude, altitude});
  }

  private static float clipLatitudeAtPoles(float latitude) {
    return Math.min(latitude, 90);
  }
}
