import org.http4k.internal.ModuleLicense.Apache2

description = "Http4k Serverless support for Apache OpenWhisk"

val license by project.extra { Apache2 }

plugins {
    id("org.http4k.community")
}

dependencies {
    api(project(":http4k-serverless-core"))
    api(project(":http4k-format-gson"))
}
