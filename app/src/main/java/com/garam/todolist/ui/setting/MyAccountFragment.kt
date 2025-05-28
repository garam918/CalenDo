package com.garam.todolist.ui.setting

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import com.garam.todolist.R
import com.garam.todolist.databinding.FragmentMyAccountBinding
import com.garam.todolist.databinding.AccountDeleteDialogLayoutBinding
import com.garam.todolist.ui.onboarding.OnboardingActivity

class MyAccountFragment : Fragment() {

    private lateinit var binding : FragmentMyAccountBinding
    private val viewModel : SettingViewModel by activityViewModels()
    private lateinit var mContext : Context


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_account,container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.accountLogoutConstraint.setOnClickListener {

            logout()

        }

        binding.accountDeleteBtn.setOnClickListener {

            accountDeleteDialog()

        }


    }

    private fun logout() {

        viewModel.logoutAccount(viewModel.userInfo.value?.uid.toString()).invokeOnCompletion {

            nextActivity()

        }

    }

    private fun accountDeleteDialog() {
        val accountDeleteDialogView = AccountDeleteDialogLayoutBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(mContext, R.style.DialogTransparentTheme).setView(accountDeleteDialogView.root).create()


        accountDeleteDialogView.accountKeepUseBtn.setOnClickListener {

            dialog.dismiss()

        }

        accountDeleteDialogView.accountDeleteBtn.setOnClickListener {

            deleteAccount(dialog)

        }


        dialog.show()
    }

    private fun deleteAccount(dialog: AlertDialog) {

        viewModel.deleteAccount(viewModel.userInfo.value?.uid.toString()).invokeOnCompletion {
            dialog.dismiss()

            nextActivity()
        }

    }

    private fun nextActivity() {

        val prefs: SharedPreferences = mContext.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)

        prefs.edit().putBoolean("isFirstRun", true).apply()

        val intent = Intent(mContext, OnboardingActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)

    }


    override fun onAttach(context: Context) {
        super.onAttach(context)

        mContext = context
    }
}