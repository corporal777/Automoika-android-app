package kg.autojuuguch.automoikakg.ui.auth.authorization

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import kg.autojuuguch.automoikakg.R
import kg.autojuuguch.automoikakg.databinding.FragmentAuthorizationBinding
import kg.autojuuguch.automoikakg.ui.base.BaseToolbarFragment
import kg.autojuuguch.automoikakg.ui.views.ToolbarLayoutView
import kg.autojuuguch.automoikakg.utils.FirebaseAuthUtils
import kg.autojuuguch.automoikakg.utils.GoogleAuthUtils
import kg.autojuuguch.automoikakg.utils.LOG_TAG
import org.koin.androidx.viewmodel.ext.android.viewModel


class AuthorizationFragment : BaseToolbarFragment<FragmentAuthorizationBinding>() {

    override val viewModel by viewModel<AuthorizationViewModel>()
    private lateinit var googleAuthUtils: GoogleAuthUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        googleAuthUtils = GoogleAuthUtils(this)
        googleAuthUtils.setSignedCallback {
            viewModel.initGoogleAccount(it)
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            btnLogin.setOnClickListener { showLoginFragment() }
            btnRegister.setOnClickListener { showRegisterFragment() }
            btnGoogle.setOnClickListener { googleAuthUtils.signInWithGoogle() }
        }
        observeLoading()
    }

    private fun observeLoading() {
        viewModel.buttonLoading.observe { mBinding.btnGoogle.showProgressLoading(it) }
        viewModel.googleAccount.observe {
            if (it) showWelcomeFragment()
            else showErrorMessage(R.string.google_account_not_found)
        }
    }


    private fun showLoginFragment() {
        findNavController().navigate(R.id.login_fragment)
    }

    private fun showRegisterFragment() {
        findNavController().navigate(R.id.user_register_fragment)
    }

    private fun showWelcomeFragment() {
        findNavController().navigate(R.id.welcome_fragment)
    }

    override fun onDestroy() {
        googleAuthUtils.unregister()
        super.onDestroy()
    }

    override val title: CharSequence by lazy { getString(R.string.auth_text) }
    override fun animationType(): AnimType = AnimType.FADE
    override fun binding() = FragmentAuthorizationBinding::class.java
    override fun layout(): Int = R.layout.fragment_authorization
}