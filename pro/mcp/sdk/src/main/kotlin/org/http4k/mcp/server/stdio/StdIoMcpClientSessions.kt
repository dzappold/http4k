package org.http4k.mcp.server.stdio

import org.http4k.core.Request
import org.http4k.mcp.model.CompletionStatus
import org.http4k.mcp.protocol.SessionId
import org.http4k.mcp.server.protocol.ClientSessions
import org.http4k.mcp.util.McpJson
import org.http4k.mcp.util.McpNodeType
import java.io.Writer
import java.util.UUID

class StdIoMcpClientSessions(private val writer: Writer) : ClientSessions<Unit, Unit> {
    override fun ok() {}

    override fun send(sessionId: SessionId, message: McpNodeType, status: CompletionStatus) = with(writer) {
        write(McpJson.compact(message) + "\n")
        flush()
    }

    override fun error() = Unit

    override fun onClose(sessionId: SessionId, fn: () -> Unit) = fn()

    override fun new(connectRequest: Request, transport: Unit) = SessionId.of(UUID.randomUUID().toString())
}
