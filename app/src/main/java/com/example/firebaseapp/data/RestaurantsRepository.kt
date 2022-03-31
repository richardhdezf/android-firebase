package com.example.firebaseapp.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.firebaseapp.data.model.Filters
import com.example.firebaseapp.data.model.Rating
import com.example.firebaseapp.data.model.Restaurant
import com.example.firebaseapp.data.model.SortDirectionBy
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class RestaurantsRepository(private val firestore: FirebaseFirestore) {
    private var restaurantsCollection: CollectionReference = firestore
        .collection("restaurants")
    private var restaurantsRegistration: ListenerRegistration? = null
    private var restaurantRegistration: ListenerRegistration? = null
    private var restaurants: MutableLiveData<List<Restaurant>> = MutableLiveData(emptyList())

    fun addRestaurant(restaurant: Restaurant, ratings: List<Rating>) {
        // Add a bunch of random restaurants
        val batch = firestore.batch()
        val restRef = restaurantsCollection.document()
        batch.set(restRef, restaurant)
        ratings.forEach { rating ->
            batch.set(restRef.collection("ratings").document(), rating)
        }
        batch.commit()
    }

    private fun updateQuery(filters: Filters): Query {
        var query: Query = restaurantsCollection
        // Category (equality filter)
        if (filters.hasCategory()) {
            query = query.whereEqualTo(Restaurant.FIELD_CATEGORY, filters.category)
        }
        // City (equality filter)
        if (filters.hasCity()) {
            query = query.whereEqualTo(Restaurant.FIELD_CITY, filters.city)
        }
        // Price (equality filter)
        if (filters.hasPrice()) {
            query = query.whereEqualTo(Restaurant.FIELD_PRICE, filters.price)
        }
        // Sort by (orderBy with direction)
        if (filters.hasSortBy()) {
            val queryDirection = when (filters.sortDirection) {
                SortDirectionBy.ASCENDING -> Query.Direction.ASCENDING
                SortDirectionBy.DESCENDING -> Query.Direction.DESCENDING
            }
            query = query.orderBy(
                filters.sortBy!!,
                queryDirection
            )
        }
        // Limit items
        query = query.limit(LIMIT.toLong())
        return query
    }

    @ExperimentalCoroutinesApi
    fun getRestaurant(restaurantId: String): Flow<Restaurant> = callbackFlow {
        val restaurantRef = restaurantsCollection.document(restaurantId)
        restaurantRegistration?.remove()
        restaurantRegistration = restaurantRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.d("error", "restaurant error", e)
                return@addSnapshotListener
            }
            if (snapshot == null) {
                return@addSnapshotListener
            }
            try {
                val restaurant = snapshot.toObject(Restaurant::class.java)
                trySend(restaurant!!)
            } catch (e: Throwable) {
                // Event couldn't be sent to the flow
                Log.d("error", "restaurant event couldn't be sent to the flow", e)
            }
        }
        awaitClose { restaurantRegistration?.remove() }
    }

    fun getRestaurants(): LiveData<List<Restaurant>> = restaurants

    @ExperimentalCoroutinesApi
    fun loadRestaurants(filters: Filters?){
        val query = if (filters == null)
            restaurantsCollection
                .orderBy("avgRating", Query.Direction.DESCENDING)
                .limit(LIMIT.toLong())
        else updateQuery(filters)
        restaurantsRegistration?.remove()
        restaurantsRegistration = query.addSnapshotListener { querySnapshot, e ->
            if (e != null) {
                Log.d("error", "restaurant query error", e)
                return@addSnapshotListener
            }
            if (querySnapshot == null) {
                return@addSnapshotListener
            }
            try {
                val itemList = ArrayList<Restaurant>()
                for (doc in querySnapshot) {
                    val item = doc.toObject(Restaurant::class.java)
                    itemList.add(item)
                }
                restaurants.value = itemList
            } catch (e: Throwable) {
                // Event couldn't be sent to the flow
                Log.d("error", "restaurant event couldn't be sent to liveData", e)
            }
        }
    }

    fun addRating(restaurantId: String, rating: Rating): Task<Void> {
        val restaurantRef = restaurantsCollection.document(restaurantId)
        val ratingRef = restaurantRef.collection("ratings").document()

        // In a transaction, add the new rating and update the aggregate totals
        return firestore.runTransaction { transaction ->
            val restaurant = transaction.get(restaurantRef).toObject(Restaurant::class.java)
                ?: throw Exception("Restaurant not found at ${restaurantRef.path}")
            // Compute new number of ratings
            val newNumRatings = restaurant.numRatings + 1
            // Compute new average rating
            val oldRatingTotal = restaurant.avgRating * restaurant.numRatings
            val newAvgRating = (oldRatingTotal + rating.rating) / newNumRatings
            // Set new restaurant info
            restaurant.numRatings = newNumRatings
            restaurant.avgRating = newAvgRating
            // Commit to Firestore
            transaction.set(restaurantRef, restaurant)
            transaction.set(ratingRef, rating)
            null
        }
    }

    @ExperimentalCoroutinesApi
    fun getRatings(restaurantId: String): Flow<List<Rating>> = callbackFlow {
        val restaurantRef = restaurantsCollection.document(restaurantId)
        val ratingsQuery = restaurantRef
            .collection("ratings")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(50)

        val subscription = ratingsQuery.addSnapshotListener { querySnapshot, e ->
            if (e != null) {
                Log.d("error", "rating query error", e)
                return@addSnapshotListener
            }
            if (querySnapshot == null) {
                return@addSnapshotListener
            }
            try {
                val ratings = ArrayList<Rating>()
                for (doc in querySnapshot) {
                    val rating = doc.toObject(Rating::class.java)
                    ratings.add(rating)
                }
                trySend(ratings)
            } catch (e: Throwable) {
                // Event couldn't be sent to the flow
                Log.d("error", "rating event couldn't be sent to the flow", e)
            }
        }
        awaitClose { subscription.remove() }
    }

    companion object {
        private const val LIMIT = 50
    }
}
