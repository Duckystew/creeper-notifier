package com.ankpudding.creepernotifier.mixin;

import com.ankpudding.creepernotifier.AlertMessageInterface;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(LocalPlayer.class)
public abstract class AlertMessage implements AlertMessageInterface {
	@Shadow
	@Final
    protected Minecraft minecraft;

	@Unique
	@Override
	public void sendOverlayMessage(final Component message) {
		this.minecraft.gui.setOverlayMessage(message, true);
	}
}