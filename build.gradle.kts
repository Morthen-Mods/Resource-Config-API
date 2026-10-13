plugins {
    id("java")
    id("idea")
    id("net.morthen.gradle.multiloader") version "0.1.13" apply false

    id("net.fabricmc.fabric-loom") version "1.18.4" apply false
    id("net.minecraftforge.gradle") version "7.0.40" apply false
    id("net.neoforged.moddev") version "2.0.148" apply false

    id("me.modmuss50.mod-publish-plugin") version "2.2.1" apply false
}

subprojects {
    pluginManager.apply("idea")
    idea.module.isDownloadSources = true
}
