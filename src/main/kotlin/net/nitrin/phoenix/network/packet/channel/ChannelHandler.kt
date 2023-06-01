package net.nitrin.phoenix.network.packet.channel

import io.netty.channel.ChannelDuplexHandler
import io.netty.channel.ChannelHandlerContext

class ChannelHandler(
    private val hook: ((ConnectionState, PacketChannel, Throwable?) -> Unit)?,
): ChannelDuplexHandler() {

    override fun channelActive(context: ChannelHandlerContext) {
        val packetChannel = PacketChannel.getPacketChannel(context.channel())
        hook?.invoke(ConnectionState.CONNECT, packetChannel, null)
    }

    override fun channelInactive(context: ChannelHandlerContext) {
        val packetChannel = PacketChannel.getPacketChannel(context.channel())
        hook?.invoke(ConnectionState.DISCONNECT, packetChannel, null)
    }

    @Suppress("OVERRIDE_DEPRECATION")
    override fun exceptionCaught(context: ChannelHandlerContext, cause: Throwable) {
        val packetChannel = PacketChannel.getPacketChannel(context.channel())
        hook?.invoke(ConnectionState.CONNECTED, packetChannel, cause)
    }
}