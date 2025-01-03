package org.http4k.core

import org.http4k.core.ContentType.Companion.TEXT_HTML
import org.http4k.lens.Header.CONTENT_TYPE
import org.http4k.lens.MultipartFormField
import org.http4k.lens.MultipartFormFile
import org.http4k.multipart.DiskLocation
import org.http4k.multipart.MultipartFormBuilder
import org.http4k.multipart.MultipartFormParser
import org.http4k.multipart.Part
import org.http4k.multipart.StreamingMultipartFormParts
import java.io.ByteArrayInputStream
import java.io.Closeable
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets.UTF_8
import java.util.UUID

sealed class MultipartEntity : Closeable {
    abstract val name: String
    internal abstract fun applyTo(builder: MultipartFormBuilder): MultipartFormBuilder

    data class Field(override val name: String, val value: String, val headers: Headers = emptyList(), val closeable: Closeable = Closeable { }) :
        MultipartEntity(), Closeable by closeable {

        override fun applyTo(builder: MultipartFormBuilder) = builder.field(name, value, headers)

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Field

            if (name != other.name) return false
            if (value != other.value) return false
            if (headers != other.headers) return false

            return true
        }

        override fun hashCode(): Int {
            var result = name.hashCode()
            result = 31 * result + value.hashCode()
            result = 31 * result + headers.hashCode()
            return result
        }
    }

    data class File(override val name: String, val file: MultipartFormFile, val headers: Headers = emptyList()) :
        MultipartEntity(), Closeable by file {

        override fun applyTo(builder: MultipartFormBuilder): MultipartFormBuilder =
            builder.file(name, file.filename, file.contentType.value, file.content, headers)
    }
}

fun HttpMessage.multipartIterator(): Iterator<MultipartEntity> {
    val boundary = CONTENT_TYPE(this)?.directives?.firstOrNull()?.second ?: ""

    return StreamingMultipartFormParts.parse(boundary.toByteArray(UTF_8), body.stream, UTF_8)
        .asSequence()
        .map {
            if (it.isFormField) MultipartEntity.Field(it.fieldName!!, it.contentsAsString, it.headers.toList())
            else MultipartEntity.File(
                it.fieldName!!,
                MultipartFormFile(it.fileName!!, ContentType(it.contentType!!, TEXT_HTML.directives), it.inputStream),
                it.headers.toList()
            )
        }.iterator()
}

/**
 * Represents a Multi-part that is backed by a stream, which should be closed after handling the content. The gotchas
 * which apply to StreamBody also apply here..
 **/
@ExposedCopyVisibility
data class MultipartFormBody private constructor(
    internal val formParts: List<MultipartEntity>,
    val boundary: String = UUID.randomUUID().toString(),
    internal val diskLocation: DiskLocation = DiskLocation.Temp()
) : Body, Closeable {

    override val length: Long? = null

    constructor(boundary: String = UUID.randomUUID().toString()) : this(emptyList(), boundary)

    override fun close() {
        formParts.forEach(MultipartEntity::close)
        diskLocation.close()
    }

    fun file(name: String) = files(name).firstOrNull()
    fun files(name: String) =
        formParts.filter { it.name == name }.mapNotNull { it as? MultipartEntity.File }.map { it.file }

    fun field(name: String) = fields(name).firstOrNull()
    fun fields(name: String) = formParts.filter { it.name == name }.mapNotNull { it as? MultipartEntity.Field }
        .map { MultipartFormField(it.value, it.headers) }

    fun fieldValue(name: String) = fieldValues(name).firstOrNull()
    fun fieldValues(name: String) =
        formParts.filter { it.name == name }.mapNotNull { it as? MultipartEntity.Field }.map { it.value }

    @JvmName("plus")
    operator fun plus(field: Pair<String, String>) =
        copy(formParts = formParts + MultipartEntity.Field(field.first, field.second))

    @JvmName("plusField")
    operator fun plus(field: Pair<String, MultipartFormField>) =
        copy(formParts = formParts + MultipartEntity.Field(field.first, field.second.value, field.second.headers))

    @JvmName("plusFile")
    operator fun plus(field: Pair<String, MultipartFormFile>) =
        copy(formParts = formParts + MultipartEntity.File(field.first, field.second))

    override val stream by lazy {
        formParts.fold(MultipartFormBuilder(boundary.toByteArray())) { memo, next ->
            next.applyTo(
                memo
            )
        }.stream()
    }
    override val payload: ByteBuffer by lazy { stream.use { ByteBuffer.wrap(it.readBytes()) } }
    override fun toString() = String(payload.array())

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MultipartFormBody

        if (formParts != other.formParts) return false
        if (boundary != other.boundary) return false

        return true
    }

    override fun hashCode(): Int {
        var result = formParts.hashCode()
        result = 31 * result + boundary.hashCode()
        return result
    }

    companion object {
        const val DEFAULT_DISK_THRESHOLD = 1000 * 1024

        fun from(
            httpMessage: HttpMessage,
            diskThreshold: Int = DEFAULT_DISK_THRESHOLD,
            diskLocation: DiskLocation = DiskLocation.Temp()
        ): MultipartFormBody {
            val boundary = CONTENT_TYPE(httpMessage)?.directives?.firstOrNull { it.first == "boundary" }?.second ?: ""

            val inputStream =
                httpMessage.body.run { if (hasContentToRead()) stream else ByteArrayInputStream(payload.array()) }
            val form = StreamingMultipartFormParts.parse(boundary.toByteArray(UTF_8), inputStream, UTF_8)

            val parts = MultipartFormParser(UTF_8, diskThreshold, diskLocation).formParts(form).map {
                if (it.isFormField) MultipartEntity.Field(it.fieldName!!, it.string(), it.headers.toList(), it)
                else MultipartEntity.File(
                    it.fieldName!!,
                    MultipartFormFile(
                        it.fileName!!,
                        ContentType(it.contentType!!, TEXT_HTML.directives),
                        it.newInputStream,
                        it
                    )
                )
            }
            return MultipartFormBody(parts, boundary, diskLocation)
        }
    }
}

internal fun Part.string(): String = when (this) {
    is Part.DiskBacked -> newInputStream.use { it.reader().readText() }
    is Part.InMemory -> String(bytes, encoding)
}
