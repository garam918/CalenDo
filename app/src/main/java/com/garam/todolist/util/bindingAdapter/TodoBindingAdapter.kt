package com.garam.todolist.util.bindingAdapter

import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import com.garam.todolist.R
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.garam.todolist.data.Category
import com.garam.todolist.data.CategoryIconType
import com.garam.todolist.data.Todo
import com.garam.todolist.util.functions.colorStringToColor
import com.garam.todolist.util.functions.dpToPx
import com.garam.todolist.util.functions.iconToDrawable

object TodoBindingAdapter {

    @BindingAdapter("set_todo_mode")
    @JvmStatic
    fun setTodoMode(constraint: ConstraintLayout, currentMode : String) {

        if(currentMode == "CalenDo") {
            constraint.visibility = View.VISIBLE
        }
        else {
            if(currentMode == "Todo") {
                if(constraint.id == R.id.todo_constraint) constraint.visibility = View.VISIBLE
                else constraint.visibility = View.GONE
            }
            else {
                if(constraint.id == R.id.plan_constraint) constraint.visibility = View.VISIBLE
                else constraint.visibility = View.GONE
            }
        }
    }

    @BindingAdapter("set_todo_mode_title_text")
    @JvmStatic
    fun setTodoModeTitleText(textView: TextView, currentMode : String) {

        if(currentMode == "CalenDo") textView.visibility = View.VISIBLE
        else textView.visibility = View.GONE
    }

    @BindingAdapter("todo_calendar_type_text")
    @JvmStatic
    fun todoCalendarTypeText(textView: TextView, isWeekMode : Boolean) {
        textView.text = if(isWeekMode) "주"
        else "월"
    }

    @BindingAdapter("set_todo_calendar_item_count_text_visibility")
    @JvmStatic
    fun setTodoCalendarItemCountTextVisibility(view: View, currentMode : String) {

        when(currentMode) {
            "" -> {

            }
            "" -> {

            }

            "" -> {

            }
            else -> {

            }


        }

    }

    @BindingAdapter("todo_goal_type_setting")
    @JvmStatic
    fun todoGoalTypeSetting(constraint: ConstraintLayout, isWeekMode: Boolean) {

        val background = constraint.background

        if(isWeekMode) background.colorFilter = PorterDuffColorFilter(ContextCompat.getColor(constraint.context, R.color.main_color), PorterDuff.Mode.SRC_ATOP)
        else background.colorFilter = PorterDuffColorFilter(ContextCompat.getColor(constraint.context, R.color.goal_type_monthly_color), PorterDuff.Mode.SRC_ATOP)

    }

    @BindingAdapter("todo_goal_type_title_text")
    @JvmStatic
    fun todoGoalTypeTitleText(textView: TextView, isWeekMode: Boolean) {

        if(isWeekMode) textView.text = "이번주 목표"
        else textView.text = "이번달 목표"
    }

    @BindingAdapter("set_goal_add_btn_count_text_visibility")
    @JvmStatic
    fun setGoalAddBtnCountTextVisibility(view: View, isExpand: Boolean) {

        if(isExpand) {
            if(view.id == R.id.week_goal_add_btn) view.visibility = View.VISIBLE
            else view.visibility = View.GONE
        }
        else {
            if(view.id == R.id.week_goal_add_btn) view.visibility = View.GONE
            else view.visibility = View.VISIBLE
        }


    }

    @BindingAdapter("todo_empty_text_expand", "todo_empty_text_list")
    @JvmStatic
    fun setTodoListInGoalEmptyText(textView: TextView, isExpand: Boolean, todoList: List<Todo>) {

        if(isExpand) {
            if(todoList.isEmpty()) textView.visibility = View.VISIBLE
            else textView.visibility = View.GONE
        }
        else textView.visibility = View.GONE

    }

    @BindingAdapter("set_todo_list_count_visibility")
    @JvmStatic
    fun setTodoListCountVisibility(textView: TextView, isExpand: Boolean) {

        if(isExpand) textView.visibility = View.GONE
        else textView.visibility = View.VISIBLE


    }

    @BindingAdapter("set_todo_goal_hint_text")
    @JvmStatic
    fun setTodoGoalHintText(editText: EditText, isWeekMode: Boolean) {

        editText.hint = if(isWeekMode) "이번주 목표를 작성해주세요"
        else "이번달 목표를 작성해주세요"

    }

    @BindingAdapter("set_goal_todo_list_count_text")
    @JvmStatic
    fun setGoalTodoListCountText(textView: TextView, todoList : List<Todo>) {

        textView.text = if(todoList.isEmpty()) "0"
        else "${todoList.size}"

    }

    @BindingAdapter("todo_item_icon_visibility")
    @JvmStatic
    fun todoItemMemoIconVisibility(view: View, todo: Todo) {

        when(view.id) {
            R.id.todo_item_priority_img -> {
                view.visibility = if(todo.priority) View.VISIBLE
                else View.GONE
            }
            R.id.todo_item_repeat_img -> {
                view.visibility = if(todo.repeatRule.isNullOrBlank()) View.GONE
                else View.VISIBLE
            }
            R.id.todo_item_memo_img -> {
                view.visibility = if(todo.memo.isNotBlank()) View.VISIBLE
                else View.GONE
            }
        }
    }


