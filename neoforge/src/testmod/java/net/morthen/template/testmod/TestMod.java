package net.morthen.template.testmod;

import net.morthen.resourceconfigapi.tesmod.TestmodConstants;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(TestmodConstants.MOD_ID)
public class TestMod {
    public TestMod(IEventBus eventBus) {
        TestmodConstants.commonInit();
    }
}
