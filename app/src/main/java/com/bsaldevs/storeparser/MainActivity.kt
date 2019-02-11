package com.bsaldevs.storeparser

import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.view.ActionMode
import android.util.Log
import android.view.*
import android.view.animation.Animation
import kotlinx.android.synthetic.main.activity_main.*
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.view.MotionEvent
import android.support.v4.view.ViewPager
import android.widget.Toast
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(), SubscribeForProductFragment.OnFragmentInteractionListener, MainContentFragment.OnMainContentFragmentInteraction {

    override fun onFragmentSetup(fragment: MainContentFragment) {
        Log.d("CDA_MA", "onFragmentSetup")
        val fragments = supportFragmentManager.fragments
        fragments.forEach { f ->
            if (f.tag == fragment.tag) {
                Log.d("CDA_MA", "found equals tag. skip next searching")
                return
            }
        }
        actionModeListeners.add(fragment)
        Log.d("CDA_MA", "action mode listener has been added")
    }

    override fun onSelectedProductCountChanged(count: Int) {
        Log.d("CDA_MA", "onProductSelected")
        if (actionModeActive && count == 0) {
            actionMode!!.finish()
            actionModeActive = false
            return
        }

        if (!actionModeActive && count != 0) {
            actionMode = startSupportActionMode(actionModeCallback)
            actionModeActive = true
            return
        }
    }

    override fun onSubscribeForProductFragmentInteraction(status : String) {
        if (isShowing) {
            when (status) {
                "incorrect url" -> Snackbar.make(coordinator, "Некорректный адрес", Snackbar.LENGTH_SHORT).show()
                "ok" -> finishActionMode()
            }
        }
    }

    private lateinit var fabShow : Animation
    private lateinit var fabHide : Animation
    private var isShowing = false
    private var actionModeActive = false
    private var actionMode : android.support.v7.view.ActionMode? = null
    private val actionModeListeners = ArrayList<OnActionModeListener>()

    private val actionModeCallback = object : android.support.v7.view.ActionMode.Callback {
        override fun onActionItemClicked(p0: ActionMode?, p1: MenuItem?): Boolean {
            Log.d("CDA", "item clicked")
            return false
        }

        override fun onCreateActionMode(p0: ActionMode?, p1: Menu?): Boolean {
            val mMenuInflater = p0!!.menuInflater
            mMenuInflater.inflate(R.menu.contextual_action_mode_menu, p1)
            val fabMarginLayoutParams = fab.layoutParams as ViewGroup.MarginLayoutParams
            val animateDistance = (fab.height + fabMarginLayoutParams.bottomMargin).toFloat()
            fab.animate().translationY(animateDistance)
            Log.d("CDA_MA", "animate distance for fab $animateDistance")
            actionModeListeners.forEach { listener ->
                listener.onStartActionMode()
            }
            return true
        }

        override fun onPrepareActionMode(p0: ActionMode?, p1: Menu?): Boolean {
            return false
        }

        override fun onDestroyActionMode(p0: ActionMode?) {
            fab.animate().translationY(0f)
            actionModeListeners.forEach { listener ->
                listener.onDestroyActionMode()
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        initAnimation()

        fab.setOnClickListener { view ->
            Log.d("CDA", "fab on click")
            fabAction()
        }

        setupViewPager()
    }

    private fun fabAction() {
        Log.d("CDA", "is showing - $isShowing")
        when (isShowing) {
            true -> {
                //fab.startAnimation(fabHide)
                (supportFragmentManager.fragments[1] as SubscribeForProductFragment).onFloatingActionButtonClick()
            }
            false -> {
                //fab.startAnimation(fabShow)
                startActionMode()
            }
        }
    }

    private fun setupViewPager() {
        val pagerAdapter = ViewPagerAdapter(supportFragmentManager)

        pagerAdapter.addPage(MainContentFragment())
        pagerAdapter.addPage(SubscribeForProductFragment())

        view_pager.adapter = pagerAdapter
        view_pager.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                return true
            }
        })
        view_pager.setPageTransformer(false, object : ViewPager.PageTransformer {
            override fun transformPage(page: View, position: Float) {

                page.translationX = -position * page.width
                page.cameraDistance = 12000F

                if (position < 0.5 && position > -0.5) {
                    page.visibility = View.VISIBLE
                } else {
                    page.visibility = View.INVISIBLE
                }

                when {
                    position < -1 -> // [-Infinity,-1)
                        // This page is way off-screen to the left.
                        page.alpha = 0f
                    position <= 0 -> {    // [-1,0]
                        page.alpha = 1f
                        page.rotationY = 180 * (1 - Math.abs(position) + 1)

                    }
                    position <= 1 -> {    // (0,1]
                        page.alpha = 1f
                        page.rotationY = -180 * (1 - Math.abs(position) + 1)

                    }
                    else -> // (1,+Infinity]
                        // This page is way off-screen to the right.
                        page.alpha = 0f
                }


            }
        })

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> {
                val intent = Intent(this@MainActivity, SettingActivity::class.java)
                startActivity(intent)
            } //Toast.makeText(this@MainActivity, "Настройки", Toast.LENGTH_SHORT).show()
            android.R.id.home -> finishActionMode()
            else -> super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun initAnimation() {
        fabShow = android.view.animation.AnimationUtils.loadAnimation(this@MainActivity, R.anim.fab_click_show)
        fabHide = android.view.animation.AnimationUtils.loadAnimation(this@MainActivity, R.anim.fab_click_hide)
    }

    private fun startActionMode() {
        //actionMode = startSupportActionMode(actionModeCallback)
        fab.setImageDrawable(ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_baseline_done_24px))
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setTitle("Добавить товар")
        view_pager.currentItem = 1
        isShowing = true
    }

    private fun finishActionMode() {
        //actionMode!!.finish()
        fab.setImageDrawable(ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_baseline_add_24px))
        supportActionBar!!.setDisplayHomeAsUpEnabled(false)
        supportActionBar!!.setTitle(R.string.app_name)
        view_pager.currentItem = 0
        isShowing = false
    }

    override fun onBackPressed() {
        when (isShowing) {
            true -> finishActionMode()
            false -> super.onBackPressed()
        }
    }

    private inner class ViewPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        val fragments = ArrayList<Fragment>()

        fun addPage(fragment: Fragment) {
            fragments.add(fragment)
        }

        override fun getItem(position: Int): Fragment {
            return fragments[position]
        }

        override fun getCount(): Int {
            return fragments.size
        }

    }

}
