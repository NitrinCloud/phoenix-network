package net.nitrin.phoenix.network

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.netty.channel.Channel
import io.netty.channel.ChannelPipeline
import io.netty.channel.EventLoopGroup
import io.netty.channel.epoll.Epoll
import io.netty.channel.epoll.EpollEventLoopGroup
import io.netty.channel.epoll.EpollServerSocketChannel
import io.netty.channel.epoll.EpollSocketChannel
import io.netty.channel.kqueue.KQueue
import io.netty.channel.kqueue.KQueueEventLoopGroup
import io.netty.channel.kqueue.KQueueServerSocketChannel
import io.netty.channel.kqueue.KQueueSocketChannel
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.ServerSocketChannel
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.handler.timeout.IdleStateHandler
import net.nitrin.phoenix.network.codec.VariableLengthDecoder
import net.nitrin.phoenix.network.codec.VariableLengthEncoder
import net.nitrin.phoenix.network.codec.packet.PacketDecoder
import net.nitrin.phoenix.network.codec.packet.PacketEncoder
import net.nitrin.phoenix.network.packet.Packet
import net.nitrin.phoenix.network.codec.PacketTypeAdapter
import net.nitrin.phoenix.network.packet.idle.IdleHandler
import java.lang.reflect.Type

object NetworkUtils {

    private var gsonBuilder = GsonBuilder()
        .serializeNulls()
        .registerTypeAdapter(Packet::class.java, PacketTypeAdapter())

    fun serverSocketChannel(): Class<out ServerSocketChannel> {
        return if (Epoll.isAvailable()) {
            EpollServerSocketChannel::class.java
        } else if (KQueue.isAvailable()) {
            KQueueServerSocketChannel::class.java
        } else {
            NioServerSocketChannel::class.java
        }
    }

    fun socketChannel(): Class<out SocketChannel> {
        return if (Epoll.isAvailable()) {
            EpollSocketChannel::class.java
        } else if (KQueue.isAvailable()) {
            KQueueSocketChannel::class.java
        } else {
            NioSocketChannel::class.java
        }
    }

    fun newEventLoopGroup(): EventLoopGroup {
        return if (Epoll.isAvailable()) {
            EpollEventLoopGroup()
        } else if (KQueue.isAvailable()) {
            KQueueEventLoopGroup()
        } else {
            NioEventLoopGroup()
        }
    }

    fun addDefaultPipeline(channel: Channel): ChannelPipeline {
        return channel.pipeline()
            .addLast("idle-state-handler", IdleStateHandler(30, 15, 60))
            .addLast("idle-handler", IdleHandler())
            .addLast("length-decoder", VariableLengthDecoder())
            .addLast("packet-decoder", PacketDecoder())
            .addLast("length-encoder", VariableLengthEncoder())
            .addLast("packet-encoder", PacketEncoder())
    }

    fun registerTypeAdapter(type: Type, adapter: Any) {
        gsonBuilder.registerTypeAdapter(type, adapter)
    }

    fun createGson(): Gson {
        return gsonBuilder.create()
    }
}