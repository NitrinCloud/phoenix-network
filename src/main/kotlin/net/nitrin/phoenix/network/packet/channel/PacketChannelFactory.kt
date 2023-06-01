package net.nitrin.phoenix.network.packet.channel

import io.netty.channel.Channel
import net.nitrin.phoenix.network.packet.PacketManager

interface PacketChannelFactory {

    fun createPacketChannel(packetManager: PacketManager, channel: Channel): PacketChannel
}