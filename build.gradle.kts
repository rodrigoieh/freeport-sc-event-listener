plugins {
    kotlin("jvm") version "1.5.31"
    kotlin("plugin.allopen") version "1.5.31"
    kotlin("plugin.jpa") version "1.5.31"

    id("io.quarkus") version "2.5.0.Final"

    idea
}

group = "nft.freeport"

repositories {
    mavenLocal()
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    // Quarkus
    implementation(enforcedPlatform("io.quarkus:quarkus-universe-bom:2.5.0.Final"))
    implementation("io.quarkus:quarkus-cache")
    implementation("io.quarkus:quarkus-config-yaml")
    implementation("io.quarkus:quarkus-smallrye-health")

    // Messaging
    implementation("io.quarkus:quarkus-smallrye-reactive-messaging")

    // DB
    implementation("io.quarkus:quarkus-jdbc-postgresql")
    implementation("io.quarkus:quarkus-hibernate-orm-panache-kotlin")
    implementation("io.quarkus:quarkus-flyway")

    // Clients
    implementation("com.github.cerebellum-network:ddc-client-kotlin:1.2.0.Final")
    implementation("io.quarkus:quarkus-rest-client-jackson")

    // Hash
    implementation("com.github.komputing:kbase58:0.2")
    implementation("com.github.komputing.khex:core:1.1.2")
    implementation("com.github.komputing.khex:extensions:1.1.2")

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
    testImplementation("io.quarkus:quarkus-junit5-mockito")
    testImplementation("org.hamcrest:hamcrest:2.2")
    testImplementation("org.mockito.kotlin:mockito-kotlin:4.0.0")
    testImplementation("com.github.tomakehurst:wiremock:2.27.2")
    testImplementation("io.kotest:kotest-assertions-core-jvm:4.6.3")

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
    annotation("javax.persistence.Entity")
}

idea.module {
    isDownloadJavadoc = true
    isDownloadSources = true
}
