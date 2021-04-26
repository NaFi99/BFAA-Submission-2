package com.example.appgithub

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter


class SectionPagerAdapter(private val activity: AppCompatActivity) : FragmentStateAdapter(activity) {

    var username : String? = null

    private val pages = listOf(
            FollowersFragment(),
            FollowingFragment()
    )

    override fun createFragment(position: Int): Fragment {
        val fragment =  pages[position]
        fragment.arguments = Bundle().apply{
            putString("username", username)
        }
        return fragment
    }

    override fun getItemCount(): Int {
        return pages.size
    }
}
