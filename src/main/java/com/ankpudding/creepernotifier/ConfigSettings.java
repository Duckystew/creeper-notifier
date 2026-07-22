package com.ankpudding.creepernotifier;

import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.autogen.*;

public class ConfigSettings {
    @SerialEntry
    @AutoGen(category = "main")
    @TickBox
    public boolean modEnabled = true;

    @SerialEntry
    @AutoGen(category = "main")
    @TickBox
    public boolean alertTextVisible = true;

    @SerialEntry
    @AutoGen(category = "main")
    @IntSlider(min = 0, max = 25, step = 1)
    public int creeperDetectionDistance = 10;


    @SerialEntry
    @AutoGen(category = "main", group = "alertSettings")
    @FloatSlider(min = 0.0f, max = 1.0f, step = 0.1f)
    public float alertVolume = 1.0f;

    @SerialEntry
    @AutoGen(category = "main", group = "alertSettings")
    @FloatSlider(min = 0.0f, max = 2.0f, step = 0.1f)
    public float alertPitch = 2.0f;

    @SerialEntry
    @AutoGen(category = "main", group = "alertSettings")
    @IntSlider(min = 1, max = 40, step = 1)
    public int alertInterval = 20;

    @SerialEntry
    @AutoGen(category = "main", group = "gamemodeToggles")
    @TickBox
    public boolean enableInSurvival = true;

    @SerialEntry
    @AutoGen(category = "main", group = "gamemodeToggles")
    @TickBox
    public boolean enableInAdventure = true;

    @SerialEntry
    @AutoGen(category = "main", group = "gamemodeToggles")
    @TickBox
    public boolean enableInCreative = false;

    @SerialEntry
    @AutoGen(category = "main", group = "gamemodeToggles")
    @TickBox
    public boolean enableInSpectator = false;
}
