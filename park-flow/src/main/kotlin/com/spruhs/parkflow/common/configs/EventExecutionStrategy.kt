package com.spruhs.parkflow.common.configs

import com.spruhs.parkflow.common.metrics.EventMetrics
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.springframework.stereotype.Component

fun interface EventExecutionStrategy {
    fun execute(block: suspend () -> Unit)
}

@Component
class AsyncEventExecutionStrategy(
    private val applicationScope: CoroutineScope,
    private val metrics: EventMetrics,
) : EventExecutionStrategy {
    override fun execute(block: suspend () -> Unit) {
        metrics.springConsumed.increment()
        applicationScope.launch { block() }
    }
}
