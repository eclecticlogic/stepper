plugins {
    java
    antlr
    groovy
    id("com.github.ben-manes.versions").version("0.20.0")
}

group = "com.eclecticlogic"
version = "0.1-SNAPSHOT"

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
    implementation ("org.antlr", "ST4", "4.1")
    implementation("com.google.guava", "guava", "27.0.1-jre")
    implementation ("com.google.code.gson", "gson", "2.8.5")
    implementation("com.jayway.jsonpath", "json-path", "2.4.0")

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
