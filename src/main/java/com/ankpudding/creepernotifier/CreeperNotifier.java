package com.ankpudding.creepernotifier;

import com.ankpudding.creepernotifier.config.ConfigHandler;
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
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.AABB;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
		ConfigHandler config = new ConfigHandler();

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			//Check if that both client.level and client.player is not null. To prevent detection code from running when not in a game.
			if (client.level == null || client.player == null) {
				return;
			}

			boolean enabledInGamemode = config.isDetectionEnabledInGamemode(client);

			//Continue only if the mod is enabled globally and is enabled in current gamemode
			if (!config.settings.modEnabled || !enabledInGamemode) {
				return;
			}

			float detectionDistance = config.settings.creeperDetectionDistance;

			//Get the closest entity that is specified in the config. Returns null if none exist within detectionDistance.
			EntityInstance<? extends Entity> trackedEntity = getClosestEntity(client, config.getEntityToDetect(), (int) Math.ceil(detectionDistance));

			if (trackedEntity.distance != null && trackedEntity.distance < detectionDistance) {
				if (ticksElapsed % config.settings.alertInterval == 0) {
					Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.EXPERIENCE_ORB_PICKUP, config.settings.alertPitch, config.settings.alertVolume));
				}

				if (config.settings.alertTextVisible) {
					displaySimpleAlertText(client, detectionDistance, trackedEntity, config);
				}
			}

			ticksElapsed++;
		});
	}

	private static void displaySimpleAlertText(Minecraft client, float detectionDistance, EntityInstance<? extends Entity> entity, ConfigHandler configHandler){
		float entityRelativeDirection = getPlayerYawRelativeToEntity(Objects.requireNonNull(client.player), entity);

		//Logic to make the text become more red as they approach a creeper
		int value = Math.clamp(Math.round((255 / detectionDistance) * entity.distance), 0, 255);
		int textColor = (255 << 16) | (value << 8) | value;

		String alertTextFormatting = configHandler.settings.alertTextFormatting;
		Component message = Component.literal(String.format(alertTextFormatting,
				String.format("%.1f", entity.distance),
				configHandler.entityRelativePositionToWarningText(entityRelativeDirection)
		)).withColor(textColor);
		client.player.sendOverlayMessage(message);
	}

	private static <T extends Entity> float getPlayerYawRelativeToEntity(LocalPlayer player, EntityInstance<T> instance){
		//Magic math from the internet that works
		double dx = instance.entity.getX() - player.getX();
		double dz = instance.entity.getZ() - player.getZ();

		float targetYaw = (float) Math.toDegrees(Math.atan2(dz, dx)) - 90.0F;

		return Mth.wrapDegrees(targetYaw - player.getYRot());
    }

	//Get distance of closest entity of specified class. Returns null if none. Legacy code
	private static <T extends Entity> EntityInstance<T> getClosestEntity(@NonNull Minecraft client, Class<T> detectionEntity, int searchRange){
		if (client.level == null || client.player == null){throw new RuntimeException("CreeperNotifier: client.level or client.player is null");}

		Float minDistance = null;
		T minEntity = null;

		AABB searchBox = client.player.getBoundingBox().inflate(searchRange);

        for (T entity : client.level.getEntitiesOfClass(detectionEntity, searchBox)) {
			float distance = entity.distanceTo(client.player);
			if (minDistance == null || distance <= minDistance) {
				minDistance = distance;
				minEntity = entity;
			}
		}
		return new EntityInstance<>(minEntity, minDistance);
	}
	//Get distance of closest entity of specified EntityType. Returns null if none
	//Suppresses unchecked casting
	@SuppressWarnings("unchecked")
	private static <T extends Entity> EntityInstance<T> getClosestEntity(@NonNull Minecraft client, EntityType<T> detectionEntity, int searchRange){
		if (client.level == null || client.player == null){throw new RuntimeException("CreeperNotifier: client.level or client.player is null");}

		Float minDistance = null;
		T minEntity = null;

		AABB searchBox = client.player.getBoundingBox().inflate(searchRange);

		//Get all nearby entities and filter them by if their type is the same as in detectionEntity
		for (Entity entity : client.level.getEntitiesOfClass(
				Entity.class,
				searchBox,
				entity -> entity.getType() == detectionEntity)
		) {
			float distance = entity.distanceTo(client.player);
			if (minDistance == null || distance <= minDistance) {
				minDistance = distance;
				minEntity = (T)entity;
			}
		}
		return new EntityInstance<>(minEntity, minDistance);
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
