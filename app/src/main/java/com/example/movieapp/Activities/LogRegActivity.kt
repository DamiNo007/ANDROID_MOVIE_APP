package com.example.movieapp.Activities

import android.os.Bundle
import android.view.MenuItem
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.viewpager.widget.PagerAdapter
import com.example.movieapp.*
import com.example.movieapp.Adapters.ViewPagerAdpaters.SlidePagerAdapter
import com.example.movieapp.Fragments.LogRegFragments.LoginFragment
import com.example.movieapp.Fragments.LogRegFragments.RegisterFragment
import com.example.movieapp.ViewPager.CustomViewPager
import com.google.android.material.bottomnavigation.BottomNavigationView



class LogRegActivity: AppCompatActivity() {
    private var registerFragment: Fragment =
        RegisterFragment()

    private var loginFragment: Fragment =
        LoginFragment()

    private var fragmentList: MutableList<Fragment> = ArrayList()

    private var fragmentManager: FragmentManager? = null

    private var transaction: FragmentTransaction? = null

    private lateinit var pager: CustomViewPager

    private var pagerAdapter: PagerAdapter? = null

    private val navListener: BottomNavigationView.OnNavigationItemSelectedListener =
        object : BottomNavigationView.OnNavigationItemSelectedListener {
            override fun onNavigationItemSelected(@NonNull menuItem: MenuItem): Boolean {
                transaction = fragmentManager?.beginTransaction()
                pagerAdapter?.notifyDataSetChanged()
                when (menuItem.getItemId()) {
                    R.id.nav_login -> pager?.setCurrentItem(0, false)
                    R.id.nav_register -> pager.setCurrentItem(1, false)
                }
                return false
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.log_reg_layout)
        fragmentManager = supportFragmentManager
        var bottomNavigation: BottomNavigationView = findViewById(R.id.log_reg_nav)
        bottomNavigation.setOnNavigationItemSelectedListener(navListener)
        fragmentList.add(loginFragment)
        fragmentList.add(registerFragment)
        pager = findViewById(R.id.regLogContainer)
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

