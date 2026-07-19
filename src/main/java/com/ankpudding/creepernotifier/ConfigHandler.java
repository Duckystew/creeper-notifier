package com.ankpudding.creepernotifier;


import com.google.gson.GsonBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.autogen.AutoGen;
import dev.isxander.yacl3.config.v2.api.autogen.IntSlider;
import dev.isxander.yacl3.config.v2.api.autogen.StringField;
import dev.isxander.yacl3.config.v2.api.autogen.TickBox;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.Identifier;

public class ConfigHandler {
    public static ConfigClassHandler<ConfigHandler> HANDLER = ConfigClassHandler.createBuilder(ConfigHandler.class)
            .id(Identifier.fromNamespaceAndPath("creeper-notifier", "config"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(FabricLoader.getInstance().getConfigDir().resolve("creeper-notifier.json5"))
                    .appendGsonBuilder(GsonBuilder::setPrettyPrinting) // not needed, pretty print by default
                    .setJson5(true)
                    .build())
            .build();

    @SerialEntry
    @AutoGen(category = "main")
    @TickBox
    public boolean modEnabled = true;

    @SerialEntry
    @AutoGen(category = "main")
    @IntSlider(min = 0, max = 25, step = 5)
    public int creeperDetectionDistance = 10;

}
