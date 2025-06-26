package com.garam.todolist.ui.setting

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.garam.todolist.R
import com.garam.todolist.databinding.FragmentMyAccountBinding
import com.garam.todolist.databinding.AccountDeleteDialogLayoutBinding
import com.garam.todolist.databinding.AccountLogoutDialogLayoutBinding
import com.garam.todolist.ui.onboarding.OnboardingActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch

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

            accountLogoutDialog()

        }

        binding.accountDeleteBtn.setOnClickListener {

            accountDeleteDialog()

        }


    }

    private fun logout(dialog: AlertDialog) {

        viewModel.logoutAccount(viewModel.userInfo.value?.uid.toString()).invokeOnCompletion {

            dialog.dismiss()
            nextActivity()

        }

    }

    private fun accountLogoutDialog() {
        val accountLogoutDialogView = AccountLogoutDialogLayoutBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(mContext, R.style.DialogTransparentTheme).setView(accountLogoutDialogView.root).create()


        accountLogoutDialogView.accountLogoutCancelBtn.setOnClickListener {

            dialog.dismiss()

        }

        accountLogoutDialogView.accountLogoutBtn.setOnClickListener {

            logout(dialog)

        }


        dialog.show()

    }

    private fun accountDeleteDialog() {
        val accountDeleteDialogView = AccountDeleteDialogLayoutBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(mContext, R.style.DialogTransparentTheme).setView(accountDeleteDialogView.root).create()


        accountDeleteDialogView.accountKeepUseBtn.setOnClickListener {

            dialog.dismiss()

        }

        accountDeleteDialogView.accountDeleteBtn.setOnClickListener {

            val auth = FirebaseAuth.getInstance()
            val user = auth.currentUser

            val credentialManager = CredentialManager.create(mContext)

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
                    context = mContext
                )
                val credential = googleSignInRequest.credential
                if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val googleIdTokenCredential =
                        GoogleIdTokenCredential.createFrom(credential.data)

                    val idToken = googleIdTokenCredential.idToken
                    val credential = GoogleAuthProvider.getCredential(idToken, null)
                    user?.reauthenticate(credential)?.addOnSuccessListener {

                        deleteAccount(dialog,auth.currentUser!!)


                    }
                }
            }




        }


        dialog.show()
    }

    private fun deleteAccount(dialog: AlertDialog, user: FirebaseUser) {

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