package com.garam.todolist.util.functions

import android.content.Context
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.content.ContextCompat.getSystemService
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

fun bottomSheetBehaviorSetting(bottomSheet: View?) {
    bottomSheet?.let {
        val behavior = BottomSheetBehavior.from(it)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
        behavior.skipCollapsed = true
        behavior.isDraggable = false

        it.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
        it.requestLayout()
    }

}

fun BottomSheetDialog.setupHideKeyboardOnTouchOutside(editText: EditText) {
    if (editText.isFocused) {
        val imm = editText.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(editText.windowToken, 0)
        editText.clearFocus()
    }
}