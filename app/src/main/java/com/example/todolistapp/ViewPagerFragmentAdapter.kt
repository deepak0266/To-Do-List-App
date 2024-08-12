package com.example.todolistapp

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class ViewPagerFragmentAdapter(fm: FragmentManager, var fragments: List<Fragment>, private var titles: List<String>) :
    FragmentPagerAdapter(fm) {


    override fun getCount(): Int = fragments.size

    override fun getItem(position: Int): Fragment = fragments[position]

    override fun getPageTitle(position: Int): CharSequence = titles[position]
}