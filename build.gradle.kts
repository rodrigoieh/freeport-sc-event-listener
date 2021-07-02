plugins {
    kotlin("jvm") version "1.4.32"
    kotlin("plugin.allopen") version "1.4.32"

    id("io.quarkus") version "2.0.0.Final"

    idea
}

group = "nft.davinci"

repositories {
    mavenLocal()
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    // Quarkus
    implementation(enforcedPlatform("io.quarkus:quarkus-universe-bom:2.0.0.Final"))
    implementation("io.quarkus:quarkus-config-yaml")
    implementation("io.quarkus:quarkus-smallrye-health")

    // Web
    implementation("io.quarkus:quarkus-resteasy-reactive-kotlin")
    implementation("io.quarkus:quarkus-resteasy-reactive-jackson")

    // DB
    implementation("io.quarkus:quarkus-reactive-pg-client")

    // Clients
    implementation("com.github.cerebellum-network:ddc-client-kotlin:1.0.0-RC14")
    implementation("io.quarkus:quarkus-rest-client-reactive-jackson")

    // Crypto
    implementation("com.github.cerebellum-network:ddc-encryption-impl-kotlin:1.5.0")

    // Kotlin
    implementation("io.quarkus:quarkus-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    // Logging
    implementation("io.quarkus:quarkus-logging-json")

    // Build
    implementation("io.quarkus:quarkus-container-image-jib")
    implementation("io.quarkus:quarkus-arc")

    // Tests
    testImplementation("io.quarkus:quarkus-junit5")
    testImplementation("io.rest-assured:rest-assured")
}

tasks {
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            jvmTarget = JavaVersion.VERSION_11.toString()
            javaParameters = true
        }
    }

    withType<Test> {
        useJUnitPlatform()
    }
}

allOpen {
    annotation("javax.enterprise.context.ApplicationScoped")
    annotation("javax.ws.rs.Path")
    annotation("io.quarkus.test.junit.QuarkusTest")
}

idea.module {
    isDownloadJavadoc = true
    isDownloadSources = true
}
