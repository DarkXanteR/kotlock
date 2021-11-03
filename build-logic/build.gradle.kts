plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
}

dependencies {
    val kotlinVersion: String by project

    api("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
}