package com.ankpudding.creepernotifier;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ModMenuCompat implements ModMenuApi {
    boolean myBooleanOption = true;
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory(){
        return parentScreen -> (Screen) YetAnotherConfigLib.createBuilder()
                .title(Component.literal("Title"))
                .category(ConfigCategory.createBuilder()
                        .name(Component.literal("Category name"))
                        .tooltip(Component.literal("Test tooltip"))
                        .group(OptionGroup.createBuilder()
                                .name(Component.literal("Group name"))
                                .description(OptionDescription.of(Component.literal("This text will appear when you hover over the name or focus on the collapse button with Tab.")))
                                .option(Option.<Boolean>createBuilder()
                                        .name(Component.literal("Boolean Option"))
                                        .description(OptionDescription.of(Component.literal("This text will appear as a tooltip when you hover over the option.")))
                                        .binding(true, () -> this.myBooleanOption, newVal -> this.myBooleanOption = newVal)
                                        .controller(TickBoxControllerBuilder::create)
                                        .build())
                                .build())
                        .build())
                .build()
                .generateScreen(parentScreen);

    }
}