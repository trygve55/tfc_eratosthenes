package net.trygve55.eratosthenes.mixins;


import net.dries007.tfc.world.region.RegionGenerator;
import net.dries007.tfc.world.settings.Settings;
import net.minecraft.util.RandomSource;
import net.trygve55.eratosthenes.WorldScaleHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RegionGenerator.class)
public class RegionGeneratorMixin {

  @Inject(method = "<init>", at = @At("RETURN"))
  public void onConstructed(Settings settings, RandomSource random, CallbackInfo ci) {
    WorldScaleHolder.setTemperatureScale(settings.temperatureScale());
    WorldScaleHolder.setRainfallScale(settings.rainfallScale());
  }
}
