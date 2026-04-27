plugins {
    kotlin("jvm") version "2.0.21"
    application
    id("org.jlleitschuh.gradle.ktlint") version "12.1.1"
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

// gradle run으로 실행시 표준입력을 사용하도록 설정
// 기본적으로 gradle은 표준입력을 애플리케이션 프로세스에 전달하지 않음
tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}

tasks.named("check") {
    dependsOn("ktlintCheck")
}
