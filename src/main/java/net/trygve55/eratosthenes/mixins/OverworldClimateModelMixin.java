package net.trygve55.eratosthenes.mixins;

import net.dries007.tfc.util.climate.OverworldClimateModel;
import net.minecraft.network.FriendlyByteBuf;
import net.trygve55.eratosthenes.WorldScaleHolder;
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
        WorldScaleHolder.setTemperatureScale(temperatureScale);
    }
}
