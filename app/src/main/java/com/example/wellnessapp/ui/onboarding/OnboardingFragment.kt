package com.example.wellnessapp.ui.onboarding

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.wellnessapp.R

class OnboardingFragment : Fragment() {
    
    companion object {
        private const val ARG_DATA = "onboarding_data"
        
        fun newInstance(data: OnboardingData): OnboardingFragment {
            val fragment = OnboardingFragment()
            val args = Bundle().apply {
                putSerializable(ARG_DATA, data)
            }
            fragment.arguments = args
            return fragment
        }
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_onboarding, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        @Suppress("DEPRECATION")
        val data = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getSerializable(ARG_DATA, OnboardingData::class.java)
        } else {
            arguments?.getSerializable(ARG_DATA) as? OnboardingData
        } ?: return
        
        val emojiText = view.findViewById<TextView>(R.id.emoji_text)
        val titleText = view.findViewById<TextView>(R.id.title_text)
        val descriptionText = view.findViewById<TextView>(R.id.description_text)
        
        emojiText.text = data.emoji
        titleText.text = data.title
        descriptionText.text = data.description
    }
}