package com.example.firebaseapp.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.firebaseapp.MainApplication
import com.example.firebaseapp.R
import com.example.firebaseapp.databinding.FragmentRestaurantDetailBinding
import com.example.firebaseapp.data.model.Rating
import com.example.firebaseapp.data.model.Restaurant
import com.example.firebaseapp.ui.adapter.RatingAdapter
import com.example.firebaseapp.util.RestaurantUtil

class RestaurantDetailFragment : Fragment(), RatingDialogFragment.RatingListener {

    private var ratingDialog: RatingDialogFragment? = null

    private lateinit var binding: FragmentRestaurantDetailBinding
    private lateinit var restaurantId: String
    private lateinit var ratingAdapter: RatingAdapter

    private val restaurantsViewModel: RestaurantsViewModel by viewModels {
        RestaurantsViewModelFactory(MainApplication.restaurantsRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRestaurantDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get restaurant ID from extras
        restaurantId = RestaurantDetailFragmentArgs.fromBundle(requireArguments()).keyRestaurantId

        // RecyclerView
        ratingAdapter = RatingAdapter()
        binding.recyclerRatings.layoutManager = LinearLayoutManager(context)
        binding.recyclerRatings.adapter = ratingAdapter

        // Get restaurant
        restaurantsViewModel.getRestaurant(restaurantId)
            .observe(viewLifecycleOwner) { restaurant ->
                if (restaurant != null) {
                    onRestaurantLoaded(restaurant)
                }
            }
        // Get ratings
        restaurantsViewModel.getRatings(restaurantId).observe(viewLifecycleOwner) { itemList ->
            ratingAdapter.submitList(itemList)
            if (itemList.isEmpty()) {
                binding.recyclerRatings.visibility = View.GONE
                binding.viewEmptyRatings.visibility = View.VISIBLE
            } else {
                binding.recyclerRatings.visibility = View.VISIBLE
                binding.viewEmptyRatings.visibility = View.GONE
            }
        }

        ratingDialog = RatingDialogFragment()

        binding.restaurantButtonBack.setOnClickListener { onBackArrowClicked() }
        binding.fabShowRatingDialog.setOnClickListener { onAddRatingClicked() }
    }

    private fun onRestaurantLoaded(restaurant: Restaurant) {
        binding.restaurantName.text = restaurant.name
        binding.restaurantRating.rating = restaurant.avgRating.toFloat()
        binding.restaurantNumRatings.text =
            getString(R.string.fmt_num_ratings, restaurant.numRatings)
        binding.restaurantCity.text = restaurant.city
        binding.restaurantCategory.text = restaurant.category
        binding.restaurantPrice.text = RestaurantUtil.getPriceString(restaurant)

        // Background image
        Glide.with(binding.restaurantImage.context)
            .load(restaurant.photo)
            .into(binding.restaurantImage)
    }

    private fun onBackArrowClicked() {
        requireActivity().onBackPressed()
    }

    private fun onAddRatingClicked() {
        ratingDialog?.show(childFragmentManager, RatingDialogFragment.TAG)
    }

    override fun onRating(rating: Rating) {
        // In a transaction, add the new rating and update the aggregate totals
        restaurantsViewModel.addRating(restaurantId, rating)
        // Hide keyboard and scroll to top
        hideKeyboard()
        binding.recyclerRatings.smoothScrollToPosition(0)
    }

    private fun hideKeyboard() {
        val view = requireActivity().currentFocus
        if (view != null) {
            (requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                .hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}
