package com.ciarandg.soundbounds.mixin;

import com.ciarandg.soundbounds.common.item.Baton;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.Hand;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class MixinClientPlayerEntity {
    @Inject(at = @At("HEAD"), method = "swingHand")
    private void onSwingHand(Hand hand, CallbackInfo info) {
        ClientPlayerEntity player = (ClientPlayerEntity) (Object) this;
        Item mainHandItem = player.getMainHandStack().getItem();
        if (hand == Hand.MAIN_HAND && mainHandItem instanceof Baton baton) {
            baton.onEntitySwing(player);
        }
    }
}
