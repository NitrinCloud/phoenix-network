package net.nitrin.phoenix.network

import io.netty.bootstrap.Bootstrap
import io.netty.channel.Channel
import io.netty.channel.ChannelInitializer
import io.netty.channel.EventLoopGroup
import net.nitrin.phoenix.network.packet.PacketManager
import net.nitrin.phoenix.network.packet.channel.AbstractPacketChannel
import net.nitrin.phoenix.network.packet.channel.PacketChannel
import net.nitrin.phoenix.network.packet.channel.PacketChannelInboundHandler
import net.nitrin.phoenix.network.packet.channel.initializer.SinglePacketChannelInitializer
import java.net.SocketAddress
import java.util.concurrent.TimeUnit

class PhoenixClient(
    packetManager: PacketManager
): AbstractPacketChannel(packetManager) {

    private val bootstrap = Bootstrap()

    private var workerGroup: EventLoopGroup? = null

    private var currentChannel: Channel? = null

    init {
        bootstrap.channel(NetworkUtils.socketChannel())
        bootstrap.handler(SinglePacketChannelInitializer(packetManager, this))
    }

    fun connect(socketAddress: SocketAddress, timeout: Long = 1000, unit: TimeUnit = TimeUnit.MILLISECONDS): Channel {
        workerGroup = NetworkUtils.newEventLoopGroup()
        bootstrap.group(workerGroup)

        val channelFuture = bootstrap.connect(socketAddress)
        channelFuture.awaitUninterruptibly(timeout, unit)

        if (!channelFuture.isSuccess) {
            throw RuntimeException("Cannot connect to server: $socketAddress")
        }
        val channel = channelFuture.channel()
        val closeFuture = channel.closeFuture()
        closeFuture.addListener {
            if (it.isSuccess) {
                workerGroup?.shutdownGracefully()
            }
        }
        currentChannel = channel
        return channel
    }

    fun isBound(): Boolean {
        return (currentChannel?.isActive ?: false) && (!(workerGroup?.isShutdown ?: true))
    }

    override fun getChannel(): Channel {
        return currentChannel
            ?: throw RuntimeException("Client not yet connected")
    }
}