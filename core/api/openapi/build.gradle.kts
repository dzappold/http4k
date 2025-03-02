import org.http4k.internal.ModuleLicense.Apache2

description = "http4k typesafe HTTP contracts and OpenApi support"

val license by project.extra { Apache2 }

plugins {
    id("org.http4k.community")
}

dependencies {
    api(project(":http4k-contract"))
}

