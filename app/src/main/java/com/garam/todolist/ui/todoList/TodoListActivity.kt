package com.garam.todolist.ui.todoList

import android.animation.ValueAnimator
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.TimePicker
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.garam.todolist.R
import com.garam.todolist.data.Category
import com.garam.todolist.data.CategoryIconType
import com.garam.todolist.data.Goal
import com.garam.todolist.data.GoalType
import com.garam.todolist.data.Todo
import com.garam.todolist.data.TodoMonthlyRptDayClass
import com.garam.todolist.data.TodoStatus
import com.garam.todolist.databinding.ActivityTodoListBinding
import com.garam.todolist.databinding.TodoEditBottomSheetLayoutBinding
import com.garam.todolist.databinding.PlanAddBottomSheetLayoutBinding
import com.garam.todolist.databinding.CategoryAddBottomSheetLayoutBinding
import com.garam.todolist.databinding.CategoryChangeBottomSheetLayoutBinding
import com.garam.todolist.databinding.TodoMemoDialogLayoutBinding
import com.garam.todolist.databinding.TodoItemRepeatSettingBottomSheetLayoutBinding
import com.garam.todolist.databinding.WeekCalendarItemLayoutBinding
import com.garam.todolist.databinding.TodoRptDayChangeCalendarItemLayoutBinding
import com.garam.todolist.databinding.TodoDeleteDialogLayoutBinding
import com.garam.todolist.databinding.PlanDeleteDialogLayoutBinding
import com.garam.todolist.todoList.TodoListViewModel
import com.garam.todolist.ui.setting.SettingActivity
import com.garam.todolist.ui.todoList.recyclerAdapter.SelectedCategoryTodoListRecyclerAdapter
import com.garam.todolist.ui.todoList.recyclerAdapter.TodoMonthlyRptDayRecyclerAdapter
import com.garam.todolist.util.clickListener.CategoryChangeClickListener
import com.garam.todolist.util.CategoryClickListener
import com.garam.todolist.util.clickListener.CategoryFilterClickListener
import com.garam.todolist.util.clickListener.CategoryIconClickListener
import com.garam.todolist.util.clickListener.MonthlyRptDayClickListener
import com.garam.todolist.util.clickListener.PlanItemClickListener
import com.garam.todolist.util.clickListener.TodoClickListener
import com.garam.todolist.util.functions.addUntilToRRule
import com.garam.todolist.util.functions.colorStringToColor
import com.garam.todolist.util.functions.dateToString
import com.garam.todolist.util.functions.filterTodosByDate
import com.garam.todolist.util.functions.bottomSheetBehaviorSetting
import com.garam.todolist.util.functions.generateRepeatRule
import com.garam.todolist.util.functions.getMonthStartAndEnd
import com.garam.todolist.util.functions.getWeekStartEnd
import com.garam.todolist.util.functions.iconToDrawable
import com.garam.todolist.util.functions.iconToFilledDrawable
import com.garam.todolist.util.functions.setCompoundDrawable
import com.garam.todolist.util.functions.setupHideKeyboardOnTouchOutside
import com.garam.todolist.util.functions.localDateToDateString
import com.garam.todolist.util.functions.localDateToDateStringForDialog
import com.garam.todolist.util.functions.localDateToString
import com.garam.todolist.util.functions.monthToDateList
import com.garam.todolist.util.functions.monthToString
import com.garam.todolist.util.functions.parseRRule
import com.garam.todolist.util.functions.preventKeyboardFromShowing
import com.garam.todolist.util.functions.showKeyboard
import com.garam.todolist.util.functions.timePickerToString
import com.garam.todolist.util.functions.toICalDay
import com.garam.todolist.util.setSingleOnClickListener
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.kizitonwose.calendar.core.*
import com.kizitonwose.calendar.view.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale
import java.util.UUID
import kotlin.math.abs
import kotlin.sequences.forEachIndexed

@AndroidEntryPoint
class TodoListActivity : AppCompatActivity(), CategoryClickListener, TodoClickListener {

    private lateinit var binding: ActivityTodoListBinding
    private val viewModel: TodoListViewModel by viewModels()

    private lateinit var categoryRecyclerAdapter: TodoListCategoryRecyclerAdapter
    private lateinit var planRecyclerAdapter: PlanListRecyclerAdapter
    private lateinit var todoInGoalRecyclerAdapter: TodoListInGoalRecyclerAdapter
    private lateinit var selectedCategoryTodoListAdapter : SelectedCategoryTodoListRecyclerAdapter
    private lateinit var caetgoryHorizonAdapter : TodoListCategoryHorizonRecyclerAdapter

    private lateinit var todoDailyRptStartDate: LocalDate
    var selectedImageView: ImageView? = null

    private var isProgrammaticWeekScroll = false
    private var isProgrammaticMonthScroll = false

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val imm: InputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        currentFocus?.clearFocus()
        return super.dispatchTouchEvent(ev)

    }

    override fun submitTodoCount(categoryId: String, count: Int) {

        binding.monthCalendarView.notifyCalendarChanged()
        binding.weekCalendarView.notifyCalendarChanged()

    }

    override fun todoEditBtnClick(todo: Todo) {
        showTodoEditDialog(todo)
    }

    override fun todoCheckBoxClick(todo: Todo, position: Int) {

        when (todo.status?.get(viewModel.selectedDate.value.toString())) {
            // 아무 상태 아닐 때
            TodoStatus.NONE -> {

                val todoStatus = todo.status!!
                todoStatus[viewModel.selectedDate.value.toString()] = TodoStatus.COMPLETED

                val editTodo = todo.copy(status = todoStatus)

                viewModel.updateTodo(editTodo)

            }
            // 진행 중 일 때
            TodoStatus.IN_PROGRESS -> {

                val todoStatus = todo.status!!
                todoStatus[viewModel.selectedDate.value.toString()] = TodoStatus.NONE

                val editTodo = todo.copy(status = todoStatus)

                viewModel.updateTodo(editTodo)
            }
            // 완료 했을 때
            TodoStatus.COMPLETED -> {
                val todoStatus = todo.status!!
                todoStatus[viewModel.selectedDate.value.toString()] = TodoStatus.IN_PROGRESS

                val editTodo = todo.copy(status = todoStatus)

                viewModel.updateTodo(editTodo)
            }

            else -> {
                val todoStatus = todo.status!!
                todoStatus[viewModel.selectedDate.value.toString()] = TodoStatus.COMPLETED

                val editTodo = todo.copy(status = todoStatus)

                viewModel.updateTodo(editTodo)
            }
        }

        categoryRecyclerAdapter.notifyItemChanged(position)
        selectedCategoryTodoListAdapter.notifyItemChanged(position)

    }

    override fun todoTitleEditClick(todo: Todo) {

        viewModel.updateTodo(todo).invokeOnCompletion {
            Log.e("todo.title", todo.title)
        }

    }

    override fun todoAdd(categoryId: String) {

        viewModel.addTodo(viewModel.selectedDate.value.toString(), categoryId, "")

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        init()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets


            val ime = insets.getInsets(WindowInsetsCompat.Type.ime())
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val navBarInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars())

            // 하단 패딩: 키보드 올라오면 그만큼 추가
            // 상단 패딩: status bar 겹치지 않게

            val isKeyboardVisible = insets.isVisible(WindowInsetsCompat.Type.ime())

            if (isKeyboardVisible) {
                // 키보드 올라올 때 AppBar 접힘
                binding.appBarLayout.setExpanded(false, true)
//                view.updatePadding(bottom = imeInsets.bottom)

                v.updatePadding(
                    0,
                    systemBars.top,
                    0,
                    ime.bottom
                )
            } else {
                // 키보드 내려가면 다시 펼침
                binding.appBarLayout.setExpanded(true, true)
                v.updatePadding(top = systemBars.top, bottom = navBarInsets.bottom)
            }

            insets
        }
    }

    private fun init() {

        binding = DataBindingUtil.setContentView(this, R.layout.activity_todo_list)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        var daysOfWeek =
            daysOfWeek(firstDayOfWeek = if (viewModel.firstDayOfWeekFlow.value == "Mon") DayOfWeek.MONDAY else DayOfWeek.SUNDAY)
        binding.titlesContainer.root.children
            .filterIsInstance<LinearLayout>()
            .flatMap { it.children }
            .filterIsInstance<TextView>()
//            .map { it as TextView }
            .forEachIndexed { index, textView ->
                textView.text =
                    daysOfWeek[index].getDisplayName(TextStyle.SHORT, Locale.getDefault())

                if (textView.text.toString() == LocalDate.parse(viewModel.selectedDate.value.toString()).dayOfWeek.getDisplayName(
                        TextStyle.SHORT,
                        Locale.getDefault()
                    )
                ) textView.alpha = 1f
                else textView.alpha = 0.4f
            }

        val currentMonth = YearMonth.now()
        val startMonth = currentMonth.minusMonths(100)
        val endMonth = currentMonth.plusMonths(100)

        setAppBarLayoutScroll()

        lifecycleScope.launch {

            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                initCategoryRecyclerView(null)
                initCategoryHorizonRecyclerView()
                initPlanRecyclerView()
                initSelectedCategoryTodoRecyclerView(null, LocalDate.parse(viewModel.selectedDate.value.toString()))

                when (viewModel.currentMode.value) {
                    "CalenDo" -> {

                        binding.calendoBtn.isSelected = true

//                        initCategoryRecyclerView(null)
//                        initCategoryHorizonRecyclerView()
//                        initPlanRecyclerView()
                    }

                    "Todo" -> {

                        binding.todoBtn.isSelected = true

//                        initCategoryRecyclerView(null)
//                        initCategoryHorizonRecyclerView()
                    }

                    "Plan" -> {
                        binding.planBtn.isSelected = true

//                        initPlanRecyclerView()
                    }

                    else -> {
                        binding.calendoBtn.isSelected = true

//                        initCategoryRecyclerView(null)
//                        initCategoryHorizonRecyclerView()
//                        initPlanRecyclerView()
                    }
                }

                setupWeekCalendar(startMonth, endMonth, currentMonth, daysOfWeek)
                setupMonthCalendar(startMonth, endMonth, currentMonth, daysOfWeek)
            }
        }

        binding.calendoBtn.setOnClickListener {
            it.isSelected = true

            binding.todoBtn.isSelected = false
            binding.planBtn.isSelected = false

            viewModel.currentMode.value = "CalenDo"

            binding.weekCalendarView.notifyCalendarChanged()
            binding.monthCalendarView.notifyCalendarChanged()
        }

        binding.todoBtn.setOnClickListener {
            it.isSelected = true

            binding.calendoBtn.isSelected = false
            binding.planBtn.isSelected = false

            viewModel.currentMode.value = "Todo"

            binding.weekCalendarView.notifyCalendarChanged()
            binding.monthCalendarView.notifyCalendarChanged()
        }

        binding.planBtn.setOnClickListener {
            it.isSelected = true

            binding.todoBtn.isSelected = false
            binding.calendoBtn.isSelected = false

            viewModel.currentMode.value = "Plan"

            binding.weekCalendarView.notifyCalendarChanged()
            binding.monthCalendarView.notifyCalendarChanged()
        }

        binding.settingBtn.setOnClickListener {
            val nextIntent = Intent(this, SettingActivity::class.java)
            startActivity(nextIntent)
        }

        binding.todolistHeaderTodayBtn.setOnClickListener {

            dateClicked(LocalDate.now())
            isProgrammaticWeekScroll = true
            isProgrammaticMonthScroll = true
            binding.weekCalendarView.scrollToWeek(LocalDate.now())
            binding.monthCalendarView.scrollToMonth(LocalDate.now().yearMonth)

        }

        binding.todolistHeaderWeekMonthBtn.setSingleOnClickListener {
            toggleCalendarMode(!viewModel.isWeekMode.value)
        }

        binding.todoCategoryAllBtn.setOnClickListener {
            it.alpha = 1f

            caetgoryHorizonAdapter.currentPosition = -1
            caetgoryHorizonAdapter.notifyDataSetChanged()
            binding.todoListWeekGoalTitleConstraint.visibility = View.VISIBLE
            binding.selectedCategoryTodoListConstraint.visibility = View.GONE
            binding.todoListRecyclerView.visibility = View.VISIBLE

            viewModel.currentSelectedCategory.value = null

            lifecycleScope.launch {
                viewModel.categoryTodoMap.collectLatest {
                    categoryRecyclerAdapter.submitList(it.sortedBy { it.category.index })
                }
            }

        }

        binding.todoCategoryAddBtn.setOnClickListener {
            showCategoryAddDialog()
        }

        binding.selectedCategoryTodoAddBtn.setOnClickListener {

            viewModel.addTodo(viewModel.selectedDate.value.toString(), viewModel.currentSelectedCategory.value!!.categoryId, "")


        }

        todoInGoalRecyclerAdapter =
            TodoListInGoalRecyclerAdapter(this, viewModel.selectedDate.value.toString())
        binding.todoListWeekGoalRecyclerView.adapter = todoInGoalRecyclerAdapter

        binding.weekGoalExpandBtn.setOnClickListener {

            // 주간, 월간에 따라 해당 하는 일정들 보여주기

            if (viewModel.isExpandTodoListInGoal.value == true) {
                viewModel.isExpandTodoListInGoal.value = false
                binding.todoListWeekGoalRecyclerView.visibility = View.GONE
            } else {
                viewModel.isExpandTodoListInGoal.value = true
                binding.todoListWeekGoalRecyclerView.visibility = View.VISIBLE

                Log.e("gExpandCurrentGoal",viewModel.currentGoal.value?.title.toString())

                setTodoInGoal().invokeOnCompletion {
                    lifecycleScope.launch {
                        viewModel.currentTodoInGoal.collectLatest {
//                            todoInGoalRecyclerAdapter = TodoListInGoalRecyclerAdapter(
//                                this@TodoListActivity,
//                                viewModel.selectedDate.value.toString()
//                            )
//                            binding.todoListWeekGoalRecyclerView.adapter = todoInGoalRecyclerAdapter

                            if(it.isNotEmpty() && it.first().categoryId == viewModel.currentGoal.value?.goalId.toString()) todoInGoalRecyclerAdapter.submitList(it)
                            else todoInGoalRecyclerAdapter.submitList(emptyList())

                        }
                    }
                }
            }


        }

        binding.planAddBtn.setOnClickListener {

            showPlanAddDialog(null)

        }

        binding.weekGoalAddBtn.setOnClickListener {

            Log.e("currentGoal",viewModel.currentGoal.value.toString())

            if (viewModel.currentGoal.value == null) {

                var (startDate, endDate) = if (viewModel.isWeekMode.value == true) getWeekStartEnd(
                    binding.weekCalendarView.findFirstVisibleWeek()?.days[0]!!.date.toString()
                )
                else getMonthStartAndEnd(viewModel.currentMonth.value.atDay(1).toString())

                val goal = Goal(
                    goalId = UUID.randomUUID().toString(),
                    title = "",
                    startDate = startDate, endDate = endDate, type = if (viewModel.isWeekMode.value == true) GoalType.WEEKLY
                    else GoalType.MONTHLY
                )

                viewModel.addGoal(goal).invokeOnCompletion {
                    viewModel.currentGoal.value = goal

                    addTodoInGoal()
                }
            }
            else addTodoInGoal()

        }

        binding.weekGoalTitleText.onFocusChangeListener =
            View.OnFocusChangeListener { view, hasFocus ->

                if (!hasFocus) {

                    if (viewModel.isWeekMode.value == true) {

                        var (startDate, endDate) = getWeekStartEnd(viewModel.selectedDate.value.toString())

                        var goal: Goal?

                        if (viewModel.currentGoal.value == null) {

                            var (startDate, endDate) = getWeekStartEnd(binding.weekCalendarView.findFirstVisibleWeek()?.days[0]!!.date.toString())

                            goal = Goal(
                                goalId = UUID.randomUUID().toString(),
                                title = binding.weekGoalTitleText.text.toString(),
                                startDate = startDate, endDate = endDate, type = GoalType.WEEKLY
                            )
                        } else goal =
                            viewModel.currentGoal.value?.copy(title = binding.weekGoalTitleText.text.toString())

                        viewModel.addGoal(goal!!).invokeOnCompletion {
                            Log.e("addGoal", "success")

                            viewModel.currentGoal.value = goal
                        }
                    } else {
                        var (startDate, endDate) = getMonthStartAndEnd(
                            viewModel.currentMonth.value.atDay(
                                1
                            ).toString()
                        )

                        var goal: Goal?

                        if (viewModel.currentGoal.value == null) {

                            var (startDate, endDate) = getMonthStartAndEnd(
                                viewModel.currentMonth.value.atDay(
                                    1
                                ).toString()
                            )

                            goal = Goal(
                                goalId = UUID.randomUUID().toString(),
                                title = binding.weekGoalTitleText.text.toString(),
                                startDate = startDate, endDate = endDate, type = GoalType.MONTHLY
                            )
                        } else goal =
                            viewModel.currentGoal.value?.copy(title = binding.weekGoalTitleText.text.toString())

                        viewModel.addGoal(goal!!).invokeOnCompletion {
                            Log.e("addGoal", "success")

                            viewModel.currentGoal.value = goal
                        }
                    }

                }

            }

    }

    private fun setAppBarLayoutScroll() {

        binding.appBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            val totalScrollRange = binding.appBarLayout.totalScrollRange

            when {
                verticalOffset == 0 -> {
                    // 완전히 펼쳐진 상태
                    binding.todoCollapsingDivideView.visibility = View.GONE
                    viewModel.currentMonthString.value = monthToString(viewModel.currentMonth.value)
                }

                abs(verticalOffset) >= totalScrollRange -> {
                    // 완전히 접힌 상태

                    viewModel.currentMonthString.value =
                        localDateToDateString(LocalDate.parse(viewModel.selectedDate.value.toString()))
                    binding.todoCollapsingDivideView.visibility = View.VISIBLE
                }

                else -> {
                    // 중간 상태 (스크롤 중)
                    binding.todoCollapsingDivideView.visibility = View.GONE
                }
            }
        })

    }

    private fun showPlanAddDialog(todo: Todo?) {

        val bottomSheetDialog = BottomSheetDialog(this, R.style.BottomSheetTheme)
        val planAddDialogView = PlanAddBottomSheetLayoutBinding.inflate(layoutInflater)

        planAddDialogView.root.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                if (planAddDialogView.planTitleEditText.isFocused) {
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(planAddDialogView.root.windowToken, 0)
                    planAddDialogView.planTitleEditText.clearFocus()
                    return@setOnTouchListener true // 이벤트 소비
                }
            }
            return@setOnTouchListener false // 이벤트 전달

