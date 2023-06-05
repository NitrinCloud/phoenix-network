package net.nitrin.phoenix.network

import io.netty.channel.Channel
import net.nitrin.phoenix.network.packet.PacketManager
import net.nitrin.phoenix.network.packet.channel.AbstractPacketChannel
import net.nitrin.phoenix.network.packet.channel.PacketChannel
import net.nitrin.phoenix.network.packet.channel.PacketChannelFactory
import org.junit.jupiter.api.Assertions.*
import java.net.InetSocketAddress
import kotlin.test.Test

class PhoenixClientTest {

    @Test
    fun testConnect() {
        val socketAddress = InetSocketAddress("127.0.0.1", 8888)
        val phoenixServer = PhoenixServer(PacketManager(), object : PacketChannelFactory {
            override fun createPacketChannel(packetManager: PacketManager, channel: Channel): PacketChannel {
                return object : AbstractPacketChannel(packetManager) {
                    override fun getChannel(): Channel {
                        return channel
                    }
                }
            }
        }, null)
        phoenixServer.bind(socketAddress)

        val phoenixClient = PhoenixClient(PacketManager(), null)
        phoenixClient.connect(socketAddress)

        assertEquals(true, phoenixClient.isConnected())
        assertEquals(socketAddress, phoenixClient.getChannel().remoteAddress())
    }
}