package com.garam.shared.util.functions

import platform.NetworkExtension.NWPath
import platform.NetworkExtension.NWPathStatusSatisfied
import platform.darwin.DISPATCH_QUEUE_PRIORITY_DEFAULT
import platform.darwin.DISPATCH_TIME_NOW
import platform.darwin.dispatch_get_global_queue
import platform.darwin.dispatch_semaphore_create
import platform.darwin.dispatch_semaphore_signal
import platform.darwin.dispatch_semaphore_wait
import platform.darwin.dispatch_time

actual object ConnectivityChecker {
    actual val isOnline : Boolean
        get() {
//            val monitor = NWPath
            var isConnected = false
//            val semaphore = dispatch_semaphore_create(0)
//
//            monitor.setUpdateHandler { path ->
//                isConnected = path.status == NWPathStatusSatisfied
//                monitor.cancel()
//                dispatch_semaphore_signal(semaphore)
//            }
//
//            val queue = dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT.toLong(), 0)
//            monitor.start(queue)
//            dispatch_semaphore_wait(semaphore, dispatch_time(DISPATCH_TIME_NOW, 500_000_000)) // 0.5초 대기

            return isConnected

        }
}