
# phoenix-network

A network library written in kotlin using https://github.com/netty/netty and https://github.com/google/gson, which simplifies the usage and saves time.


## Usage/Examples

### Packets & Listener

```kotlin
data class TestPacket(
    val name: String
): Packet

class TestPacketListener: PacketListener<TestPacket> {

    override fun receivePacket(packetChannel: PacketChannel, uuid: UUID?, packet: TestPacket) {
        println(packet.name)
    }
}

data class TestQueryPacket(
    val name: String
): QueryPacket<TestQueryResultPacket>

data class TestQueryResultPacket(
    val size: Int
): Packet

class TestQueryPacketListener: PacketListener<TestQueryPacket> {

    override fun receivePacket(packetChannel: PacketChannel, uuid: UUID?, packet: TestQueryPacket) {
        packetChannel.sendPacket(TestQueryResultPacket(packet.name.length), uuid)
    }
}
```

### Server

```kotlin
val socketAddress = InetSocketAddress("127.0.0.1", 8888)
val packetManager = PacketManager()
packetManager.registerListener(TestPacket::class.java, TestPacketListener())
packetManager.registerListener(TestQueryPacket::class.java, TestQueryPacketListener())
val phoenixServer = PhoenixServer(packetManager, object : PacketChannelFactory {
    override fun createPacketChannel(packetManager: PacketManager, channel: Channel): PacketChannel {
        return object : AbstractPacketChannel(packetManager) {
            override fun getChannel(): Channel {
                return channel                }
                }
    }
}) { connectionState, packetChannel, throwable ->
    when (connectionState) {
        ConnectionState.CONNECT -> println("${packetChannel.getChannel().remoteAddress()} connected")
        ConnectionState.CONNECTED -> println("${packetChannel.getChannel().remoteAddress()} error")
        ConnectionState.DISCONNECT -> println("${packetChannel.getChannel().remoteAddress()} disconnected")
    }
}

phoenixServer.bind(socketAddress)
```

### Client

```kotlin
val socketAddress = InetSocketAddress("127.0.0.1", 8888)
val packetManager = PacketManager()
val phoenixClient = PhoenixClient(packetManager) { connectionState, packetChannel, throwable -> }

phoenixClient.connect(socketAddress)
phoenixClient.sendPacket(TestPacket("TestName"))
phoenixClient.sendQuery(TestQueryPacket("TestQuery")).thenAcceptAsync { packet ->
    println(packet.size)
}
```