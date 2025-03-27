package kg.autojuuguch.automoikakg.ui.dialogs

import android.content.Context
import android.view.LayoutInflater
import android.widget.EditText
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import kg.autojuuguch.automoikakg.R
import kg.autojuuguch.automoikakg.databinding.BottomSheetDialogAuthBinding
import kg.autojuuguch.automoikakg.extensions.findItem
import kg.autojuuguch.automoikakg.extensions.showKeyboard

class AuthBottomSheet(val requireContext: Context, val type: AuthType) :
    BottomSheetDialog(requireContext) {

    private val mBinding = BottomSheetDialogAuthBinding.inflate(LayoutInflater.from(context))
    private var onLogin: () -> Unit = {}
    private var onRegister: () -> Unit = {}

    init {
        setContentView(mBinding.root)
        behavior.skipCollapsed = true
        behavior.state = BottomSheetBehavior.STATE_EXPANDED


        mBinding.apply {
            tvBottomSheetLabel.text =
                if (type == AuthType.LOGIN) context.getString(R.string.login)
                else context.getString(R.string.register)

            btnClose.setOnClickListener { dismiss() }
            btnLogin.apply {
                text = if (type == AuthType.LOGIN) context.getString(R.string.login_phone)
                else context.getString(R.string.register_user)
                setOnClickListener {
                    onLogin.invoke()
                    dismiss()
                }
            }
            btnRegister.apply {
                text = if (type == AuthType.LOGIN) context.getString(R.string.login_google)
                else context.getString(R.string.register_car_wash)
                setOnClickListener {
                    onRegister.invoke()
                    dismiss()
                }
            }
        }
    }


    fun setLoginCallback(block: () -> Unit): AuthBottomSheet {
        onLogin = block
        return this
    }

    fun setRegisterCallback(block: () -> Unit): AuthBottomSheet {
        onRegister = block
        return this
    }
}

enum class AuthType {
    LOGIN, REGISTER
}