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
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

application {
    mainClass = "com.tollfreeroad.kafkawalk.Producer"
}