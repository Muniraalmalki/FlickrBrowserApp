package com.example.flickrbrowser

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.flickrbrowser.databinding.ItemRowBinding


class RecyclerViewAdapter(private val  activity: MainActivity, private var imagesList:ArrayList<ImageData>): RecyclerView.Adapter<RecyclerViewAdapter.ItemViewHolder>() {
    class ItemViewHolder(val binding: ItemRowBinding): RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return  ItemViewHolder(ItemRowBinding.inflate(
            LayoutInflater.from(parent.context)
            , parent,
            false))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val image = imagesList[position]
        holder.binding.apply {
            tvName.text = image.title
            Glide.with(activity).load(image.link).into(pic)
            LinearLayoutRowItem.setOnClickListener { activity.viewImage(image.link) }
        }
    }


    override fun getItemCount(): Int {
        return imagesList.size
    }

}