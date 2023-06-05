package net.nitrin.phoenix.network.codec

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder
import net.nitrin.phoenix.network.NetworkUtils.writeVarInt

class VariableLengthEncoder: MessageToByteEncoder<ByteBuf>() {

    override fun encode(context: ChannelHandlerContext, input: ByteBuf, output: ByteBuf) {
        val size = input.readableBytes()

        output.writeVarInt(size)
        output.writeBytes(input)
    }
}