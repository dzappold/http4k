package org.http4k.contract

import org.http4k.core.Filter
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.lens.LensFailure
import org.http4k.lens.Validator

internal class PreFlightExtractionFilter(meta: RouteMeta, preFlightExtraction: PreFlightExtraction) : Filter {
    private val preFlightChecks = (meta.preFlightExtraction ?: preFlightExtraction)(meta).toTypedArray()
    override fun invoke(next: HttpHandler): HttpHandler = {
        when (it.method) {
            Method.OPTIONS -> next(it)
            else -> {
                val failures = Validator.Strict(it, *preFlightChecks)
                if (failures.isEmpty()) next(it) else throw LensFailure(failures, target = it)
            }
        }
    }
}
