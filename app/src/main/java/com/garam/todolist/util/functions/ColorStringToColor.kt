package com.garam.todolist.util.functions

import android.content.Context
import androidx.core.content.ContextCompat
import com.garam.todolist.R

fun colorStringToColor(color: String,context: Context) : Int = when(color) {
    "default_color_1" -> ContextCompat.getColor(context, R.color.todo_color_1)
    "default_color_2" -> ContextCompat.getColor(context, R.color.todo_color_2)
    "default_color_3" -> ContextCompat.getColor(context, R.color.todo_color_3)
    "default_color_4" -> ContextCompat.getColor(context, R.color.todo_color_4)
    "default_color_5" -> ContextCompat.getColor(context, R.color.todo_color_5)
    "default_color_6" -> ContextCompat.getColor(context, R.color.todo_color_6)
    "default_color_7" -> ContextCompat.getColor(context, R.color.todo_color_7)
    "default_color_8" -> ContextCompat.getColor(context, R.color.todo_color_8)
    "default_color_9" -> ContextCompat.getColor(context, R.color.todo_color_9)
    "default_color_10" -> ContextCompat.getColor(context, R.color.todo_color_10)
    "default_color_11" -> ContextCompat.getColor(context, R.color.todo_color_11)
    "default_color_12" -> ContextCompat.getColor(context, R.color.todo_color_12)

    else -> ContextCompat.getColor(context, R.color.todo_color_1)

}