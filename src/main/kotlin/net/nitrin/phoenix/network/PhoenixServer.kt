package net.nitrin.phoenix.network

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.Channel
import io.netty.channel.EventLoopGroup
import net.nitrin.phoenix.network.packet.PacketManager
import net.nitrin.phoenix.network.packet.channel.ConnectionState
import net.nitrin.phoenix.network.packet.channel.PacketChannel
import net.nitrin.phoenix.network.packet.channel.PacketChannelFactory
import net.nitrin.phoenix.network.packet.channel.initializer.MultiPacketChannelInitializer
import java.net.SocketAddress
import java.util.Collections
import java.util.concurrent.TimeUnit

class PhoenixServer(
    private val packetManager: PacketManager,
    packetChannelFactory: PacketChannelFactory,
    hook: ((ConnectionState, PacketChannel, Throwable?) -> Unit)?,
) {

    private val serverBootstrap = ServerBootstrap()

    private var bossGroup: EventLoopGroup? = null
    private var workerGroup: EventLoopGroup? = null

    private var currentChannel: Channel? = null

    private val connectedChannels: MutableList<Channel> = Collections.synchronizedList(mutableListOf())

    init {
        serverBootstrap.channel(NetworkUtils.serverSocketChannel())
        serverBootstrap.childHandler(MultiPacketChannelInitializer(packetManager, packetChannelFactory) { connectionState, packetChannel, throwable ->
            when (connectionState) {
                ConnectionState.CONNECT -> connectedChannels.add(packetChannel.getChannel())
                ConnectionState.CONNECTED -> { }
                ConnectionState.DISCONNECT -> connectedChannels.remove(packetChannel.getChannel())
            }
            hook?.invoke(connectionState, packetChannel, throwable)
        })
    }

    fun bind(socketAddress: SocketAddress, timeout: Long = 1000, unit: TimeUnit = TimeUnit.MILLISECONDS): Channel {
        bossGroup = NetworkUtils.newEventLoopGroup()
        workerGroup = NetworkUtils.newEventLoopGroup()
        serverBootstrap.group(bossGroup, workerGroup)

        packetManager.setExecutor(workerGroup)

        val channelFuture = serverBootstrap.bind(socketAddress)

        if (!channelFuture.awaitUninterruptibly(timeout, unit)) {
            throw RuntimeException("Cannot bind server to address: $socketAddress")
        }
        val channel = channelFuture.channel()
        val closeFuture = channel.closeFuture()
        closeFuture.addListener {
            if (it.isSuccess) {
                bossGroup?.shutdownGracefully()
                workerGroup?.shutdownGracefully()
            }
        }
        currentChannel = channel
        return channel
    }

    fun isBound(): Boolean {
        return (currentChannel?.isActive ?: false) && (!(bossGroup?.isShutdown ?: true)) && (!(workerGroup?.isShutdown ?: true))
    }

    fun getChannel(): Channel {
        return currentChannel
            ?: throw RuntimeException("Server not yet bound")
    }

    fun getConnectedChannels(): List<Channel> {
        return connectedChannels
    }
}