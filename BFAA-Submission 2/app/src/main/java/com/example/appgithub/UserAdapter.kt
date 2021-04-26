package com.example.appgithub

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mygithub.R
import com.example.mygithub.databinding.ItemUserBinding

class UserAdapter(private var user: ArrayList<User>) : RecyclerView.Adapter<UserAdapter.ListViewHolder>() {
    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback {
        fun OnItemClicked(data: User)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, position: Int): ListViewHolder {
        val listView = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_user, viewGroup, false)
        return ListViewHolder(listView)
    }

    override fun onBindViewHolder(userHolder: ListViewHolder, position: Int) {
        userHolder.bind(user[position])
        userHolder.itemView.setOnClickListener {
            onItemClickCallback.OnItemClicked(user[userHolder.adapterPosition])
        }
    }

    override fun getItemCount(): Int = user.size

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemUserBinding.bind(itemView)

        fun bind(dataUsers: User) {
            with(itemView) {
                Glide.with(this)
                    .load(dataUsers.avatar)
                    .into(binding.avatar)
                binding.name.text = if (dataUsers.name == "null") "-" else dataUsers.name
                binding.username.text = if (dataUsers.username == "null") "-" else dataUsers.username
            }
        }
    }
}
