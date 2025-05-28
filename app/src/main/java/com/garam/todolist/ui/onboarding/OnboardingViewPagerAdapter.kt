package com.garam.todolist.ui.onboarding

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class OnboardingViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    // 페이지 갯수 설정
    override fun getItemCount(): Int = 3

    // 불러올 Fragment 정의
    override fun createFragment(position: Int): Fragment = when (position) {
        0 -> OnboardingFirstFragment()
        1 -> OnboardingSecondFragment()
        2 -> OnboardingThirdFragment()
        else -> throw IllegalArgumentException("Invalid position $position")
    }
}