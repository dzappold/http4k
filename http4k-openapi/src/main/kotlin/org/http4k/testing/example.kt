package org.http4k.testing

import org.http4k.contract.openapi.ApiInfo
import org.http4k.contract.openapi.v3.CRoute
import org.http4k.contract.openapi.v3.bind
import org.http4k.contract.openapi.v3.meta
import org.http4k.contract.openapi.v3.openApi3
import org.http4k.contract.openapi.v3.routes
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.format.Jackson
import org.http4k.routing.bind

val croute1: CRoute = "/{bob}" bind GET meta {} to { Response(OK) }
val croute2: CRoute = "/{bob2}" bind POST meta {} to { Response(OK) }
val croute3: Iterable<CRoute> = "/{bob2}" bind routes(
    GET meta {} to { r: Request -> Response(OK) },
    POST meta {} to { r: Request -> Response(OK) }
)

fun main() {
    val a = openApi3(
        ApiInfo("title", "version"),
        routes(
            croute1,
            croute2,
            croute3
        ),
        Jackson
    )

    a(Request(GET, ""))
}
