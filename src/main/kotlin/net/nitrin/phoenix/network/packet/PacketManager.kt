@file:Suppress("UNCHECKED_CAST")

package net.nitrin.phoenix.network.packet

import net.nitrin.phoenix.network.packet.idle.PingPacket
import net.nitrin.phoenix.network.packet.idle.PingPacketListener
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage

class PacketManager {

    private val queries: MutableMap<UUID, CompletableFuture<out Packet>> = Collections.synchronizedMap(mutableMapOf())
    private val listeners: MutableMap<Class<out Packet>, MutableList<PacketListener<out Packet>>> = mutableMapOf()

    init {
        registerListener(PingPacket::class.java, PingPacketListener())
    }

    fun <T: Packet> registerListener(packet: Class<T>, listener: PacketListener<T>) {
        val listeners = this.listeners[packet]
        if (listeners != null) {
            listeners.add(listener)
        } else {
            this.listeners[packet] = mutableListOf(listener)
        }
    }

    fun <T: Packet> getListeners(packet: Class<T>): List<PacketListener<T>> {
        return (listeners[packet]?.toList()
            ?: listOf()) as List<PacketListener<T>>
    }

    fun <T: Packet> createFuture(uuid: UUID): CompletionStage<T> {
        val future = CompletableFuture<T>()
        queries[uuid] = future
        return future
    }

    fun <T: Packet> tryCompleteFuture(uuid: UUID?, packet: T): Boolean {
        val future = (queries[uuid]
            ?: return false) as CompletableFuture<T>
        future.complete(packet)
        return true
    }
}