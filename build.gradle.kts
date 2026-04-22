plugins {
    kotlin("jvm") version "2.0.21"
    application
    id("org.jlleitschuh.gradle.ktlint") version "12.1.1"
}

application {
    mainClass.set("MainKt")
}

sourceSets {
    main {
        kotlin.srcDir("src")
    }
}

repositories {
    mavenCentral()
}

ktlint {
    verbose.set(true)
    outputToConsole.set(true)
    ignoreFailures.set(false)

    filter {
        include("src/**/*.kt")
        include("*.kts")
        exclude("**/build/**")
    }
}

tasks.named("check") {
    dependsOn("ktlintCheck")
}
