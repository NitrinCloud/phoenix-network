package net.nitrin.phoenix.network.codec

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import net.nitrin.phoenix.network.NetworkUtils
import net.nitrin.phoenix.network.packet.Packet

class PacketTypeAdapter: TypeAdapter<Packet>() {

    override fun write(writer: JsonWriter, value: Packet) {
        writer.beginObject()
            .name(value::class.java.name)
            .value(NetworkUtils.createGson().toJson(value))
            .endObject()
    }

    override fun read(reader: JsonReader): Packet {
        reader.beginObject()
        val name = reader.nextName()
        val json = reader.nextString()
        reader.endObject()
        return NetworkUtils.createGson().fromJson(json, Class.forName(name)) as Packet
    }
}