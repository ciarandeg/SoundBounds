package com.ciarandg.soundbounds.mixin;

import com.ciarandg.soundbounds.SoundBounds;

import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundSystem;
import net.minecraft.sound.SoundCategory;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoundSystem.class)
public class MixinSoundSystem {
    @Inject(at = @At("HEAD"), method = "play", cancellable = true)
    private void onPlay(SoundInstance sound, CallbackInfo info) {
        if (sound.getCategory() == SoundCategory.MUSIC) {
            SoundBounds.Companion.getLOGGER().info("Cancelling vanilla Minecraft music");
            info.cancel();
        }
    }
}