//            if(categoryAddDialogView.categoryAddTitleEditText.isFocused) {
//                bottomSheetDialog.setupHideKeyboardOnTouchOutside(categoryAddDialogView.categoryAddTitleEditText)
//            }

        }

        bottomSheetDialog.setContentView(planAddDialogView.root)

        ViewCompat.setOnApplyWindowInsetsListener(planAddDialogView.root) { view, insets ->
            val imeHeight = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
            view.updatePadding(bottom = imeHeight)
            insets
        }

        bottomSheetDialog.setOnShowListener {
            val bottomSheet =
                bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)

            bottomSheetBehaviorSetting(bottomSheet)
        }

        planAddDialogView.dialogCloseBtn.setOnClickListener {

            bottomSheetDialog.dismiss()

        }

        planAddDialogView.planAddTitleText.text =
            "${localDateToDateStringForDialog(LocalDate.parse(viewModel.selectedDate.value))} 일정 추가"

        planAddDialogView.plan = todo

        // todo 편집 화면 구현 해야함

        var planTitle = ""
        var color = "default_color_1"
        var repeatRule = ""
        var startTime = ""

        todo?.let {

            planAddDialogView.planAddTitleText.text =
                "${localDateToDateStringForDialog(LocalDate.parse(viewModel.selectedDate.value))} 일정 수정"


            planTitle = it.title
            color = it.color.toString()
            repeatRule = it.repeatRule.toString()
            startTime = it.startTime.toString()

            planAddDialogView.planColorImg.colorFilter = PorterDuffColorFilter(
                colorStringToColor(color, this),
                PorterDuff.Mode.SRC_ATOP
            )
            planAddDialogView.planIconImg.setImageResource(iconToDrawable(it.icon!!))

            if (repeatRule != "") {
                val parsedRule = parseRRule(repeatRule)
                val frequency = parsedRule["FREQ"]!!

                planAddDialogView.planRepeatSettingText.text =
                    dateToString(LocalDate.parse(it.startDate), frequency)

                when (frequency) {
                    "WEEKLY" -> planAddDialogView.planRepeatWeeklyBtn.isSelected = true
                    "MONTHLY" -> planAddDialogView.planRepeatMonthlyBtn.isSelected = true
                    "YEARLY" -> planAddDialogView.planRepeatYearlyBtn.isSelected = true
                }
            }
        }

        var editCategoryIconType = CategoryIconType.HOME

        planAddDialogView.planAllDaySettingSwitch.isChecked = todo?.startTime == "AllDay"

        if (todo != null) planAddDialogView.planDeleteBtn.visibility = View.VISIBLE

        val adapter = CategoryIconRecyclerAdapter(object : CategoryIconClickListener {
            override fun iconClick(categoryIconType: CategoryIconType) {
                editCategoryIconType = categoryIconType

                if (planAddDialogView.planTitleEditText.isFocused) {
                    bottomSheetDialog.setupHideKeyboardOnTouchOutside(planAddDialogView.planTitleEditText)
                }

                planAddDialogView.planIconImg.apply {
                    background.colorFilter = PorterDuffColorFilter(
                        colorStringToColor(color, this@TodoListActivity),
                        PorterDuff.Mode.SRC_ATOP
                    )
                    background.alpha = 51

                    val imageDrawable = ContextCompat.getDrawable(
                        this@TodoListActivity,
                        iconToDrawable(categoryIconType)
                    )
                    imageDrawable!!.colorFilter = PorterDuffColorFilter(
                        colorStringToColor(color, this@TodoListActivity),
                        PorterDuff.Mode.SRC_ATOP
                    )
                    setImageDrawable(imageDrawable)
                }
            }
        })
        planAddDialogView.planIconRecycler.layoutManager = GridLayoutManager(this, 6)
        planAddDialogView.planIconRecycler.adapter = adapter

        val iconList = mutableListOf<CategoryIconType>()
        iconList.add(CategoryIconType.HOME)
        iconList.add(CategoryIconType.HEALTH_CROSS)
        iconList.add(CategoryIconType.PILLS)
        iconList.add(CategoryIconType.CAFE)
        iconList.add(CategoryIconType.RESTAURANT)
        iconList.add(CategoryIconType.DRINK)

        iconList.add(CategoryIconType.FAVORITE)
        iconList.add(CategoryIconType.STRAWBERRY_CAKE)
        iconList.add(CategoryIconType.GIFT)
        iconList.add(CategoryIconType.MUSIC)
        iconList.add(CategoryIconType.PIGGY_BANK_SLOT)
        iconList.add(CategoryIconType.RECEIPT)

        iconList.add(CategoryIconType.BOOKMARK)
        iconList.add(CategoryIconType.FLAG)
        iconList.add(CategoryIconType.PORTFOLIO)
        iconList.add(CategoryIconType.DOCUMENT)
        iconList.add(CategoryIconType.CYCLIST)
        iconList.add(CategoryIconType.TENNIS)

        iconList.add(CategoryIconType.PLANE)
        iconList.add(CategoryIconType.CAR)
        iconList.add(CategoryIconType.CAMPSITE)
        iconList.add(CategoryIconType.LIGHTNING)
        iconList.add(CategoryIconType.CROSS)

        adapter.submitList(iconList)

        val imageViews = listOf(
            planAddDialogView.planColorSettingLayout.categoryColorImg1,
            planAddDialogView.planColorSettingLayout.categoryColorImg2,
            planAddDialogView.planColorSettingLayout.categoryColorImg3,
            planAddDialogView.planColorSettingLayout.categoryColorImg4,
            planAddDialogView.planColorSettingLayout.categoryColorImg5,
            planAddDialogView.planColorSettingLayout.categoryColorImg6,
            planAddDialogView.planColorSettingLayout.categoryColorImg7,
            planAddDialogView.planColorSettingLayout.categoryColorImg8,
            planAddDialogView.planColorSettingLayout.categoryColorImg9,
            planAddDialogView.planColorSettingLayout.categoryColorImg10,
            planAddDialogView.planColorSettingLayout.categoryColorImg11,
            planAddDialogView.planColorSettingLayout.categoryColorImg12
        )


        imageViews.forEach { imageView ->
            imageView.setOnClickListener {
                if (planAddDialogView.planTitleEditText.isFocused) {
                    bottomSheetDialog.setupHideKeyboardOnTouchOutside(planAddDialogView.planTitleEditText)
                }

                updateSelection(imageView, planAddDialogView.planColorImg)

                color = when (imageView) {
                    planAddDialogView.planColorSettingLayout.categoryColorImg1 -> "default_color_1"
                    planAddDialogView.planColorSettingLayout.categoryColorImg2 -> "default_color_2"
                    planAddDialogView.planColorSettingLayout.categoryColorImg3 -> "default_color_3"
                    planAddDialogView.planColorSettingLayout.categoryColorImg4 -> "default_color_4"
                    planAddDialogView.planColorSettingLayout.categoryColorImg5 -> "default_color_5"
                    planAddDialogView.planColorSettingLayout.categoryColorImg6 -> "default_color_6"
                    planAddDialogView.planColorSettingLayout.categoryColorImg7 -> "default_color_7"
                    planAddDialogView.planColorSettingLayout.categoryColorImg8 -> "default_color_8"
                    planAddDialogView.planColorSettingLayout.categoryColorImg9 -> "default_color_9"
                    planAddDialogView.planColorSettingLayout.categoryColorImg10 -> "default_color_10"
                    planAddDialogView.planColorSettingLayout.categoryColorImg11 -> "default_color_11"
                    planAddDialogView.planColorSettingLayout.categoryColorImg12 -> "default_color_12"
                    else -> "default_color_1"
                }
                planAddDialogView.planColorImg.colorFilter = PorterDuffColorFilter(
                    colorStringToColor(color, this),
                    PorterDuff.Mode.SRC_ATOP
                )

                planAddDialogView.planIconImg.apply {
                    background.colorFilter = PorterDuffColorFilter(
                        colorStringToColor(color, this@TodoListActivity),
                        PorterDuff.Mode.SRC_ATOP
                    )
                    background.alpha = 51

                    val imageDrawable = this.drawable
                    imageDrawable!!.colorFilter = PorterDuffColorFilter(
                        colorStringToColor(color, this@TodoListActivity),
                        PorterDuff.Mode.SRC_ATOP
                    )
                    setImageDrawable(imageDrawable)
                }

            }
        }

        planAddDialogView.planTitleEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                p0: CharSequence?,
                p1: Int,
                p2: Int,
                p3: Int
            ) {

            }

            override fun onTextChanged(
                p0: CharSequence?,
                p1: Int,
                p2: Int,
                p3: Int
            ) {

            }

            override fun afterTextChanged(p0: Editable?) {
                planTitle = p0.toString()
            }
        })

        var isPlanIconExpanded = false
        var isPlanColorExpanded = false
        var isPlanRepeatExpanded = false
        var isPlanStartTimeExpanded = false

        // 선택 이벤트

        preventKeyboardFromShowing(planAddDialogView.planStartTimePicker)

        planAddDialogView.planIconSettingConstraint.setOnClickListener {
            if (planAddDialogView.planTitleEditText.isFocused) {
                bottomSheetDialog.setupHideKeyboardOnTouchOutside(planAddDialogView.planTitleEditText)
            }


            if (isPlanIconExpanded) {
                isPlanIconExpanded = false
                planAddDialogView.planIconSettingExpandView.visibility = View.GONE
            } else {
                isPlanIconExpanded = true
                planAddDialogView.planIconSettingExpandView.visibility = View.VISIBLE
            }
        }

        planAddDialogView.planColorSettingConstraint.setOnClickListener {
            if (planAddDialogView.planTitleEditText.isFocused) {
                bottomSheetDialog.setupHideKeyboardOnTouchOutside(planAddDialogView.planTitleEditText)
            }

            if (isPlanColorExpanded) {
                isPlanColorExpanded = false
                planAddDialogView.planColorSettingExpandView.visibility = View.GONE
            } else {
                isPlanColorExpanded = true
                planAddDialogView.planColorSettingExpandView.visibility = View.VISIBLE
            }
        }

        planAddDialogView.planRepeatSettingConstraint.setOnClickListener {
            if (planAddDialogView.planTitleEditText.isFocused) {
                bottomSheetDialog.setupHideKeyboardOnTouchOutside(planAddDialogView.planTitleEditText)
            }

            if (isPlanRepeatExpanded) {
                isPlanRepeatExpanded = false
                planAddDialogView.planRepeatSettingExpandView.visibility = View.GONE
            } else {
                isPlanRepeatExpanded = true
                planAddDialogView.planRepeatSettingExpandView.visibility = View.VISIBLE
            }
        }

        planAddDialogView.planRepeatWeeklyBtn.setOnClickListener {
            if (planAddDialogView.planTitleEditText.isFocused) {
                bottomSheetDialog.setupHideKeyboardOnTouchOutside(planAddDialogView.planTitleEditText)
            }

            repeatRule = generateRepeatRule(
                "WEEKLY",
                daysOfWeek = listOf(LocalDate.parse(viewModel.selectedDate.value.toString()).dayOfWeek.toICalDay())
            )

            planAddDialogView.planRepeatSettingText.text =
                if (todo != null) dateToString(LocalDate.parse(todo!!.startDate), "WEEKLY")
                else dateToString(
                    LocalDate.parse(viewModel.selectedDate.value.toString()),
                    "WEEKLY"
                )


            if (it.isSelected) {

            } else {
                it.isSelected = true
                planAddDialogView.planRepeatMonthlyBtn.isSelected = false
                planAddDialogView.planRepeatYearlyBtn.isSelected = false
            }

        }

        planAddDialogView.planRepeatMonthlyBtn.setOnClickListener {
            if (planAddDialogView.planTitleEditText.isFocused) {
                bottomSheetDialog.setupHideKeyboardOnTouchOutside(planAddDialogView.planTitleEditText)
            }

            repeatRule = generateRepeatRule(
                "MONTHLY",
                monthDays = listOf(LocalDate.parse(viewModel.selectedDate.value.toString()).dayOfMonth)
            )

            planAddDialogView.planRepeatSettingText.text =
                if (todo != null) dateToString(LocalDate.parse(todo!!.startDate), "MONTHLY")
                else dateToString(
                    LocalDate.parse(viewModel.selectedDate.value.toString()),
                    "MONTHLY"
                )

            if (it.isSelected) {

            } else {
                it.isSelected = true
                planAddDialogView.planRepeatWeeklyBtn.isSelected = false
                planAddDialogView.planRepeatYearlyBtn.isSelected = false
            }

        }

        planAddDialogView.planRepeatYearlyBtn.setOnClickListener {
            if (planAddDialogView.planTitleEditText.isFocused) {
                bottomSheetDialog.setupHideKeyboardOnTouchOutside(planAddDialogView.planTitleEditText)
            }

            // 저장된 일정인지, 새로 저장하는 일정인지 구분해야 함
            repeatRule = generateRepeatRule(
                "YEARLY",
                monthDays = listOf(LocalDate.parse(viewModel.selectedDate.value.toString()).dayOfMonth),
                months = listOf(LocalDate.parse(viewModel.selectedDate.value.toString()).monthValue)
            )

            planAddDialogView.planRepeatSettingText.text =
                if (todo != null) dateToString(LocalDate.parse(todo!!.startDate), "YEARLY")
                else dateToString(
                    LocalDate.parse(viewModel.selectedDate.value.toString()),
                    "YEARLY"
                )

            if (it.isSelected) {

            } else {
                it.isSelected = true
                planAddDialogView.planRepeatWeeklyBtn.isSelected = false
                planAddDialogView.planRepeatMonthlyBtn.isSelected = false
            }

        }

        planAddDialogView.planAllDaySettingSwitch.setOnCheckedChangeListener(object :
            CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
                if (planAddDialogView.planTitleEditText.isFocused) {
                    bottomSheetDialog.setupHideKeyboardOnTouchOutside(planAddDialogView.planTitleEditText)
                }
                // 스위치 On 돼있을 때 시작 시간 부분 비활성화

                startTime = if (p1) "AllDay"
                else ""
            }
        })

        planAddDialogView.planStartTimeSettingConstraint.setOnClickListener {
            if (planAddDialogView.planTitleEditText.isFocused) {
                bottomSheetDialog.setupHideKeyboardOnTouchOutside(planAddDialogView.planTitleEditText)
            }

            if (isPlanStartTimeExpanded) {
                isPlanStartTimeExpanded = false
                planAddDialogView.planStartTimeSettingExpandView.visibility = View.GONE
            } else {
                isPlanStartTimeExpanded = true
                planAddDialogView.planStartTimeSettingExpandView.visibility = View.VISIBLE
            }
        }

        planAddDialogView.planStartTimePicker.setOnTimeChangedListener(object :
            TimePicker.OnTimeChangedListener {
            override fun onTimeChanged(
                p0: TimePicker?,
                p1: Int,
                p2: Int
            ) {

                startTime = timePickerToString(p1, p2)

                planAddDialogView.planStartTimeSettingText.apply {
                    visibility = View.VISIBLE
                    text = startTime
                }

            }
        })

        planAddDialogView.planSaveBtn.setOnClickListener {

            if (todo == null) viewModel.addPlan(
                viewModel.selectedDate.value.toString(), planTitle.toString(),
                repeatRule, color.toString(), editCategoryIconType, startTime
            ).invokeOnCompletion {

                if (it is CancellationException) {

                } else bottomSheetDialog.dismiss()

            }
            else viewModel.updateTodo(
                todo.copy(
                    title = planTitle.toString(),
                    repeatRule = repeatRule,
                    color = color.toString(),
                    icon = editCategoryIconType,
                    startTime = startTime
                )
            ).invokeOnCompletion {
                if (it is CancellationException) {

                } else bottomSheetDialog.dismiss()
            }

        }

        planAddDialogView.planDeleteBtn.setOnClickListener {

            if(todo?.repeatRule == null) viewModel.deleteTodo(todo?.id.toString()).invokeOnCompletion {

                if (it is CancellationException) {

                } else bottomSheetDialog.dismiss()
            }
            else planDeleteDialog(todo?.id.toString(), bottomSheetDialog)

        }

        bottomSheetDialog.show()

    }

    private fun setTodoInGoal() = lifecycleScope.launch {
        viewModel.getTodoByGoal(viewModel.currentGoal.value?.goalId.toString())
    }

    private fun showTodoEditDialog(todo: Todo) {
        val bottomSheetDialog = BottomSheetDialog(this, R.style.BottomSheetTheme)
        val todoEditBottomSheetView = TodoEditBottomSheetLayoutBinding.inflate(layoutInflater)

        bottomSheetDialog.setContentView(todoEditBottomSheetView.root)

        bottomSheetDialog.behavior.isDraggable = false

        todoEditBottomSheetView.dialogCloseBtn.setOnClickListener {

            bottomSheetDialog.dismiss()

        }

        todoEditBottomSheetView.bottomSheetTodoTitleText.text = todo.title

        if (todo.categoryId == viewModel.currentGoal.value?.goalId) {
            todoEditBottomSheetView.todoRepeatBtn.visibility = View.GONE
            todoEditBottomSheetView.todoTodayTomorrowBtn.visibility = View.GONE

            todoEditBottomSheetView.todoCategoryChangeBtn.visibility = View.GONE
            todoEditBottomSheetView.todoRepeatEndDateBtn.visibility = View.GONE
            todoEditBottomSheetView.todoRepeatEndDateBtn.visibility = View.GONE
        }

        if(todo.repeatRule == null) todoEditBottomSheetView.todoRepeatEndDateBtn.visibility = View.GONE

        when {

            // 선택된 날짜가 오늘 날짜와 같은 경우
            viewModel.selectedDate.value.toString() == LocalDate.now().toString() -> {

                if (todo.status?.get(viewModel.selectedDate.value.toString()) == TodoStatus.COMPLETED
                    || todo.status?.get(viewModel.selectedDate.value.toString()) == TodoStatus.IN_PROGRESS) {
                    todoEditBottomSheetView.todoTodayTomorrowBtn.text = "내일도 하기"
                    todoEditBottomSheetView.todoTodayTomorrowBtn.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        R.drawable.todo_edit_plus_icon,
                        0,
                        0
                    )
                } else {
                    todoEditBottomSheetView.todoTodayTomorrowBtn.text = "내일 하기"
                    todoEditBottomSheetView.todoTodayTomorrowBtn.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        R.drawable.todo_tomorrow_icon,
                        0,
                        0
                    )


                }
                // 미완료 인지, 완료 인지 구분
            }

            // 미래의 일정인 경우
            LocalDate.parse(viewModel.selectedDate.value.toString()) > LocalDate.now() -> {

                if (todo.status?.get(viewModel.selectedDate.value.toString()) == TodoStatus.COMPLETED
                    || todo.status?.get(viewModel.selectedDate.value.toString()) == TodoStatus.IN_PROGRESS) {
                    todoEditBottomSheetView.todoTodayTomorrowBtn.text = "오늘도 하기"
                    todoEditBottomSheetView.todoTodayTomorrowBtn.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        R.drawable.todo_edit_tomorrow_check_icon,
                        0,
                        0
                    )
                } else {
                    todoEditBottomSheetView.todoTodayTomorrowBtn.text = "오늘 하기"
                    todoEditBottomSheetView.todoTodayTomorrowBtn.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        R.drawable.todo_edit_check_icon,
                        0,
                        0
                    )
                }

            }

            // 과거의 일정인 경우
            LocalDate.parse(viewModel.selectedDate.value.toString()) < LocalDate.now() -> {

                if (todo.status?.get(viewModel.selectedDate.value.toString()) == TodoStatus.COMPLETED
                    || todo.status?.get(viewModel.selectedDate.value.toString()) == TodoStatus.IN_PROGRESS) {
                    todoEditBottomSheetView.todoTodayTomorrowBtn.text = "오늘도 하기"
                    todoEditBottomSheetView.todoTodayTomorrowBtn.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        R.drawable.todo_edit_tomorrow_check_icon,
                        0,
                        0
                    )

                } else {
                    todoEditBottomSheetView.todoTodayTomorrowBtn.text = "오늘 하기"
                    todoEditBottomSheetView.todoTodayTomorrowBtn.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        R.drawable.todo_edit_check_icon,
                        0,
                        0
                    )

                }

            }

            // 미완료 인지, 완료 인지 구분

        }

        todoEditBottomSheetView.todoRepeatBtn.setOnClickListener {

            bottomSheetDialog.dismiss()
            todoRepeatSettingDialog(todo)

        }


        todoEditBottomSheetView.todoTodayTomorrowBtn.setOnClickListener {

            var editTodo: Todo = todo

            // 로직 다시 확인해야 함 오늘도 하기, 내일도 하기는 새로운 일정 추가되는 방식으로
            when (todoEditBottomSheetView.todoTodayTomorrowBtn.text) {
                "오늘 하기" -> {
                    editTodo = todo.copy(
                        startDate = LocalDate.now().toString(),
                        endDate = LocalDate.now().toString()
                    )

                }

                // 반복 규칙 없애야함 
                "오늘도 하기" -> {
                    editTodo = todo.copy(
                        id = UUID.randomUUID().toString(),
                        startDate = LocalDate.now().toString(),
                        endDate = LocalDate.now().toString(),
                        repeatRule = null
                    )

                }

                "내일 하기" -> {
                    editTodo = todo.copy(
                        startDate = LocalDate.now().plusDays(1L).toString(),
                        endDate = LocalDate.now().plusDays(1L).toString()
                    )

                }

                "내일도 하기" -> {
                    editTodo = todo.copy(
                        id = UUID.randomUUID().toString(),
                        startDate = LocalDate.now().plusDays(1L).toString(),
                        endDate = LocalDate.now().plusDays(1L).toString(),
                        repeatRule = null
                    )
                }
            }

            viewModel.updateTodo(editTodo).invokeOnCompletion {
                bottomSheetDialog.dismiss()
            }

        }

        // 우선순위 t/f에 따라 아이콘 변경 물어봐야 함

        if (todo.priority) {
            todoEditBottomSheetView.todoPriorityBtn.text = "우선순위 해제"
            todoEditBottomSheetView.todoPriorityBtn.setCompoundDrawablesRelativeWithIntrinsicBounds(
                R.drawable.todo_priority_fill_icon,
                0,
                0,
                0
            )
        } else {
            todoEditBottomSheetView.todoPriorityBtn.text = "우선순위 등록"
            todoEditBottomSheetView.todoPriorityBtn.setCompoundDrawablesRelativeWithIntrinsicBounds(
                R.drawable.todo_priority_icon,
                0,
                0,
                0
            )
        }

        todoEditBottomSheetView.todoPriorityBtn.setOnClickListener {

            var editTodo: Todo = if (todo.priority) todo.copy(priority = false)
            else todo.copy(priority = true)


            viewModel.updateTodo(editTodo).invokeOnCompletion {
                bottomSheetDialog.dismiss()
            }
        }

        todoEditBottomSheetView.todoDeleteBtn.setOnClickListener {



            val dialogView = TodoDeleteDialogLayoutBinding.inflate(layoutInflater)
            val dialog =
                AlertDialog.Builder(this, R.style.DialogTransparentTheme).setView(dialogView.root)
                    .create()


            dialogView.todoDeleteCancelBtn.setOnClickListener {

                dialog.dismiss()

            }

            dialogView.todoDeleteBtn.setOnClickListener {
                viewModel.deleteTodo(todo.id).invokeOnCompletion {

                    if (it is CancellationException)
                    else {
                        if(todo.categoryId == viewModel.currentGoal.value?.goalId.toString()) {

                            Log.e("goalTodoDelete",viewModel.currentTodoInGoal.value.toString())
//                            todoInGoalRecyclerAdapter.notifyDataSetChanged()
                        }
                        
                        dialog.dismiss()
                        bottomSheetDialog.dismiss()
                        


                    }
                }

            }

            if(todo.repeatRule == null) {
                viewModel.deleteTodo(todo.id).invokeOnCompletion {

                    if (it is CancellationException)
                    else {
                        dialog.dismiss()
                        bottomSheetDialog.dismiss()


                    }
                }
            }
            else dialog.show()
        }

        todoEditBottomSheetView.todoMemoBtn.setOnClickListener {

            bottomSheetDialog.dismiss()
            todoMemoDialog(todo)

        }

        todoEditBottomSheetView.todoRepeatEndDateBtn.setOnClickListener {

            val editTodo = todo.copy(repeatRule = addUntilToRRule(todo.repeatRule.toString(), viewModel.selectedDate.value.toString()))

            viewModel.updateTodo(editTodo)
                .invokeOnCompletion {
                    bottomSheetDialog.dismiss()
                }

//            viewModel.updateTodo(todo.copy(endDate = viewModel.selectedDate.value.toString()))
//                .invokeOnCompletion {
//                    bottomSheetDialog.dismiss()
//                }

        }

        todoEditBottomSheetView.todoCategoryChangeBtn.setOnClickListener {
            bottomSheetDialog.dismiss()
            todoCategoryChangeDialog(todo)
        }

        bottomSheetDialog.show()

    }

    private fun planDeleteDialog(planId : String, bottomSheetDialog: BottomSheetDialog) {


        val dialogView = PlanDeleteDialogLayoutBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(this, R.style.DialogTransparentTheme).setView(dialogView.root)
                .create()

        dialogView.planDeleteCancelBtn.setOnClickListener {
            dialog.dismiss()
        }

        dialogView.planDeleteBtn.setOnClickListener {
            viewModel.deleteTodo(planId).invokeOnCompletion {

                if (it is CancellationException) {

                } else {
                    dialog.dismiss()
                    bottomSheetDialog.dismiss()
                }
            }


        }


        dialog.show()

    }

    private fun showCategoryAddDialog() {

        val bottomSheetDialog = BottomSheetDialog(this, R.style.BottomSheetTheme)
        val categoryAddDialogView = CategoryAddBottomSheetLayoutBinding.inflate(layoutInflater)

        var categoryIconConstraintExpanded = false
        var categoryColorConstraintExpanded = false

        categoryAddDialogView.root.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                if (categoryAddDialogView.categoryAddTitleEditText.isFocused) {
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(categoryAddDialogView.root.windowToken, 0)
                    categoryAddDialogView.categoryAddTitleEditText.clearFocus()
                    return@setOnTouchListener true // 이벤트 소비
                }
            }
            return@setOnTouchListener false // 이벤트 전달
        }

        bottomSheetDialog.setOnShowListener {
            val bottomSheet =
                bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)

            bottomSheetBehaviorSetting(bottomSheet)
        }

        ViewCompat.setOnApplyWindowInsetsListener(categoryAddDialogView.root) { view, insets ->
            val imeHeight = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
            view.updatePadding(bottom = imeHeight)
            insets
        }

        val imageViews = listOf(
            categoryAddDialogView.categoryColorSettingLayout.categoryColorImg1,
            categoryAddDialogView.categoryColorSettingLayout.categoryColorImg2,
            categoryAddDialogView.categoryColorSettingLayout.categoryColorImg3,
            categoryAddDialogView.categoryColorSettingLayout.categoryColorImg4,
            categoryAddDialogView.categoryColorSettingLayout.categoryColorImg5,
            categoryAddDialogView.categoryColorSettingLayout.categoryColorImg6,
            categoryAddDialogView.categoryColorSettingLayout.categoryColorImg7,
            categoryAddDialogView.categoryColorSettingLayout.categoryColorImg8,
            categoryAddDialogView.categoryColorSettingLayout.categoryColorImg9,
            categoryAddDialogView.categoryColorSettingLayout.categoryColorImg10,
            categoryAddDialogView.categoryColorSettingLayout.categoryColorImg11,
            categoryAddDialogView.categoryColorSettingLayout.categoryColorImg12
        )
        var categoryColor = "default_color_1"

        imageViews.forEach { imageView ->
            imageView.setOnClickListener {
                if (categoryAddDialogView.categoryAddTitleEditText.isFocused) {
                    bottomSheetDialog.setupHideKeyboardOnTouchOutside(categoryAddDialogView.categoryAddTitleEditText)
                }

                updateSelection(imageView, categoryAddDialogView.categoryAddColorSettingImg)

                categoryColor = when (imageView) {
                    categoryAddDialogView.categoryColorSettingLayout.categoryColorImg1 -> "default_color_1"
                    categoryAddDialogView.categoryColorSettingLayout.categoryColorImg2 -> "default_color_2"
                    categoryAddDialogView.categoryColorSettingLayout.categoryColorImg3 -> "default_color_3"
                    categoryAddDialogView.categoryColorSettingLayout.categoryColorImg4 -> "default_color_4"
                    categoryAddDialogView.categoryColorSettingLayout.categoryColorImg5 -> "default_color_5"
                    categoryAddDialogView.categoryColorSettingLayout.categoryColorImg6 -> "default_color_6"
                    categoryAddDialogView.categoryColorSettingLayout.categoryColorImg7 -> "default_color_7"
                    categoryAddDialogView.categoryColorSettingLayout.categoryColorImg8 -> "default_color_8"
                    categoryAddDialogView.categoryColorSettingLayout.categoryColorImg9 -> "default_color_9"
                    categoryAddDialogView.categoryColorSettingLayout.categoryColorImg10 -> "default_color_10"
                    categoryAddDialogView.categoryColorSettingLayout.categoryColorImg11 -> "default_color_11"
                    categoryAddDialogView.categoryColorSettingLayout.categoryColorImg12 -> "default_color_12"
                    else -> "default_color_1"
                }

                categoryAddDialogView.categoryAddIconSettingImg.apply {
                    background.colorFilter = PorterDuffColorFilter(
                        colorStringToColor(
                            categoryColor,
                            this@TodoListActivity
                        ), PorterDuff.Mode.SRC_ATOP
                    )
                    background.alpha = 51

                    val imageDrawable = this.drawable
                    imageDrawable.colorFilter = PorterDuffColorFilter(
                        colorStringToColor(
                            categoryColor,
                            this@TodoListActivity
                        ), PorterDuff.Mode.SRC_ATOP
                    )
                    setImageDrawable(imageDrawable)
                }
            }
        }

        var categoryTitle = ""

        bottomSheetDialog.setContentView(categoryAddDialogView.root)

        var editCategoryIconType = CategoryIconType.HOME

        categoryAddDialogView.dialogCloseBtn.setOnClickListener {

            bottomSheetDialog.dismiss()

        }

        categoryAddDialogView.categoryAddTitleEditText.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(
                p0: CharSequence?,
                p1: Int,
                p2: Int,
                p3: Int
            ) {

            }

            override fun onTextChanged(
                p0: CharSequence?,
                p1: Int,
                p2: Int,
                p3: Int
            ) {

            }

            override fun afterTextChanged(p0: Editable?) {
                categoryTitle = p0.toString()
            }
        })

        categoryAddDialogView.categoryAddSaveBtn.setOnClickListener {

            viewModel.addCategory(title = categoryTitle, editCategoryIconType, categoryColor)
                .invokeOnCompletion { throwable ->

                    when (throwable) {
                        is CancellationException -> {

                        }

                        else -> bottomSheetDialog.dismiss()

                    }

                }

        }


        val adapter = CategoryIconRecyclerAdapter(object : CategoryIconClickListener {
            override fun iconClick(categoryIconType: CategoryIconType) {
                editCategoryIconType = categoryIconType

                if (categoryAddDialogView.categoryAddTitleEditText.isFocused) {
                    bottomSheetDialog.setupHideKeyboardOnTouchOutside(categoryAddDialogView.categoryAddTitleEditText)
                }

                categoryAddDialogView.categoryAddIconSettingImg.apply {
                    background.colorFilter = PorterDuffColorFilter(
                        colorStringToColor(
                            categoryColor,
                            this@TodoListActivity
                        ), PorterDuff.Mode.SRC_ATOP
                    )
                    background.alpha = 51

                    val imageDrawable = ContextCompat.getDrawable(
                        this@TodoListActivity,
                        iconToDrawable(categoryIconType)
                    )
                    imageDrawable!!.colorFilter = PorterDuffColorFilter(
                        colorStringToColor(
                            categoryColor,
                            this@TodoListActivity
                        ), PorterDuff.Mode.SRC_ATOP
                    )
                    setImageDrawable(imageDrawable)
                }

//                categoryAddDialogView.categoryAddIconSettingImg.setImageResource(iconToDrawable(categoryIconType))
            }
        })
        categoryAddDialogView.categoryIconRecycler.layoutManager = GridLayoutManager(this, 6)
        categoryAddDialogView.categoryIconRecycler.adapter = adapter

        val iconList = mutableListOf<CategoryIconType>()
        iconList.add(CategoryIconType.HOME)
        iconList.add(CategoryIconType.HEALTH_CROSS)
        iconList.add(CategoryIconType.PILLS)
        iconList.add(CategoryIconType.CAFE)
        iconList.add(CategoryIconType.RESTAURANT)
        iconList.add(CategoryIconType.DRINK)

        iconList.add(CategoryIconType.FAVORITE)
        iconList.add(CategoryIconType.STRAWBERRY_CAKE)
        iconList.add(CategoryIconType.GIFT)
        iconList.add(CategoryIconType.MUSIC)
        iconList.add(CategoryIconType.PIGGY_BANK_SLOT)
        iconList.add(CategoryIconType.RECEIPT)

        iconList.add(CategoryIconType.BOOKMARK)
        iconList.add(CategoryIconType.FLAG)
        iconList.add(CategoryIconType.PORTFOLIO)
        iconList.add(CategoryIconType.DOCUMENT)
        iconList.add(CategoryIconType.CYCLIST)
        iconList.add(CategoryIconType.TENNIS)

        iconList.add(CategoryIconType.PLANE)
        iconList.add(CategoryIconType.CAR)
        iconList.add(CategoryIconType.CAMPSITE)
        iconList.add(CategoryIconType.LIGHTNING)
        iconList.add(CategoryIconType.CROSS)

        adapter.submitList(iconList)

        categoryAddDialogView.categoryAddIconConstraint.setOnClickListener {
            if (categoryAddDialogView.categoryAddTitleEditText.isFocused) {
                bottomSheetDialog.setupHideKeyboardOnTouchOutside(categoryAddDialogView.categoryAddTitleEditText)
            }

            if (categoryIconConstraintExpanded) {

                categoryAddDialogView.categoryAddIconSettingConstraint.visibility = View.GONE

                categoryIconConstraintExpanded = false
            } else {

                categoryAddDialogView.categoryAddIconSettingConstraint.visibility = View.VISIBLE
                categoryIconConstraintExpanded = true
            }
        }

        categoryAddDialogView.categoryAddColorConstraint.setOnClickListener {
            if (categoryAddDialogView.categoryAddTitleEditText.isFocused) {
                bottomSheetDialog.setupHideKeyboardOnTouchOutside(categoryAddDialogView.categoryAddTitleEditText)
            }


            if (categoryColorConstraintExpanded) {

                categoryAddDialogView.categoryAddColorSettingConstraint.visibility = View.GONE
                categoryColorConstraintExpanded = false
            } else {

                categoryAddDialogView.categoryAddColorSettingConstraint.visibility = View.VISIBLE
                categoryColorConstraintExpanded = true
            }
        }

        bottomSheetDialog.show()

    }

    private fun todoRepeatSettingDialog(todo: Todo) {

        val bottomSheetDialog = BottomSheetDialog(this, R.style.BottomSheetTheme)
        val todoRepeatDialogView =
            TodoItemRepeatSettingBottomSheetLayoutBinding.inflate(layoutInflater)

        bottomSheetDialog.setContentView(todoRepeatDialogView.root)


        bottomSheetDialog.setOnShowListener {
            val bottomSheet =
                bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)

            bottomSheetBehaviorSetting(bottomSheet)
        }

        var editTodo = todo
        todoRepeatDialogView.todoRepeatDailyBtn.isSelected = true
        todoRepeatDialogView.repeatDailySettingLayout.todayStartBtn.isSelected = true


        var repeatType = if(todo.repeatRule == null) "DAILY"
        else parseRRule(todo.repeatRule)["FREQ"]

        var dailyRepeatRule = generateRepeatRule("DAILY")
        var weeklyRepeatRule = if(repeatType == "WEEKLY") parseRRule(todo.repeatRule.toString())["BYDAY"]
        else ""
        var monthlyRepeatRule = if(repeatType == "MONTHLY") parseRRule(todo.repeatRule.toString())["BYMONTHDAY"]
        else ""

        val monthlyRptSelectedDates = mutableListOf<Int>()

        preventKeyboardFromShowing(todoRepeatDialogView.todoRepeatEndDatePicker)

        editTodo = todo.copy(repeatRule = if(todo.repeatRule == null) generateRepeatRule("DAILY")
        else todo.repeatRule)

        todo.repeatRule?.let { rptRule ->

            when {
                rptRule.contains("DAILY") -> todoRepeatDialogView.todoRepeatDailyBtn.isSelected = true

                rptRule.contains("WEEKLY") -> {
                    todoRepeatDialogView.todoRepeatDailyBtn.isSelected = false
                    todoRepeatDialogView.todoRepeatWeeklyBtn.isSelected = true

                    todoRepeatDialogView.todoRepeatMonthlyBtn.isSelected = false

                    todoRepeatDialogView.repeatDailySettingLayout.root.visibility = View.GONE
                    todoRepeatDialogView.repeatWeeklySettingLayout.root.visibility = View.VISIBLE

                    val rRule = parseRRule(rptRule)


                    if(rRule["BYDAY"]?.split(",")?.contains("MO") == true) todoRepeatDialogView.repeatWeeklySettingLayout.repeatMondayTextView.isSelected = true
                    if(rRule["BYDAY"]?.split(",")?.contains("TU") == true) todoRepeatDialogView.repeatWeeklySettingLayout.repeatTuesdayTextView.isSelected = true
                    if(rRule["BYDAY"]?.split(",")?.contains("WE") == true) todoRepeatDialogView.repeatWeeklySettingLayout.repeatWednesdayTextView.isSelected = true
                    if(rRule["BYDAY"]?.split(",")?.contains("TH") == true) todoRepeatDialogView.repeatWeeklySettingLayout.repeatThursdayTextView.isSelected = true
                    if(rRule["BYDAY"]?.split(",")?.contains("FR") == true) todoRepeatDialogView.repeatWeeklySettingLayout.repeatFridayTextView.isSelected = true
                    if(rRule["BYDAY"]?.split(",")?.contains("SA") == true) todoRepeatDialogView.repeatWeeklySettingLayout.repeatSaturdayTextView.isSelected = true
                    if(rRule["BYDAY"]?.split(",")?.contains("SU") == true) todoRepeatDialogView.repeatWeeklySettingLayout.repeatSundayTextView.isSelected = true


                    todoRepeatDialogView.repeatMonthlySettingLayout.root.visibility = View.GONE

                }
                rptRule.contains("MONTHLY") -> {
                    val rRule = parseRRule(rptRule)
                    val byMonthDay = rRule["BYMONTHDAY"]?.split(",")?.map { it.toInt() } ?: mutableListOf<Int>()


                    monthlyRptSelectedDates.addAll(byMonthDay)

                    todoRepeatDialogView.repeatMonthlySettingLayout.root.visibility = View.VISIBLE

                    todoRepeatDialogView.todoRepeatDailyBtn.isSelected = false
                    todoRepeatDialogView.todoRepeatWeeklyBtn.isSelected = false

                    todoRepeatDialogView.todoRepeatMonthlyBtn.isSelected = true
                }

            }

            if(todo.startDate != todo.endDate) {

                todoRepeatDialogView.todoRepeatEndDateSwitch.isChecked = true
                todoRepeatDialogView.todoRepeatEndDatePicker.visibility = View.VISIBLE

                val endDate = LocalDate.parse(todo.endDate)

                todoRepeatDialogView.todoRepeatEndDatePicker.updateDate(
                    endDate.year,
                    endDate.monthValue - 1,
                    endDate.dayOfMonth
                )

                todoRepeatDialogView.todoRepeatEndDateSettingText.text = localDateToDateString(endDate)

            }
            else {

                todoRepeatDialogView.todoRepeatEndDateSettingText.text = localDateToDateString(LocalDate.parse(viewModel.selectedDate.value.toString()))

            }



        }


        todoRepeatDialogView.dialogCloseBtn.setOnClickListener {

            bottomSheetDialog.dismiss()

        }


        todoRepeatDialogView.todoRepeatDailyBtn.setOnClickListener {

            repeatType = "DAILY"
            dailyRepeatRule = generateRepeatRule("DAILY")

            todoRepeatDialogView.todoRepeatDailyBtn.isSelected = true
            todoRepeatDialogView.todoRepeatWeeklyBtn.isSelected = false
            todoRepeatDialogView.todoRepeatMonthlyBtn.isSelected = false

//            todoRepeatDialogView.repeatDailySettingLayout.root.visibility = View.VISIBLE
//            todoRepeatDialogView.repeatDailySettingLayout.startDateChangeCalendarConstraint.visibility = View.VISIBLE

            todoRepeatDialogView.repeatWeeklySettingLayout.root.visibility = View.GONE
            todoRepeatDialogView.repeatMonthlySettingLayout.root.visibility = View.GONE
        }

        todoRepeatDialogView.repeatDailySettingLayout.todayStartBtn.setOnClickListener {

            if (!it.isSelected) {
                it.isSelected = true
                todoRepeatDialogView.repeatDailySettingLayout.startDateChangeBtn.isSelected = false
                todoRepeatDialogView.repeatDailySettingLayout.startDateChangeCalendarConstraint.visibility =
                    View.GONE
            }

            todoDailyRptStartDate = LocalDate.now()
            dailyRepeatRule = generateRepeatRule("DAILY")

        }

        val daysOfWeek =
            daysOfWeek(firstDayOfWeek = if (viewModel.firstDayOfWeekFlow.value == "Mon") DayOfWeek.MONDAY else DayOfWeek.SUNDAY)
        var currentMonth = YearMonth.now()
        val startMonth = currentMonth.minusMonths(100)
        val endMonth = currentMonth.plusMonths(100)

        todoDailyRptStartDate = LocalDate.parse(todo.startDate)

        todoRepeatDialogView.repeatDailySettingLayout.todoRptCalendarTitleContainer.root.children
            .filterIsInstance<LinearLayout>()
            .flatMap { it.children }
            .filterIsInstance<TextView>()
