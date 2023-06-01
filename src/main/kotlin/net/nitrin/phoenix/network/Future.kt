package net.nitrin.phoenix.network

import java.util.concurrent.CompletableFuture

class Future<T: Any>: CompletableFuture<T>() {

    override fun <U : Any> newIncompleteFuture(): CompletableFuture<U> {
        return Future()
    }
}