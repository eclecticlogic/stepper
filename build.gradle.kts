import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    java
    antlr
    groovy
    id("com.github.ben-manes.versions").version("0.20.0")
    `maven-publish`
    signing
    id("io.codearte.nexus-staging") version "0.20.0"
    id("org.springframework.boot") version "2.1.3.RELEASE"
} 


repositories {
    mavenCentral()
}

val antlrGenSrc by extra { "${project.buildDir}/generated-src"}

sourceSets {
    main {
        java {
            srcDirs(file("${antlrGenSrc}/antlr/main"))
        }
    }
}

dependencies {
    antlr ("org.antlr", "antlr4", "4.7.2")

    implementation(platform("software.amazon.awssdk:bom:2.5.45"))
    implementation("software.amazon.awssdk", "sfn")
    implementation("software.amazon.awssdk", "lambda")

    implementation ("org.antlr", "ST4", "4.1")
    implementation("com.google.guava", "guava", "27.0.1-jre")
    implementation ("com.google.code.gson", "gson", "2.8.5")
    implementation("com.jayway.jsonpath", "json-path", "2.4.0")
    implementation("com.beust", "jcommander", "1.72")
    implementation("org.yaml", "snakeyaml", "1.21")

    testCompile("junit", "junit", "4.12")
    testCompile("org.codehaus.groovy", "groovy-all", "2.5.6")
    testCompile("org.spockframework", "spock-core", "1.2-groovy-2.5")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

tasks {
    generateGrammarSource {
        arguments.addAll(listOf("-visitor", "-package", "com.eclecticlogic.stepper.antlr"))

        doLast {
            copy {
                from("build/generated-src/antlr/main/")
                include("*.*")
                into("build/generated-src/antlr/main/com/eclecticlogic/stepper/antlr")
            }
            project.delete(fileTree("build/generated-src/antlr/main").include("*.*"))
        }
    }
}

tasks.register<Jar>("sourcesJar") {
    from(sourceSets.main.get().allJava)
    archiveClassifier.set("sources")
}

tasks.register<Jar>("javadocJar") {
    from(tasks.javadoc)
    archiveClassifier.set("javadoc")
}

val classpathVal: Array<Any> = arrayOf(configurations.runtimeClasspath, sourceSets.main.get().getOutput())

tasks.register<BootJar>("stepperShade") {
    getArchiveClassifier().set("shade")
    classpath(classpathVal)
    mainClassName = "com.eclecticlogic.stepper.Stepper"
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            pom {
                name.set("Stepper")
                description.set("A programming language for AWS Step Functions")
                url.set("https://github.com/eclecticlogic/stepper")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("kabram")
                        name.set("Karthik Abram")
                        email.set("karthik@k2d2.org")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/eclecticlogic/stepper.git")
                    developerConnection.set("scm:git:git://github.com:eclecticlogic/stepper.git")
                    url.set("https://github.com/eclecticlogic/stepper")
                }
            }

            groupId = "com.eclecticlogic"
            artifactId = "stepper"
            version = "0.9.0"

            from(components["java"])
            artifact(tasks["sourcesJar"])
            artifact(tasks["javadocJar"])
            artifact(tasks["stepperShade"])
        }

        repositories {
            maven {
                // change URLs to point to your repos, e.g. http://my.org/repo
                val releasesRepoUrl = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
                val snapshotsRepoUrl = uri("https://oss.sonatype.org/content/repositories/snapshots")
                url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl
                credentials {
                    username = properties["nexusUsername"].toString()
                    password = properties["nexusPassword"].toString()
                }
            }
        }
    }
}

signing {
    useGpgCmd()
    sign(publishing.publications["maven"])
}

nexusStaging {
    packageGroup = "com.eclecticlogic"
}

/*
 How to publish:
 gradlew publish
 gradlew closeAndReleaseRepository
 /*