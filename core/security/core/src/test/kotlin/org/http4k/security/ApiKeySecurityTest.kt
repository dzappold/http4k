package org.http4k.security


import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.http4k.core.HttpHandler
import org.http4k.core.Method.GET
import org.http4k.core.Method.OPTIONS
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.core.Status.Companion.UNAUTHORIZED
import org.http4k.lens.Header
import org.http4k.lens.Query
import org.http4k.lens.int
import org.http4k.lens.string
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ApiKeySecurityTest {

    @Test
    fun `valid API key is granted access and result carried through`() {
        val param = Query.int().required("name")
        val next: HttpHandler = { Response(OK).body("hello") }

        val response = ApiKeySecurity(param, { true }).filter(next)(Request(GET, "?name=1"))

        assertThat(response.status, equalTo(OK))
        assertThat(response.bodyString(), equalTo("hello"))
    }

    @Test
    fun `OPTIONS request is granted access even with no API key if toggled off`() {
        val param = Query.int().required("name")
        val next: HttpHandler = { Response(OK).body("hello") }

        val response = ApiKeySecurity(param, { true }, false).filter(next)(Request(OPTIONS, "/"))

        assertThat(response.status, equalTo(OK))
        assertThat(response.bodyString(), equalTo("hello"))
    }

    @Test
    fun `missing API key is unauthorized`() {
        val param = Query.int().required("name")
        val next: HttpHandler = { Response(OK).body("hello") }

        val response = ApiKeySecurity(param, { true }).filter(next)(Request(GET, ""))

        assertThat(response.status, equalTo(UNAUTHORIZED))
    }

    @Test
    fun `bad API key is unauthorized`() {
        val param = Query.int().required("name")
        val next: HttpHandler = { Response(OK).body("hello") }

        val response = ApiKeySecurity(param, { true }).filter(next)(Request(GET, "?name=asdasd"))

        assertThat(response.status, equalTo(UNAUTHORIZED))
    }

    @Test
    fun `unknown API key is unauthorized`() {
        val param = Query.int().required("name")
        val next: HttpHandler = { Response(OK).body("hello") }

        val response = ApiKeySecurity(param, { false }).filter(next)(Request(GET, "?name=1"))

        assertThat(response.status, equalTo(UNAUTHORIZED))
    }

    @Nested
    inner class WithConsumer {
        @Test
        fun `valid API key is granted access and result carried through`() {
            val param = Query.int().required("name")
            val consumer = Header.string().required("consumer-name")
            val next: HttpHandler = { Response(OK).body("hello ${consumer[it]}") }

            val response = ApiKeySecurity(param, consumer, { if (it == 1) "found-consumer" else null }).filter(next)(Request(GET, "?name=1"))

            assertThat(response.status, equalTo(OK))
            assertThat(response.bodyString(), equalTo("hello found-consumer"))
        }

        @Test
        fun `API key without consumer is unauthorized`() {
            val param = Query.int().required("name")
            val consumer = Header.string().required("consumer-name")
            val next: HttpHandler = { Response(OK).body("hello") }

            val response = ApiKeySecurity(param, consumer, { if (it == 1) "found-consumer" else null }).filter(next)(Request(GET, "?name=2"))

            assertThat(response.status, equalTo(UNAUTHORIZED))
        }


        @Test
        fun `OPTIONS request is granted access even with no API key if toggled off`() {
            val param = Query.int().required("name")
            val consumer = Header.string().defaulted("consumer-name", "unknown-consumer")
            val next: HttpHandler = { Response(OK).body("hello ${consumer[it]}") }

            val response = ApiKeySecurity(param, consumer, { "found-consumer" }, false).filter(next)(Request(OPTIONS, "/"))

            assertThat(response.status, equalTo(OK))
            assertThat(response.bodyString(), equalTo("hello unknown-consumer"))
        }

        @Test
        fun `missing API key is unauthorized`() {
            val param = Query.int().required("name")
            val consumer = Header.string().required("consumer-name")
            val next: HttpHandler = { Response(OK).body("hello") }

            val response = ApiKeySecurity(param, consumer, { "found-consumer" }).filter(next)(Request(GET, ""))

            assertThat(response.status, equalTo(UNAUTHORIZED))
        }

        @Test
        fun `bad API key is unauthorized`() {
            val param = Query.int().required("name")
            val consumer = Header.string().required("consumer-name")
            val next: HttpHandler = { Response(OK).body("hello") }

            val response = ApiKeySecurity(param, consumer, { "found-consumer" }).filter(next)(Request(GET, "?name=asdasd"))

            assertThat(response.status, equalTo(UNAUTHORIZED))
        }

        @Test
        fun `unknown API key is unauthorized`() {
            val param = Query.int().required("name")
            val next: HttpHandler = { Response(OK).body("hello") }

            val response = ApiKeySecurity(param, { false }).filter(next)(Request(GET, "?name=1"))

            assertThat(response.status, equalTo(UNAUTHORIZED))
        }
    }
}
