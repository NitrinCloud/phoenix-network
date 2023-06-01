package net.nitrin.phoenix.network.packet.idle

import net.nitrin.phoenix.network.packet.PacketListener
import net.nitrin.phoenix.network.packet.channel.PacketChannel
import java.util.*

class PingPacketListener: PacketListener<PingPacket> {

    override fun receivePacket(packetChannel: PacketChannel, uuid: UUID?, packet: PingPacket) {
        packetChannel.sendPacket(PongPacket())
    }
}