package com.example.taxibooking.presentation.tutorial.content

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.example.taxibooking.R
import com.example.taxibooking.databinding.FragmentContentBinding
import com.example.taxibooking.domain.model.Tutorial


class ContentFragment(
    private val tutorial: Tutorial
) : Fragment() {

    private lateinit var binding: FragmentContentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentContentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tutorial.let {
            binding.apply {
                title.text = it.title
                image.setImageResource(it.image)
                desc.text = it.description
            }
        }
    }

}