plugins {
    id("com.github.darkxanter.library-convention")
}

dependencies {
    val logbackVersion: String by project
    val kotlinxCoroutinesVersion: String by project
    val testcontainersVersion: String by project

    api(projects.kotlockCore)

    implementation(platform(kotlin("bom")))
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxCoroutinesVersion")

    api("org.testcontainers:junit-jupiter:$testcontainersVersion")

    implementation("org.jetbrains.kotlin:kotlin-test")
    implementation("org.jetbrains.kotlin:kotlin-test-junit5")
}
