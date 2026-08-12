package net.trygve55.eratosthenes.mixins;

import static net.trygve55.eratosthenes.config.ServerConfig.MAP_PROJECTION;

import net.dries007.tfc.util.climate.OverworldClimateModel;
import net.dries007.tfc.world.ChunkGeneratorExtension;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.trygve55.eratosthenes.MapProjectionHolder;
import net.trygve55.eratosthenes.compat.TFCRealWorld;
import net.trygve55.eratosthenes.config.MapProjectionConfig;
import net.trygve55.eratosthenes.config.ServerConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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

    MapProjectionHolder.set(mapProjectionConfig.toMapProjection(halfMeridian));
  }

  @Inject(method = "onWorldLoad", at = @At("RETURN"), remap = false)
  public void onWorldLoad(ServerLevel level, CallbackInfo ci) {
    MapProjectionConfig mapProjectionConfig = ServerConfig.getOrDefault(MAP_PROJECTION).get();

    final ChunkGeneratorExtension extension =
        (ChunkGeneratorExtension) level.getChunkSource().getGenerator();

    int halfMeridian = extension.settings().temperatureScale();
    if (TFCRealWorld.isLoaded()) {
      halfMeridian = TFCRealWorld.getHalfMeridian();
    }

    MapProjectionHolder.set(mapProjectionConfig.toMapProjection(halfMeridian));
  }
}
