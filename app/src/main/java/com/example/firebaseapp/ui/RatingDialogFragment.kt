package com.example.firebaseapp.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.example.firebaseapp.MainApplication
import com.example.firebaseapp.databinding.DialogRatingBinding
import com.example.firebaseapp.data.model.Rating

/**
 * Dialog Fragment containing rating form.
 */
class RatingDialogFragment : DialogFragment() {

    private var _binding: DialogRatingBinding? = null
    private val binding get() = _binding!!
    private var ratingListener: RatingListener? = null
    private val authenticationViewModel: AuthenticationViewModel by viewModels {
        AuthenticationViewModelFactory(MainApplication.authenticationRepository)
    }

    internal interface RatingListener {

        fun onRating(rating: Rating)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogRatingBinding.inflate(inflater, container, false)

        binding.restaurantFormButton.setOnClickListener { onSubmitClicked() }
        binding.restaurantFormCancel.setOnClickListener { onCancelClicked() }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (parentFragment is RatingListener) {
            ratingListener = parentFragment as RatingListener
        }
    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    private fun onSubmitClicked() {
        val user = authenticationViewModel.getCurrentUser()
        user?.let {
            val rating = Rating(
                user.id,
                user.displayName,
                user.email,
                binding.restaurantFormRating.rating.toDouble(),
                binding.restaurantFormText.text.toString()
            )

            ratingListener?.onRating(rating)
        }

        dismiss()
    }

    private fun onCancelClicked() {
        dismiss()
    }

    companion object {

        const val TAG = "RatingDialog"
    }
}
