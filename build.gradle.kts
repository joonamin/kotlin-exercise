plugins {
    kotlin("jvm") version "2.2.20"
    application
    id("org.jlleitschuh.gradle.ktlint") version "14.2.0"
    id("org.jetbrains.kotlinx.kover") version "0.9.8"
}

kotlin {
    jvmToolchain(21)
}
application {
    mainClass.set("MainKt")
}

sourceSets {
    main {
        kotlin.srcDir("src")
    }
    test {
        kotlin.srcDir("test")
    }
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("io.kotest:kotest-runner-junit5:5.9.1")
    testImplementation("io.kotest:kotest-assertions-core:5.9.1")
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

// gradle run으로 실행시 표준입력을 사용하도록 설정
// 기본적으로 gradle은 표준입력을 애플리케이션 프로세스에 전달하지 않음
tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}

tasks.named("check") {
    dependsOn("ktlintCheck")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
