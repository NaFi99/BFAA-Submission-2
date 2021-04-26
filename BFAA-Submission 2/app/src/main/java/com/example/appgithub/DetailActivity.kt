package com.example.appgithub


import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.annotation.StringRes
import androidx.viewpager2.widget.ViewPager2
import com.example.mygithub.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_USER = "extra_user"

        @StringRes
        private val TAB_TITLES = intArrayOf(
            R.string.tab2,
            R.string.tab3
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        setData()
        viewPagerConfig()
    }

    private fun viewPagerConfig() {
        val user = intent.getParcelableExtra<User>(EXTRA_USER) as User
        val pagerAdapter = SectionPagerAdapter(this)
        pagerAdapter.username = user.username
        val viewPager: ViewPager2 = findViewById(R.id.pager)
        viewPager.adapter = pagerAdapter
        val tabs: TabLayout = findViewById(R.id.tab)
        TabLayoutMediator(tabs, viewPager) {tab, position -> tab.text = resources.getString(
            TAB_TITLES[position])}.attach()

        supportActionBar?.elevation = 0f
    }

    private fun setData() {
        val progressBar = findViewById<ProgressBar>(R.id.progress_bar)
        progressBar.visibility = View.GONE
        val user = intent.getParcelableExtra<User>(EXTRA_USER) as User
        val imagesAvatar: CircleImageView = findViewById(R.id.imgAvatar)
        val images: String? = user.avatar
        Glide.with(this)
            .load(images)
            .apply(RequestOptions().override(150, 150))
            .into(imagesAvatar)
        tvName.text = if (user.name == "null") "-" else user.name
        tvUsername.text = if (user.username == "null") "-" else user.username
        tv_followers.text = user.followers
        tv_following.text = user.following
        tv_repository.text = user.repository
        tvCompany.text = if (user.company == "null") "-" else user.company
        tvLocation.text = if (user.location == "null") "-" else user.location
        }
}