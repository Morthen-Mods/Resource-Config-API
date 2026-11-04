import net.morthen.gradle.multiloader.api.Loader

plugins {
    id("net.morthen.gradle.multiloader")
}

multiloader {
    loader = Loader.COMMON
}