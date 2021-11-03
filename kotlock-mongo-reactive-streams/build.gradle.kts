plugins {
    id("com.github.darkxanter.library-convention")
}

dependencies {
    val kotlinxCoroutinesVersion: String by project
    val mongodbVersion: String by project

    api(projects.kotlockCore)

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxCoroutinesVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactive:$kotlinxCoroutinesVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:$kotlinxCoroutinesVersion")

    implementation("org.mongodb:mongodb-driver-reactivestreams:$mongodbVersion")
    testImplementation(projects.kotlockTest)
}