    @BindingAdapter("set_todo_monthly_rpt_day_text")
    @JvmStatic
    fun setTodoMonthlyRptDayText(textView: TextView, day : String) {
        val params = textView.layoutParams

        if(day == "-1") {

            textView.apply {
                text = "마지막 날"
                setPadding(dpToPx(14,textView.context),dpToPx(8,textView.context),dpToPx(14,textView.context),dpToPx(8,textView.context))
            }

            params.width = ViewGroup.LayoutParams.MATCH_PARENT
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT

        }
        else {
            params.width = dpToPx(32,textView.context)
            params.height = dpToPx(32,textView.context)

            textView.apply {
                text = day
                setPadding(dpToPx(0,textView.context),dpToPx(0,textView.context),dpToPx(0,textView.context),dpToPx(0,textView.context))
            }
        }
    }

    @BindingAdapter("set_todo_monthly_rpt_day_bg")
    @JvmStatic
    fun setTodoMonthlyRptDayBg(textView: TextView, selected: Boolean) {

        textView.isSelected = selected
        
        textView.background = if(selected) {
            if(textView.id == R.id.todo_rpt_day_text) ContextCompat.getDrawable(textView.context,R.drawable.todo_rpt_day_change_calendar_item_pressed_bg)
            else ContextCompat.getDrawable(textView.context,R.drawable.todo_monthly_rpt_last_day_item_selected_bg)
        }
        else {
            if(textView.id == R.id.todo_rpt_day_text) ContextCompat.getDrawable(textView.context,R.drawable.todo_rpt_day_change_calendar_item_default_bg)
            else ContextCompat.getDrawable(textView.context,R.drawable.todo_monthly_rpt_last_day_item_layout_bg)
        }

    }

    @BindingAdapter("category_icon_setting")
    @JvmStatic
    fun categoryIconSetting(imageView: ImageView, category: Category) {

        val drawableInt = iconToDrawable(category.icon)
        val drawable = ContextCompat.getDrawable(imageView.context,drawableInt)
        drawable!!.colorFilter = PorterDuffColorFilter(colorStringToColor(category.color,imageView.context), PorterDuff.Mode.SRC_ATOP)
        imageView.setImageDrawable(drawable)

        imageView.background.apply {
            colorFilter = PorterDuffColorFilter(colorStringToColor(category.color,imageView.context), PorterDuff.Mode.SRC_ATOP)
            alpha = 51
        }
    }

    @BindingAdapter("category_item_icon_setting")
    @JvmStatic
    fun categoryItemIconSetting(imageView: ImageView, icon: CategoryIconType) {

        imageView.setImageResource(iconToDrawable(icon))

    }

    @BindingAdapter("plan_item_icon_setting", "plan_item_icon_color_setting")
    @JvmStatic
    fun planItemIconSetting(imageView: ImageView, icon: CategoryIconType, color : String) {

        val drawableInt = iconToDrawable(icon)
        val drawable = ContextCompat.getDrawable(imageView.context,drawableInt)

        drawable!!.colorFilter = PorterDuffColorFilter(colorStringToColor(color,imageView.context), PorterDuff.Mode.SRC_ATOP)
        imageView.setImageDrawable(drawable)

        imageView.background.apply {
            colorFilter = PorterDuffColorFilter(colorStringToColor(color,imageView.context), PorterDuff.Mode.SRC_ATOP)
            alpha = 51
        }

    }

    @BindingAdapter("set_pre_todo_hint_text")
    @JvmStatic
    fun setPreTodoHintText(textView: TextView, todo: Todo) {

        textView.hint = when(todo.id) {
            "pre_todo_1" -> "할일을 작성해주세요"
            "pre_todo_2" -> "한번 터치하면 완료 상태가 됩니다"
            "pre_todo_3" -> "한번 더 터치하면 진행중 상태가 됩니다"
            else -> ""
        }

        

    }

    @BindingAdapter("category_color_setting")
    @JvmStatic
    fun todoItemIconColorImgSetting(imageView: ImageView, str : String) {

        imageView.drawable?.colorFilter = PorterDuffColorFilter(ContextCompat.getColor(imageView.context,when(imageView.id) {
            R.id.category_color_img_1 -> R.color.todo_color_1
            R.id.category_color_img_2 -> R.color.todo_color_2
            R.id.category_color_img_3 -> R.color.todo_color_3
            R.id.category_color_img_4 -> R.color.todo_color_4
            R.id.category_color_img_5 -> R.color.todo_color_5
            R.id.category_color_img_6 -> R.color.todo_color_6

            R.id.category_color_img_7 -> R.color.todo_color_7
            R.id.category_color_img_8 -> R.color.todo_color_8
            R.id.category_color_img_9 -> R.color.todo_color_9
            R.id.category_color_img_10 -> R.color.todo_color_10
            R.id.category_color_img_11 -> R.color.todo_color_11
            R.id.category_color_img_12 -> R.color.todo_color_12

            else -> R.color.todo_color_1

        }), PorterDuff.Mode.SRC_OVER)

        imageView.setImageDrawable(imageView.drawable)

    }

    @BindingAdapter("set_title_color_text")
    @JvmStatic
    fun setTitleColorText(textView: TextView, color : String) {

        textView.setTextColor(colorStringToColor(color,textView.context))
    }

    @BindingAdapter("set_plan_start_time_text")
    @JvmStatic
    fun setPlanStartTimeText(textView: TextView, startTime : String?) {

        when(startTime) {
            "AllDay" -> {
                textView.text = ""
                textView.visibility = View.GONE
            }
            null -> {
                textView.apply {
                    visibility = View.GONE
                    text = ""
                }
            }
            else -> {
                textView.visibility = View.VISIBLE
                textView.text = startTime
            }


        }

    }
}