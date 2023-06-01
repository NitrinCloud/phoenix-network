package net.nitrin.phoenix.network.packet

import net.nitrin.phoenix.network.packet.channel.PacketChannel
import java.util.UUID

interface PacketListener<T: Packet> {

    fun receivePacket(packetChannel: PacketChannel, uuid: UUID?, packet: T)
}