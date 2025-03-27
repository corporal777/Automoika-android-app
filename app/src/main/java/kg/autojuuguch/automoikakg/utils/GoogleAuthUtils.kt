package kg.autojuuguch.automoikakg.utils

import android.app.Activity.RESULT_OK
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import kg.autojuuguch.automoikakg.R
import java.util.concurrent.TimeUnit

class GoogleAuthUtils (val fragment: Fragment) {


    private val accountSubject: BehaviorSubject<GoogleSignInAccount> = BehaviorSubject.create()
    private var onRequest : (account : GoogleSignInAccount?) -> Unit = {}

    private val launcher =
        fragment.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val account: GoogleSignInAccount = task.getResult(ApiException::class.java)
                    onRequest.invoke(account)
                } catch (e: ApiException) {
                    onRequest.invoke(null)
                }
            }
        }


    fun signInWithGoogle() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(fragment.getString(R.string.google_auth_web_clien_id))
            .requestEmail()
            .build()

        val mGoogleSignInClient = GoogleSignIn.getClient(fragment.requireActivity(), gso)

        val signInIntent = mGoogleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    fun setSignedCallback(block : (value : GoogleSignInAccount?) -> Unit){
        onRequest = block
    }

    fun unregister(){
        launcher.unregister()
    }
}