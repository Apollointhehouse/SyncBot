plugins {
    kotlin("jvm") version "1.9.20"
    application
}

group = "io.github.apollointhehouse"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.jessecorbett:diskord-bot:4.0.0")
}

application {
    mainClass.set("io.github.apollointhehouse.MainKt")
}
