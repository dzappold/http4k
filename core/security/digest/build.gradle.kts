import org.http4k.internal.ModuleLicense.Apache2

description = "Http4k Security Digest support"

val license by project.extra { Apache2 }

plugins {
    id("org.http4k.community")
}

dependencies {
    api(project(":http4k-core"))
    api(project(":http4k-security-core"))
    testImplementation(testFixtures(project(":http4k-core")))
}