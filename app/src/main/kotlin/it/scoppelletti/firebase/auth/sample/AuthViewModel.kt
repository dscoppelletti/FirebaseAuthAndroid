package it.scoppelletti.firebase.auth.sample

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.facebook.AccessToken
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GetTokenResult
import com.google.firebase.auth.GoogleAuthProvider
import it.scoppelletti.spaceship.gms.coroutines.suspendTask
import it.scoppelletti.spaceship.lifecycle.SingleEvent
import it.scoppelletti.spaceship.types.joinLines
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import mu.KotlinLogging

class AuthViewModel : ViewModel() {

    private val _cmd = MutableLiveData<SingleEvent<AuthCommand>>()

    val auth = FirebaseAuth.getInstance()
    val command: LiveData<SingleEvent<AuthCommand>> = _cmd

    private val idTokenListener = FirebaseAuth.IdTokenListener { auth ->
        _cmd.value = SingleEvent(AuthCommand.IdTokenChanged(
                auth.currentUser))
    }

    init {
        auth.addIdTokenListener(idTokenListener)
    }

    fun createUserWithEmailAndPassword(
            auth: FirebaseAuth,
            email: String,
            password: String
    ) = viewModelScope.launch {
        try {
            suspendTask(auth::createUserWithEmailAndPassword, email,
                password)
        } catch (ex: CancellationException) {
            throw ex
        } catch (ex: Exception) {
            _cmd.value = SingleEvent(AuthCommand.Error(ex))
            return@launch
        }

        _cmd.value = SingleEvent(AuthCommand.LoggedIn)
    }

    fun signInWithEmailAndPassword(
            auth: FirebaseAuth,
            email: String,
            password: String
    ) = viewModelScope.launch {
        try {
            suspendTask(auth::signInWithEmailAndPassword, email,
                    password)
        } catch (ex: CancellationException) {
            throw ex
        } catch (ex: Exception) {
            _cmd.value = SingleEvent(AuthCommand.Error(ex))
            return@launch
        }

        _cmd.value = SingleEvent(AuthCommand.LoggedIn)
    }

    fun signInWithGoogleCredential(
            auth: FirebaseAuth,
            task: Task<GoogleSignInAccount>
    ) = viewModelScope.launch {
        try {
            val account = checkNotNull(task.getResult(
                    ApiException::class.java)) {
                "Missing account"
            }

            val credential = GoogleAuthProvider.getCredential(account.idToken,
                    null)
            suspendTask(auth::signInWithCredential, credential)
        } catch (ex: CancellationException) {
            throw ex
        } catch (ex: Exception) {
            _cmd.value = SingleEvent(AuthCommand.Error(ex))
            return@launch
        }

        _cmd.value = SingleEvent(AuthCommand.LoggedIn)
    }

    fun signInWithFacebookCredential(
            auth: FirebaseAuth,
            token: AccessToken
    ) = viewModelScope.launch {
        try {
            val credential = FacebookAuthProvider.getCredential(token.token)

            suspendTask(auth::signInWithCredential, credential)
        } catch (ex: CancellationException) {
            throw ex
        } catch (ex: Exception) {
            _cmd.value = SingleEvent(AuthCommand.Error(ex))
            return@launch
        }

        _cmd.value = SingleEvent(AuthCommand.LoggedIn)
    }

    fun signOut(
            auth: FirebaseAuth,
            googleClient: GoogleSignInClient
    ) = viewModelScope.launch {
        try {
            auth.signOut()
        } catch (ex: CancellationException) {
            throw ex
        } catch (ex: Exception) {
            logger.error("Firebase Sign-Out failed.", ex)
        }

        try {
            suspendTask(googleClient::signOut)
        } catch (ex: CancellationException) {
            throw ex
        } catch (ex: Exception) {
            logger.error("Google Sign-Out failed.", ex)
        }

        try {
            LoginManager.getInstance().logOut()
        } catch (ex: CancellationException) {
            throw ex
        } catch (ex: Exception) {
            logger.error("Facebook Sign-Out failed.", ex)
        }

        _cmd.value = SingleEvent(AuthCommand.Finish)
    }

    fun getIdToken(user: FirebaseUser?) = viewModelScope.launch {
        val token: GetTokenResult

        try {
            if (user == null) {
                logger.info("User signed-out.")
            } else {
                token = suspendTask(user::getIdToken, false)
                logger.info { """userId=${user.uid};
                    |signedInProvider=${token.signInProvider};
                    |token=${token.token}""".trimMargin().joinLines() }
            }
        } catch (ex: CancellationException) {
            throw ex
        } catch (ex: Exception) {
            _cmd.value = SingleEvent(AuthCommand.Error(ex))
            return@launch
        }
    }

    fun error(ex: Exception) {
        _cmd.value = SingleEvent(AuthCommand.Error(ex))
    }

    override fun onCleared() {
        auth.removeIdTokenListener(idTokenListener)
        super.onCleared()
    }

    private companion object {
        val logger = KotlinLogging.logger { }
    }
}
