package com.garam.todolist.util.bindingAdapter

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.StyleSpan
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.garam.todolist.R

object OnboardingBindingAdapter {

    @BindingAdapter("onboarding_previous_btn_visibility")
    @JvmStatic
    fun onboardingPreviousBtnVisibility(button: Button, position: Int) {

        button.visibility = if(position == 0) View.GONE
        else View.VISIBLE

    }

    @BindingAdapter("onboarding_next_btn_text")
    @JvmStatic
    fun onboardingNextBtnText(button: Button, position: Int) {

        button.text = if(position == 2) "시작"
        else "다음"

    }

    @BindingAdapter("terms_policy_text_bold")
    @JvmStatic
    fun termsPolicyTextBold(textView: TextView, text: String) {

        val fullText = textView.text
        val boldTargets = mapOf(
            "이용약관" to textView.context.getString(R.string.terms_of_use_string),
            "개인정보처리방침" to textView.context.getString(R.string.privacy_policy_string)
        )

        val spannableString = SpannableString(fullText)

        for ((target, url) in boldTargets) {
            val start = fullText.indexOf(target)
            if (start >= 0) {
                val end = start + target.length

                spannableString.setSpan(
                    StyleSpan(Typeface.BOLD),
                    start,
                    end,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                spannableString.setSpan(object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        textView.context.startActivity(intent)
                    }
                    override fun updateDrawState(ds: TextPaint) {
                        super.updateDrawState(ds)
                        ds.isUnderlineText = false              // 밑줄 제거
                        ds.color = textView.currentTextColor                 // 글자 색 고정
                        ds.bgColor = Color.TRANSPARENT          // 배경색 제거
                    }
                }, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }

        textView.text = spannableString
        textView.movementMethod = LinkMovementMethod.getInstance()
        textView.highlightColor = ContextCompat.getColor(textView.context, android.R.color.transparent)

    }
}