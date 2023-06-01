package net.nitrin.phoenix.network.packet.idle

import io.netty.channel.ChannelDuplexHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.timeout.IdleState
import io.netty.handler.timeout.IdleStateEvent
import net.nitrin.phoenix.network.packet.channel.PacketChannel

class IdleHandler: ChannelDuplexHandler() {

    override fun userEventTriggered(context: ChannelHandlerContext, event: Any) {
        if (event is IdleStateEvent) {
            when (event.state()) {
                IdleState.READER_IDLE -> {
                    context.close()
                }
                IdleState.WRITER_IDLE -> {
                    val packetChannel = PacketChannel.getPacketChannel(context.channel())
                    packetChannel.sendPacket(PingPacket())
                }
                else -> throw RuntimeException("${context.channel().remoteAddress()} didn't send or receive any data")
            }
        }
    }
}