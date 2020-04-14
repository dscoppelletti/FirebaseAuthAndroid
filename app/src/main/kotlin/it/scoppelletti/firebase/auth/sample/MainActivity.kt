@file:Suppress("JoinDeclarationAndAssignment")

package it.scoppelletti.firebase.auth.sample

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.facebook.CallbackManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import it.scoppelletti.firebase.auth.sample.databinding.MainActivityBinding
import it.scoppelletti.firebase.auth.sample.inject.ServiceLocator
import it.scoppelletti.spaceship.app.showExceptionDialog
import it.scoppelletti.spaceship.app.tryFinish

class MainActivity : AppCompatActivity(), ServiceLocator {

    private lateinit var viewModel: AuthViewModel
    private lateinit var binding: MainActivityBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfig: AppBarConfiguration
    private lateinit var googleClient: GoogleSignInClient
    private lateinit var callbackManager: CallbackManager

    override fun onCreate(savedInstanceState: Bundle?) {
        val navHost: NavHostFragment

        super.onCreate(savedInstanceState)

        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        navHost = supportFragmentManager.findFragmentById(
                R.id.nav_host_fragment) as NavHostFragment
        navController = navHost.navController

        appBarConfig = AppBarConfiguration(setOf(R.id.dest_user,
                R.id.dest_signin))
        setupActionBarWithNavController(navController, appBarConfig)

        val googleOptions = GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(BuildConfig.WEBCLIENT_CLIENTID)
                .requestEmail()
                .build()

        googleClient = GoogleSignIn.getClient(applicationContext, googleOptions)
        callbackManager = CallbackManager.Factory.create()

        viewModel = ViewModelProvider(this).get(AuthViewModel::class.java)

        viewModel.command.observe(this, Observer { command ->
            command?.poll()?.let {
                viewCommandHandler(it)
            }
        })
    }

    private fun viewCommandHandler(command: AuthCommand) {
        when (command) {
            is AuthCommand.LoggedIn -> {
                binding.progressIndicator.hide()
                navController.navigate(R.id.action_user)
            }

            is AuthCommand.IdTokenChanged -> {
                viewModel.getIdToken(command.user)
            }

            is AuthCommand.Finish -> {
                tryFinish()
            }

            is AuthCommand.Error -> {
                binding.progressIndicator.hide()
                showExceptionDialog(command.ex)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfig)
    }

    override fun onActivityResult(
            requestCode: Int,
            resultCode: Int,
            data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> getService(key: String): T =
            when (key) {
                ServiceLocator.PROGRESS_INDICATOR ->
                    binding.progressIndicator as T
                ServiceLocator.GOOGLE_CLIENT ->
                    googleClient as T
                ServiceLocator.FACEBOOK_CALLBACKMANAGER ->
                    callbackManager as T
                else ->
                    throw NoSuchElementException("Service $key not found.")
            }
}
