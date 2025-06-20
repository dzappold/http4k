package org.http4k.security.oauth.server

import org.http4k.core.Uri
import org.http4k.security.Nonce
import org.http4k.security.ResponseMode
import org.http4k.security.ResponseType
import org.http4k.security.ResponseType.Code
import org.http4k.security.State
import org.http4k.security.oauth.server.request.RequestObject
import org.http4k.security.openid.RequestJwtContainer
import java.util.*

data class AuthRequest(
    val client: ClientId,
    val scopes: List<String>,
    val redirectUri: Uri?,
    val state: State?,
    val responseType: ResponseType = Code,
    val nonce: Nonce? = null,
    val responseMode: ResponseMode? = null,
    val request: RequestJwtContainer? = null,
    val requestObject: RequestObject? = null,
    val additionalProperties: Map<String, Any> = emptyMap(),
    val codeChallenge: String? = null,
    val resourceUri: Uri? = null,
) {

    fun isOIDC() = scopes.map { it.lowercase(Locale.getDefault()) }.contains(OIDC_SCOPE)

    companion object {

        const val OIDC_SCOPE = "openid"
    }
}
