package net.nitrin.phoenix.network.packet

import com.google.gson.*
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import org.junit.jupiter.api.Assertions.*
import java.util.UUID
import kotlin.test.Test

class PacketDataTest {

    private val gson = GsonBuilder().registerTypeAdapter(Packet::class.java, CustomTypeAdapter()).serializeNulls().create()

    class CustomTypeAdapter: TypeAdapter<Packet>() {

        private val gson = GsonBuilder().serializeNulls().create()

        override fun write(writer: JsonWriter, value: Packet) {
            val json = gson.toJson(value)
            writer.beginObject()
                .name(value::class.java.name)
                .value(json)
                .endObject()
        }

        override fun read(reader: JsonReader): Packet {
            reader.beginObject()
            val className = reader.nextName()
            val packetClass = Class.forName(className)
            val packetJson = reader.nextString()
            reader.endObject()
            return gson.fromJson(packetJson, packetClass) as Packet
        }
    }

    @Test
    fun testPacket() {
        val testPacket = TestPacket("NitrinCloud")
        val frame = PacketFrame(UUID.fromString("dbffca41-ace6-49dc-b936-aac27952c9c9"), testPacket)
        val json = gson.toJson(frame)
        assertEquals("""
            {"uuid":"dbffca41-ace6-49dc-b936-aac27952c9c9","packet":{"net.nitrin.phoenix.network.packet.TestPacket":"{\"name\":\"NitrinCloud\"}"}}
        """.trimIndent(), json)
        val finalFrame = gson.fromJson(json, PacketFrame::class.java)
        assertEquals("NitrinCloud", (finalFrame.packet as TestPacket).name)
    }
}