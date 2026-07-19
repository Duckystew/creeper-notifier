package com.ankpudding.creepernotifier;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.minecraft.client.gui.screens.Screen;

public class ModMenuCompat implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory(){
        return parentScreen -> (Screen) ConfigHandler.HANDLER.generateGui().generateScreen(parentScreen);
    }
}