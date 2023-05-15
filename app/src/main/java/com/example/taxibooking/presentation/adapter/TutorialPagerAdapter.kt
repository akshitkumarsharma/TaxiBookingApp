package com.example.taxibooking.presentation.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.taxibooking.domain.model.Tutorial
import com.example.taxibooking.presentation.tutorial.content.ContentFragment

class TutorialPagerAdapter(fragment: Fragment, var list: ArrayList<Tutorial>): FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int {
        return list.size
    }

    override fun createFragment(position: Int): Fragment {
        return ContentFragment(list[position])
    }
}