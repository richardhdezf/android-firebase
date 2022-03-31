package com.example.firebaseapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.firebaseapp.R
import com.example.firebaseapp.databinding.ItemRestaurantBinding
import com.example.firebaseapp.data.model.Restaurant
import com.example.firebaseapp.util.RestaurantUtil

/**
 * RecyclerView adapter for a list of Restaurants.
 */
class RestaurantAdapter(private val listener: OnRestaurantSelectedListener) :
    ListAdapter<Restaurant, RestaurantAdapter.ViewHolder>(RESTAURANT_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemRestaurantBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), listener)
    }

    class ViewHolder(private val binding: ItemRestaurantBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            restaurant: Restaurant,
            listener: OnRestaurantSelectedListener?
        ) {
            val resources = binding.root.resources
            // Load image
            Glide.with(binding.restaurantItemImage.context)
                .load(restaurant.photo)
                .into(binding.restaurantItemImage)

            val numRatings: Int = restaurant.numRatings

            binding.restaurantItemName.text = restaurant.name
            binding.restaurantItemRating.rating = restaurant.avgRating.toFloat()
            binding.restaurantItemCity.text = restaurant.city
            binding.restaurantItemCategory.text = restaurant.category
            binding.restaurantItemNumRatings.text = resources.getString(
                R.string.fmt_num_ratings,
                numRatings
            )
            binding.restaurantItemPrice.text = RestaurantUtil.getPriceString(restaurant)

            // Click listener
            binding.root.setOnClickListener {
                listener?.onRestaurantSelected(restaurant)
            }
        }
    }

    interface OnRestaurantSelectedListener {

        fun onRestaurantSelected(restaurant: Restaurant)
    }

    companion object {
        private val RESTAURANT_COMPARATOR = object : DiffUtil.ItemCallback<Restaurant>() {
            override fun areItemsTheSame(
                oldItem: Restaurant,
                newItem: Restaurant
            ): Boolean {
                return oldItem.name == newItem.name
            }

            override fun areContentsTheSame(
                oldItem: Restaurant,
                newItem: Restaurant
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}
