package net.nitrin.phoenix.network.packet.channel

import io.netty.channel.Channel
import io.netty.util.AttributeKey
import net.nitrin.phoenix.network.packet.Packet
import net.nitrin.phoenix.network.packet.QueryPacket
import java.util.UUID
import java.util.concurrent.CompletionStage

sealed interface PacketChannel {
    companion object {
        private val ATTRIBUTE_KEY = AttributeKey.valueOf<PacketChannel>("packet-channel")

        fun setPacketChannel(channel: Channel, packetChannel: PacketChannel) {
            val packetChannelAttribute = channel.attr(ATTRIBUTE_KEY)
            packetChannelAttribute.set(packetChannel)
        }

        fun getPacketChannel(channel: Channel): PacketChannel {
            val packetChannelAttribute = channel.attr(ATTRIBUTE_KEY)
            return packetChannelAttribute.get()
        }
    }

    fun sendPacket(packet: Packet, uuid: UUID? = null)

    fun <T: Packet> sendQuery(packet: QueryPacket<T>): CompletionStage<T>

    fun getChannel(): Channel
}