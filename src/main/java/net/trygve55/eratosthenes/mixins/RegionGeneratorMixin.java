package net.trygve55.eratosthenes.mixins;

import net.dries007.tfc.world.Seed;
import net.dries007.tfc.world.noise.Noise2D;
import net.dries007.tfc.world.region.Region;
import net.dries007.tfc.world.region.RegionGenerator;
import net.dries007.tfc.world.settings.Settings;
import net.trygve55.eratosthenes.Config;
import net.trygve55.eratosthenes.MapProjectionHolder;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RegionGenerator.class)
public class RegionGeneratorMixin {

    @Shadow
    public Settings settings;

    @Shadow
    @Mutable
    @Final
    public Noise2D temperatureNoise;

    @Inject(method = "continentFactor", at = @At("HEAD"), cancellable = true)
    public void continentFactor(Region.Point point, CallbackInfoReturnable<Float> ci) {
        if (Config.LIMIT_WORLD_GENERATION_OUTSIDE_MAP_PROJECTION.isTrue()) {
            ci.setReturnValue(MapProjectionHolder.get().continentFactor(point));
            ci.cancel();
        }
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    public void onConstructed(Settings settings, Seed seed, CallbackInfo ci) {
        MapProjectionHolder.set(Config.MAP_PROJECTION.get().toMapProjection(settings.temperatureScale()));
    }
}
