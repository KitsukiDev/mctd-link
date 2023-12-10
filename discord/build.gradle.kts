dependencies {
    implementation(project(":common"))
    implementation("net.dv8tion:JDA:5.0.0-beta.18") {
        exclude(module = "opus-java")
    }
}

tasks.shadowJar {
    manifest {
        attributes(Pair("Main-Class", "fr.kitsxki_.mctdlink.discord.Bootstrap"))
    }
}
