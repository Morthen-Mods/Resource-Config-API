package net.morthen.template.testmod;

import net.minecraftforge.fml.common.Mod;
import net.morthen.resourceconfigapi.tesmod.TestmodConstants;

@Mod(TestmodConstants.MOD_ID)
public class TestMod {
    public TestMod() {
        TestmodConstants.commonInit();
    }
}
