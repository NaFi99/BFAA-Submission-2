package com.example.appgithub

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mygithub.BuildConfig
import com.example.mygithub.R
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.fragment_follow.*
import org.json.JSONArray
import org.json.JSONObject


class FollowingFragment : Fragment() {

    companion object {
        private val TAG = FollowingFragment::class.java.simpleName
        const val EXTRA_USER = "extra_user"
        private const val apikey = BuildConfig.API_TOKEN
    }

    private var dataUser: ArrayList<User> = ArrayList()
    private lateinit var adapter: FollowingAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_follow, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = FollowingAdapter(dataUser)
        dataUser.clear()

        val followers = activity!!.intent.getParcelableExtra<User>(FollowingFragment.EXTRA_USER)
        listFollowing()
        getFollowing(followers?.username.toString())
    }


    private fun getFollowing(id: String) {
        progress_bar.visibility = View.VISIBLE
        val client = AsyncHttpClient()
        client.addHeader("User-Agent", "request")
        client.addHeader("Authorization", "token $apikey")
        val url = "https://api.github.com/users/$id/following"
        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<Header>, responseBody: ByteArray) {

                if (progress_bar != null) {
                    progress_bar.visibility = View.INVISIBLE
                    val result = String(responseBody)
                    Log.d(TAG, result)
                    try {
                        val jsonArray = JSONArray(result)
                        for (i in 0 until jsonArray.length()) {
                            val jsonObject = jsonArray.getJSONObject(i)
                            val username: String = jsonObject.getString("login")
                            getFollowingDetail(username)
                        }
                    } catch (e: Exception) {
                        activity?.let {
                            Toast.makeText(activity, e.message, Toast.LENGTH_SHORT).show()
                        }
                        e.printStackTrace()
                    }
                }
            }
            override fun onFailure(statusCode: Int, headers: Array<Header>, responseBody: ByteArray, error: Throwable) {

                progress_bar.visibility = View.INVISIBLE
                val errorMessage = when (statusCode) {
                    401 -> "$statusCode : Bad Request"
                    403 -> "$statusCode : Forbidden"
                    404 -> "$statusCode : Not Found"
                    else -> "$statusCode : ${error.message}"
                }

                Toast.makeText(activity, errorMessage, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun getFollowingDetail(id: String) {
        progress_bar.visibility = View.VISIBLE
        val client = AsyncHttpClient()
        client.addHeader("User-Agent", "request")
        client.addHeader("Authorization", "token $apikey")
        val url = "https://api.github.com/users/$id"
        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<Header>, responseBody: ByteArray) {

                if (progress_bar != null) {
                    progress_bar.visibility = View.INVISIBLE
                    val result = String(responseBody)
                    Log.d(TAG, result)
                    try {
                        val jsonObject = JSONObject(result)
                        val avatar: String = jsonObject.getString("avatar_url")
                        val username: String = jsonObject.getString("login")
                        val name: String = jsonObject.getString("name")
                        val followers: String = jsonObject.getString("followers")
                        val following: String = jsonObject.getString("following")
                        val repository: String = jsonObject.getString("public_repos")
                        val company: String = jsonObject.getString("company")
                        val location: String = jsonObject.getString("location")
                        dataUser.add(
                            User(
                                avatar,
                                username,
                                name,
                                followers,
                                following,
                                repository,
                                company,
                                location
                            )
                        )
                        listFollowing()
                    } catch (e: Exception) {
                        Toast.makeText(activity, e.message, Toast.LENGTH_SHORT).show()
                        e.printStackTrace()
                    }
                }
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>, responseBody: ByteArray, error: Throwable) {

                progress_bar.visibility = View.INVISIBLE
                val errorMessage = when (statusCode) {
                    401 -> "$statusCode : Bad Request"
                    403 -> "$statusCode : Forbidden"
                    404 -> "$statusCode : Not Found"
                    else -> "$statusCode : ${error.message}"
                }

                Toast.makeText(activity, errorMessage, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun listFollowing() {
        rvfollow.layoutManager = LinearLayoutManager(activity)
        val adapter = FollowingAdapter(dataUser)
        rvfollow.adapter = adapter

        adapter.setOnItemClickCallback(object : FollowingAdapter.OnItemClickCallBack {
            override fun onItemClicked(data: User) = openSelectedList(data)
        })
    }

    private fun openSelectedList(user: User) {
        User(
                user.avatar,
                user.name,
                user.username,
                user.followers,
                user.following,
                user.repository,
                user.company,
                user.location
        )

        val intent = Intent(activity, DetailActivity::class.java)
        intent.putExtra(DetailActivity.EXTRA_USER, user)

        activity?.startActivity(intent)
        Toast.makeText(activity, user.name, Toast.LENGTH_SHORT).show()
    }
}