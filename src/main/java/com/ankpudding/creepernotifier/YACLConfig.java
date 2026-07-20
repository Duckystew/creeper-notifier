package com.ankpudding.creepernotifier;


import com.google.gson.GsonBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.Identifier;

public class YACLConfig extends ConfigObject{
    public static ConfigClassHandler<ConfigObject> HANDLER = ConfigClassHandler.createBuilder(ConfigObject.class)
            .id(Identifier.fromNamespaceAndPath("creeper-notifier", "config"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(FabricLoader.getInstance().getConfigDir().resolve("creeper-notifier.json5"))
                    .appendGsonBuilder(GsonBuilder::setPrettyPrinting) // not needed, pretty print by default
                    .setJson5(true)
                    .build())
            .build();

}
