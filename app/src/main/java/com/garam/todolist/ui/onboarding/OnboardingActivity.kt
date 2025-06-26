package com.garam.todolist.ui.onboarding

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.garam.todolist.R
import com.garam.todolist.data.Category
import com.garam.todolist.data.CategoryIconType
import com.garam.todolist.data.Todo
import com.garam.todolist.data.TodoStatus
import com.garam.todolist.data.UserData
import com.garam.todolist.databinding.ActivityOnboardingBinding
import com.garam.todolist.databinding.OnboardingLoginBottomSheetDialogLayoutBinding
import com.garam.todolist.ui.todoList.TodoListActivity
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.Date
import java.util.UUID


@AndroidEntryPoint
class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding
    private val viewModel: OnboardingViewModel by viewModels()
    private lateinit var auth: FirebaseAuth
    private lateinit var bottomSheetDialog : BottomSheetDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        init()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun init() {

        binding = DataBindingUtil.setContentView(this, R.layout.activity_onboarding)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        auth = FirebaseAuth.getInstance()

        binding.onboardingViewPager.adapter = OnboardingViewPagerAdapter(this)

        setupIndicators()
        updateIndicators(0)

        binding.onboardingViewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                viewModel.currentPage.value = position
                updateIndicators(position)
            }
        })

        binding.onboardingPreviousBtn.setOnClickListener {
            binding.onboardingViewPager.currentItem = binding.onboardingViewPager.currentItem - 1
        }

        binding.onboardingNextBtn.setOnClickListener {
            if (viewModel.currentPage.value != 2) binding.onboardingViewPager.currentItem =
                binding.onboardingViewPager.currentItem + 1
            else {

                loginBottomSheetDialog()

//                val intent = Intent(this, TodoListActivity::class.java)
//                startActivity(intent)
                // 로그인 화면
            }

        }
    }

    private fun loginBottomSheetDialog() {

        bottomSheetDialog = BottomSheetDialog(this, R.style.BottomSheetTheme)
        val loginDialogView = OnboardingLoginBottomSheetDialogLayoutBinding.inflate(layoutInflater)

        bottomSheetDialog.setContentView(loginDialogView.root)

        loginDialogView.googleLoginBtn.setOnClickListener {

            val credentialManager = CredentialManager.create(this)

            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(getString(R.string.google_web_client_id))
                .build()
            val credentialRequest = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            lifecycleScope.launch {
                val googleSignInRequest = credentialManager.getCredential(
                    request = credentialRequest,
                    context = this@OnboardingActivity
                )
                val credential = googleSignInRequest.credential
                if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val googleIdTokenCredential =
                        GoogleIdTokenCredential.createFrom(credential.data)

                    firebaseAuthWithGoogle(googleIdTokenCredential.idToken)
                }
            }


        }

        loginDialogView.anonymousLoginBtn.setOnClickListener {

            anonymousLogin()


        }

        bottomSheetDialog.show()
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    val user = auth.currentUser!!

                    // 현재 이메일로 가입된 정보 있으면 데이터 동기화

                    val userData = UserData(
                        uid = user.uid.toString(),
                        email = user.email.toString(),
                        loginType = "Google"
                    )

