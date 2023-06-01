package net.nitrin.phoenix.network.codec

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder

class VariableLengthDecoder: ByteToMessageDecoder() {

    override fun decode(context: ChannelHandlerContext, input: ByteBuf, output: MutableList<Any>) {
        if (!input.isReadable(4)) {
            return
        }

        val readerIndex = input.readerIndex()
        val size = input.readInt()

        if (input.readableBytes() >= size) {
            output.add(input.copy(input.readerIndex(), size))
            input.skipBytes(size)
        } else {
            input.readerIndex(readerIndex)
        }
    }
}