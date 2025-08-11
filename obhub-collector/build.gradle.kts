plugins {
    id("java")
    id("org.springframework.boot") version "3.3.2"
    id("io.spring.dependency-management") version "1.1.5"
}

group = "io.obhub.collector"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencyManagement {
    imports {
        mavenBom("io.opentelemetry.instrumentation:opentelemetry-instrumentation-bom:1.26.0")
        mavenBom("io.opentelemetry:opentelemetry-bom:1.26.0")
    }
}

dependencies {

    implementation(project(":obhub-receiver"))

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    implementation(platform("io.opentelemetry.instrumentation:opentelemetry-instrumentation-bom:1.26.0"))

    implementation("com.google.guava:guava:33.0.0-jre")
    implementation("org.springframework.boot:spring-boot-starter")


    // Slf4J / logback
    implementation("org.slf4j:slf4j-api:2.0.17")
    implementation("ch.qos.logback:logback-core:1.5.18")
    implementation("ch.qos.logback:logback-classic:1.5.18")
    implementation("io.opentelemetry.instrumentation:opentelemetry-logback-appender-1.0:1.26.0-alpha")

    // OpenTelemetry core
    implementation("io.opentelemetry:opentelemetry-api")
    implementation("io.opentelemetry:opentelemetry-sdk")
    implementation("io.opentelemetry:opentelemetry-exporter-otlp")

    implementation("io.opentelemetry:opentelemetry-api-logs:1.26.0-alpha")
    implementation("io.opentelemetry.semconv:opentelemetry-semconv:1.34.0")
    implementation("io.opentelemetry:opentelemetry-exporter-otlp-logs:1.26.0-alpha")
    implementation("io.opentelemetry.proto:opentelemetry-proto:1.2.0-alpha")
    implementation("io.grpc:grpc-protobuf:1.57.2")
    implementation("io.grpc:grpc-stub:1.57.2")
    implementation("io.grpc:grpc-netty-shaded:1.57.2")

}

tasks.named<Test>("test") {
    // Use JUnit Platform for unit tests.
    useJUnitPlatform()
}
