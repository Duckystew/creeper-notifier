package com.ankpudding.creepernotifier;

import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.autogen.*;

public class ConfigObject {
    @SerialEntry
    @AutoGen(category = "main")
    @TickBox
    public boolean modEnabled = true;

    @SerialEntry
    @AutoGen(category = "main")
    @IntSlider(min = 0, max = 25, step = 1)
    public int creeperDetectionDistance = 10;


    @SerialEntry
    @AutoGen(category = "main", group = "alertSettings")
    @FloatSlider(min = 0.0f, max = 10.0f, step = 0.1f)
    public float alertVolume = 5.0f;

    @SerialEntry
    @AutoGen(category = "main", group = "alertSettings")
    @FloatSlider(min = 0.0f, max = 10.0f, step = 0.1f)
    public float alertPitch = 2.0f;

    @SerialEntry
    @AutoGen(category = "main", group = "alertSettings")
    @IntSlider(min = 1, max = 40, step = 1)
    public int alertInterval = 20;
}
