dependencies {
    implementation("com.fasterxml.jackson.core:jackson-databind:2.16.0")
    implementation("org.mongodb:mongodb-driver-sync:4.11.1")
    implementation("org.redisson:redisson:3.25.0")
    implementation("net.dv8tion:JDA:5.0.0-beta.18") {
        exclude(module = "opus-java")
    }
}

base {
    archivesName.set("mctdlink-common")
}
