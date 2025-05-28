package com.garam.todolist.ui.setting

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.garam.todolist.BuildConfig
import com.garam.todolist.R
import com.garam.todolist.data.UserData
import com.garam.todolist.databinding.FragmentSettingMainBinding
import com.garam.todolist.databinding.OnboardingLoginBottomSheetDialogLayoutBinding
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch

class SettingMainFragment : Fragment() {

    private lateinit var binding : FragmentSettingMainBinding
    private val viewModel : SettingViewModel by activityViewModels()
    private lateinit var mContext : Context

    private lateinit var auth: FirebaseAuth

    private lateinit var bottomSheetDialog : BottomSheetDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_setting_main, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        auth = FirebaseAuth.getInstance()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.settingAppVersionText.text = "ver ${BuildConfig.VERSION_NAME}"

        binding.settingFeedbackBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(mContext.getString(R.string.calendo_feedback_link_string)))
            mContext.startActivity(intent)

        }

        binding.settingAccountConstraint.setOnClickListener {

            // 로그인 안한 사용자는 dialog

            if(viewModel.userInfo.value == null || viewModel.userInfo.value?.loginType == "Anonymous") loginBottomSheetDialog()
            else findNavController().navigate(R.id.action_settingMainFragment_to_accountSettingFragment)

        }

        binding.settingScreenConstraint.setOnClickListener {

            findNavController().navigate(R.id.action_settingMainFragment_to_screenSettingFragment)

        }

        binding.settingCategoryEditConstraint.setOnClickListener {

            findNavController().navigate(R.id.action_settingMainFragment_to_categoryEditFragment)


        }

        binding.settingTermsOfUseConstraint.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(mContext.getString(R.string.terms_of_use_string)))
            mContext.startActivity(intent)
        }

        binding.settingPrivacyConstraint.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(mContext.getString(R.string.privacy_policy_string)))
            mContext.startActivity(intent)
        }



    }

    private fun loginBottomSheetDialog() {

        bottomSheetDialog = BottomSheetDialog(mContext, R.style.BottomSheetTheme)
        val loginDialogView = OnboardingLoginBottomSheetDialogLayoutBinding.inflate(layoutInflater)

        bottomSheetDialog.setContentView(loginDialogView.root)

        loginDialogView.anonymousLoginBtn.visibility = View.GONE

        loginDialogView.googleLoginBtn.setOnClickListener {

            val credentialManager = CredentialManager.create(mContext)

            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(mContext.getString(R.string.google_web_client_id))
                .build()
            val credentialRequest = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            lifecycleScope.launch {
                try {
                    val googleSignInRequest = credentialManager.getCredential(
                        request = credentialRequest,
                        context = mContext
                    )
                    val credential = googleSignInRequest.credential
                    if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                        val googleIdTokenCredential =
                            GoogleIdTokenCredential.createFrom(credential.data)

                        firebaseAuthWithGoogle(googleIdTokenCredential.idToken, bottomSheetDialog)
                    }
                }
                catch (e: Exception) {

                    Log.e("googleLogin", "${e.message}")

                }
            }


        }


        bottomSheetDialog.show()

    }

    private fun firebaseAuthWithGoogle(idToken: String, bottomSheetDialog: BottomSheetDialog) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {

                    val user = auth.currentUser!!

                    lifecycleScope.launch {

                        viewModel.isExistAccount().invokeOnCompletion {

                            if (viewModel.isExistAccount.value) {

                                lifecycleScope.launch {
                                    viewModel.getUserInfoData()

                                    viewModel.saveTodoList().invokeOnCompletion {
//                                        launchTodoActivity()

                                        bottomSheetDialog.dismiss()
                                    }
                                }

                            } else {
                                val userData = UserData(
                                    uid = user.uid.toString(),
                                    email = user.email.toString(),
                                    loginType = "Google"
                                )

                                lifecycleScope.launch {
                                    viewModel.getUserInfoData()

                                    viewModel.setUserData(userData).invokeOnCompletion {
//                                        launchTodoActivity()

                                        bottomSheetDialog.dismiss()
                                    }
                                }
                            }

                        }
                    }


                } else {


                }
            }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        mContext = context
    }

}