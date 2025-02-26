package org.http4k.mcp.server

import org.http4k.core.Request
import org.http4k.mcp.protocol.SessionId
import java.util.UUID

/**
 * Provides a session identifier for a given connection request. This can be used to allocate a particular session
 * which can be used to track the connection.
 */
fun interface SessionIdProvider {
    operator fun invoke(connectRequest: Request): SessionId

    companion object {
        /**
         * Provides a totally random session identifier.
         */
        val Random = SessionIdProvider { SessionId.of(UUID.randomUUID().toString()) }
    }
}
