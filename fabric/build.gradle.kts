import net.morthen.gradle.multiloader.api.Loader

plugins {
    id("net.morthen.gradle.multiloader")
}

multiloader {
    loader = Loader.FABRIC
}

dependencies {
    implementation("com.terraformersmc:modmenu:18.0.2")
}