//            .map { it as TextView }
            .forEachIndexed { index, textView ->
                textView.text =
                    daysOfWeek[index].getDisplayName(TextStyle.SHORT, Locale.getDefault())

                textView.setTextColor(R.color.white)
            }

        todoRepeatDialogView.repeatDailySettingLayout.todoRptCalendarTitleContainer.root.getChildAt(
            1
        ).visibility = View.GONE

        todoRepeatDialogView.repeatDailySettingLayout.startDateChangeBtn.setOnClickListener {

            if (!it.isSelected) {
                it.isSelected = true
                todoRepeatDialogView.repeatDailySettingLayout.todayStartBtn.isSelected = false
            }

            todoRepeatDialogView.repeatDailySettingLayout.startDateChangeCalendarConstraint.visibility =
                View.VISIBLE
            initTodoDailyRepeatCalendar(
                todoRepeatDialogView.repeatDailySettingLayout.startDateChangeCalendar,
                startMonth,
                endMonth,
                currentMonth,
                daysOfWeek,
                todoRepeatDialogView.repeatDailySettingLayout.calendarMonthText
            )

        }

        todoRepeatDialogView.repeatDailySettingLayout.calendarPreviousBtn.setOnClickListener {
            currentMonth = currentMonth.minusMonths(1)
            todoRepeatDialogView.repeatDailySettingLayout.startDateChangeCalendar.scrollToMonth(
                currentMonth
            )
        }

        todoRepeatDialogView.repeatDailySettingLayout.calendarNextBtn.setOnClickListener {
            currentMonth = currentMonth.plusMonths(1)

            todoRepeatDialogView.repeatDailySettingLayout.startDateChangeCalendar.scrollToMonth(
                currentMonth
            )
        }

        todoRepeatDialogView.todoRepeatWeeklyBtn.setOnClickListener {

            repeatType = "WEEKLY"

            todoRepeatDialogView.todoRepeatDailyBtn.isSelected = false
            todoRepeatDialogView.todoRepeatWeeklyBtn.isSelected = true
            todoRepeatDialogView.todoRepeatMonthlyBtn.isSelected = false

            todoRepeatDialogView.repeatWeeklySettingLayout.root.visibility = View.VISIBLE

            todoRepeatDialogView.repeatDailySettingLayout.root.visibility = View.GONE
            todoRepeatDialogView.repeatMonthlySettingLayout.root.visibility = View.GONE
        }

        val textViews = listOf(
            todoRepeatDialogView.repeatWeeklySettingLayout.repeatWeeklyEveryWeekBtn,
            todoRepeatDialogView.repeatWeeklySettingLayout.repeatWeeklyFirstWeekBtn,
            todoRepeatDialogView.repeatWeeklySettingLayout.repeatWeeklySecondWeekBtn,
            todoRepeatDialogView.repeatWeeklySettingLayout.repeatWeeklyThirdWeekBtn,
            todoRepeatDialogView.repeatWeeklySettingLayout.repeatWeeklyFourthWeekBtn,
            todoRepeatDialogView.repeatWeeklySettingLayout.repeatWeeklyLastWeekBtn
        )

        val selectedViews = mutableSetOf<TextView>()

        textViews.forEachIndexed { index, tv ->
            tv.setOnClickListener {
                if (index == 0) {
                    // 첫 번째 클릭: 모두 해제하고 자신만 선택
                    selectedViews.forEach { it.isSelected = false }
                    selectedViews.clear()
                    tv.isSelected = true
                    selectedViews.add(tv)

                    weeklyRepeatRule = generateRepeatRule("WEEKLY", interval = 1)
                } else {
                    // 다른 거 클릭: 첫 번째 해제하고 toggle
                    val firstView = textViews[0]
                    if (firstView in selectedViews) {
                        firstView.isSelected = false
                        selectedViews.remove(firstView)
                    }

                    if (tv in selectedViews) {
                        tv.isSelected = false
                        selectedViews.remove(tv)
                    } else {
                        tv.isSelected = true
                        selectedViews.add(tv)
                    }

                    weeklyRepeatRule = generateRepeatRule("MONTHLY", weekNumbers = listOf())
                }

            }
        }

        val dayMap = mapOf(
            todoRepeatDialogView.repeatWeeklySettingLayout.repeatMondayTextView to "MO",
            todoRepeatDialogView.repeatWeeklySettingLayout.repeatTuesdayTextView to "TU",
            todoRepeatDialogView.repeatWeeklySettingLayout.repeatWednesdayTextView to "WE",
            todoRepeatDialogView.repeatWeeklySettingLayout.repeatThursdayTextView to "TH",
            todoRepeatDialogView.repeatWeeklySettingLayout.repeatFridayTextView to "FR",
            todoRepeatDialogView.repeatWeeklySettingLayout.repeatSaturdayTextView to "SA",
            todoRepeatDialogView.repeatWeeklySettingLayout.repeatSundayTextView to "SU"
        )

        val selectedDays = mutableSetOf<TextView>()
        val selectedDaysOfWeek = if(todo.repeatRule == null) mutableListOf<String>()
        else parseRRule(todo.repeatRule)["BYDAY"]?.split(",")?.toMutableList() ?: mutableListOf<String>()

        selectedDaysOfWeek.forEach {
            when(it) {
                "MO" -> selectedDays.add(todoRepeatDialogView.repeatWeeklySettingLayout.repeatMondayTextView)
                "TU" -> selectedDays.add(todoRepeatDialogView.repeatWeeklySettingLayout.repeatTuesdayTextView)
                "WE" -> selectedDays.add(todoRepeatDialogView.repeatWeeklySettingLayout.repeatWednesdayTextView)
                "TH" -> selectedDays.add(todoRepeatDialogView.repeatWeeklySettingLayout.repeatThursdayTextView)
                "FR" -> selectedDays.add(todoRepeatDialogView.repeatWeeklySettingLayout.repeatFridayTextView)
                "SA" -> selectedDays.add(todoRepeatDialogView.repeatWeeklySettingLayout.repeatSaturdayTextView)
                "SU" -> selectedDays.add(todoRepeatDialogView.repeatWeeklySettingLayout.repeatSundayTextView)
                else -> selectedDays.add(todoRepeatDialogView.repeatWeeklySettingLayout.repeatMondayTextView)
            }


        }

        Log.e("selectedDaysOfWeek", selectedDaysOfWeek.toString())

        dayMap.forEach { (tv, code) ->
            tv.setOnClickListener {
                // 선택 상태 토글
                if (tv.isSelected) {
                    tv.isSelected = false
                    tv.background = ContextCompat.getDrawable(
                        this,
                        R.drawable.todo_rpt_day_change_calendar_item_default_bg
                    )
                    selectedDays.remove(tv)
                } else {
                    tv.isSelected = true
                    tv.background = ContextCompat.getDrawable(
                        this,
                        R.drawable.todo_rpt_day_change_calendar_item_pressed_bg
                    )
                    if(!selectedDays.contains(tv)) selectedDays.add(tv)
                }

                // 선택된 요일 문자열 리스트 추출
                selectedDaysOfWeek.clear()
                selectedDaysOfWeek.addAll(selectedDays.mapNotNull { dayMap[it] })

                // generateRepeatRule 호출
                weeklyRepeatRule = generateRepeatRule(
                    frequency = "WEEKLY",
                    daysOfWeek = selectedDaysOfWeek
                )

            }
        }


        todoRepeatDialogView.todoRepeatMonthlyBtn.setOnClickListener {

            repeatType = "MONTHLY"
            monthlyRepeatRule = generateRepeatRule("MONTHLY", monthDays = monthlyRptSelectedDates)

            todoRepeatDialogView.todoRepeatDailyBtn.isSelected = false
            todoRepeatDialogView.todoRepeatWeeklyBtn.isSelected = false
            todoRepeatDialogView.todoRepeatMonthlyBtn.isSelected = true

            todoRepeatDialogView.repeatMonthlySettingLayout.root.visibility = View.VISIBLE

            todoRepeatDialogView.repeatDailySettingLayout.root.visibility = View.GONE
            todoRepeatDialogView.repeatWeeklySettingLayout.root.visibility = View.GONE

        }


        val adapter = TodoMonthlyRptDayRecyclerAdapter(object : MonthlyRptDayClickListener {
            override fun dayClick(rptDay: TodoMonthlyRptDayClass) {

                if (monthlyRptSelectedDates.contains(rptDay.day.toInt())) {
                    rptDay.selected = false
                    monthlyRptSelectedDates.remove(rptDay.day.toInt())
                } else {
                    rptDay.selected = true
                    monthlyRptSelectedDates.add(rptDay.day.toInt())
                }

                monthlyRepeatRule =
                    generateRepeatRule("MONTHLY", monthDays = monthlyRptSelectedDates)
            }
        })
        todoRepeatDialogView.repeatMonthlySettingLayout.repeatMonthDayRecyclerView.adapter = adapter

        val layoutManager = GridLayoutManager(this, 7)

        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (adapter.getItemViewType(position)) {
                    TodoMonthlyRptDayRecyclerAdapter.TYPE_LAST -> 3 // 전체 너비 사용
                    else -> 1 // 기본 숫자칸은 1칸
                }
            }
        }

        todoRepeatDialogView.repeatMonthlySettingLayout.repeatMonthDayRecyclerView.layoutManager =
            layoutManager

        val monthList = monthToDateList(LocalDate.now().yearMonth, monthlyRptSelectedDates)

        monthList.add(TodoMonthlyRptDayClass("-1", false))

        adapter.submitList(monthList)


        todoRepeatDialogView.todoRepeatEndDateSwitch.setOnCheckedChangeListener(object :
            CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {

                if (p1) {
                    todoRepeatDialogView.todoRepeatEndDateSettingText.visibility = View.VISIBLE

                    val currentDate = LocalDate.parse(viewModel.selectedDate.value)
                    todoRepeatDialogView.todoRepeatEndDatePicker.visibility = View.VISIBLE
                    todoRepeatDialogView.todoRepeatEndDatePicker.updateDate(
                        currentDate.year,
                        currentDate.monthValue - 1,
                        currentDate.dayOfMonth
                    )
                } else {
                    todoRepeatDialogView.todoRepeatEndDateSettingText.visibility = View.GONE

                    editTodo = editTodo.copy(endDate = editTodo.startDate)
                    todoRepeatDialogView.todoRepeatEndDatePicker.visibility = View.GONE
                }

            }
        })

        todoRepeatDialogView.todoRepeatEndDatePicker.setOnDateChangedListener { view, year, monthOfYear, dayOfMonth ->

            val endDate = LocalDate.of(year, monthOfYear + 1, dayOfMonth)

            todoRepeatDialogView.todoRepeatEndDateSettingText.text = localDateToDateString(endDate)

            editTodo = editTodo.copy(endDate = endDate.toString())


        }


        todoRepeatDialogView.todoRepeatSettingSaveBtn.setOnClickListener {

            val repeatRule = when (repeatType) {
                "DAILY" -> dailyRepeatRule
                "WEEKLY" -> weeklyRepeatRule
                "MONTHLY" -> monthlyRepeatRule
                else -> dailyRepeatRule
            }

            editTodo =
                if (repeatType == "DAILY" && todoDailyRptStartDate.toString() != todo.startDate) editTodo.copy(
                    startDate = todoDailyRptStartDate.toString(),
                    endDate = todoDailyRptStartDate.toString(),
                    repeatRule = generateRepeatRule("DAILY")
                )
                else editTodo.copy(repeatRule = repeatRule)


            viewModel.updateTodo(editTodo).invokeOnCompletion {

                bottomSheetDialog.dismiss()
            }


        }

        bottomSheetDialog.show()
    }

    private fun updateSelection(selected: ImageView, targetImageView: ImageView) {

        selectedImageView = selected  // 현재 선택된 ImageView 업데이트

        targetImageView.setImageDrawable(selectedImageView?.drawable)

    }

    private fun todoMemoDialog(todo: Todo) {
        val dialogView = TodoMemoDialogLayoutBinding.inflate(layoutInflater)
        val dialog = AlertDialog
            .Builder(this, R.style.DialogTransparentTheme)
            .setView(dialogView.root)
            .create()

        if (todo.memo.isNotBlank()) {
            dialogView.todoMemoEditText.setText(todo.memo)
        }

        var editTodo = todo


        dialogView.todoMemoEditText.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(
                p0: CharSequence?,
                p1: Int,
                p2: Int,
                p3: Int
            ) {

            }

            override fun onTextChanged(
                p0: CharSequence?,
                p1: Int,
                p2: Int,
                p3: Int
            ) {

            }

            override fun afterTextChanged(p0: Editable?) {

                editTodo = todo.copy(memo = p0.toString())
            }
        })

        dialog.setOnDismissListener(object : DialogInterface.OnDismissListener {
            override fun onDismiss(p0: DialogInterface?) {

                // 메모 저장

                viewModel.updateTodo(editTodo).invokeOnCompletion {
                    dialog.dismiss()
                }
            }
        })


        dialog.setOnCancelListener(object : DialogInterface.OnCancelListener {
            override fun onCancel(p0: DialogInterface?) {

                // 메모 저장

                viewModel.updateTodo(editTodo).invokeOnCompletion {
                    dialog.dismiss()
                }

            }
        })

        dialog.setOnShowListener(object : DialogInterface.OnShowListener {
            override fun onShow(p0: DialogInterface?) {

                showKeyboard(this@TodoListActivity, dialogView.todoMemoEditText)

            }
        })

        dialog.show()
    }

    private fun todoCategoryChangeDialog(todo: Todo) {

        val bottomSheetDialog = BottomSheetDialog(this, R.style.BottomSheetTheme)
        val todoCategoryChangeDialogView =
            CategoryChangeBottomSheetLayoutBinding.inflate(layoutInflater)

        bottomSheetDialog.setContentView(todoCategoryChangeDialogView.root)

        todoCategoryChangeDialogView.dialogCloseBtn.setOnClickListener {

            bottomSheetDialog.dismiss()

        }

        bottomSheetDialog.behavior.isDraggable = false

        var editTodo = todo

        // 클릭 이벤트 리스너 추가
        val adapter = TodoCategoryChangeRecyclerAdapter(todo.categoryId.toString(),
            object : CategoryChangeClickListener {
                override fun categoryChangeItemClick(category: Category) {

                    editTodo = todo.copy(categoryId = category.categoryId)

                }
            })
        todoCategoryChangeDialogView.categoryChangeRecyclerView.adapter = adapter

        adapter.submitList(viewModel.categoryList.value)

        todoCategoryChangeDialogView.categoryChangeSaveBtn.setOnClickListener {

            viewModel.updateTodo(editTodo).invokeOnCompletion {

                bottomSheetDialog.dismiss()

            }
        }

        bottomSheetDialog.show()

    }

    private fun initCategoryHorizonRecyclerView() {

        caetgoryHorizonAdapter = TodoListCategoryHorizonRecyclerAdapter(object : CategoryFilterClickListener {
            override fun categoryFilter(category: Category, position: Int) {
                // 카테고리 필터

                binding.todoCategoryAllBtn.alpha = 0.4f
                binding.selectedCategoryTodoListConstraint.visibility = View.VISIBLE
                binding.todoListRecyclerView.visibility = View.GONE

                viewModel.currentSelectedCategory.value = category
//                binding.planConstraint.visibility = View.GONE

//                if(caetgoryHorizonAdapter.currentList.size == 1) caetgoryHorizonAdapter.notifyItemChanged(0)
//                else if(position != 0) caetgoryHorizonAdapter.notifyItemRangeChanged(0, position - 1)
//                else caetgoryHorizonAdapter.notifyItemRangeChanged(1, caetgoryHorizonAdapter.currentList.lastIndex-1)

                lifecycleScope.launch {
                    viewModel.categoryTodoMap.collectLatest { categoryTodoMap ->
//                        viewModel.selectedCategoryTodoList.value = it.filter { it.category == category }[0].todoList.toMutableList()

                        viewModel.selectedCategoryTodoList.update {

                            categoryTodoMap.filter { it.category == category }[0].todoList.toMutableList()

                        }
                        initSelectedCategoryTodoRecyclerView(category, LocalDate.parse(viewModel.selectedDate.value.toString()))

                        categoryRecyclerAdapter.submitList(categoryTodoMap.filter { it.category == category }) {
//                            binding.todoListWeekGoalTitleConstraint.visibility = View.GONE

                        }
                    }
                }
            }
        })
        binding.todoCategoryRecyclerView.adapter = caetgoryHorizonAdapter

        lifecycleScope.launch {
            viewModel.categoryList.collectLatest {
                caetgoryHorizonAdapter.submitList(it)
            }
        }
    }

    private fun initCategoryRecyclerView(category: Category?) {

        val selectedDate = LocalDate.parse(viewModel.selectedDate.value)

        categoryRecyclerAdapter = TodoListCategoryRecyclerAdapter(this, this, selectedDate,viewModel.sortModeFlow.value.toString())

        binding.todoListRecyclerView.adapter = categoryRecyclerAdapter

        lifecycleScope.launch {
            viewModel.categoryTodoMap.collectLatest {
                if (category != null) {
                    categoryRecyclerAdapter.submitList(it.filter { it.category == category })
                } else {
                    categoryRecyclerAdapter.submitList(it.sortedBy { it.category.index })
                }

            }
        }
    }

    private fun initPlanRecyclerView() {
        planRecyclerAdapter = PlanListRecyclerAdapter(object : PlanItemClickListener {
            override fun onPlanTitleClick(plan: Todo) {
                viewModel.updateTodo(plan).invokeOnCompletion {

                }
            }

            override fun onPlanEditBtnClick(plan: Todo) {
                showPlanAddDialog(plan)
            }
        })
        binding.planRecyclerView.adapter = planRecyclerAdapter

        lifecycleScope.launch {
            viewModel.selectedDatePlanList.collectLatest {

                val list = filterTodosByDate(it, LocalDate.parse(viewModel.selectedDate.value))
//                viewModel.datePlanMap.value.put(viewModel.selectedDate.value.toString(), list)
                planRecyclerAdapter.submitList(list)

            }

        }
    }

    private fun initSelectedCategoryTodoRecyclerView(category : Category?, selectedDate : LocalDate) {

        selectedCategoryTodoListAdapter = SelectedCategoryTodoListRecyclerAdapter(this, selectedDate.toString())
        binding.selectedCategoryTodoListRecyclerView.adapter = selectedCategoryTodoListAdapter
        lifecycleScope.launch {
            viewModel.categoryTodoMap.collectLatest { it ->
                val list = if(category != null) filterTodosByDate(it.filter { it.category == category }[0].todoList.toMutableList(), LocalDate.parse(viewModel.selectedDate.value.toString()))
                else filterTodosByDate(viewModel.selectedCategoryTodoList.value, selectedDate)

                selectedCategoryTodoListAdapter.submitList(list.sortedByDescending {
                    val priorityRank = if (it.priority == true) 1L else 0L

                    val sortRank = when(viewModel.sortModeFlow.value) {
                        "Saved" -> {
                            it.savedTime.toInstant().epochSecond
                        }
                        "Completed_Reversed" -> {
                            val isCompleted = it.status?.values?.contains(TodoStatus.COMPLETED) == true
                            val completedRank = if (isCompleted) 0 else 1
                            completedRank * 1_000_000_000L + it.savedTime.toInstant().epochSecond
                        }
                        "Completed" -> {
                            val isCompleted = it.status?.values?.contains(TodoStatus.COMPLETED) == true
                            val completedRank = if (isCompleted) 1 else 0
                            completedRank * 1_000_000_000L + it.savedTime.toInstant().epochSecond
                        }
                        else -> {
                            it.savedTime.toInstant().epochSecond

                        }
                    }

                    priorityRank * 1_000_000_000_000_000_000 + sortRank
                }
                )
            }
        }

    }

    private fun setupWeekCalendar(
        startMonth: YearMonth,
        endMonth: YearMonth,
        currentMonth: YearMonth,
        daysOfWeek: List<DayOfWeek>,
    ) {
        class WeekDayViewContainer(view: View) : ViewContainer(view) {
            lateinit var day: WeekDay
            val textView = WeekCalendarItemLayoutBinding.bind(view).weekCalendarDayText

            val calendoModeConstraint =
                WeekCalendarItemLayoutBinding.bind(view).calendarItemCalendoModeConstraint
            val calendoTodoPlanModeConstraint =
                WeekCalendarItemLayoutBinding.bind(view).calendarItemTodoPlanModeConstraint
            val calendoPlanModeConstraint =
                WeekCalendarItemLayoutBinding.bind(view).calendarItemPlanModeConstraint

            val calendoTodoText = WeekCalendarItemLayoutBinding.bind(view).calendarCalendoTodoText
            val calendoPlanText = WeekCalendarItemLayoutBinding.bind(view).calendarCalendoPlanText

            val todoFirstText = WeekCalendarItemLayoutBinding.bind(view).todoPlanModeFirstText
            val todoSecondText = WeekCalendarItemLayoutBinding.bind(view).todoPlanModeSecondText
            val todoThirdText = WeekCalendarItemLayoutBinding.bind(view).todoPlanModeThirdText
            val todoFourthText = WeekCalendarItemLayoutBinding.bind(view).todoPlanModeFourthText


            val planFirstImg = WeekCalendarItemLayoutBinding.bind(view).planModeFirstImg
            val planSecondImg = WeekCalendarItemLayoutBinding.bind(view).planModeSecondImg
            val planThirdImg = WeekCalendarItemLayoutBinding.bind(view).planModeThirdImg
            val planFourthImg = WeekCalendarItemLayoutBinding.bind(view).planModeFourthImg


            init {
                view.setOnClickListener {
                    if (day.position == WeekDayPosition.RangeDate) {
                        dateClicked(date = day.date)
                    }
                }
            }
        }
        binding.weekCalendarView.dayBinder = object : WeekDayBinder<WeekDayViewContainer> {
            override fun create(view: View): WeekDayViewContainer = WeekDayViewContainer(view)
            override fun bind(container: WeekDayViewContainer, data: WeekDay) {
                container.day = data
                bindDate(
                    data.date,
                    container.textView,
                    data.position == WeekDayPosition.RangeDate,
                    container.calendoTodoText,
                    container.calendoPlanText,

                    container.calendoModeConstraint,
                    container.calendoTodoPlanModeConstraint,
                    container.calendoPlanModeConstraint,

                    container.todoFirstText,
                    container.todoSecondText,
                    container.todoThirdText,
                    container.todoFourthText,

                    container.planFirstImg,
                    container.planSecondImg,
                    container.planThirdImg,
                    container.planFourthImg
                )
            }
        }
        binding.weekCalendarView.weekScrollListener = {

            val selectedDate = it.days[0].date

            if (isProgrammaticWeekScroll) {
                isProgrammaticWeekScroll = false
                dateClicked(LocalDate.parse(viewModel.selectedDate.value))
            } else dateClicked(selectedDate)

            val (startDate, endDate) = getWeekStartEnd(selectedDate.toString())


            if (viewModel.isWeekMode.value == true) viewModel.getGoal(startDate, endDate)
                .invokeOnCompletion {
                    Log.e("wScrollCurrentGoal",viewModel.currentGoal.value?.title.toString())
//                if(viewModel.currentGoal.value == null) {
//
//                    viewModel.currentTodoInGoal.value = emptyList<Todo>()
//                    todoInGoalRecyclerAdapter.submitList(viewModel.currentTodoInGoal.value)
//
//                }
//                else
                    setTodoInGoal()
//                        .invokeOnCompletion {
//
//                        lifecycleScope.launch {
//                            viewModel.currentTodoInGoal.collectLatest {
////`                                todoInGoalRecyclerAdapter = TodoListInGoalRecyclerAdapter(
////                                    this@TodoListActivity,
////                                    viewModel.selectedDate.value.toString()
////                                )
////                                binding.todoListWeekGoalRecyclerView.adapter =
////                                    todoInGoalRecyclerAdapter`
//
//                                if (it.isNotEmpty()) {
////                                    todoInGoalRecyclerAdapter.submitList(it)
//                                }
//                                else {
////                                    viewModel.currentTodoInGoal.value = emptyList<Todo>()
////                                    todoInGoalRecyclerAdapter.submitList(viewModel.currentTodoInGoal.value)
//                                }
//
//                            }
//                        }
//
//                    }

                }
        }

        binding.weekCalendarView.setup(
            startMonth.atStartOfMonth(),
            endMonth.atEndOfMonth(),
            daysOfWeek.first(),
        )
        isProgrammaticWeekScroll = true

        binding.weekCalendarView.scrollToWeek(LocalDate.now())
    }

    private fun setupMonthCalendar(
        startMonth: YearMonth,
        endMonth: YearMonth,
        currentMonth: YearMonth,
        daysOfWeek: List<DayOfWeek>,
    ) {
        class DayViewContainer(view: View) : ViewContainer(view) {
            lateinit var day: CalendarDay
            val textView = WeekCalendarItemLayoutBinding.bind(view).weekCalendarDayText

            val calendoModeConstraint =
                WeekCalendarItemLayoutBinding.bind(view).calendarItemCalendoModeConstraint
            val calendoTodoPlanModeConstraint =
                WeekCalendarItemLayoutBinding.bind(view).calendarItemTodoPlanModeConstraint

            val calendoTodoText = WeekCalendarItemLayoutBinding.bind(view).calendarCalendoTodoText
            val calendoPlanText = WeekCalendarItemLayoutBinding.bind(view).calendarCalendoPlanText

            val todoFirstText = WeekCalendarItemLayoutBinding.bind(view).todoPlanModeFirstText
            val todoSecondText = WeekCalendarItemLayoutBinding.bind(view).todoPlanModeSecondText
            val todoThirdText = WeekCalendarItemLayoutBinding.bind(view).todoPlanModeThirdText
            val todoFourthText = WeekCalendarItemLayoutBinding.bind(view).todoPlanModeFourthText

            val calendoPlanModeConstraint =
                WeekCalendarItemLayoutBinding.bind(view).calendarItemPlanModeConstraint

            val planFirstImg = WeekCalendarItemLayoutBinding.bind(view).planModeFirstImg
            val planSecondImg = WeekCalendarItemLayoutBinding.bind(view).planModeSecondImg
            val planThirdImg = WeekCalendarItemLayoutBinding.bind(view).planModeThirdImg
            val planFourthImg = WeekCalendarItemLayoutBinding.bind(view).planModeFourthImg

            init {
                view.setOnClickListener {
                    if (day.position == DayPosition.MonthDate) {
                        dateClicked(date = day.date)
                    }
                }
            }
        }
        binding.monthCalendarView.dayBinder = object : MonthDayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, data: CalendarDay) {
                container.day = data

                bindDate(
                    data.date,
                    container.textView,
                    data.position == DayPosition.MonthDate,
                    container.calendoTodoText,
                    container.calendoPlanText,

                    container.calendoModeConstraint,
                    container.calendoTodoPlanModeConstraint,
                    container.calendoPlanModeConstraint,

                    container.todoFirstText,
                    container.todoSecondText,
                    container.todoThirdText,
                    container.todoFourthText,


                    container.planFirstImg,
                    container.planSecondImg,
                    container.planThirdImg,
                    container.planFourthImg
                )
            }
        }

        binding.monthCalendarView.monthScrollListener = {
            viewModel.currentMonth.value = it.yearMonth
            val selectedDate = it.yearMonth.atDay(1)

            Log.e("monthScroll", isProgrammaticMonthScroll.toString())
            Log.e("monthDate", viewModel.selectedDate.value.toString())

            if (isProgrammaticMonthScroll) {
                isProgrammaticMonthScroll = false
                dateClicked(LocalDate.parse(viewModel.selectedDate.value))
            } else dateClicked(selectedDate)

//            viewModel.selectedDate.value = selectedDate.toString()
//            binding.monthCalendarView.notifyCalendarChanged()

            val (startDate, endDate) = getMonthStartAndEnd(selectedDate.toString())

            if (viewModel.isWeekMode.value == false) viewModel.getGoal(startDate, endDate)
                .invokeOnCompletion {

                    Log.e("mScrollCurrentGoal",viewModel.currentGoal.value?.title.toString())
//                if (viewModel.currentGoal.value == null) {
//
//                    viewModel.currentTodoInGoal.value = emptyList<Todo>()
//                    todoInGoalRecyclerAdapter.submitList(viewModel.currentTodoInGoal.value)
//
//                } else
                    setTodoInGoal()
//                        .invokeOnCompletion {
//
//                        lifecycleScope.launch {
//                            viewModel.currentTodoInGoal.collectLatest {
////                                todoInGoalRecyclerAdapter = TodoListInGoalRecyclerAdapter(
////                                    this@TodoListActivity,
////                                    viewModel.selectedDate.value.toString()
////                                )
////                                binding.todoListWeekGoalRecyclerView.adapter =
////                                    todoInGoalRecyclerAdapter
//
//                                if (it.isNotEmpty()) {
////                                    todoInGoalRecyclerAdapter.submitList(it)
//                                }
//                                else {
//
////                                    viewModel.currentTodoInGoal.value = emptyList<Todo>()
////                                    todoInGoalRecyclerAdapter.submitList(viewModel.currentTodoInGoal.value)
//                                }
//                            }
//                        }
//
//                    }
                }

//            viewModel.currentMonthString.value = monthToString(it.yearMonth)
        }
        binding.monthCalendarView.setup(startMonth, endMonth, daysOfWeek.first())
        isProgrammaticMonthScroll = true
        binding.monthCalendarView.scrollToMonth(currentMonth)
    }

    private fun bindDate(
        date: LocalDate,
        textView: TextView,
        isSelectable: Boolean,
        calendoTodoText: TextView,
        calendoPlanText: TextView,

        calendoModeConstraint: ConstraintLayout,
        calendoTodoPlanModeConstraint: ConstraintLayout,
        calendoPlanModeConstraint: ConstraintLayout,

        todoFirstText: TextView,
        todoSecondText: TextView,
        todoThirdText: TextView,
        todoFourthText: TextView,

        planFirstImg: ImageView,
        planSecondImg: ImageView,
        planThirdImg: ImageView,
        planFourthImg: ImageView,

        ) {
        textView.text = date.dayOfMonth.toString()

        when {
            isSelectable == false -> {
                textView.apply {
                    setTextAppearance(
                        R.style.calendar_out_date_text_style
                    )
                    background = null
                }
            }

            viewModel.selectedDate.value.toString() == date.toString() -> {
                textView.apply {

                    setTextAppearance(
                        R.style.calendar_selected_text_style
                    )
                    background = ContextCompat.getDrawable(
                        this@TodoListActivity,
                        R.drawable.calendar_selected_date_bg
                    )

                }
            }

            date == LocalDate.now() -> {
                textView.apply {
                    setTextAppearance(
                        R.style.calendar_today_text_style
                    )
                    background = null
                }
            }

            else -> {

                textView.apply {
                    setTextAppearance(R.style.calendar_default_text_style)
                    background = null
                }
            }
        }

        viewModel.dateTodoCountMap.value[date.toString()] = mutableMapOf()

        viewModel.categoryTodoMap.value.forEach { categoryWithTodo ->
            val list = categoryWithTodo.todoList.toMutableList()
            viewModel.dateTodoCountMap.value[date.toString()]?.put(
                categoryWithTodo.category,
                filterTodosByDate(list, date)
            )
        }
        when (viewModel.currentMode.value) {
            "CalenDo" -> {
                calendoModeConstraint.visibility = View.VISIBLE
                calendoTodoPlanModeConstraint.visibility = View.GONE
                calendoPlanModeConstraint.visibility = View.GONE

                bindCalendoModeText(
                    date,
                    calendoTodoText,
                    calendoPlanText
                )
            }

            "Todo" -> {
                calendoModeConstraint.visibility = View.INVISIBLE
                calendoTodoPlanModeConstraint.visibility = View.VISIBLE
                calendoPlanModeConstraint.visibility = View.GONE

                bindTodoModeText(date, todoFirstText, todoSecondText, todoThirdText, todoFourthText)

            }

            "Plan" -> {
                calendoModeConstraint.visibility = View.INVISIBLE
                calendoTodoPlanModeConstraint.visibility = View.GONE
                calendoPlanModeConstraint.visibility = View.VISIBLE

                bindPlanModeText(date, planFirstImg, planSecondImg, planThirdImg, planFourthImg)

            }

            else -> {
                calendoModeConstraint.visibility = View.VISIBLE
                calendoTodoPlanModeConstraint.visibility = View.GONE
                calendoPlanModeConstraint.visibility = View.GONE

                bindCalendoModeText(
                    date,
                    calendoTodoText,
                    calendoPlanText

                )
            }
        }
    }

    private fun bindCalendoModeText(
        date: LocalDate,
        calendoTodoText: TextView,
        calendoPlanText: TextView
    ) {

        var todoCount = 0

        lifecycleScope.launch {

            viewModel.dateTodoCountMap.collectLatest {

                it[date.toString()]?.forEach { (_, value) ->
                    todoCount += value.size

                }

                calendoTodoText.apply {
                    if (todoCount == 0) {
                        visibility = View.GONE
                        text = ""
                    } else {
                        visibility = View.VISIBLE
                        text = "할일 $todoCount"
                    }
                }


                viewModel.selectedDatePlanList.collectLatest { planList ->

                    val list = filterTodosByDate(planList, date)
                    viewModel.datePlanMap.value.put(date.toString(), list)

                    calendoPlanText.apply {
                        if (viewModel.datePlanMap.value[date.toString()].isNullOrEmpty() == true) {
                            visibility = View.INVISIBLE
                            text = ""
                        } else {
                            visibility = View.VISIBLE
                            text = "일정 ${viewModel.datePlanMap.value[date.toString()]?.size}"
                        }
                    }

                }
            }
        }

    }

    private fun bindTodoModeText(
        date: LocalDate,
        firstText: TextView,
        secondText: TextView,
        thirdText: TextView,
        fourthText: TextView
    ) {

        lifecycleScope.launch {

            viewModel.dateTodoCountMap.collectLatest {

                firstText.visibility = View.INVISIBLE
                secondText.visibility = View.INVISIBLE
                thirdText.visibility = View.INVISIBLE
                fourthText.visibility = View.INVISIBLE

                it[date.toString()]?.filter { it.value.isNotEmpty() }?.entries?.toList()
                    ?.forEachIndexed { index, (category, list) ->

                        when (index) {
                            0 -> {
                                firstText.apply {
                                    visibility = View.VISIBLE
                                    background.colorFilter = PorterDuffColorFilter(
                                        colorStringToColor(
                                            category.color,
                                            context
                                        ), PorterDuff.Mode.SRC_ATOP
                                    )
                                    background.alpha = 51
                                    setCompoundDrawable(
                                        category.icon,
                                        category.color,
                                        9,
                                        this@TodoListActivity
                                    )
                                    text = list.size.toString()
                                    setTextColor(
                                        colorStringToColor(
                                            category.color,
                                            this@TodoListActivity
                                        )
                                    )

                                }
                            }

                            1 -> {
                                secondText.apply {
                                    visibility = View.VISIBLE
                                    background.colorFilter = PorterDuffColorFilter(
                                        colorStringToColor(
                                            category.color,
                                            context
                                        ), PorterDuff.Mode.SRC_ATOP
                                    )
                                    background.alpha = 51
                                    setCompoundDrawable(
                                        category.icon,
                                        category.color,
                                        9,
                                        this@TodoListActivity
                                    )
                                    text = list.size.toString()
                                    setTextColor(
                                        colorStringToColor(
                                            category.color,
                                            this@TodoListActivity
                                        )
                                    )
                                }

                            }

                            2 -> {
                                thirdText.apply {
                                    visibility = View.VISIBLE
                                    background.colorFilter = PorterDuffColorFilter(
                                        colorStringToColor(
                                            category.color,
                                            context
                                        ), PorterDuff.Mode.SRC_ATOP
                                    )
                                    background.alpha = 51
                                    setCompoundDrawable(
                                        category.icon,
                                        category.color,
                                        9,
                                        this@TodoListActivity
                                    )
                                    text = list.size.toString()
                                    setTextColor(
                                        colorStringToColor(
                                            category.color,
                                            this@TodoListActivity
                                        )
                                    )
                                }
                            }

                            3 -> {
                                fourthText.apply {
                                    visibility = View.VISIBLE
                                    background.colorFilter = PorterDuffColorFilter(
                                        colorStringToColor(
                                            category.color,
                                            context
                                        ), PorterDuff.Mode.SRC_ATOP
                                    )
                                    background.alpha = 51
                                    setCompoundDrawable(
                                        category.icon,
                                        category.color,
                                        9,
                                        this@TodoListActivity
                                    )
                                    text = list.size.toString()
                                    setTextColor(
                                        colorStringToColor(
                                            category.color,
                                            this@TodoListActivity
                                        )
                                    )
                                }

                            }

                        }


                    }
            }
        }

    }

    private fun bindPlanModeText(
        date: LocalDate,
        planFirstImg: ImageView,
        planSecondImg: ImageView,
        planThirdImg: ImageView,
        planFourthImg: ImageView
    ) {

        planFirstImg.visibility = View.INVISIBLE
        planSecondImg.visibility = View.INVISIBLE
        planThirdImg.visibility = View.INVISIBLE
        planFourthImg.visibility = View.INVISIBLE

        lifecycleScope.launch {

            viewModel.selectedDatePlanList.collectLatest { planList ->

                val list = filterTodosByDate(planList, date)
                viewModel.datePlanMap.value.put(date.toString(), list)

                viewModel.datePlanMap.value[date.toString()]?.forEachIndexed { index, todo ->

                    when (index) {
                        0 -> {

                            planFirstImg.apply {

                                visibility = View.VISIBLE
                                background.colorFilter = PorterDuffColorFilter(
                                    colorStringToColor(todo.color!!, context),
                                    PorterDuff.Mode.SRC_ATOP
                                )
                                background.alpha = 51
                                val drawable = ContextCompat.getDrawable(
                                    this@TodoListActivity,
                                    iconToFilledDrawable(todo.icon!!)
                                )
                                drawable?.colorFilter = PorterDuffColorFilter(
                                    colorStringToColor(todo.color!!, context),
                                    PorterDuff.Mode.SRC_ATOP
                                )
                                setImageDrawable(drawable)

                            }
                        }

                        1 -> {
                            planSecondImg.apply {

                                visibility = View.VISIBLE
                                background.colorFilter = PorterDuffColorFilter(
                                    colorStringToColor(todo.color!!, context),
                                    PorterDuff.Mode.SRC_ATOP
                                )
                                background.alpha = 51
//                                setImageResource(iconToFilledDrawable(todo.icon!!))
                                val drawable = ContextCompat.getDrawable(
                                    this@TodoListActivity,
                                    iconToFilledDrawable(todo.icon!!)
                                )
                                drawable?.colorFilter = PorterDuffColorFilter(
                                    colorStringToColor(todo.color!!, context),
                                    PorterDuff.Mode.SRC_ATOP
                                )
                                setImageDrawable(drawable)
                            }

                        }

                        2 -> {
                            planThirdImg.apply {

                                visibility = View.VISIBLE
                                background.colorFilter = PorterDuffColorFilter(
                                    colorStringToColor(todo.color!!, context),
                                    PorterDuff.Mode.SRC_ATOP
                                )
                                background.alpha = 51
//                                setImageResource(iconToFilledDrawable(todo.icon!!))
                                val drawable = ContextCompat.getDrawable(
                                    this@TodoListActivity,
                                    iconToFilledDrawable(todo.icon!!)
                                )
                                drawable?.colorFilter = PorterDuffColorFilter(
                                    colorStringToColor(todo.color!!, context),
                                    PorterDuff.Mode.SRC_ATOP
                                )
                                setImageDrawable(drawable)
                            }

                        }

                        3 -> {
                            planFourthImg.apply {

                                visibility = View.VISIBLE
                                background.colorFilter = PorterDuffColorFilter(
                                    colorStringToColor(todo.color!!, context),
                                    PorterDuff.Mode.SRC_ATOP
                                )
                                background.alpha = 51
//                                setImageResource(iconToFilledDrawable(todo.icon!!))
                                val drawable = ContextCompat.getDrawable(
                                    this@TodoListActivity,
                                    iconToFilledDrawable(todo.icon!!)
                                )
                                drawable?.colorFilter = PorterDuffColorFilter(
                                    colorStringToColor(todo.color!!, context),
                                    PorterDuff.Mode.SRC_ATOP
                                )
                                setImageDrawable(drawable)
                            }

                        }

                        else -> {


                        }


                    }


                }

            }
        }

    }

    // 중복 선택 안되게 수정해야 됨
    private fun todoDailyRepeatDateClicked(date: LocalDate) {

//        selectedDates.clear()

//        selectedDates.add(date)

        todoDailyRptStartDate = date


//        if(!selectedDates.contains(date)) selectedDates.add(date)
//        else selectedDates.remove(date)

    }

    private fun dateClicked(date: LocalDate) {

        val currentSelection = LocalDate.parse(viewModel.selectedDate.value.toString())
        viewModel.selectedDate.value = date.toString()
        viewModel.currentMonth.value = date.yearMonth
        viewModel.currentMonthString.value =
            localDateToString(LocalDate.parse(viewModel.selectedDate.value))

        binding.monthCalendarView.notifyCalendarChanged()
        binding.weekCalendarView.notifyDateChanged(date)

        // 현재 필터링된 카테고리는 유지해야 됨

        when (viewModel.currentMode.value) {
            "CalenDo" -> {
                initCategoryRecyclerView(null)
                initPlanRecyclerView()
                initSelectedCategoryTodoRecyclerView(null,date)
            }

            "Todo" -> {
                initCategoryRecyclerView(null)
                initSelectedCategoryTodoRecyclerView(null, date)
            }

            "Plan" -> {
                initPlanRecyclerView()
            }

            else -> {
                initCategoryRecyclerView(null)
                initPlanRecyclerView()
            }
        }

        if (currentSelection != null) {

            binding.monthCalendarView.notifyDateChanged(currentSelection)
            binding.weekCalendarView.notifyDateChanged(currentSelection)
        }

        binding.titlesContainer.root.children
            .filterIsInstance<LinearLayout>()
            .flatMap { it.children }
            .filterIsInstance<TextView>()
//        binding.titlesContainer.root.children
//            .map { it as TextView }
            .forEachIndexed { index, textView ->

                if (textView.text.toString() == date.dayOfWeek.getDisplayName(
                        TextStyle.SHORT,
                        Locale.getDefault()
                    )
                ) textView.alpha = 1f
                else textView.alpha = 0.4f
//                textView.setTextColor(R.color.white)
            }
    }

    // 월간, 주간 변환될 때 currentGoal 업데이트 해야됨
    private fun toggleCalendarMode(toWeekMode: Boolean) {
        if (toWeekMode) {

            isProgrammaticWeekScroll = true
            binding.weekCalendarView.scrollToWeek(LocalDate.parse(viewModel.selectedDate.value!!))

        } else {

            isProgrammaticMonthScroll = true
            binding.monthCalendarView.scrollToMonth(LocalDate.parse(viewModel.selectedDate.value!!).yearMonth)
        }


        if (toWeekMode) {

            val (startDate, endDate) = getWeekStartEnd(viewModel.selectedDate.value.toString())
            viewModel.getGoal(startDate, endDate).invokeOnCompletion {

                Log.e("tcmCurrentGoal1",viewModel.currentGoal.value?.title.toString())

                setTodoInGoal()
//                    .invokeOnCompletion {
//                    lifecycleScope.launch {
//                        viewModel.currentTodoInGoal.collectLatest {
////                            todoInGoalRecyclerAdapter = TodoListInGoalRecyclerAdapter(
////                                this@TodoListActivity,
////                                viewModel.selectedDate.value.toString()
////                            )
////                            binding.todoListWeekGoalRecyclerView.adapter = todoInGoalRecyclerAdapter
//
////                            todoInGoalRecyclerAdapter.submitList(it)
//
//                        }
//                    }
//                }
            }
        } else {
            val selectedDate = viewModel.currentMonth.value.atDay(1)

            val (startDate, endDate) = getMonthStartAndEnd(selectedDate.toString())
            viewModel.getGoal(startDate, endDate).invokeOnCompletion { throwable ->

                when (throwable) {
                    is CancellationException -> {

                        Log.e("Cancel", "cancel")
                    }

                    else -> {

                        Log.e("tcmCurrentGoal2",viewModel.currentGoal.value?.title.toString())

                        setTodoInGoal()
//                            .invokeOnCompletion {
//                            lifecycleScope.launch {
//                                viewModel.currentTodoInGoal.collectLatest {
////                                    todoInGoalRecyclerAdapter = TodoListInGoalRecyclerAdapter(
////                                        this@TodoListActivity,
////                                        viewModel.selectedDate.value.toString()
////                                    )
////                                    binding.todoListWeekGoalRecyclerView.adapter = todoInGoalRecyclerAdapter
//
////                                    todoInGoalRecyclerAdapter.submitList(it)
//
//                                }
//                            }
//                        }
                    }
                }


            }
        }

        val weekHeight = binding.weekCalendarView.height
        val visibleMonthHeight =
            weekHeight * binding.monthCalendarView.findFirstVisibleMonth()?.weekDays.orEmpty()
                .count()

        val oldHeight = if (toWeekMode) visibleMonthHeight else weekHeight
        val newHeight = if (toWeekMode) weekHeight else visibleMonthHeight

        val animator = ValueAnimator.ofInt(oldHeight, newHeight)
        animator.addUpdateListener { anim ->
            binding.monthCalendarView.updateLayoutParams {
                height = anim.animatedValue as Int
            }

            binding.monthCalendarView.children.forEach { child ->
                child.requestLayout()
            }

            binding.weekCalendarView.updateLayoutParams {
                height = anim.animatedValue as Int
            }

            binding.weekCalendarView.children.forEach { child ->
                child.requestLayout()
            }
        }

        animator.doOnStart {
            if (!toWeekMode) {
                binding.weekCalendarView.visibility = View.GONE
                binding.monthCalendarView.visibility = View.VISIBLE

//                binding.weekCalendarView.isInvisible = true
//                binding.monthCalendarView.isVisible = true

                viewModel.isWeekMode.value = false

//                val selectedDate = binding.monthCalendarView.findFirstVisibleMonth()?.yearMonth?.atDay(1)
//                val (startDate, endDate) = getWeekStartEnd(selectedDate.toString())
//                viewModel.getGoal(startDate, endDate).invokeOnCompletion {
//
//                }

            } else {
                binding.weekCalendarView.visibility = View.VISIBLE
                binding.weekCalendarView.isVisible = true
                binding.weekCalendarView.updateLayoutParams { height = weekHeight }
            }
        }
        animator.doOnEnd {
            if (toWeekMode) {
                binding.weekCalendarView.visibility = View.VISIBLE
                binding.monthCalendarView.visibility = View.GONE
//                binding.weekCalendarView.isVisible = true
//                binding.monthCalendarView.isInvisible = true

                viewModel.isWeekMode.value = true
                binding.weekCalendarView.updateLayoutParams { height = weekHeight }

            } else {
                binding.monthCalendarView.visibility = View.VISIBLE
                binding.monthCalendarView.updateLayoutParams {
                    height = ViewGroup.LayoutParams.WRAP_CONTENT
                }
            }
//            updateTitle()
        }


        animator.duration = 250
        animator.start()

        binding.appBarLayout.setExpanded(true, true)

    }

    private fun initTodoDailyRepeatCalendar(
        calendarView: CalendarView,
        startMonth: YearMonth,
        endMonth: YearMonth,
        currentMonth: YearMonth,
        daysOfWeek: List<DayOfWeek>,
        currentMonthTextView: TextView
    ) {
        class DayViewContainer(view: View) : ViewContainer(view) {
            lateinit var day: CalendarDay
            val textView = TodoRptDayChangeCalendarItemLayoutBinding.bind(view).weekCalendarDayText

            init {
                view.setOnClickListener {
                    if (day.position == DayPosition.MonthDate) {
                        todoDailyRepeatDateClicked(date = day.date)
                        calendarView.notifyCalendarChanged()
//                        calendarView.notifyMonthChanged(day.date.yearMonth)
//                        calendarView.notifyDateChanged(day.date)
                    }
                }
            }
        }
        calendarView.dayBinder = object : MonthDayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, data: CalendarDay) {
                container.day = data
                if (data.position == DayPosition.MonthDate) {
                    container.textView.visibility = View.VISIBLE
                    container.textView.text = data.date.dayOfMonth.toString()
                    when {
                        todoDailyRptStartDate == data.date -> {
                            container.textView.setTextAppearance(R.style.todo_daily_repeat_calendar_selected_text_style)
                            container.textView.background = ContextCompat.getDrawable(
                                this@TodoListActivity,
                                R.drawable.todo_rpt_day_change_calendar_item_pressed_bg
                            )
                        }

                        data.date == LocalDate.now() -> {
                            container.textView.setTextAppearance(R.style.todo_daily_repeat_calendar_default_text_style)
                            container.textView.background = ContextCompat.getDrawable(
                                this@TodoListActivity,
                                R.drawable.todo_rpt_day_change_calendar_item_today_bg
                            )
                        }

                        else -> {
                            container.textView.setTextAppearance(R.style.todo_daily_repeat_calendar_default_text_style)
                            container.textView.background = ContextCompat.getDrawable(
                                this@TodoListActivity,
                                R.drawable.todo_rpt_day_change_calendar_item_default_bg
                            )
                        }
                    }

                    // bindDate(data.date, container.textView, data.position == DayPosition.MonthDate,selectedDates)
                } else container.textView.visibility = View.INVISIBLE
            }
        }

        calendarView.monthScrollListener = {
            it
            currentMonthTextView.text = monthToString(it.yearMonth)
        }

        calendarView.setup(startMonth, endMonth, daysOfWeek.first())
        calendarView.scrollToMonth(currentMonth)
    }

    private fun addTodoInGoal() {
        viewModel.currentGoal.value?.let {

            Log.e("currentGoal",it.goalId)
            Log.e("currentGoal",it.title)

            val startDate = it.startDate
            val endDate = it.endDate
            val goalId = it.goalId

            viewModel.addTodoInGoal(startDate, endDate, goalId).invokeOnCompletion {

                lifecycleScope.launch {

                    viewModel.currentTodoInGoal.collectLatest {
//                        todoInGoalRecyclerAdapter = TodoListInGoalRecyclerAdapter(
//                            this@TodoListActivity,
//                            viewModel.selectedDate.value.toString()
//                        )
//                        binding.todoListWeekGoalRecyclerView.adapter = todoInGoalRecyclerAdapter

//                        todoInGoalRecyclerAdapter.submitList(it)
                    }
                }
            }

        }
    }
}