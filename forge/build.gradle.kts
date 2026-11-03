import net.morthen.gradle.multiloader.api.Loader

plugins {
    id("net.morthen.gradle.multiloader")
}

multiloader {
    loader = Loader.FORGE

    // Forge reads the mixin configs from the jar manifest and the run arguments
    mixins = listOf(
        "$modId.mixins.json"
    )
}