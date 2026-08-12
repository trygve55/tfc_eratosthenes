package net.trygve55.eratosthenes.mixins;

import static net.trygve55.eratosthenes.config.ServerConfig.MAP_PROJECTION;

import net.dries007.tfc.util.climate.OverworldClimateModel;
import net.minecraft.network.FriendlyByteBuf;
import net.trygve55.eratosthenes.MapProjectionHolder;
import net.trygve55.eratosthenes.WorldScaleHolder;
import net.trygve55.eratosthenes.compat.TFCRealWorld;
import net.trygve55.eratosthenes.config.MapProjectionConfig;
import net.trygve55.eratosthenes.config.ServerConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.trygve55.eratosthenes.config.ServerConfig.MAP_PROJECTION;

@Mixin(OverworldClimateModel.class)
public class OverworldClimateModelMixin {

  @Shadow(remap = false)
  public float temperatureScale;

  @Inject(method = "onReceiveOnClient", at = @At("RETURN"), remap = false)
  public void onReceiveOnClient(FriendlyByteBuf buffer, CallbackInfo ci) {
    MapProjectionConfig mapProjectionConfig = ServerConfig.getOrDefault(MAP_PROJECTION).get();

    int halfMeridian = (int) temperatureScale;
    if (TFCRealWorld.isLoaded()) {
      halfMeridian = TFCRealWorld.getHalfMeridian();
    }

    WorldScaleHolder.setTemperatureScale(halfMeridian);
    MapProjectionHolder.set(mapProjectionConfig.toMapProjection(halfMeridian));
  }
}
