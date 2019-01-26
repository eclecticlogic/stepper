plugins {
    java
    antlr
}

group = "com.eclecticlogic"
version = "1.0-SNAPSHOT"

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
    compile ("org.antlr", "ST4", "4.1")
    implementation ("com.google.code.gson", "gson", "2.8.5")
    testCompile("junit", "junit", "4.12")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

tasks {
    generateGrammarSource {
        arguments.addAll(listOf("-package", "com.eclecticlogic.ezra.antlr"))

        doLast {
            copy {
                from("build/generated-src/antlr/main/")
                include("*.*")
                into("build/generated-src/antlr/main/com/eclecticlogic/ezra/antlr")
            }
            project.delete(fileTree("build/generated-src/antlr/main").include("*.*"))
        }
    }


}
