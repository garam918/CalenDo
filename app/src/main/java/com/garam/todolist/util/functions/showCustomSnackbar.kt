package com.garam.todolist.util.functions

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.garam.todolist.databinding.CustomSnackBarLayoutBinding
import com.google.android.material.snackbar.Snackbar

fun showCustomSnackbar(view: View, message: String, onAction: () -> Unit ) {

    val snackbar = Snackbar.make(view, "", Snackbar.LENGTH_LONG)
    val snackbarLayout = snackbar.view as FrameLayout

    // 기존 패딩 제거
    snackbarLayout.setPadding(0, 0, 0, 0)
    snackbarLayout.setBackgroundColor(Color.TRANSPARENT)

    // 커스텀 뷰 inflate
    val customView = CustomSnackBarLayoutBinding.inflate(LayoutInflater.from(view.context))
    customView.snackBarTitleText.text = message

    customView.snackBarRestoreBtn.setOnClickListener {
        onAction()
        snackbar.dismiss()
    }

    // 기존 뷰 모두 제거 후 커스텀 뷰 추가
    snackbarLayout.removeAllViews()
    snackbarLayout.addView(customView.root)

    snackbar.show()

}