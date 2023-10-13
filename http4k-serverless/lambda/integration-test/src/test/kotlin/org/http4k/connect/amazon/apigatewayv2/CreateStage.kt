package org.http4k.connect.amazon.apigatewayv2

import org.http4k.core.Body
import org.http4k.core.ContentType
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.with
import org.http4k.connect.amazon.apigatewayv2.ApiGatewayJackson.auto
import org.http4k.connect.amazon.kClass

class CreateStage(private val apiId: ApiId, private val stage: Stage) : AwsApiGatewayV2Action<Unit>(kClass()) {
    private val createStageLens =
        Body.auto<Stage>(contentType = ContentType.APPLICATION_JSON.withNoDirectives()).toLens()

    override fun toRequest() = Request(Method.POST, "/v2/apis/${apiId.value}/stages")
        .with(createStageLens of stage)
}