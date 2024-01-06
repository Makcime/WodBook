package com.example.wodbook.domain

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.wodbook.R
import com.example.wodbook.data.WOD

class WodAdapter(private var wods: List<WOD> = emptyList()) : RecyclerView.Adapter<WodAdapter.WodViewHolder>() {

    class WodViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.image_view_wod)
        val textView: TextView = view.findViewById(R.id.text_view_wod)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WodViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.wod_item, parent, false)
        return WodViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: WodViewHolder, position: Int) {
        val currentItem = wods[position]
        holder.textView.text = currentItem.notes

        // Load the image using Glide
        val uri = Uri.parse(currentItem.picture) // Convert String to Uri
        Glide.with(holder.itemView.context)
            .load(uri) // URL or file path
            .placeholder(R.drawable.ic_placeholder_foreground) // Replace with your placeholder image
            .into(holder.imageView)
    }

    override fun getItemCount() = wods.size

    fun setWods(wods: List<WOD>) {
        this.wods = wods
        notifyDataSetChanged()
    }
}
