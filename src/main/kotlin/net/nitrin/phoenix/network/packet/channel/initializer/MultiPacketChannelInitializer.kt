package net.nitrin.phoenix.network.packet.channel.initializer

import io.netty.channel.Channel
import net.nitrin.phoenix.network.packet.PacketManager
import net.nitrin.phoenix.network.packet.channel.ConnectionState
import net.nitrin.phoenix.network.packet.channel.PacketChannel
import net.nitrin.phoenix.network.packet.channel.PacketChannelFactory

class MultiPacketChannelInitializer(
    private val packetManager: PacketManager,
    private val packetChannelFactory: PacketChannelFactory,
    hook: ((ConnectionState, PacketChannel, Throwable?) -> Unit)?,
): PacketChannelInitializer(packetManager, hook) {

    override fun initChannel(channel: Channel) {
        val packetChannel = packetChannelFactory.createPacketChannel(packetManager, channel)
        PacketChannel.setPacketChannel(channel, packetChannel)

        super.initChannel(channel)
    }
}