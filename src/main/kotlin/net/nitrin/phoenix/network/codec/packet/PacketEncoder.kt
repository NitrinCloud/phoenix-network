package net.nitrin.phoenix.network.codec.packet

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder
import net.nitrin.phoenix.network.NetworkUtils
import net.nitrin.phoenix.network.NetworkUtils.writeString
import net.nitrin.phoenix.network.packet.PacketFrame

class PacketEncoder: MessageToByteEncoder<PacketFrame>() {

    override fun encode(context: ChannelHandlerContext, frame: PacketFrame, output: ByteBuf) {
        val gson = NetworkUtils.createGson()
        val json = gson.toJson(frame)

        output.writeString(json)
    }
}