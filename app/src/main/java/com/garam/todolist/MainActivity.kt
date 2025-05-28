package com.garam.todolist

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.garam.todolist.ui.onboarding.OnboardingActivity
import com.garam.todolist.ui.todoList.TodoListActivity
import com.google.firebase.FirebaseApp

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 최초 실행 확인 로직 추가 해야됨

        val prefs: SharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE)
        val isFirstRun = prefs.getBoolean("isFirstRun", true)

        if (isFirstRun) {
            // 최초 실행: 온보딩으로 이동
            startActivity(Intent(this, OnboardingActivity::class.java))

        } else {
            // 이후 실행: 메인 화면으로 이동
            startActivity(Intent(this, TodoListActivity::class.java))
        }

        finish() // 이 액티비티는 종료

//        val intent = Intent(this, OnboardingActivity::class.java)
//        startActivity(intent)
    }
}