package com.garam.todolist.util.functions

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.util.TypedValue
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.garam.todolist.data.CategoryIconType

fun TextView.setCompoundDrawable(icon: CategoryIconType, color: String, sizeDp : Int, context: Context) {

    val drawable = ContextCompat.getDrawable(context, iconToFilledDrawable(icon))
    drawable?.colorFilter = PorterDuffColorFilter(colorStringToColor(color,context), PorterDuff.Mode.SRC_ATOP)

    val sizePx = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        sizeDp.toFloat(),
        resources.displayMetrics
    ).toInt()

    drawable?.setBounds(0, 0, sizePx, sizePx)

    setCompoundDrawables(drawable,null,null,null)
}