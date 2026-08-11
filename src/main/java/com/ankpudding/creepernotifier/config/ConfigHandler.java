package com.ankpudding.creepernotifier.config;

import com.ankpudding.creepernotifier.CreeperNotifier;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;

import java.util.Objects;

public class ConfigHandler {
    public ConfigSettings settings;

    public ConfigHandler(){
        if (isConfigurable()){
            settings = YACLConfig.HANDLER.instance();
        }else {
            settings = new ConfigSettings();
        }
    }

    public static boolean isConfigurable(){
        return (FabricLoader.getInstance().isModLoaded("modmenu") && FabricLoader.getInstance().isModLoaded("yet_another_config_lib_v3"));
    }

    public static void init(){
        if (isConfigurable()){
            YACLConfig.HANDLER.load();
        }
        else{
            CreeperNotifier.LOGGER.warn("CreeperNotifier: Did not find Mod Menu or YACL. No configuration available");
        }
    }

    public boolean isDetectionEnabledInGamemode(Minecraft client) {
        boolean enabledInGamemode = false;

        switch (Objects.requireNonNull(client.gameMode).getPlayerMode()){
            case ADVENTURE -> {
                if (settings.enableInAdventure){
                    enabledInGamemode = true;
                }
            }

            case SPECTATOR -> {
                if (settings.enableInSpectator){
                    enabledInGamemode = true;
                }
            }

            case SURVIVAL -> {
                if (settings.enableInSurvival){
                    enabledInGamemode = true;
                }
            }

            case CREATIVE -> {
                if (settings.enableInCreative){
                    enabledInGamemode = true;
                }
            }
        }
        return enabledInGamemode;
    }

    public String entityRelativePositionToWarningText(float entityDir){
        entityDir += 45;

        if (entityDir > 180){
            entityDir -= 360;
        } else if (entityDir < -180){
            entityDir += 360;
        }

        if (entityDir >= 0 && entityDir < 90){
            return settings.alertTextFront;
        } else if (entityDir >= 90 && entityDir < 180){
            return settings.alertTextRight;
        } else if (entityDir < 0 && entityDir >= -90){
            return settings.alertTextLeft;
        } else{
            return settings.alertTextBack;
        }
    }
}
