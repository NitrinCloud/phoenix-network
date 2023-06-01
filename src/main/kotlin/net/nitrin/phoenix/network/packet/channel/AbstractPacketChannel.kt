package net.nitrin.phoenix.network.packet.channel

import net.nitrin.phoenix.network.Future
import net.nitrin.phoenix.network.packet.Packet
import net.nitrin.phoenix.network.packet.PacketFrame
import net.nitrin.phoenix.network.packet.PacketManager
import net.nitrin.phoenix.network.packet.QueryPacket
import java.util.UUID

abstract class AbstractPacketChannel(
    private val packetManager: PacketManager
): PacketChannel {

    final override fun sendPacket(packet: Packet, uuid: UUID?) {
        val channel = getChannel()
        channel.writeAndFlush(PacketFrame(uuid, packet))
    }

    final override fun <T: Packet> sendQuery(packet: QueryPacket<T>): Future<T> {
        val randomUUID = UUID.randomUUID()
        val future = packetManager.createFuture<T>(randomUUID)
        sendPacket(packet, randomUUID)
        return future
    }
}