package com.ankpudding.creepernotifier;

import net.fabricmc.api.ClientModInitializer;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
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

					EntityInstance<Creeper> detectedEntity = getClosestEntity(client, Creeper.class, (int) Math.ceil(detectionDistance));

					if (detectedEntity.distance != null && detectedEntity.distance < detectionDistance) {
						if (ticksElapsed % config.alertInterval == 0) {
							Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.EXPERIENCE_ORB_PICKUP, config.alertPitch, config.alertVolume));
						}

						if (config.alertTextVisible) {
							float entityRelativeDirection = getPlayerYawRelativeToEntity(client.player, detectedEntity.entity);

							//Logic to make the text become more red as they approach a creeper
							int value = Math.clamp(Math.round((255 / detectionDistance) * detectedEntity.distance), 0, 255);
							int textColor = (255 << 16) | (value << 8) | value;

							String alertTextFormatting = config.alertTextFormatting;
							Component message = Component.literal(String.format(alertTextFormatting,
									String.format("%.1f", detectedEntity.distance),
									entityRelativePositionToWarningText(entityRelativeDirection, config)
							)).withColor(textColor);
							client.player.sendOverlayMessage(message);
						}
					}
				}
				ticksElapsed++;
			}
		});
	}
	private static String entityRelativePositionToWarningText(float entityDir, ConfigSettings config){
		entityDir += 45;

		if (entityDir > 180){
			entityDir -= 360;
		} else if (entityDir < -180){
			entityDir += 360;
		}

		if (entityDir >= 0 && entityDir < 90){
			return config.alertTextFront;
		} else if (entityDir >= 90 && entityDir < 180){
			return config.alertTextRight;
		} else if (entityDir < 0 && entityDir >= -90){
			return config.alertTextLeft;
		} else{
			return config.alertTextBack;
		}
	}

	private static <T extends Entity> float getPlayerYawRelativeToEntity(LocalPlayer player, T entity){
		//Magic math from the internet that works
		double dx = entity.getX() - player.getX();
		double dz = entity.getZ() - player.getZ();

		float targetYaw = (float) Math.toDegrees(Math.atan2(dz, dx)) - 90.0F;

		return Mth.wrapDegrees(targetYaw - player.getYRot());
    }

	//Get distance of closest entity of specified type. Returns null if none
	private static <T extends Entity> EntityInstance<T> getClosestEntity(@NonNull Minecraft client, Class<T> detectionEntity, int searchRange){
		if (client.level == null || client.player == null){throw new RuntimeException("CreeperNotifier: client.level or client.player is null");}

		Float minDistance = null;
		T minEntity = null;

		AABB searchBox = client.player.getBoundingBox().inflate(searchRange);

        for (T entity : client.level.getEntitiesOfClass(detectionEntity, searchBox)) {
			float distance = entity.distanceTo(client.player);
			if (minDistance == null || distance <= minDistance) {
				minDistance = distance;
				minEntity = (T) entity;
			}
		}
		return new EntityInstance<T>(minEntity, minDistance);
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
