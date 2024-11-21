import org.http4k.internal.ModuleLicense.Apache2

val license by project.extra { Apache2 }

plugins {
    id("org.http4k.community")
    id("org.http4k.connect.module")
    id("org.http4k.connect.fake")
}

dependencies {
    api(project(":http4k-connect-ai-openai-plugin"))
    api(project(":http4k-template-pebble"))
    api(project(":http4k-contract-ui-swagger"))
    api("de.sven-jacobs:loremipsum:_")
}