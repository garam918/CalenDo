package com.garam.todolist.util.functions

import android.content.Context

fun dpToPx(dp: Int, context : Context): Int = (dp * context.resources.displayMetrics.density).toInt()