//                    lifecycleScope.launch {
//                        viewModel.setUserData(userData).invokeOnCompletion {
//                            launchTodoActivity()
//                        }
//                    }

                    lifecycleScope.launch {

                        viewModel.isExistAccount().invokeOnCompletion {

                            if (viewModel.isExistAccount.value) {


                                lifecycleScope.launch {

                                    viewModel.saveTodoList().invokeOnCompletion {
                                        launchTodoActivity()
                                    }
                                }

                                Log.e("success", "isExist")

                            } else {
                                val userData = UserData(
                                    uid = user.uid.toString(),
                                    email = user.email.toString(),
                                    loginType = "Google"
                                )

                                val category = Category(
                                    categoryId = UUID.randomUUID().toString(),
                                    title = "카테고리",
                                    index = 0,
                                    icon = CategoryIconType.HOME,
                                    color = "default_color_1"
                                )

                                val tutorialTodoList = listOf(
                                    Todo(
                                        id = "pre_todo_1",
                                        title = "",
                                        categoryId = category.categoryId,
                                        startDate = LocalDate.now().toString(),
                                        endDate = LocalDate.now().toString(),
                                        repeatRule = null,
                                        status = mutableMapOf(LocalDate.now().toString() to TodoStatus.NONE),
                                        priority = false,
                                        memo = "",
                                        icon = null,
                                        color = null,
                                        startTime = null,
                                        index = null,
                                        savedTime = Timestamp.now()
                                    ),
                                    Todo(
                                        id = "pre_todo_2",
                                        title = "",
                                        categoryId = category.categoryId,
                                        startDate = LocalDate.now().toString(),
                                        endDate = LocalDate.now().toString(),
                                        repeatRule = null,
                                        status = mutableMapOf(LocalDate.now().toString() to TodoStatus.NONE),
                                        priority = false,
                                        memo = "",
                                        icon = null,
                                        color = null,
                                        startTime = null,
                                        index = null,
                                        savedTime = Timestamp(Date(Timestamp.now().toDate().time + 10))
                                    ),
                                    Todo(
                                        id = "pre_todo_3",
                                        title = "",
                                        categoryId = category.categoryId,
                                        startDate = LocalDate.now().toString(),
                                        endDate = LocalDate.now().toString(),
                                        repeatRule = null,
                                        status = mutableMapOf(LocalDate.now().toString() to TodoStatus.COMPLETED),
                                        priority = false,
                                        memo = "",
                                        icon = null,
                                        color = null,
                                        startTime = null,
                                        index = null,
                                        savedTime = Timestamp(Date(Timestamp.now().toDate().time + 20))
                                    )
                                )

                                lifecycleScope.launch {
                                    viewModel.saveTutorialTodo(
                                        category,
                                        tutorialTodoList,
                                        user.uid.toString()
                                    ).invokeOnCompletion {
                                        viewModel.setUserData(userData).invokeOnCompletion {
                                            launchTodoActivity()
                                        }
                                    }
                                }

//                                lifecycleScope.launch {
//                                    viewModel.setUserData(userData).invokeOnCompletion {
//                                        launchTodoActivity()
//                                    }
//                                }
                            }

                        }
                    }


                } else {


                    Log.e("fail", "signInWithCredential:failure", task.exception)

                }
            }
    }

    private fun anonymousLogin() {

        auth.signInAnonymously().addOnCompleteListener { task ->

            if (task.isSuccessful) {
                val user = auth.currentUser!!
                val userData =
                    UserData(uid = user.uid.toString(), email = "Guest", loginType = "Anonymous")

                lifecycleScope.launch {

                    val category = Category(
                        categoryId = UUID.randomUUID().toString(),
                        title = "카테고리",
                        index = 0,
                        icon = CategoryIconType.HOME,
                        color = "default_color_1"
                    )

                    val tutorialTodoList = listOf(
                        Todo(
                            id = "pre_todo_1",
                            title = "",
                            categoryId = category.categoryId,
                            startDate = LocalDate.now().toString(),
                            endDate = LocalDate.now().toString(),
                            repeatRule = null,
                            status = mutableMapOf(LocalDate.now().toString() to TodoStatus.NONE),
                            priority = false,
                            memo = "",
                            icon = null,
                            color = null,
                            startTime = null,
                            index = null,
                            savedTime = Timestamp.now()
                        ),
                        Todo(
                            id = "pre_todo_2",
                            title = "",
                            categoryId = category.categoryId,
                            startDate = LocalDate.now().toString(),
                            endDate = LocalDate.now().toString(),
                            repeatRule = null,
                            status = mutableMapOf(LocalDate.now().toString() to TodoStatus.NONE),
                            priority = false,
                            memo = "",
                            icon = null,
                            color = null,
                            startTime = null,
                            index = null,
                            savedTime = Timestamp(Date(Timestamp.now().toDate().time + 10))
                        ),
                        Todo(
                            id = "pre_todo_3",
                            title = "",
                            categoryId = category.categoryId,
                            startDate = LocalDate.now().toString(),
                            endDate = LocalDate.now().toString(),
                            repeatRule = null,
                            status = mutableMapOf(LocalDate.now().toString() to TodoStatus.COMPLETED),
                            priority = false,
                            memo = "",
                            icon = null,
                            color = null,
                            startTime = null,
                            index = null,
                            savedTime = Timestamp(Date(Timestamp.now().toDate().time + 20))
                        )
                    )

                    viewModel.saveTutorialTodo(category, tutorialTodoList,user.uid.toString()).invokeOnCompletion {
                        viewModel.setUserData(userData).invokeOnCompletion {
                            launchTodoActivity()
                        }
                    }


                }
            } else {


            }
        }
    }

    private fun setupIndicators() {
        val indicators = arrayOfNulls<ImageView>(3)
        val layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(8, 0, 8, 0) // indicator 간 간격 설정

        for (i in 0 until 3) {
            indicators[i] = ImageView(this)
            indicators[i]?.setImageDrawable(
                ContextCompat.getDrawable(this, R.drawable.indicator_inactive)
            )
            indicators[i]?.layoutParams = layoutParams
            binding.onboardingViewPagerIndicatorLayout.addView(indicators[i])
        }
    }

    private fun updateIndicators(position: Int) {
        val childCount = binding.onboardingViewPagerIndicatorLayout.childCount
        for (i in 0 until childCount) {
            val imageView = binding.onboardingViewPagerIndicatorLayout.getChildAt(i) as ImageView
            if (i == position) {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(this, R.drawable.indicator_active)
                )
            } else {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(this, R.drawable.indicator_inactive)
                )
            }
        }
    }


    private fun launchTodoActivity() {
        val prefs: SharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE)

        prefs.edit().putBoolean("isFirstRun", false).apply()
        bottomSheetDialog.dismiss()

        val intent = Intent(this, TodoListActivity::class.java)
        startActivity(intent)
        finish()
    }
}