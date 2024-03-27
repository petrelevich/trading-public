plugins {
    id("com.google.cloud.tools.jib")
    id("idea")
}

dependencies {
    implementation(project(":tkf-contract"))
    implementation("com.linecorp.armeria:armeria-grpc")
    implementation("ch.qos.logback:logback-classic")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    implementation ("io.micrometer:micrometer-registry-prometheus")

    compileOnly ("org.projectlombok:lombok")
    annotationProcessor ("org.projectlombok:lombok")
}


jib {
    container {
        creationTime.set("USE_CURRENT_TIMESTAMP")
    }
    from {
        image = "bellsoft/liberica-openjdk-alpine-musl:21.0.1"
    }

    to {
        image = "localrun/datafeed-tks"
        tags = setOf(project.version.toString())
    }
}

//tasks {
//    build {
//        dependsOn(jibBuildTar)
//    }
//}
