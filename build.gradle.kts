import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.72"
    application
}

group = "com.example"
version = "1.0.0-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation("com.jessecorbett:diskord:1.8.1")

    // This library is selected for convenience.  Feel free to replace it with your own, preferred logging library.
    implementation("org.slf4j:slf4j-simple:1.7.30")
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.set("com.example.MainKt")
}
