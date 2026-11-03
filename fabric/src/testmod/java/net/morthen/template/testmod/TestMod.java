package net.morthen.template.testmod;

import net.fabricmc.api.ModInitializer;
import net.morthen.resourceconfigapi.tesmod.TestmodConstants;

public class TestMod implements ModInitializer {
    @Override
    public void onInitialize() {
        TestmodConstants.commonInit();
    }
}
