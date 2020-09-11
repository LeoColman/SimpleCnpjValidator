import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.0"
    `maven-publish`
    signing
    id("org.jetbrains.dokka") version "0.10.1"
    id("io.gitlab.arturbosch.detekt").version("1.11.0")
    
}

group = "br.com.colman.simplecnpjvalidator"
version = System.getenv("RELEASE_VERSION") ?: "local"

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    testImplementation("io.kotest:kotest-runner-junit5-jvm:4.2.4")
    testImplementation("io.kotest:kotest-property-jvm:4.2.4")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.withType<Test> {
    useJUnitPlatform()
}

val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.getByName("main").allSource)
}

val javadocJar by tasks.registering(Jar::class) {
    val javadoc = tasks["dokka"] as DokkaTask
    javadoc.outputFormat = "javadoc"
    javadoc.outputDirectory = "$buildDir/javadoc"
    dependsOn(javadoc)
    archiveClassifier.set("javadoc")
    from(javadoc.outputDirectory)
}

publishing {
    repositories {
        
        maven("https://oss.sonatype.org/service/local/staging/deploy/maven2") {
            credentials {
                username = System.getenv("OSSRH_USERNAME")
                password = System.getenv("OSSRH_PASSWORD")
            }
        }
    }
    
    publications {
        
        register("mavenJava", MavenPublication::class) {
            from(components["java"])
            artifact(sourcesJar.get())
            artifact(javadocJar.get())
            
            pom {
                name.set("SimpleCnpjValidator")
                description.set("Simple CNPJ Validator")
                url.set("https://www.github.com/LeoColman/SimpleCnpjValidator")
                
                
                scm {
                    connection.set("scm:git:http://www.github.com/LeoColman/SimpleCnpjValidator/")
                    developerConnection.set("scm:git:http://github.com/LeoColman/")
                    url.set("https://www.github.com/LeoColman/SimpleCnpjValidator")
                }
                
                licenses {
                    license {
                        name.set("The Apache 2.0 License")
                        url.set("https://opensource.org/licenses/Apache-2.0")
                    }
                }
                
                developers {
                    developer {
                        id.set("LeoColman")
                        name.set("Leonardo Colman Lopes")
                        email.set("leonardo.dev@colman.com.br")
                    }
                }
            }
        }
    }
}

val signingKey: String? by project
val signingPassword: String? by project

signing {
    useGpgCmd()
    if(signingKey != null && signingPassword != null) {
        useInMemoryPgpKeys(signingKey, signingPassword)
    }

    sign(publishing.publications["mavenJava"])
}