package it.scoppelletti.firebase.auth.sample

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GetTokenResult

sealed class AuthCommand {

    object LoggedIn : AuthCommand()

    object Finish : AuthCommand()

    data class IdTokenChanged(
            val user: FirebaseUser?
    ) : AuthCommand()

    data class Error(
            val ex: Throwable
    ) : AuthCommand()
}
