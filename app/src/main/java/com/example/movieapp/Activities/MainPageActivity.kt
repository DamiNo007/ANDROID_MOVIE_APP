package com.example.movieapp.Activities

import FirstFragment
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.MenuItem
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.viewpager.widget.PagerAdapter
import com.example.movieapp.*
import com.example.movieapp.Adapters.ViewPagerAdpaters.SlidePagerAdapter
import com.example.movieapp.Fragments.MainPageFragments.SecondFragment
import com.example.movieapp.Fragments.MainPageFragments.ThirdFragment
import com.example.movieapp.ViewPager.CustomViewPager
import com.google.android.material.bottomnavigation.BottomNavigationView
//MAINPAGEACTIVITY
class MainPageActivity: AppCompatActivity(){

    private var firstFragment: Fragment = FirstFragment()
    private var secondFragment: Fragment =
        SecondFragment()
    private var thirdFragment: Fragment =
        ThirdFragment()
    private var fragmentList: MutableList<Fragment> = ArrayList()
    private var fragmentManager: FragmentManager?=null
    private var transaction : FragmentTransaction?=null
    private lateinit var pager: CustomViewPager
    private var pagerAdapter: PagerAdapter?=null

    private val navListener: BottomNavigationView.OnNavigationItemSelectedListener =
        object : BottomNavigationView.OnNavigationItemSelectedListener {
            override fun onNavigationItemSelected(@NonNull menuItem: MenuItem): Boolean {
                transaction = fragmentManager?.beginTransaction()
                pagerAdapter?.notifyDataSetChanged()
                when (menuItem.getItemId()) {
                    R.id.nav_home -> pager.setCurrentItem(0,false)
                    R.id.nav_favorite -> pager?.setCurrentItem(1, false)
                    R.id.nav_account -> pager?.setCurrentItem(2,false)
                }
                return false
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_page)
        fragmentManager = supportFragmentManager
        var bottomNavigation:BottomNavigationView = findViewById(R.id.nav_menu)
        bottomNavigation.setOnNavigationItemSelectedListener ( navListener )
        fragmentList.add(firstFragment)
        fragmentList.add(secondFragment)
        fragmentList.add(thirdFragment)
        pager = findViewById(R.id.container)
        pager.setSwipable(false)
        pagerAdapter =
            SlidePagerAdapter(
                supportFragmentManager,
                fragmentList
            )
        pager.adapter = pagerAdapter
        pager?.adapter = pagerAdapter
    }
}
