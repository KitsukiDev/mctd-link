dependencies {
    implementation(project(":common"))
    implementation("ch.qos.logback:logback-classic:1.4.14")
    implementation("net.dv8tion:JDA:5.0.0-beta.18") {
        exclude(module = "opus-java")
    }
}

base {
    archivesName.set("mctdlink-discord")
}

tasks.shadowJar {
    manifest {
        attributes(Pair("Main-Class", "fr.kitsxki_.mctdlink.discord.Bootstrap"))
    }
}
