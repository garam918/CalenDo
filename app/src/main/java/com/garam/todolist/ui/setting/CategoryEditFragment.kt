package com.garam.todolist.ui.setting

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.garam.todolist.R
import com.garam.todolist.data.Category
import com.garam.todolist.data.CategoryIconType
import com.garam.todolist.databinding.FragmentCategoryEditBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.garam.todolist.databinding.CategoryAddBottomSheetLayoutBinding
import com.garam.todolist.databinding.CategoryDeleteDialogLayoutBinding
import com.garam.todolist.ui.todoList.CategoryIconRecyclerAdapter
import com.garam.todolist.util.clickListener.CategoryIconClickListener
import com.garam.todolist.util.clickListener.SettingCategoryEditClickListener
import com.garam.todolist.util.functions.bottomSheetBehaviorSetting
import com.garam.todolist.util.functions.colorStringToColor
import com.garam.todolist.util.functions.iconToDrawable
import com.garam.todolist.util.functions.setupHideKeyboardOnTouchOutside
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CategoryEditFragment : Fragment() {

    private lateinit var mContext : Context
    private lateinit var binding: FragmentCategoryEditBinding
    private val viewModel : SettingViewModel by activityViewModels()
    var selectedImageView: ImageView? = null

    private lateinit var adapter : SettingCategoryEditRecyclerAdapter

    private val itemTouchHelperCallback = object : ItemTouchHelper.Callback() {
        override fun getMovementFlags(rv: RecyclerView, vh: RecyclerView.ViewHolder): Int {
            val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
            return makeMovementFlags(dragFlags, 0)
        }

        override fun onMove(rv: RecyclerView, vh: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
            val from = vh.absoluteAdapterPosition
            val to = target.absoluteAdapterPosition
            adapter.onMoveItem(from, to)
            return true
        }

        override fun onSwiped(vh: RecyclerView.ViewHolder, direction: Int) { }

        override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
            super.clearView(recyclerView, viewHolder)

            // ✨ 드래그가 완료된 시점
            val current = adapter.currentList

            current.forEachIndexed { index, category ->
                val category = category.copy(index = index)

                viewModel.updateCategory(category)
            }

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_category_edit, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getCategoryList()

        binding.settingCategoryAddBtn.setOnClickListener {

            showCategoryAddDialog(null)

        }






    }

    private fun getCategoryList() {

        adapter = SettingCategoryEditRecyclerAdapter(
            { from, to ->
                val currentList = adapter.currentList.toMutableList()
                val item = currentList.removeAt(from)
                currentList.add(to, item)
                adapter.submitList(currentList)
            },

            object : SettingCategoryEditClickListener {
            override fun categoryEditBtnClick(category: Category) {

                showCategoryAddDialog(category)

            }
        })

        binding.settingCategoryRecycler.adapter = adapter

        val touchHelper = ItemTouchHelper(itemTouchHelperCallback)
        touchHelper.attachToRecyclerView(binding.settingCategoryRecycler)

        lifecycleScope.launch {

            viewModel.categoryList.collectLatest {
                adapter.submitList(it.sortedBy {it.index})
            }
        }

    }

    private fun showCategoryAddDialog(category: Category?) {

        val bottomSheetDialog = BottomSheetDialog(mContext, R.style.BottomSheetTheme)
        val categoryAddDialogView = CategoryAddBottomSheetLayoutBinding.inflate(layoutInflater)

        var categoryIconConstraintExpanded = false
        var categoryColorConstraintExpanded = false

        categoryAddDialogView.root.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                if (categoryAddDialogView.categoryAddTitleEditText.isFocused) {
                    val imm = mContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(categoryAddDialogView.root.windowToken, 0)
                    categoryAddDialogView.categoryAddTitleEditText.clearFocus()
                    return@setOnTouchListener true // 이벤트 소비
                }
            }
            return@setOnTouchListener false // 이벤트 전달
        }

        var categoryColor = "default_color_1"
        var categoryTitle = ""
        var editCategoryIconType = CategoryIconType.HOME


        category?.let {

            categoryAddDialogView.categoryAddTitleText.text = "카테고리 수정"
            categoryTitle = category.title
            categoryColor = category.color

            categoryAddDialogView.categoryAddTitleEditText.setText(categoryTitle)

            editCategoryIconType = category.icon

            categoryAddDialogView.categoryAddIconSettingImg.apply {
                background.colorFilter = PorterDuffColorFilter(
                    colorStringToColor(
                        categoryColor,
                        mContext
                    ), PorterDuff.Mode.SRC_ATOP
                )
                background.alpha = 51

                val imageDrawable = ContextCompat.getDrawable(
                    mContext,
                    iconToDrawable(category.icon)
                )
                imageDrawable?.colorFilter = PorterDuffColorFilter(
                    colorStringToColor(
                        categoryColor,
                        mContext
                    ), PorterDuff.Mode.SRC_ATOP
                )
                setImageDrawable(imageDrawable)
            }
            updateSelection(when(categoryColor) {
                "default_color_1" -> categoryAddDialogView.categoryColorSettingLayout.categoryColorImg1
                "default_color_2" -> categoryAddDialogView.categoryColorSettingLayout.categoryColorImg2
                "default_color_3" -> categoryAddDialogView.categoryColorSettingLayout.categoryColorImg3
                "default_color_4" -> categoryAddDialogView.categoryColorSettingLayout.categoryColorImg4
                "default_color_5" -> categoryAddDialogView.categoryColorSettingLayout.categoryColorImg5
                "default_color_6" -> categoryAddDialogView.categoryColorSettingLayout.categoryColorImg6
                "default_color_7" -> categoryAddDialogView.categoryColorSettingLayout.categoryColorImg7
                "default_color_8" -> categoryAddDialogView.categoryColorSettingLayout.categoryColorImg8
                "default_color_9" -> categoryAddDialogView.categoryColorSettingLayout.categoryColorImg9
                "default_color_10" -> categoryAddDialogView.categoryColorSettingLayout.categoryColorImg10
                "default_color_11" -> categoryAddDialogView.categoryColorSettingLayout.categoryColorImg11
                "default_color_12" -> categoryAddDialogView.categoryColorSettingLayout.categoryColorImg12
                else -> categoryAddDialogView.categoryColorSettingLayout.categoryColorImg1

            }, categoryAddDialogView.categoryAddColorSettingImg)


            categoryAddDialogView.categoryDeleteBtn.visibility = View.VISIBLE
        }


        bottomSheetDialog.setOnShowListener {
            val bottomSheet =
                bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)

            bottomSheetBehaviorSetting(bottomSheet)
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
                            mContext
                        ), PorterDuff.Mode.SRC_ATOP
                    )
                    background.alpha = 51

                    val imageDrawable = this.drawable
                    imageDrawable.colorFilter = PorterDuffColorFilter(
                        colorStringToColor(
                            categoryColor,
                            mContext
                        ), PorterDuff.Mode.SRC_ATOP
                    )
                    setImageDrawable(imageDrawable)
                }
            }
        }

        bottomSheetDialog.setContentView(categoryAddDialogView.root)

        bottomSheetDialog.behavior.isDraggable = false

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

            if(category == null) viewModel.addCategory(title = categoryTitle, editCategoryIconType, categoryColor)
                .invokeOnCompletion { throwable ->

                    when (throwable) {
                        is CancellationException -> {

                        }

                        else -> bottomSheetDialog.dismiss()

                    }

                }
            else {
                val editCategory = category.copy(title = categoryTitle, icon = editCategoryIconType, color = categoryColor)

                viewModel.updateCategory(editCategory).invokeOnCompletion { throwable ->

                    when (throwable) {
                        is CancellationException -> {

                        }

                        else -> bottomSheetDialog.dismiss()

                    }

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
                            mContext
                        ), PorterDuff.Mode.SRC_ATOP
                    )
                    background.alpha = 51

                    val imageDrawable = ContextCompat.getDrawable(
                        mContext,
                        iconToDrawable(categoryIconType)
                    )
                    imageDrawable!!.colorFilter = PorterDuffColorFilter(
                        colorStringToColor(
                            categoryColor,
                            mContext
                        ), PorterDuff.Mode.SRC_ATOP
                    )
                    setImageDrawable(imageDrawable)
                }
            }
        })
        categoryAddDialogView.categoryIconRecycler.layoutManager = GridLayoutManager(mContext, 6)
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

        categoryAddDialogView.categoryDeleteBtn.setOnClickListener {
            categoryDeleteConfirmDialog(category!!.categoryId, bottomSheetDialog)

        }

        bottomSheetDialog.show()

    }

    private fun updateSelection(selected: ImageView, targetImageView: ImageView) {

        selectedImageView = selected  // 현재 선택된 ImageView 업데이트

        targetImageView.setImageDrawable(selectedImageView?.drawable)

    }

    private fun categoryDeleteConfirmDialog(categoryId : String, bottomSheetDialog: BottomSheetDialog) {

        val dialogView = CategoryDeleteDialogLayoutBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(mContext,R.style.DialogTransparentTheme).setView(dialogView.root).create()

        dialogView.categoryDeleteCancelBtn.setOnClickListener {
            dialog.dismiss()

        }

        dialogView.categoryDeleteBtn.setOnClickListener {
            viewModel.deleteCategory(categoryId).invokeOnCompletion { throwable ->

                bottomSheetDialog.dismiss()
                dialog.dismiss()

            }


        }


        dialog.show()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        mContext = context
    }
}