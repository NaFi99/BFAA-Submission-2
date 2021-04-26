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
import com.example.mygithub.databinding.FragmentFollowBinding
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.fragment_follow.*
import org.json.JSONArray
import org.json.JSONObject


class FollowersFragment:Fragment() {

    companion object {
        private val TAG = FollowersFragment::class.java.simpleName
        const val EXTRA_USER = "extra_user"
        private const val apikey = BuildConfig.API_TOKEN
    }

    private val binding: FragmentFollowBinding?
        get() {
            return null
        }

    private var dataUser: ArrayList<User> = ArrayList()
    private lateinit var adapter: FollowersAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_follow, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = FollowersAdapter(dataUser)
        dataUser.clear()


        val followers = activity!!.intent.getParcelableExtra<User>(EXTRA_USER)
        listFollowers()
        getFollowers(followers?.username.toString())
    }

    private fun getFollowers(id: String) {
        progress_bar.visibility = View.VISIBLE
        val client = AsyncHttpClient()
        client.addHeader("User-Agent", "request")
        client.addHeader("Authorization", "token $apikey")
        val url = "https://api.github.com/users/$id/followers"
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
                            getFollowersDetail(username)
                        }
                    } catch (e: Exception) {
                        Toast.makeText(activity, e.message, Toast.LENGTH_SHORT).show()
                        e.printStackTrace()
                    }
                }
            }
            override fun onFailure(
                statusCode: Int,
                headers: Array<Header>,
                responseBody: ByteArray,
                error: Throwable
            ) {

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

    private fun getFollowersDetail(id: String) {
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
                        listFollowers()
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

    private fun listFollowers() {
            rvfollow.layoutManager = LinearLayoutManager(activity)
            val follAdapter = FollowersAdapter(dataUser)
            rvfollow.adapter = follAdapter

            follAdapter.setOnItemClickCallback(object : FollowersAdapter.OnItemClickCallBack {
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