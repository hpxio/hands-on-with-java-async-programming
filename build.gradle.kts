plugins {
    java
    id("com.diffplug.spotless") version "7.0.1"
    id("org.springframework.boot") version "3.4.1"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "io.appx.codelabs"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

allprojects {
    apply(plugin = "java")
    apply(plugin = "com.diffplug.spotless")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")

    // auto-linter for *.java & *.gradle.kts files
    configure<com.diffplug.gradle.spotless.SpotlessExtension> {
        java {
            googleJavaFormat()
            importOrder(
                "java",
                "javax",
                "",
                "org.springframework",
                "org",
                "lombok",
                "io",
                "com",
            )
        }
    }
    spotless {
        kotlinGradle {
            target("*.gradle.kts")
            ktlint()
        }
    }

    repositories {
        mavenCentral()
    }

    tasks.bootJar { enabled = false }
    tasks.bootBuildImage { enabled = false }
}

subprojects {
    configurations {
        all {
            // enforce exclusion of Junit4 & Logback in support for log4j2
            exclude("ch.qos.logback:logback-classic")
            exclude("org.junit.vintage", "junit-vintage-engine")
            exclude("org.springframework.boot", "spring-boot-starter-logging")
        }
    }

    dependencies {
        implementation("org.springframework.boot:spring-boot-starter")
        implementation("org.springframework.boot:spring-boot-starter-web")
        implementation("org.springframework.boot:spring-boot-starter-log4j2")

        compileOnly("org.projectlombok:lombok:1.18.36")
        annotationProcessor("org.projectlombok:lombok:1.18.36")

        testImplementation("org.springframework.boot:spring-boot-starter-test")
        testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}

project(":hands-on-with-java-async-programming-server") {
    tasks.jar { enabled = true }
    tasks.bootJar { enabled = true }
}
