package com.ankpudding.creepernotifier;

import net.fabricmc.api.ClientModInitializer;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.phys.AABB;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.Objects;

public class CreeperNotifier implements ClientModInitializer {
	public static final String MOD_ID = "creeper-notifier";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static long ticksElapsed = 0;

	@Override
	public void onInitializeClient() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		ConfigHandler.init();

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
            ConfigSettings config = ConfigHandler.getInstance();

			//Check if that both client.level and client.player is not null. To prevent detection code from running when not in a game.
			if (client.level != null && client.player != null) {
				boolean enabledInGamemode = isEnabledInGamemode(client, config);

				if (config.modEnabled && enabledInGamemode) {
					float detectionDistance = config.creeperDetectionDistance;

					Float creeperDetected = getClosestEntity(client, Creeper.class, (int) Math.ceil(detectionDistance));

					if (creeperDetected != null && creeperDetected < detectionDistance) {
						if (ticksElapsed % config.alertInterval == 0) {
							Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.EXPERIENCE_ORB_PICKUP, config.alertPitch, config.alertVolume));
						}

						if (config.alertTextVisible) {
							//Logic to make the text become more red as they approach a creeper
							int value = Math.clamp(Math.round((255 / detectionDistance) * creeperDetected), 0, 255);
							int textColor = (255 << 16) | (value << 8) | value;

							Component message = Component.literal("Creeper Nearby! " + String.format("%.1f", creeperDetected) + "m away.").withColor(textColor);
							client.player.sendOverlayMessage(message);
						}
					}
				}
				ticksElapsed++;
			}
		});
	}

	//Get distance of closest entity of specified type. Returns null if none
	private static <T extends Entity> Float getClosestEntity(@NonNull Minecraft client, Class<T> detectionEntity, int searchRange){
		if (client.level == null || client.player == null){throw new RuntimeException("CreeperNotifier: client.level or client.player is null");}

		Float minDistance = null;

		AABB searchBox = client.player.getBoundingBox().inflate(searchRange);

        for (Entity entity : client.level.getEntitiesOfClass(detectionEntity, searchBox)) {
			float distance = entity.distanceTo(client.player);
			if (minDistance == null || distance <= minDistance) {
				minDistance = distance;
			}
		}
		return minDistance;
	}

	private static boolean isEnabledInGamemode(Minecraft client, ConfigSettings config) {
		boolean enabledInGamemode = false;

		switch (Objects.requireNonNull(client.gameMode).getPlayerMode()){
			case ADVENTURE -> {
				if (config.enableInAdventure){
					enabledInGamemode = true;
				}
			}

			case SPECTATOR -> {
				if (config.enableInSpectator){
					enabledInGamemode = true;
				}
			}

			case SURVIVAL -> {
				if (config.enableInSurvival){
					enabledInGamemode = true;
				}
			}

			case CREATIVE -> {
				if (config.enableInCreative){
					enabledInGamemode = true;
				}
			}
		}
		return enabledInGamemode;
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
