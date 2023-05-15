package com.example.taxibooking.presentation.screen.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.taxibooking.databinding.ProfileRowBinding
import com.example.taxibooking.domain.model.User

class ProfilesAdapter(
    private val userList: ArrayList<User> = arrayListOf()
): RecyclerView.Adapter<ProfilesAdapter.ProfileViewHolder>() {
    inner class ProfileViewHolder(private val binding: ProfileRowBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {
            binding.apply {
                tvName.text = user.firstName
                tvLastName.text = user.secondName
                tvEmail.text = user.email
                tvNumber.text = user.phoneNumber
                tvType.text = user.type.name
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ProfileRowBinding.inflate(layoutInflater, parent, false)
        return ProfileViewHolder(binding)
    }

    override fun getItemCount(): Int = userList.size

    override fun onBindViewHolder(holder: ProfileViewHolder, position: Int) {
        holder.bind(userList[position])
    }

    fun setUserList(newList: ArrayList<User>) {
        userList.clear()
        userList.addAll(newList)
        notifyDataSetChanged()
    }
}