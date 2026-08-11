package net.trygve55.eratosthenes.mixins;

import static net.trygve55.eratosthenes.config.ServerConfig.MAP_PROJECTION;

import net.dries007.tfc.util.climate.OverworldClimateModel;
import net.trygve55.eratosthenes.MapProjectionHolder;
import net.trygve55.eratosthenes.compat.TFCRealWorld;
import net.trygve55.eratosthenes.config.MapProjectionConfig;
import net.trygve55.eratosthenes.config.ServerConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OverworldClimateModel.class)
public class OverworldClimateModelMixin {

  @Inject(method = "<init>(JF)V", at = @At("RETURN"))
  public void onConstructed(long climateSeed, float temperatureScale, CallbackInfo ci) {
    MapProjectionConfig mapProjectionConfig = ServerConfig.getOrDefault(MAP_PROJECTION).get();

    int halfMeridian = (int) temperatureScale;
    if (TFCRealWorld.isLoaded()) {
      halfMeridian = TFCRealWorld.getHalfMeridian();
    }

    MapProjectionHolder.set(mapProjectionConfig.toMapProjection(halfMeridian));
  }
}
