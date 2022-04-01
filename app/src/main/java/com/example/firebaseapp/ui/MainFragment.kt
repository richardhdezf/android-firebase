package com.example.firebaseapp.ui

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.firebaseapp.MainApplication
import com.example.firebaseapp.R
import com.example.firebaseapp.databinding.FragmentMainBinding
import com.example.firebaseapp.data.model.Filters
import com.example.firebaseapp.data.model.Restaurant
import com.example.firebaseapp.util.RatingUtil
import com.example.firebaseapp.util.RestaurantUtil
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.example.firebaseapp.ui.adapter.RestaurantAdapter
import com.firebase.ui.auth.AuthUI

class MainFragment : Fragment(),
    FilterDialogFragment.FilterListener,
    RestaurantAdapter.OnRestaurantSelectedListener {

    private lateinit var binding: FragmentMainBinding
    private lateinit var filterDialog: FilterDialogFragment
    private lateinit var adapter: RestaurantAdapter

    private val authUI: AuthUI = MainApplication.authUI
    private val signInLauncher =
        registerForActivityResult(FirebaseAuthUIActivityResultContract()) { result ->
            this.onSignInResult(result)
        }

    private val authenticationViewModel: AuthenticationViewModel by viewModels {
        AuthenticationViewModelFactory(MainApplication.authenticationRepository)
    }
    private val restaurantsViewModel: RestaurantsViewModel by viewModels {
        RestaurantsViewModelFactory(MainApplication.restaurantsRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // RecyclerView
        adapter = RestaurantAdapter(this@MainFragment)

        binding.recyclerRestaurants.layoutManager = LinearLayoutManager(context)
        binding.recyclerRestaurants.adapter = adapter

        // Get restaurants
        restaurantsViewModel.getRestaurants().observe(viewLifecycleOwner) { itemList ->
            adapter.submitList(itemList)
            if (itemList.isEmpty()) {
                binding.recyclerRestaurants.visibility = View.GONE
                binding.viewEmpty.visibility = View.VISIBLE
            } else {
                binding.recyclerRestaurants.visibility = View.VISIBLE
                binding.viewEmpty.visibility = View.GONE
            }

            val filters = restaurantsViewModel.getFilters()
            // Set header
            binding.textCurrentSearch.text = HtmlCompat.fromHtml(
                filters.getSearchDescription(requireContext()),
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
            binding.textCurrentSortBy.text = filters.getOrderDescription(requireContext())
        }

        // Filter Dialog
        filterDialog = FilterDialogFragment()

        binding.filterBar.setOnClickListener { onFilterClicked() }
        binding.buttonClearFilter.setOnClickListener { onClearFilterClicked() }
    }

    override fun onStart() {
        super.onStart()

        // Start sign in if necessary
        if (shouldStartSignIn()) {
            startSignIn()
            return
        }

        // Apply filters
        onFilter(restaurantsViewModel.getFilters())
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_add_items -> onAddItemsClicked()
            R.id.menu_sign_out -> {
                authUI.signOut(requireContext())
                startSignIn()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun onAddItemsClicked() {
        for (i in 0..9) {
            // Create random restaurant / ratings
            val randomRestaurant = RestaurantUtil.getRandom(requireContext())
            val randomRatings = RatingUtil.getRandomList(randomRestaurant.numRatings)
            randomRestaurant.avgRating = RatingUtil.getAverageRating(randomRatings)
            // Add restaurant
            restaurantsViewModel.addRestaurant(randomRestaurant, randomRatings)
        }
    }

    private fun onFilterClicked() {
        // Show the dialog containing filter options
        filterDialog.show(childFragmentManager, FilterDialogFragment.TAG)
    }

    private fun onClearFilterClicked() {
        filterDialog.resetFilters()

        onFilter(Filters.default)
    }

    override fun onRestaurantSelected(restaurant: Restaurant) {
        val action = MainFragmentDirections
            .actionMainFragmentToRestaurantDetailFragment(restaurant.id!!)
        findNavController().navigate(action)
    }

    override fun onFilter(filters: Filters) {
        restaurantsViewModel.updateFilters(filters)
    }


    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        authenticationViewModel.updateSigningStatus(false)

        if (result.resultCode != Activity.RESULT_OK) {
            if (response == null) {
                // User pressed the back button.
                requireActivity().finish()
            } else if (response.error != null &&
                response.error!!.errorCode == ErrorCodes.NO_NETWORK
            ) {
                showSignInErrorDialog(R.string.message_no_network)
            } else {
                showSignInErrorDialog(R.string.message_unknown)
            }
        }
    }

    private fun shouldStartSignIn(): Boolean {
        return !authenticationViewModel.isSigningIn() &&
                authenticationViewModel.getCurrentUser() == null
    }

    private fun startSignIn() {
        val intent = authUI.createSignInIntentBuilder()
            .setAvailableProviders(listOf(AuthUI.IdpConfig.EmailBuilder().build()))
            .setIsSmartLockEnabled(false)
            .build()

        signInLauncher.launch(intent)
        authenticationViewModel.updateSigningStatus(true)
    }

    private fun showSignInErrorDialog(@StringRes message: Int) {
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(R.string.title_sign_in_error)
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton(R.string.option_retry) { _, _ -> startSignIn() }
            .setNegativeButton(R.string.option_exit) { _, _ -> requireActivity().finish() }
            .create()
        dialog.show()
    }
}
