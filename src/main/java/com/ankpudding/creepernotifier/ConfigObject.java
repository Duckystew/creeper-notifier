package com.ankpudding.creepernotifier;

import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.autogen.AutoGen;
import dev.isxander.yacl3.config.v2.api.autogen.IntSlider;
import dev.isxander.yacl3.config.v2.api.autogen.TickBox;

public class ConfigObject {
    @SerialEntry
    @AutoGen(category = "main")
    @TickBox
    public boolean modEnabled = true;

    @SerialEntry
    @AutoGen(category = "main")
    @IntSlider(min = 0, max = 25, step = 5)
    public int creeperDetectionDistance = 10;
}
