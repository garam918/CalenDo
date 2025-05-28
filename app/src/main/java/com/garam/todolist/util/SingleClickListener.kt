package com.garam.todolist.util

import android.view.View

abstract class SingleClickListener(
    private val interval: Long = 1000L // 기본 1초 간격
) : View.OnClickListener {
    private var lastClickTime = 0L

    abstract fun onSingleClick(v: View)

    override fun onClick(v: View) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime >= interval) {
            lastClickTime = currentTime
            onSingleClick(v)
        }
    }
}

// File: extensions/ViewExtensions.kt
fun View.setSingleOnClickListener(interval: Long = 500L, onSingleClick: (View) -> Unit) {
    val singleClickListener = object : SingleClickListener(interval) {
        override fun onSingleClick(v: View) {
            onSingleClick(v)
        }
    }
    setOnClickListener(singleClickListener)
}