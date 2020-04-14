@file:Suppress("RemoveRedundantQualifierName")

package it.scoppelletti.firebase.auth.sample

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import it.scoppelletti.firebase.auth.sample.databinding.SigninFragmentBinding
import it.scoppelletti.firebase.auth.sample.inject.ServiceLocator
import it.scoppelletti.firebase.auth.sample.inject.serviceLocator
import it.scoppelletti.spaceship.widget.ProgressOverlay
import mu.KotlinLogging

class SignInFragment : Fragment() {

    private lateinit var viewModel: AuthViewModel
    private lateinit var auth: FirebaseAuth
    private lateinit var googleClient: GoogleSignInClient
    private lateinit var callbackManager: CallbackManager
    private lateinit var progressIndicator: ProgressOverlay
    private var binding: SigninFragmentBinding? = null

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = SigninFragmentBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.cmdSignIn?.setOnClickListener {
            binding?.let {
                progressIndicator.show()
                viewModel.signInWithEmailAndPassword(
                        auth,
                        it.txtEmail.text.toString(),
                        it.txtPassword.text.toString())
            }
        }

        binding?.cmdSignUp?.setOnClickListener {
            binding?.let {
                progressIndicator.show()
                viewModel.createUserWithEmailAndPassword(
                        auth,
                        it.txtEmail.text.toString(),
                        it.txtPassword.text.toString())
            }
        }

        binding?.cmdGoogle?.setOnClickListener {
            startActivityForResult(googleClient.signInIntent,
                    SignInFragment.REQ_GOOGLESIGN)
        }

        binding?.cmdFacebook?.setPermissions("email")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        googleClient = serviceLocator().getService(ServiceLocator.GOOGLE_CLIENT)
        callbackManager = serviceLocator().getService(
                ServiceLocator.FACEBOOK_CALLBACKMANAGER)

        progressIndicator = serviceLocator().getService(
                ServiceLocator.PROGRESS_INDICATOR)
        viewModel = ViewModelProvider(requireActivity()).get(
                AuthViewModel::class.java)
        auth = viewModel.auth

        binding?.cmdFacebook?.registerCallback(callbackManager,
                object : FacebookCallback<LoginResult> {
                    override fun onSuccess(result: LoginResult?) {
                        result?.let {
                            progressIndicator.show()
                            viewModel.signInWithFacebookCredential(
                                    auth, it.accessToken)
                        }
                    }

                    override fun onCancel() {
                        logger.info("Facebook login canceled.")
                    }

                    override fun onError(error: FacebookException?) {
                        error?.let {
                            viewModel.error(it)
                        }
                    }
                })
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    override fun onActivityResult(
            requestCode: Int,
            resultCode: Int,
            data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            SignInFragment.REQ_GOOGLESIGN -> {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                progressIndicator.show()
                viewModel.signInWithGoogleCredential(auth, task)
            }
        }
    }

    private companion object {
        const val REQ_GOOGLESIGN = MainApp.REQ_GOOGLESIGNIN
        val logger = KotlinLogging.logger { }
    }
}
