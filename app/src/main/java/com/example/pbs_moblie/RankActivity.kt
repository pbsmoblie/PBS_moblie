package com.example.pbs_moblie

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_rank.*

class RankActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rank)

        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(Fragment_Num(), "뚜벅수랭킹")
        adapter.addFragment(Fragment_time(), "뚜벅시간랭킹")


        viewPager.adapter = adapter
        tabs.setupWithViewPager(viewPager)
    }
}
