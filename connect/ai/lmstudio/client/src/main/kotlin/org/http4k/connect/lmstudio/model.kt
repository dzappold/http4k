package org.http4k.connect.lmstudio

import dev.forkhandles.values.NonBlankStringValueFactory
import dev.forkhandles.values.StringValue
import org.http4k.ai.model.ModelName
import org.http4k.ai.model.StopReason

class Org private constructor(value: String) : StringValue(value) {
    companion object : NonBlankStringValueFactory<Org>(::Org) {
        val ALL = Org.of("*")
        val DEFAULT = Org.of("organization-owner")
    }
}

class ObjectType private constructor(value: String) : StringValue(value) {
    companion object : NonBlankStringValueFactory<ObjectType>(::ObjectType) {
        val List = ObjectType.of("list")
        val Model = ObjectType.of("model")
        val ChatCompletion = ObjectType.of("chat.completion")
        val ChatCompletionChunk = ObjectType.of("chat.completion.chunk")
        val Embedding = ObjectType.of("embedding")
        val ModelPermission = ObjectType.of("model_permission")
    }
}

class ObjectId private constructor(value: String) : StringValue(value) {
    companion object : NonBlankStringValueFactory<ObjectId>(::ObjectId)
}

val ModelName.Companion.CHAT_MODEL get() = ModelName.of("chat-model")
val ModelName.Companion.EMBEDDING_MODEL get() = ModelName.of("embedding-model")

class ResponseFormatType private constructor(value: String) : StringValue(value) {
    companion object : NonBlankStringValueFactory<ResponseFormatType>(::ResponseFormatType) {
        val JsonObject = ResponseFormatType.of("json_object")
        val url = ResponseFormatType.of("url")
        val text = ResponseFormatType.of("text")
    }
}

class TokenId private constructor(value: String) : StringValue(value) {
    companion object : NonBlankStringValueFactory<TokenId>(::TokenId)
}

class User private constructor(value: String) : StringValue(value) {
    companion object : NonBlankStringValueFactory<User>(::User)
}

val StopReason.Companion.stop get() = StopReason.of("stop")
val StopReason.Companion.length get() = StopReason.of("length")
val StopReason.Companion.content_filter get() = StopReason.of("content_filter")
val StopReason.Companion.tool_calls get() = StopReason.of("tool_calls")
