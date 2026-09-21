package dev.tr7zw.exordium.mixin;

import org.spongepowered.asm.mixin.Mixin;
//? if >= 26.2 {

import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.tr7zw.exordium.ExordiumModBase;
import dev.tr7zw.transition.mc.ComponentProvider;
import net.minecraft.client.Minecraft;
//? }
import net.minecraft.client.gui.Gui;

/**
 * Since 26.2 setScreen lives in the Gui. See MinecraftMixin for the older
 * versions.
 */
@Mixin(Gui.class)
public class GuiNagMixin {

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

}
