plugins {
    id("net.morthen.gradle.multiloader")
}

multiloader {
    neoFormVersion = providers.gradleProperty("neoform")

    withTestMod()
}
