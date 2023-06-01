package net.nitrin.phoenix.network.packet.channel.initializer

import io.netty.channel.Channel
import io.netty.channel.ChannelInitializer
import net.nitrin.phoenix.network.NetworkUtils
import net.nitrin.phoenix.network.packet.PacketManager
import net.nitrin.phoenix.network.packet.channel.ChannelHandler
import net.nitrin.phoenix.network.packet.channel.ConnectionState
import net.nitrin.phoenix.network.packet.channel.PacketChannel
import net.nitrin.phoenix.network.packet.channel.PacketChannelInboundHandler

sealed class PacketChannelInitializer(
    private val packetManager: PacketManager,
    private val hook: ((ConnectionState, PacketChannel, Throwable?) -> Unit)?,
): ChannelInitializer<Channel>() {

    override fun initChannel(channel: Channel) {
        NetworkUtils.addDefaultPipeline(channel)
            .addLast("packet-handler", PacketChannelInboundHandler(packetManager))
            .addLast("channel-handler", ChannelHandler(hook))
    }
}