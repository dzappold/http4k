package org.http4k.contract.openapi.v3

import org.http4k.contract.PreFlightExtraction
import org.http4k.contract.PreFlightExtraction.Companion.All
import org.http4k.contract.RouteMeta
import org.http4k.contract.RouteMetaDsl
import org.http4k.contract.openapi.ApiInfo
import org.http4k.contract.openapi.OpenApiExtension
import org.http4k.contract.security.Security
import org.http4k.core.Filter
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.NoOp
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.format.Jackson
import org.http4k.format.Json
import org.http4k.routing.PathMethod
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind

fun <NODE> openApi3(
    apiInfo: ApiInfo,
    routes: Iterable<CRoute>,
    json: Json<NODE>,
    security: Security? = null,
    preFlightExtraction: PreFlightExtraction = All,
    preSecurityFilter: Filter = Filter.NoOp,
    postSecurityFilter: Filter = Filter.NoOp,
    description: DescriptionRoute = DescriptionRoute("/"),
    extensions: List<OpenApiExtension> = emptyList(),
): RoutingHttpHandler = TODO()

data class DescriptionRoute(val descriptionPath: String = "/",
                            val descriptionSecurity: Security? = null,
                            val includeDescriptionRoute: Boolean = false)

val a = openApi3(
    ApiInfo("title", "version"),
    routes(
        "/{bob}" bind GET meta {} to { Response(OK) },
        "/{bob2}" bind POST meta {} to { Response(OK) },
        "/{bob2}" bind routes(
            GET meta {} to { r: Request -> Response(OK) },
            POST meta {} to { r: Request -> Response(OK) }
        )
    ),
    Jackson
)

infix fun String.bind(methodMetas: MethodMetaBindings): Iterable<CRoute> = TODO()

infix fun Method.meta(new: RouteMetaDsl.() -> Unit): MethodMeta = TODO()
infix fun PathMethod.meta(new: RouteMetaDsl.() -> Unit): PathMethodMeta = TODO()

data class MethodMeta(val method: Method, val meta: RouteMeta)

data class PathMethodMeta(val pathMethod: PathMethod, val meta: RouteMeta) {
    infix fun to(handler: HttpHandler): CRoute = TODO()
}

class MethodMetaBindings(vararg val bindings: Pair<MethodMeta, HttpHandler>)

fun routes(vararg routes: Iterable<CRoute>): Iterable<CRoute> = routes.map { it.toList() }.flatten()
fun routes(vararg routes: Pair<MethodMeta, HttpHandler>): MethodMetaBindings = MethodMetaBindings(*routes)

interface CRoute : Iterable<CRoute>
