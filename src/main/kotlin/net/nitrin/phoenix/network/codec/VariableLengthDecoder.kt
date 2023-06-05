package net.nitrin.phoenix.network.codec

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder
import net.nitrin.phoenix.network.NetworkUtils.readVarInt
import net.nitrin.phoenix.network.NetworkUtils.readVarIntOrNull

class VariableLengthDecoder: ByteToMessageDecoder() {

    override fun decode(context: ChannelHandlerContext, input: ByteBuf, output: MutableList<Any>) {
        val readerIndex = input.readerIndex()
        val size = input.readVarIntOrNull()

        if (size == null || input.readableBytes() < size) {
            input.readerIndex(readerIndex)
            return
        }

        output.add(input.copy(input.readerIndex(), size))
        input.skipBytes(size)
    }
}