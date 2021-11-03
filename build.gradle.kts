allprojects {
    repositories {
        mavenCentral()
    }
}

println("version $version")

//subprojects {
//    apply(plugin = "kotlin")
//    apply(plugin = "java-library")
//    apply(plugin = "maven-publish")
//
//    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
//        kotlinOptions {
//            jvmTarget = JavaVersion.VERSION_11.toString()
//        }
//    }
//
//    publishing {
//        publications {
//            register("maven", MavenPublication::class) {
//                from(components["java"])
//            }
//        }
//    }
//}
