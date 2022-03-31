package com.example.firebaseapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.firebaseapp.databinding.ItemRatingBinding
import com.example.firebaseapp.data.model.Rating
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * RecyclerView adapter for a list of [Rating].
 */
class RatingAdapter : ListAdapter<Rating, RatingAdapter.ViewHolder>(RATING_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemRatingBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(private val binding: ItemRatingBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(rating: Rating?) {
            if (rating == null) {
                return
            }

            binding.ratingItemName.text = rating.userName
            binding.ratingItemRating.rating = rating.rating.toFloat()
            binding.ratingItemText.text = rating.text

            if (rating.timestamp != null) {
                binding.ratingItemDate.text = FORMAT.format(rating.timestamp!!)
            }
        }

        companion object {

            private val FORMAT = SimpleDateFormat(
                "MM/dd/yyyy", Locale.US
            )
        }
    }

    companion object {
        private val RATING_COMPARATOR = object : DiffUtil.ItemCallback<Rating>() {
            override fun areItemsTheSame(
                oldItem: Rating,
                newItem: Rating
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: Rating,
                newItem: Rating
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}
