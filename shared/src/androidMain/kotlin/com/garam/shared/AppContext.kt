package com.garam.shared

import android.content.Context
import java.lang.ref.WeakReference

object AppContext {
    private var contextRef: WeakReference<Context>? = null

    fun setUp(context: Context) {
        contextRef = WeakReference(context.applicationContext)
    }

    fun get(): Context {
        return contextRef?.get() ?: throw IllegalStateException("Context가 초기화되지 않았습니다. MainActivity에서 AppContext.setUp을 호출해주세요.")
    }
}