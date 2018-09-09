package com.timer

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import com.firebase.ui.auth.AuthUI
import com.timer.fragmetPagerAdapter.ViewPagerAdapter
import kotlinx.android.synthetic.main.activity_home.*
import org.jetbrains.anko.*

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        supportActionBar!!.title = " Home"
        supportActionBar!!.setIcon(R.drawable.ic_home_black_24dp)
        //Button State
        chat.setOnClickListener {

        }
        chat.setOnClickListener {
            viewpager.currentItem = 0
        }
        chat.apply {
            setTextColor(Color.WHITE)
            setTextSize(20f)
        }
        profile.setOnClickListener {
            viewpager.currentItem = 1
        }

        viewpager.adapter = ViewPagerAdapter(supportFragmentManager)
        viewpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                setOnPageChangerListener(position)
            }
        })
    }


    private fun setOnPageChangerListener(position: Int) {
        when (position) {
            0 -> {
                chat.apply {
                    setTextColor(Color.WHITE)
                    setTextSize(20f)
                }
                profile.apply {
                    setTextColor(Color.BLACK)
                    setTextSize(16f)
                }
            }
            1 -> {
                chat.apply {
                    setTextColor(Color.BLACK)
                    setTextSize(16f)
                }
                profile.apply {
                    setTextColor(Color.WHITE)
                    setTextSize(20f)
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.setting, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_logout -> {
                onSignout()
                toast("Signing Out..")
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun onSignout() {
        AuthUI.getInstance().signOut(this).addOnSuccessListener {
            startActivity(intentFor<Registration>().newTask().clearTask())
        }

    }
}