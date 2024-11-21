import org.http4k.internal.ModuleLicense.Apache2

val license by project.extra { Apache2 }

plugins {
    id("org.http4k.community")
    id("org.http4k.connect.module")
    id("org.http4k.connect.fake")
}

dependencies {
    api(project(":http4k-template-pebble"))
    testFixturesApi("software.amazon.awssdk:s3:_")
    testFixturesApi(testFixtures(project(":http4k-connect-amazon-core")))
}