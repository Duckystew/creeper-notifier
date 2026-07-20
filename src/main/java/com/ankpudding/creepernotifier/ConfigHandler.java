package com.ankpudding.creepernotifier;

import net.fabricmc.loader.api.FabricLoader;

public class ConfigHandler {
    public static boolean isConfigurable(){
        return (FabricLoader.getInstance().isModLoaded("modmenu") && FabricLoader.getInstance().isModLoaded("yet_another_config_lib_v3"));
    }

    public static void init(){
        if (isConfigurable()){
            YACLConfig.HANDLER.load();
        }
    }

    public static ConfigSettings getInstance(){
        if (isConfigurable()){
            return YACLConfig.HANDLER.instance();
        }
        return new ConfigSettings();
    }
}
