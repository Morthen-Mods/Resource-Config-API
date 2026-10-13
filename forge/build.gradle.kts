plugins {
    id("net.morthen.gradle.multiloader")
}

multiloader {
    loader = "forge"

    forgeVersion = providers.gradleProperty("forge")

    withTestMod()
    withModPublish()

    applyMetadataReplacements(listOf("pack.mcmeta", "META-INF/mods.toml"), mapOf(
        "forge_version" to forgeVersion.get(),
        "issues_url" to providers.gradleProperty("issues_url")
    ))
}
