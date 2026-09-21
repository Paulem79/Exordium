package dev.tr7zw.exordium.mixin;

import org.spongepowered.asm.mixin.Mixin;
//? if >= 26.2 {

import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.blaze3d.pipeline.RenderTarget;

import dev.tr7zw.exordium.ExordiumModBase;
//? }
import net.minecraft.client.renderer.GameRenderer;

/**
 * Since 26.2 the main render target lives in the GameRenderer. See
 * MinecraftMixin for the older versions.
 */
@Mixin(GameRenderer.class)
public class GameRendererMixin {

    //? if >= 26.2 {

    @Inject(method = "mainRenderTarget", at = @At("HEAD"), cancellable = true)
    public void mainRenderTarget(CallbackInfoReturnable<RenderTarget> ci) {
        if (ExordiumModBase.instance == null) {
            return; // Mod not initialized yet, so no temporary screen overwrite
        }
        RenderTarget target = ExordiumModBase.instance.getTemporaryScreenOverwrite();
        if (target != null) {
            ci.setReturnValue(target);
            ci.cancel();
        }
    }
    //? }

}
