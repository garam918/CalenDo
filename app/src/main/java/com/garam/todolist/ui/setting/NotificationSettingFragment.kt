package com.garam.todolist.ui.setting

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import com.garam.todolist.R
import com.garam.todolist.databinding.FragmentNotificationSettingBinding


class NotificationSettingFragment : Fragment() {

    private lateinit var binding : FragmentNotificationSettingBinding
    private val viewModel : SettingViewModel by activityViewModels()

    private lateinit var mContext : Context

    private val notificationSettingLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13 이상에서는 POST_NOTIFICATIONS 권한이 필요
            if(ContextCompat.checkSelfPermission(
                mContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED) {
                // 스위치 on
            }
            else {

                // 스위치 off

            }
        }


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_notification_setting,container,false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.notificationSettingConstraint.setOnClickListener {
            moveToNotificationSetting()

        }

        binding.notificationTodoSettingSwitch.setOnCheckedChangeListener { buttonView, isChecked ->

            if(isChecked) {

                binding.notificationTodoTimeSettingConstraint.visibility = View.VISIBLE

            }
            else {

                binding.notificationTodoTimeSettingConstraint.visibility = View.GONE

            }


        }

        binding.notificationAllDayPlanSettingSwitch.setOnCheckedChangeListener { buttonView, isChecked ->


        }

        binding.notificationPlanSettingSwitch.setOnCheckedChangeListener { buttonView, isChecked ->


        }

    }

    private fun moveToNotificationSetting() {

        val intent = Intent().apply {
            when {
                // Android 13(API 33) 이상에서는 새로운 알림 설정 화면으로 이동
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                    action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                    putExtra(Settings.EXTRA_APP_PACKAGE, mContext.packageName)
                }
                // Android 8.0(API 26) 이상에서는 앱 정보 화면으로 이동
                else -> {
                    action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                    putExtra(Settings.EXTRA_APP_PACKAGE, mContext.packageName)
                }
            }
        }
        notificationSettingLauncher.launch(intent)

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        mContext = context
    }
}