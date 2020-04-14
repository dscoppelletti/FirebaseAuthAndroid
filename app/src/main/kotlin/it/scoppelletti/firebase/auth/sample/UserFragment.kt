package it.scoppelletti.firebase.auth.sample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import it.scoppelletti.firebase.auth.sample.databinding.UserFragmentBinding
import it.scoppelletti.firebase.auth.sample.inject.ServiceLocator
import it.scoppelletti.firebase.auth.sample.inject.serviceLocator

class UserFragment : Fragment() {

    private lateinit var viewModel: AuthViewModel
    private lateinit var auth: FirebaseAuth
    private lateinit var googleClient: GoogleSignInClient
    private lateinit var navController: NavController
    private var binding: UserFragmentBinding? = null

    init {
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = UserFragmentBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        navController = findNavController()
        googleClient = serviceLocator().getService(ServiceLocator.GOOGLE_CLIENT)

        viewModel = ViewModelProvider(requireActivity()).get(
                AuthViewModel::class.java)
        auth = viewModel.auth

        val user = auth.currentUser
        if (user == null) {
            navController.navigate(R.id.action_login)
        } else {
            binding?.apply {
                txtUserName.text = user.displayName
                txtEmail.text = user.email
                txtEmailVerified.text = if (user.isEmailVerified)
                    getString(android.R.string.yes) else
                    getString(android.R.string.no)
                txtProviderId.text = user.providerId
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.user, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.cmd_signOut -> {
                viewModel.signOut(auth, googleClient)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}
