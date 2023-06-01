package net.nitrin.phoenix.network.packet.channel

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import net.nitrin.phoenix.network.packet.PacketFrame
import net.nitrin.phoenix.network.packet.PacketManager

class PacketChannelInboundHandler(
    private val packetManager: PacketManager
): SimpleChannelInboundHandler<PacketFrame>() {

    override fun channelRead0(context: ChannelHandlerContext, frame: PacketFrame) {
        if (!packetManager.tryCompleteFuture(frame.uuid, frame.packet)) {
            val packetChannel = PacketChannel.getPacketChannel(context.channel())
            val listeners = packetManager.getListeners(frame.packet.javaClass)
            listeners.forEach { listener ->
                listener.receivePacket(packetChannel, frame.uuid, frame.packet)
            }
        }
    }
}