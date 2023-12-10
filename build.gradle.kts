plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "com.github.johnrengelman.shadow")

    group = "fr.kitsxki_"
    version = "1.0-SNAPSHOT"

    repositories {
        mavenCentral()
        mavenLocal()
    }

    dependencies {
        implementation("org.jetbrains:annotations:24.1.0")
    }

    val targetJavaVersion = JavaVersion.VERSION_1_8
    java {
        sourceCompatibility = targetJavaVersion
        targetCompatibility = targetJavaVersion
        if (JavaVersion.current() < targetJavaVersion)
            toolchain.languageVersion.set(JavaLanguageVersion.of(targetJavaVersion.majorVersion))
    }

    tasks.withType(JavaCompile::class).configureEach {
        options.encoding = "UTF-8"
        if (targetJavaVersion >= JavaVersion.VERSION_1_10 || JavaVersion.current().isJava10Compatible)
            options.release.set(targetJavaVersion.majorVersion.toInt()) // The string represent a number, like "1" for Java1
    }

    tasks.shadowJar {
        mergeServiceFiles()
    }
}
