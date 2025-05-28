package com.garam.todolist.util.functions

import android.text.InputType
import android.view.View
import android.view.ViewGroup
import android.widget.EditText

fun preventKeyboardFromShowing(view: View) {
    if (view is EditText) {
        view.showSoftInputOnFocus = false
        view.inputType = InputType.TYPE_NULL
        view.isFocusable = false
        view.isCursorVisible = false
    } else if (view is ViewGroup) {
        for (i in 0 until view.childCount) {
            preventKeyboardFromShowing(view.getChildAt(i))
        }
    }
}