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

    // Forge 1.20.1 (legacy FML) needs to know which output directory belongs to which mod. Without the mod ids
    // ForgeGradle passes main and testmod as one mod, FML then only reads the mods.toml of the testmod and fails
    // with "mods.toml missing metadata for modid resourceconfigapi". Each entry is doubled, FML wants two per mod.
    val forgeOutputs = project(":forge").layout.buildDirectory.dir("sourcesSets")
    val modId = providers.gradleProperty("mod_id").get()
    runs.matching { it.name.startsWith("testmod") }.configureEach {
        val main = forgeOutputs.get().dir("main").asFile.absolutePath
        val testmod = forgeOutputs.get().dir("testmod").asFile.absolutePath
        environment("MOD_CLASSES", listOf(
            "$modId%%$main", "$modId%%$main",
            "${modId}_testmod%%$testmod", "${modId}_testmod%%$testmod"
        ).joinToString(File.pathSeparator))
    }
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