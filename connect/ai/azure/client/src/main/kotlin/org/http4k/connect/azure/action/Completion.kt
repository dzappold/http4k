package org.http4k.connect.azure.action

import org.http4k.ai.model.MaxTokens
import org.http4k.ai.model.UserPrompt
import org.http4k.ai.model.Temperature
import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.azure.AzureAIMoshi.autoBody
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.with
import se.ansman.kotshi.JsonSerializable

@Http4kConnectAction
@JsonSerializable
data class Completion(
    val prompt: UserPrompt,
    val presence_penalty: Double = 0.0,
    val frequency_penalty: Double = 0.0,
    val temperature: Temperature = Temperature.ONE,
    override val stream: Boolean = false,
    val max_tokens: MaxTokens? = null,
    val seed: Integer? = null,
    val stop: List<String>? = null,
    val n: Integer? = null,
    val top_p: Double = 1.0,
) : ModelCompletion {
    override fun toRequest() = Request(POST, "/completions")
        .with(autoBody<Completion>().toLens() of this)

    constructor(prompt: UserPrompt, max_tokens: MaxTokens, stream: Boolean = true) : this(
        prompt,
        max_tokens = max_tokens,
        stream = stream,
        top_p = 1.0
    )

    override fun content() = listOf(Message.User(prompt.value))

}
