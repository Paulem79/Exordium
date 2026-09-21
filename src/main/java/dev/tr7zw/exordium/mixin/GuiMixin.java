package dev.tr7zw.exordium.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.tr7zw.exordium.ExordiumModBase;
import dev.tr7zw.exordium.compat.CustomRenderHook;
import dev.tr7zw.transition.mc.ComponentProvider;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

@Mixin(value = Gui.class, priority = 1500)
public class GuiMixin {

    //? if >= 26.2 {

    private boolean naggedThisSession = false;

    @Inject(method = "setScreen", at = @At("HEAD"), cancellable = true)
    public void setScreen(net.minecraft.client.gui.screens.Screen screen, CallbackInfo ci) {
        if (!naggedThisSession && !ExordiumModBase.instance.config.iKnowThisIsNotAnActivelyDevelopedMod
                && screen instanceof net.minecraft.client.gui.screens.TitleScreen) {
            // Only nag once per session
            naggedThisSession = true;
            Minecraft.getInstance().gui.setScreen(new net.minecraft.client.gui.screens.ConfirmScreen(b -> {
                if (b) {
                    ExordiumModBase.instance.config.iKnowThisIsNotAnActivelyDevelopedMod = true;
                    ExordiumModBase.instance.writeConfig();
                }
                Minecraft.getInstance().gui.setScreen(screen);
            }, ComponentProvider.translatable("text.exordium.warning"),
                    ComponentProvider.translatable("text.exordium.warning_desc")));
            ci.cancel();
        }
    }
    //? }

    //? if >= 26.0 {

    @Inject(method = "extractRenderState", at = @At("HEAD"), cancellable = true)
    //? } else {
    /*
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    *///? }
    public void render(net.minecraft.client.gui.GuiGraphicsExtractor guiGraphics, DeltaTracker deltaTracker,
            CallbackInfo ci) {
        if (ExordiumModBase.instance != null && ExordiumModBase.instance.getMainBuffer().skipGuiRendering()) {
            // Trick minecraft into thinking we rendered something, so it still runs the GuiRenderer logic
            //? if >= 26.0 {

            guiGraphics.text(Minecraft.getInstance().font, ComponentProvider.literal("Missingno."), 10, 10, -1);
            //? } else {
            /*
            guiGraphics.drawString(Minecraft.getInstance().font, ComponentProvider.literal("Missingno."), 10, 10, -1);
            *///? }
            for (CustomRenderHook hook : ExordiumModBase.instance.getCustomRenderHooks()) {
                hook.render(guiGraphics, deltaTracker);
            }
            ci.cancel();
        }
    }

}
