package net.nitrin.phoenix.network.packet

import io.netty.channel.Channel
import net.nitrin.phoenix.network.PhoenixClient
import net.nitrin.phoenix.network.PhoenixServer
import net.nitrin.phoenix.network.packet.channel.AbstractPacketChannel
import net.nitrin.phoenix.network.packet.channel.PacketChannel
import net.nitrin.phoenix.network.packet.channel.PacketChannelFactory
import java.net.InetSocketAddress
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class PacketQueryTest {

    class TestPacketChannel(packetManager: PacketManager, private val channel: Channel): AbstractPacketChannel(packetManager) {
        override fun getChannel(): Channel {
            return channel
        }
    }

    class TestPacketChannelFactory: PacketChannelFactory {
        override fun createPacketChannel(packetManager: PacketManager, channel: Channel): PacketChannel {
            return TestPacketChannel(packetManager, channel)
        }
    }

    data class TestQueryPacket(
        val name: String
    ): QueryPacket<TestQueryResultPacket>

    data class TestQueryResultPacket(
        val size: Int
    ): Packet

    class TestQueryPacketListener : PacketListener<TestQueryPacket> {
        override fun receivePacket(packetChannel: PacketChannel, uuid: UUID?, packet: TestQueryPacket) {
            packetChannel.sendPacket(TestQueryResultPacket(packet.name.length), uuid)
        }
    }

    private val phoenixServer: PhoenixServer
    private val phoenixClient: PhoenixClient

    init {
        val socketAddress = InetSocketAddress("127.0.0.1", 8888)
        val packetManager = PacketManager()
        packetManager.registerListener(TestQueryPacket::class.java, TestQueryPacketListener())
        phoenixServer = PhoenixServer(packetManager, TestPacketChannelFactory(), null)
        phoenixServer.bind(socketAddress)

        phoenixClient = PhoenixClient(PacketManager(), null)
        phoenixClient.connect(socketAddress)
    }

    @Test
    fun testQuery() {
        val resultPacket = phoenixClient.sendQuery(TestQueryPacket("NitrinCloud")).get()
        assertEquals(resultPacket.size, 11)
    }
}