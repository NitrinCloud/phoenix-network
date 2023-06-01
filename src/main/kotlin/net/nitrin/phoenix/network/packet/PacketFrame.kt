package net.nitrin.phoenix.network.packet

import java.util.UUID

data class PacketFrame(
    val uuid: UUID?,
    val packet: Packet
)