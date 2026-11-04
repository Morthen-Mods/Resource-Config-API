import net.morthen.gradle.multiloader.api.Loader

plugins {
    id("net.morthen.gradle.multiloader")
}

multiloader {
    loader = Loader.FABRIC
}

// 1.21.1 is obfuscated, so mods have to be remapped. The multiloader plugin applies Loom
// (and with it the modImplementation configuration) only in afterEvaluate.
afterEvaluate {
    dependencies {
        "modImplementation"("com.terraformersmc:modmenu:11.0.5")
    }
}
