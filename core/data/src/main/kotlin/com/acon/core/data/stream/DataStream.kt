package com.acon.core.data.stream

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.transformLatest
import javax.inject.Inject

interface DataStream {
    suspend fun notifyDataChanged()
    fun <T> subscribe(block: suspend FlowCollector<T>.() -> Unit): Flow<T>
}

class DataStreamImpl @Inject constructor() : DataStream {
    private val trigger = MutableSharedFlow<Unit>(replay = 1)

    init {
        trigger.tryEmit(Unit)
    }

    override suspend fun notifyDataChanged() {
        trigger.emit(Unit)
    }

    override fun <T> subscribe(block: suspend FlowCollector<T>.() -> Unit): Flow<T> {
        return trigger.transformLatest { this.block() }
    }
}