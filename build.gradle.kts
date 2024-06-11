import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension
import org.gradle.plugins.ide.idea.model.IdeaLanguageLevel

plugins {
    idea
    id("fr.brouillard.oss.gradle.jgitver")
    id("io.spring.dependency-management")
    id("name.remal.sonarlint") apply false
    id("com.diffplug.spotless") apply false
}

idea {
    project {
        languageLevel = IdeaLanguageLevel(21)
    }
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}

val armeriaBom: String by project
val springbootBom: String by project
val protobufBom: String by project
val grpc: String by project
val errorProneAnnotations: String by project
val tomcatAnnotationsApi: String by project

allprojects {
    group = "ru.petrelevich"

    repositories {
        mavenLocal()
        mavenCentral()
    }

    apply(plugin = "io.spring.dependency-management")
    dependencyManagement {
        dependencies {
            imports {
                mavenBom("org.springframework.boot:spring-boot-dependencies:$springbootBom")
                mavenBom("com.linecorp.armeria:armeria-bom:$armeriaBom")
                mavenBom("com.google.protobuf:protobuf-bom:$protobufBom")
            }
            dependency("io.grpc:grpc-netty:$grpc")
            dependency("io.grpc:grpc-protobuf:$grpc")
            dependency("io.grpc:grpc-stub:$grpc")
            dependency("com.google.errorprone:error_prone_annotations:$errorProneAnnotations")
            dependency("org.apache.tomcat:annotations-api:$tomcatAnnotationsApi")
        }
    }

    configurations.all {
        resolutionStrategy {
            failOnVersionConflict()
            force("com.google.guava:guava:32.1.2-jre")
            force("io.grpc:grpc-api:1.62.2")
            force("io.grpc:grpc-core:1.61.0")
            force("io.perfmark:perfmark-api:0.26.0")
            force("org.sonarsource.sslr:sslr-core:1.24.0.633")
            force("com.google.code.findbugs:jsr305:3.0.2")
            force("org.eclipse.platform:org.eclipse.osgi:3.18.300")
            force("org.eclipse.platform:org.eclipse.equinox.common:3.17.100")
            force("org.checkerframework:checker-qual:3.33.0")
        }
    }
}

subprojects {
    plugins.apply(JavaPlugin::class.java)
    extensions.configure<JavaPluginExtension> {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.compilerArgs.addAll(listOf("-Xlint:all,-serial,-processing"))
    }

    apply<name.remal.gradle_plugins.sonarlint.SonarLintPlugin>()
    apply<com.diffplug.gradle.spotless.SpotlessPlugin>()
    configure<com.diffplug.gradle.spotless.SpotlessExtension> {
        java {
            palantirJavaFormat("2.38.0")
        }
    }

    plugins.apply(fr.brouillard.oss.gradle.plugins.JGitverPlugin::class.java)
    extensions.configure<fr.brouillard.oss.gradle.plugins.JGitverPluginExtension> {
        strategy("PATTERN")
        nonQualifierBranches("main,master")
        tagVersionPattern("\${v}\${<meta.DIRTY_TEXT}")
        versionPattern(
                "\${v}\${<meta.COMMIT_DISTANCE}\${<meta.GIT_SHA1_8}" +
                        "\${<meta.QUALIFIED_BRANCH_NAME}\${<meta.DIRTY_TEXT}-SNAPSHOT"
        )
    }

    tasks.withType<Test> {
        useJUnitPlatform()
        testLogging.showExceptions = true
        reports {
            junitXml.required.set(true)
            html.required.set(true)
        }
    }
}

tasks {
    val managedVersions by registering {
        doLast {
            project.extensions.getByType<DependencyManagementExtension>()
                .managedVersions
                .toSortedMap()
                .map { "${it.key}:${it.value}" }
                .forEach(::println)
        }
    }
}