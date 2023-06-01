package net.nitrin.phoenix.network

import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor

class Future<T: Any>: CompletableFuture<T>() {
    companion object {
        private val eventLoopGroup = NetworkUtils.newEventLoopGroup()
    }

    override fun <U : Any> newIncompleteFuture(): CompletableFuture<U> {
        return Future()
    }

    override fun defaultExecutor(): Executor {
        return eventLoopGroup
    }
}