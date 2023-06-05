package net.nitrin.phoenix.network.codec.packet

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder
import net.nitrin.phoenix.network.NetworkUtils
import net.nitrin.phoenix.network.NetworkUtils.readString
import net.nitrin.phoenix.network.packet.PacketFrame

class PacketDecoder: ByteToMessageDecoder() {

    override fun decode(context: ChannelHandlerContext, input: ByteBuf, output: MutableList<Any>) {
        val json = input.readString()
        val gson = NetworkUtils.createGson()
        val frame = gson.fromJson(json, PacketFrame::class.java)

        output.add(frame)
    }
}