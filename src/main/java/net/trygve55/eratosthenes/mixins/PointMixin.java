package net.trygve55.eratosthenes.mixins;

import net.dries007.tfc.world.region.Region;
import net.trygve55.eratosthenes.MapProjectionHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Region.Point.class)
public class PointMixin {
  private static final Logger log = LoggerFactory.getLogger(PointMixin.class);
  private static final float CONTINENT_FACTOR_NOT_CALCULATED = Float.MIN_VALUE;

  @Shadow public int x, z;

  float continentFactor = CONTINENT_FACTOR_NOT_CALCULATED;

  @Inject(method = "setLand", at = @At("HEAD"), cancellable = true)
  public void setLand(CallbackInfo ci) {
    if (getContinentFactor() <= 0.45) {
      ci.cancel();
      log.info("canceled land at point: {} {} {}", x, z, getContinentFactor());
    }
  }

  @Inject(method = "setIsland", at = @At("HEAD"), cancellable = true)
  public void setIsland(CallbackInfo ci) {
    if (getContinentFactor() <= 0.45) {
      ci.cancel();
      log.info("canceled island at point: {} {} {}", x, z, getContinentFactor());
    }
  }

  @Inject(method = "setMountain", at = @At("HEAD"), cancellable = true)
  public void setMountain(CallbackInfo ci) {
    if (getContinentFactor() <= 0.45) {
      ci.cancel();
      log.info("canceled mountain at point: {} {} {}", x, z, getContinentFactor());
    }
  }

  @Inject(method = "setCoastalMountain", at = @At("HEAD"), cancellable = true)
  public void setCoastalMountain(CallbackInfo ci) {
    if (getContinentFactor() <= 0.45) {
      ci.cancel();
      log.info("canceled mountain at point: {} {} {}", x, z, getContinentFactor());
    }
  }

  @Inject(method = "setVolcanic", at = @At("HEAD"), cancellable = true)
  public void setVolcanic(CallbackInfo ci) {
    if (getContinentFactor() <= 0.45) {
      ci.cancel();
      log.info("canceled mountain at point: {} {} {}", x, z, getContinentFactor());
    }
  }

  @Inject(method = "setBarrierIsland", at = @At("HEAD"), cancellable = true)
  public void setBarrierIsland(CallbackInfo ci) {
    if (getContinentFactor() <= 0.45) {
      ci.cancel();
      log.info("canceled mountain at point: {} {} {}", x, z, getContinentFactor());
    }
  }

  private float getContinentFactor() {
    if (continentFactor == CONTINENT_FACTOR_NOT_CALCULATED) {
      Region.Point point = (Region.Point) (Object) this;
      continentFactor = MapProjectionHolder.get().continentFactor(point);
    }

    return continentFactor;
  }
}
