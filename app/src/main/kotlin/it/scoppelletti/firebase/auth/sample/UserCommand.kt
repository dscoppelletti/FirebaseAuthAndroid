package it.scoppelletti.firebase.auth.sample

import com.google.firebase.auth.GetTokenResult

sealed class UserCommand {

    data class TokenGotten(
            val tokenResult: GetTokenResult
    ) : UserCommand()

    data class Error(
            val ex: Throwable
    ) : UserCommand()
}
