plugins {
    id("java")
    application
}

group = "com.tollfreeroad.kafkawalk"
version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.apache.kafka:kafka-clients:3.8.0")
    implementation("org.slf4j:slf4j-api:2.0.16")
    implementation("org.slf4j:slf4j-simple:2.0.16")
    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    implementation("com.launchdarkly:okhttp-eventsource:2.5.0")
}

tasks.test {
    useJUnitPlatform()
}

application {
    mainClass = "com.tollfreeroad.kafkawalk.Producer"
}
