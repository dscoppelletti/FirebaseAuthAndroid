package it.scoppelletti.firebase.auth.sample.inject

import androidx.fragment.app.Fragment

interface ServiceLocator {

    fun <T> getService(key: String): T

    companion object {
        const val PROGRESS_INDICATOR = "app.klosed.ui.1"
        const val GOOGLE_CLIENT = "app.klosed.ui.2"
        const val FACEBOOK_CALLBACKMANAGER = "app.klosed.ui.3"
    }
}

fun Fragment.serviceLocator() = this.activity as ServiceLocator
