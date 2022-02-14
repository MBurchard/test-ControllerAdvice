import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  id("com.github.ben-manes.versions")
  id("io.spring.dependency-management")
  id("org.springframework.boot")
  kotlin("jvm")
  kotlin("plugin.spring")
}

val javaVersion = JavaVersion.VERSION_11
val kotlinLoggingVersion: String by project

group = "de.mbur"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = javaVersion

configurations {
  compileOnly {
    extendsFrom(configurations.annotationProcessor.get())
  }
}

repositories {
  mavenCentral()
}

dependencies {
  annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

  implementation("org.springframework.boot:spring-boot-starter-actuator")
  implementation("org.springframework.boot:spring-boot-starter-security")
  implementation("org.springframework.boot:spring-boot-starter-web")
  implementation("org.springframework.boot:spring-boot-starter-mustache")

  implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
  implementation("org.jetbrains.kotlin:kotlin-reflect")
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

  implementation("io.github.microutils:kotlin-logging-jvm:$kotlinLoggingVersion")

  developmentOnly("org.springframework.boot:spring-boot-devtools")

  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testImplementation("org.springframework.security:spring-security-test")
}

springBoot {
  buildInfo()
}

fun isStable(version: String): Boolean {
  val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.toUpperCase().contains(it) }
  val nonStableKeyword = listOf("preview", "rc").any { version.toLowerCase().contains(it) }
  val regex =
    "^(0|[1-9]\\d*)\\.(0|[1-9]\\d*)\\.(0|[1-9]\\d*)(?:\\.jre(:?8|11))?(?:-((?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*)(?:\\.(?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*))*))?(?:\\+([0-9a-zA-Z-]+(?:\\.[0-9a-zA-Z-]+)*))?\$".toRegex()
  return !nonStableKeyword && (stableKeyword || regex.matches(version))
}

tasks.withType<DependencyUpdatesTask> {
  rejectVersionIf {
    // uncomment when debugging dependency resolution process
    // val isStableCurrent = isStable(currentVersion)
    // val isStableCandidate = isStable(candidate.version)
    // println("${candidate.group}:${candidate.module}: $currentVersion (stable: $isStableCurrent) -> ${candidate.version} (stable: $isStableCandidate)")
    val reject = !isStable(candidate.version) && isStable(currentVersion)
    if (reject) {
      println("reject ${candidate.group}:${candidate.module}: $currentVersion -> ${candidate.version}")
    }
    reject
  }
}

tasks.withType<KotlinCompile> {
  kotlinOptions {
    freeCompilerArgs = listOf("-Xjsr305=strict")
    jvmTarget = javaVersion.toString()
  }
}

tasks.withType<Test> {
  useJUnitPlatform()
}

tasks.wrapper {
  gradleVersion = "7.3.3"
  distributionType = Wrapper.DistributionType.ALL
}
