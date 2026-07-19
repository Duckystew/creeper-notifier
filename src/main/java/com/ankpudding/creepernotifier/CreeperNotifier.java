package com.ankpudding.creepernotifier;

import net.fabricmc.api.ClientModInitializer;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Creeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;

public class CreeperNotifier implements ClientModInitializer {
	public static final String MOD_ID = "creeper-notifier";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitializeClient() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		ConfigHandler.HANDLER.load();

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
            ConfigHandler config = ConfigHandler.HANDLER.instance();
			if(config.modEnabled) {
				float detectionDistance = config.creeperDetectionDistance;
				if (client.level != null && client.player != null) {
					boolean creeperDetected = false;
					float minDistance = detectionDistance;
					for (Entity entity : client.level.entitiesForRendering()) {
						if (entity instanceof Creeper creeper) {
							float distance = creeper.distanceTo(client.player);
							if (distance <= minDistance) {
								minDistance = distance;
								creeperDetected = true;
							}
						}
					}
					if (creeperDetected) {
						int value = Math.clamp(Math.round((255 / detectionDistance) * minDistance), 0, 255);
						Color color = new Color(255, value, value);
						Component message = Component.literal("Creeper Nearby! " + String.format("%.1f", minDistance) + "m away.").withColor(color.getRGB());
						client.player.sendOverlayMessage(message);
					}
				}
			}
		});
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
