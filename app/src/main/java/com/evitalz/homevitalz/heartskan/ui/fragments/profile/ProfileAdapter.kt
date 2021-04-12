package com.evitalz.homevitalz.heartskan.ui.fragments.profile

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.evitalz.homevitalz.heartskan.R
import com.evitalz.homevitalz.heartskan.databinding.RowProfileBinding


class ProfileAdapter internal constructor(
    private val context: Context,
    profileFragment: ProfileFragment
):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class ProfileViewholder(val binding: RowProfileBinding) : RecyclerView.ViewHolder(binding.root)
    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding: RowProfileBinding = DataBindingUtil.inflate(inflater, R.layout.row_profile, parent, false)
        return ProfileViewholder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

    }

    override fun getItemCount(): Int {
        return 5
    }
}