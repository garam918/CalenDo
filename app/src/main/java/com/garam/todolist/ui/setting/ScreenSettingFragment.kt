package com.garam.todolist.ui.setting

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.garam.todolist.R
import com.garam.todolist.databinding.AppRestartDialogLayoutBinding
import com.garam.todolist.databinding.FragmentScreenSettingBinding
import com.garam.todolist.databinding.SettingScreenCustomDialogLayoutBinding
import com.garam.todolist.util.AppRestarter
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.observeOn
import kotlinx.coroutines.launch

class ScreenSettingFragment : Fragment() {

    private lateinit var binding : FragmentScreenSettingBinding
    private val viewModel : SettingViewModel by activityViewModels()
    private lateinit var mContext : Context

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_screen_setting, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.startScreenSettingConstraint.setOnClickListener {

            screenSettingDialog("StartScreen")

        }

        binding.todoSortSettingConstraint.setOnClickListener {

            screenSettingDialog("TodoSort")

        }


        binding.daysOfWeekStartSettingSwitch.isChecked = viewModel.firstDayOfWeekFlow.value == "Sun"

        binding.daysOfWeekStartSettingSwitch.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {

                if(p1) viewModel.saveFirstDayOfWeek("Sun")
                else viewModel.saveFirstDayOfWeek("Mon")

                appRestartDialog()
            }
        })

    }

    private fun screenSettingDialog(type: String) {

        val bottomSheetDialog = BottomSheetDialog(mContext, R.style.BottomSheetTheme)
        val screenSettingCustomDialogBinding = SettingScreenCustomDialogLayoutBinding.inflate(layoutInflater)

        bottomSheetDialog.setContentView(screenSettingCustomDialogBinding.root)

        bottomSheetDialog.behavior.isDraggable = false

        screenSettingCustomDialogBinding.dialogCloseBtn.setOnClickListener {

            bottomSheetDialog.dismiss()

        }

        if(type == "StartScreen") {

            screenSettingCustomDialogBinding.settingScreenCustomTitleText.text = "시작 화면 설정"
            screenSettingCustomDialogBinding.settingScreenCustomFirstMenuText.text = "캘린두"
            screenSettingCustomDialogBinding.settingScreenCustomSecondMenuText.text = "할일"
            screenSettingCustomDialogBinding.settingScreenCustomThirdMenuText.text = "일정"

            when(viewModel.startModeFlow.value) {
                "CalenDo" -> screenSettingCustomDialogBinding.settingScreenCustomFirstMenuText.apply {

                    background = ContextCompat.getDrawable(mContext,R.drawable.setting_screen_custom_menu_selected_bg)
                    setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.todo_category_change_check_icon,0)

                }
                "Todo" -> screenSettingCustomDialogBinding.settingScreenCustomSecondMenuText.apply {

                    background = ContextCompat.getDrawable(mContext,R.drawable.setting_screen_custom_menu_selected_bg)
                    setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.todo_category_change_check_icon,0)

                }
                "Plan" -> screenSettingCustomDialogBinding.settingScreenCustomThirdMenuText.apply {

                    background = ContextCompat.getDrawable(mContext,R.drawable.setting_screen_custom_menu_selected_bg)
                    setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.todo_category_change_check_icon,0)

                }
                else -> screenSettingCustomDialogBinding.settingScreenCustomFirstMenuText.apply {

                    background = ContextCompat.getDrawable(mContext,R.drawable.setting_screen_custom_menu_selected_bg)
                    setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.todo_category_change_check_icon,0)

                }
            }
        }

        else {

            screenSettingCustomDialogBinding.settingScreenCustomTitleText.text = "할일 정렬"
            screenSettingCustomDialogBinding.settingScreenCustomFirstMenuText.text = "작성한 순"
            screenSettingCustomDialogBinding.settingScreenCustomSecondMenuText.text = "완료한 일이 위"
            screenSettingCustomDialogBinding.settingScreenCustomThirdMenuText.text = "완료한 일이 아래"

            when(viewModel.sortModeFlow.value) {
                "Saved" -> screenSettingCustomDialogBinding.settingScreenCustomFirstMenuText.apply {

                    background = ContextCompat.getDrawable(mContext,R.drawable.setting_screen_custom_menu_selected_bg)
                    setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.todo_category_change_check_icon,0)

                }
                "Completed" -> screenSettingCustomDialogBinding.settingScreenCustomSecondMenuText.apply {

                    background = ContextCompat.getDrawable(mContext,R.drawable.setting_screen_custom_menu_selected_bg)
                    setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.todo_category_change_check_icon,0)

                }
                "Completed_Reversed" -> screenSettingCustomDialogBinding.settingScreenCustomThirdMenuText.apply {

                    background = ContextCompat.getDrawable(mContext,R.drawable.setting_screen_custom_menu_selected_bg)
                    setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.todo_category_change_check_icon,0)

                }
                else -> screenSettingCustomDialogBinding.settingScreenCustomFirstMenuText.apply {

                    background = ContextCompat.getDrawable(mContext,R.drawable.setting_screen_custom_menu_selected_bg)
                    setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.todo_category_change_check_icon,0)

                }
            }

        }

        screenSettingCustomDialogBinding.settingScreenCustomFirstMenuText.setOnClickListener {

            if(type == "StartScreen") {

                viewModel.saveStartMode("CalenDo").invokeOnCompletion {

                    bottomSheetDialog.dismiss()
                    appRestartDialog()

                }

            }
            else {

                viewModel.saveSortMode("Saved").invokeOnCompletion {

                    bottomSheetDialog.dismiss()
                    appRestartDialog()

                }

            }

        }

        screenSettingCustomDialogBinding.settingScreenCustomSecondMenuText.setOnClickListener {

            if(type == "StartScreen") {

                viewModel.saveStartMode("Todo").invokeOnCompletion {

                    bottomSheetDialog.dismiss()
                    appRestartDialog()

                }

            }
            else {

                viewModel.saveSortMode("Completed").invokeOnCompletion {

                    bottomSheetDialog.dismiss()
                    appRestartDialog()

                }

            }

        }

        screenSettingCustomDialogBinding.settingScreenCustomThirdMenuText.setOnClickListener {

            if(type == "StartScreen") {

                viewModel.saveStartMode("Plan").invokeOnCompletion {

                    bottomSheetDialog.dismiss()
                    appRestartDialog()

                }

            }
            else {

                viewModel.saveSortMode("Completed_Reversed").invokeOnCompletion {

                    bottomSheetDialog.dismiss()
                    appRestartDialog()

                }

            }

        }



        bottomSheetDialog.show()
    }

    private fun appRestartDialog() {
        val dialogView = AppRestartDialogLayoutBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(mContext,R.style.DialogTransparentTheme).setView(dialogView.root).create()

        dialogView.appRestartDialogLaterBtn.setOnClickListener {

            dialog.dismiss()

        }

        dialogView.appRestartBtn.setOnClickListener {

            AppRestarter.restartApp(mContext)

        }


        dialog.show()

    }


    override fun onAttach(context: Context) {
        super.onAttach(context)

        mContext = context
    }

}