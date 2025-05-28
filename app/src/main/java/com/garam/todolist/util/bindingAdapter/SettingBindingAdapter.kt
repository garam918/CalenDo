package com.garam.todolist.util.bindingAdapter

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.garam.todolist.data.UserData

object SettingBindingAdapter {

    @BindingAdapter("set_account_text")
    @JvmStatic
    fun setAccountText(textView: TextView, userInfo : UserData?) {

        textView.text = if(userInfo?.loginType == "Anonymous" || userInfo == null) "로그인하여 기록 저장"
        else userInfo.email

    }

    @BindingAdapter("set_start_mode_text")
    @JvmStatic
    fun setStartModeText(textView: TextView, startMode : String?) {

        when(startMode) {
            "CalenDo" -> textView.text = "캘린두"
            "Todo" -> textView.text = "할일"
            "Plan" -> textView.text = "일정"
            else -> textView.text = "캘린두"
        }
    }

    @BindingAdapter("set_sort_mode_text")
    @JvmStatic
    fun setSortModeText(textView: TextView, sortMode : String?) {

        when(sortMode) {
            "Saved" -> textView.text = "작성한 순"
            "Completed" -> textView.text = "완료한 일이 위"
            "Completed_Reversed" -> textView.text = "완료한 일이 아래"
            else -> textView.text = "작성한 순"
        }
    }
}