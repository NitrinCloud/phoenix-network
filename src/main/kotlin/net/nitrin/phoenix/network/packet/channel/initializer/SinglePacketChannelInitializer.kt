package net.nitrin.phoenix.network.packet.channel.initializer

import io.netty.channel.Channel
import net.nitrin.phoenix.network.packet.PacketManager
import net.nitrin.phoenix.network.packet.channel.ConnectionState
import net.nitrin.phoenix.network.packet.channel.PacketChannel

class SinglePacketChannelInitializer(
    packetManager: PacketManager,
    private val packetChannel: PacketChannel,
    hook: ((ConnectionState, PacketChannel, Throwable?) -> Unit)?,
): PacketChannelInitializer(packetManager, hook) {

    override fun initChannel(channel: Channel) {
        PacketChannel.setPacketChannel(channel, packetChannel)

        super.initChannel(channel)
    }
}