package net.morthen.template.testmod;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.morthen.resourceconfigapi.tesmod.TestmodConstants;

@Mod(TestmodConstants.MOD_ID)
public class TestMod {
    public TestMod(FMLJavaModLoadingContext context) {
        TestmodConstants.commonInit();
    }
}
