plugins {
    id("java")
    id("idea")
    id("net.morthen.gradle.multiloader") version "0.2.3"

    id("net.fabricmc.fabric-loom") version "1.19.3" apply false
    id("net.minecraftforge.gradle") version "7.0.40" apply false
    id("net.neoforged.moddev") version "2.0.148" apply false
}

// Shared settings for every subproject. Mod info and loader versions are read from gradle.properties.
multiloaderSettings {
    withTestmod()
    // Game directories of all runs live in common/runs
    commonRunDirectory = true

    applyMetadataReplacements(
        listOf("pack.mcmeta", "*.mixins.json", "fabric.mod.json", "META-INF/mods.toml"),
        mapOf(
            "fabric_api" to fabricApiVersion,
            "fabric_loader" to fabricLoaderVersion,
            "forge_version" to forgeVersion,
            "sources_url" to providers.gradleProperty("sources_url"),
            "issues_url" to providers.gradleProperty("issues_url")
        )
    )

    // Uploads to CurseForge (curseforge_id) and Modrinth (modrinth_id)
    withModPublish {
        dependencies {
            fabric {
                requires("fabric-api")
                optional("modmenu")
            }
        }
    }
}