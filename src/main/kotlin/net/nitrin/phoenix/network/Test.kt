package net.nitrin.phoenix.network

import io.netty.channel.Channel
import net.nitrin.phoenix.network.packet.Packet
import net.nitrin.phoenix.network.packet.PacketListener
import net.nitrin.phoenix.network.packet.PacketManager
import net.nitrin.phoenix.network.packet.QueryPacket
import net.nitrin.phoenix.network.packet.channel.AbstractPacketChannel
import net.nitrin.phoenix.network.packet.channel.PacketChannel
import net.nitrin.phoenix.network.packet.channel.PacketChannelFactory
import java.net.InetSocketAddress
import java.util.*
import kotlin.concurrent.thread

data class TestPacket(
    val name: String
): Packet

class TestPacketListener: PacketListener<TestPacket> {

    override fun receivePacket(packetChannel: PacketChannel, uuid: UUID?, packet: TestPacket) {
        println("Received : ${packet.name}")
    }
}

data class TestQueryPacket(
    val name: String
): QueryPacket<TestQueryPacket>

class TestQueryPacketListener: PacketListener<TestQueryPacket> {

    override fun receivePacket(packetChannel: PacketChannel, uuid: UUID?, packet: TestQueryPacket) {
        packetChannel.sendPacket(packet.copy(name = packet.name.dropLast(3)), uuid)
    }
}

fun main() {
    val socketAddress = InetSocketAddress(8888)

    thread {
        val packetManager = PacketManager()
        packetManager.registerListener(TestPacket::class.java,  TestPacketListener())
        packetManager.registerListener(TestQueryPacket::class.java, TestQueryPacketListener())

        val phoenixServer = PhoenixServer(packetManager, object : PacketChannelFactory {
            override fun createPacketChannel(packetManager: PacketManager, channel: Channel): PacketChannel {
                return object : AbstractPacketChannel(packetManager) {
                    override fun getChannel(): Channel {
                        return channel
                    }
                }
            }
        })

        phoenixServer.bind(socketAddress)
    }

    Thread.sleep(10)

    thread {
        val packetManager = PacketManager()

        val phoenixClient = PhoenixClient(packetManager)
        phoenixClient.connect(socketAddress)

        phoenixClient.sendPacket(TestPacket("NitrinCloud"))
        phoenixClient.sendQuery(TestQueryPacket("Nitrin")).thenAcceptAsync { packet ->
            println("Received : Query : ${packet.name}")
        }
    }
}