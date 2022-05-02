rootProject.name = "kotlock"

includeBuild("build-logic")
include("kotlock-core")
include("kotlock-mongo-reactive-streams")
include("kotlock-sql-exposed")
include("kotlock-test")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }

    val kotlinVersion: String by settings

    plugins {
        kotlin("jvm") version kotlinVersion
        id("org.jetbrains.dokka") version kotlinVersion
    }
}
