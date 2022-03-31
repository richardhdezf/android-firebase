package com.example.firebaseapp.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.example.firebaseapp.data.RestaurantsRepository
import com.example.firebaseapp.data.model.Filters
import com.example.firebaseapp.data.model.Rating
import com.example.firebaseapp.data.model.Restaurant
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.ExperimentalCoroutinesApi

class RestaurantsViewModel(private val restaurantsRepository: RestaurantsRepository) : ViewModel() {
    private var filters: Filters = Filters.default

    fun addRestaurant(restaurant: Restaurant, ratings: List<Rating>) =
        restaurantsRepository.addRestaurant(restaurant, ratings)

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getRestaurant(restaurantId: String): LiveData<Restaurant> =
        restaurantsRepository.getRestaurant(restaurantId).asLiveData()

    fun getFilters(): Filters = filters

    @OptIn(ExperimentalCoroutinesApi::class)
    fun updateFilters(value: Filters) {
        filters = value
        restaurantsRepository.loadRestaurants(value)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getRestaurants(): LiveData<List<Restaurant>> {
        restaurantsRepository.loadRestaurants(null)
        return restaurantsRepository.getRestaurants()
    }

    fun addRating(restaurantId: String, rating: Rating): Task<Void> =
        restaurantsRepository.addRating(restaurantId, rating)

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getRatings(restaurantId: String): LiveData<List<Rating>> =
        restaurantsRepository.getRatings(restaurantId).asLiveData()
}

class RestaurantsViewModelFactory(private val restaurantsRepository: RestaurantsRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RestaurantsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RestaurantsViewModel(restaurantsRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
