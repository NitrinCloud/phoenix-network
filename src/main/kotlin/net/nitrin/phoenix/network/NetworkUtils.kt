package net.nitrin.phoenix.network

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.netty.buffer.ByteBuf
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
import io.netty.util.concurrent.DefaultThreadFactory
import net.nitrin.phoenix.network.codec.VariableLengthDecoder
import net.nitrin.phoenix.network.codec.VariableLengthEncoder
import net.nitrin.phoenix.network.codec.packet.PacketDecoder
import net.nitrin.phoenix.network.codec.packet.PacketEncoder
import net.nitrin.phoenix.network.packet.Packet
import net.nitrin.phoenix.network.codec.PacketTypeAdapter
import net.nitrin.phoenix.network.packet.idle.IdleHandler
import java.lang.reflect.Type
import kotlin.experimental.and

object NetworkUtils {

    private val defaultThreadFactory = DefaultThreadFactory("phoenix-network")

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
            EpollEventLoopGroup(defaultThreadFactory)
        } else if (KQueue.isAvailable()) {
            KQueueEventLoopGroup(defaultThreadFactory)
        } else {
            NioEventLoopGroup(defaultThreadFactory)
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

    private const val SEGMENT_BITS = 0x7F
    private const val CONTINUE_BIT = 0x80

    fun ByteBuf.readVarIntOrNull(): Int? {
        var value = 0
        var position = 0
        var currentByte: Int

        while (true) {
            currentByte = readByte().toInt()
            value = value.or(currentByte.and(SEGMENT_BITS).shl(position))

            if (currentByte.and(CONTINUE_BIT) == 0) break

            position += 7

            if (position >= 32) return null
        }
        return value
    }

    fun ByteBuf.readVarInt(): Int {
        return readVarIntOrNull()
            ?: throw RuntimeException("VarInt too big")
    }

    fun ByteBuf.writeVarInt(value: Int) {
        var newValue = value
        while (true) {
            if (newValue.and(SEGMENT_BITS.inv()) == 0) {
                writeByte(newValue)
                return
            }
            writeByte(newValue.and(SEGMENT_BITS).or(CONTINUE_BIT))
            newValue = newValue.ushr(7)
        }
    }

    fun ByteBuf.readString(): String {
        val size = readVarInt()
        val bytes = ByteArray(size)
        readBytes(bytes)
        return String(bytes)
    }

    fun ByteBuf.writeString(value: String) {
        val bytes = value.toByteArray()
        writeVarInt(bytes.size)
        writeBytes(bytes)
    }
}